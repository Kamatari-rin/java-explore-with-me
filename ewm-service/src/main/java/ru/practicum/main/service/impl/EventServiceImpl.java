package ru.practicum.main.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.GetStatsDto;
import ru.practicum.main.dto.event.EventFullDto;
import ru.practicum.main.dto.event.EventShortDto;
import ru.practicum.main.dto.event.NewEventDto;
import ru.practicum.main.dto.request.UpdateEventDto;
import ru.practicum.main.entity.*;
import ru.practicum.main.entity.enums.EventSort;
import ru.practicum.main.entity.enums.EventStatus;
import ru.practicum.main.entity.enums.RequestStatus;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mapper.EventMapper;
import ru.practicum.main.mapper.LocationMapper;
import ru.practicum.main.repository.*;
import ru.practicum.main.service.EventService;
import ru.practicum.main.util.Pagination;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.constant.Constants.TIMESTAMP_PATTERN;
import static ru.practicum.main.entity.enums.EventStatus.*;
import static ru.practicum.main.entity.enums.StateAction.*;
import static ru.practicum.main.exception.NotFoundException.notFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;

    private final StatsClient statsClient;

    private final LocationMapper locationMapper;
    private final EventMapper eventMapper;

    @Value("${app.name}")
    private String app;

///////////////////////////////////////////////// ADMIN SERVICE ////////////////////////////////////////////////////////

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getEventsByAdmin(Set<Long> userIds,
                                               Set<Long> categoryIds,
                                               List<EventStatus> states,
                                               LocalDateTime rangeStart,
                                               LocalDateTime rangeEnd,
                                               Integer from,
                                               Integer size) {
        List<Event> events = eventRepository.findAllForAdmin(
                userIds, states, categoryIds, rangeStart, new Pagination(from, size, Sort.unsorted()));
        return mapToEventFullDto(events);
    }

    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventDto dto) {
        Event event = getEventOrThrowException(eventId);
        updateEventFields(event, dto);

        if (dto.getStateAction() != null) {
            if (event.getState().equals(PENDING)) {
                if (dto.getStateAction().equals(PUBLISH_EVENT)) {
                    event.setState(PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                }
                if (dto.getStateAction().equals(REJECT_EVENT)) {
                    event.setState(CANCELED);
                }
            } else {
                throw new ValidationException(
                        "Cannot publish or cancel the event because it's not in the right state: " + event.getState()
                );
            }
        }

        if (dto.getEventDate() != null && event.getState().equals(PUBLISHED)) {
            if (dto.getEventDate().isAfter(event.getPublishedOn().plusHours(1))) {
                event.setEventDate(dto.getEventDate());
            } else {
                throw new ValidationException("The event date must be at least 1 hour after the published date.");
            }
        }

        eventRepository.save(event);
        locationRepository.save(event.getLocation());

        return mapToEventFullDto(List.of(event)).get(0);
    }

///////////////////////////////////////////////// PUBLIC SERVICE ////////////////////////////////////////////////////////

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getAllForPublic(String text,
                                               Set<Long> categoriesIds,
                                               Boolean paid,
                                               LocalDateTime rangeStart,
                                               LocalDateTime rangeEnd,
                                               Boolean onlyAvailable,
                                               EventSort sort,
                                               Integer from,
                                               Integer size,
                                               HttpServletRequest request) {
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd != null && rangeEnd.isBefore(rangeStart)) {
            throw new ValidationException("RangeStart cannot be later than rangeEnd");
        }

        List<Event> events = eventRepository.findAllPublishStateAvailable(PUBLISHED, rangeStart,
                categoriesIds, paid, text, new Pagination(from, size, Sort.unsorted()));

        statsClient.saveStats(app, request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());

        return mapToEventShortDto(events);
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventByIdPublic(Long eventId, HttpServletRequest request) {
        Event event = getEventOrThrowException(eventId);
        if (!event.getState().equals(PUBLISHED)) {
            throw new NotFoundException(String.format("Event %s not published", eventId));
        }

        statsClient.saveStats(app, request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());
        return mapToEventFullDto(List.of(event)).get(0);
    }

///////////////////////////////////////////////// PRIVATE SERVICE //////////////////////////////////////////////////////

    @Override
    public EventFullDto save(Long userId, NewEventDto newEventDto) {
        User user = getUserOrThrowException(userId);

        Category category = getCategoryOrThrowException(newEventDto.getCategory());

        Location location = locationRepository.save(
                locationMapper.toLocation(newEventDto.getLocation())
        );

        Event event = eventMapper.toEvent(newEventDto, location, category, PENDING, user);
        event.setCreatedOn(LocalDateTime.now());

        return eventMapper.toEventFullDto(
                eventRepository.save(event)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getAllByUserId(Long userId, Integer from, Integer size) {
        return mapToEventShortDto(
                eventRepository.findAllByInitiator_Id(userId, new Pagination(from, size, Sort.unsorted()))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getOneByUserId(Long userId, Long eventId) {
        getUserOrThrowException(userId);
        return mapToEventFullDto(
                List.of(getEventOrThrowException(eventId)))
                .get(0);
    }

    @Override
    public EventFullDto updateByUser(Long userId, Long eventId, UpdateEventDto dto) {
        getUserOrThrowException(userId);
        Event event = getEventOrThrowException(eventId);

        if (event.getState().equals(PUBLISHED)) {
            throw new ValidationException("Only canceled events or events pending moderation can be changed");
        }

        validateEventDate(event.getEventDate());

        updateEventFields(event, dto);

        if (dto.getStateAction() != null) {
            if (dto.getStateAction().equals(CANCEL_REVIEW)) {
                event.setState(CANCELED);
            }
            if (dto.getStateAction().equals(SEND_TO_REVIEW)) {
                event.setState(PENDING);
            }
        }

        Event savedEvent = eventRepository.save(event);
        locationRepository.save(savedEvent.getLocation());
        return mapToEventFullDto(List.of(savedEvent)).get(0);
    }

///////////////////////////////////////////////// UTILITY METHODS //////////////////////////////////////////////////////

    private Event getEventOrThrowException(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(notFoundException("Event {0} not found", eventId)
        );
    }

    private User getUserOrThrowException(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(notFoundException("User {0} not found.", userId)
        );
    }

    private Category getCategoryOrThrowException(Long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(notFoundException("Category {0} not found.", catId)
        );
    }

    private void updateEventFields(Event event, UpdateEventDto updateEventRequest) {
        if (updateEventRequest.getAnnotation() != null && !updateEventRequest.getAnnotation().isBlank()) {
            event.setAnnotation(updateEventRequest.getAnnotation());
        }
        if (updateEventRequest.getDescription() != null && !updateEventRequest.getDescription().isBlank()) {
            event.setDescription(updateEventRequest.getDescription());
        }
        if (updateEventRequest.getCategory() != null) {
            Category category = getCategoryOrThrowException(updateEventRequest.getCategory());
            event.setCategory(category);
        }
        if (updateEventRequest.getPaid() != null) {
            event.setPaid(updateEventRequest.getPaid());
        }
        if (updateEventRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventRequest.getParticipantLimit());
        }
        if (updateEventRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventRequest.getRequestModeration());
        }
        if (updateEventRequest.getTitle() != null && !updateEventRequest.getTitle().isBlank()) {
            event.setTitle(updateEventRequest.getTitle());
        }
        if (updateEventRequest.getLocation() != null) {
            event.setLocation(locationMapper.toLocation(updateEventRequest.getLocation()));
        }
        if (updateEventRequest.getEventDate() != null) {
            validateEventDate(updateEventRequest.getEventDate());
            event.setEventDate(updateEventRequest.getEventDate());
        }
    }

    private void validateEventDate(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ValidationException("Event date should be in future.");
        }
    }

    private List<EventFullDto> mapToEventFullDto(Collection<Event> events) {
        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        List<EventFullDto> dtos = events.stream()
                .map(eventMapper::toEventFullDto)
                .collect(Collectors.toList());

        Map<Long, Integer> eventsViews = getViews(eventIds);
        Map<Long, Long> confirmedRequests = getConfirmedRequests(eventIds);

        dtos.forEach(event -> {
            event.setViews(eventsViews.getOrDefault(event.getId(), 0));
            event.setConfirmedRequests(confirmedRequests.getOrDefault(event.getId(), 0L));
        });

        return dtos;
    }

    private List<EventShortDto> mapToEventShortDto(Collection<Event> events) {
        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        List<EventShortDto> dtos = events.stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());

        Map<Long, Integer> eventsViews = getViews(eventIds);
        Map<Long, Long> confirmedRequests = getConfirmedRequests(eventIds);

        dtos.forEach(event -> {
            event.setViews(eventsViews.getOrDefault(event.getId(), 0));
            event.setConfirmedRequests(confirmedRequests.getOrDefault(event.getId(), 0L));
        });

        return dtos;
    }

    private Map<Long, Integer> getViews(List<Long> eventsId) {
        List<String> uris = eventsId
                .stream()
                .map(id -> "/events/" + id)
                .collect(Collectors.toList());

        Optional<LocalDateTime> start = eventRepository.getStart(eventsId);

        Map<Long, Integer> views = new HashMap<>();

        if (start.isPresent()) {
            List<GetStatsDto> statsResponse = statsClient.getStats(
                    start.get().format(DateTimeFormatter.ofPattern(TIMESTAMP_PATTERN)),
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern(TIMESTAMP_PATTERN)),
                    uris, true).getBody();

            statsResponse.forEach(dto -> {
                String uri = dto.getUri();
                String[] split = uri.split("/");
                String id = split[2];
                Long eventId = Long.parseLong(id);
                views.put(eventId, (int) dto.getHits());
            });
        } else {
            eventsId.forEach(el -> views.put(el, 0));
        }
        return views;
    }

    private Map<Long, Long> getConfirmedRequests(Collection<Long> eventsId) {
        List<Request> confirmedRequests = requestRepository
                .findAllByStatusAndEventIdIn(RequestStatus.CONFIRMED, eventsId);

        return confirmedRequests.stream()
                .collect(Collectors.groupingBy(request -> request.getEvent().getId()))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> (long) e.getValue().size()));
    }
}
