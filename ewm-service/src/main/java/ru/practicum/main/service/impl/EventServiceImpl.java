package ru.practicum.main.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.GetStatsDto;
import ru.practicum.dto.HitRequestDto;
import ru.practicum.main.dto.event.EventFullDto;
import ru.practicum.main.dto.event.EventShortDto;
import ru.practicum.main.dto.event.NewEventDto;
import ru.practicum.main.dto.request.UpdateEventDto;
import ru.practicum.main.entity.*;
import ru.practicum.main.entity.enums.EventSort;
import ru.practicum.main.entity.enums.EventStatus;
import ru.practicum.main.entity.enums.RequestStatus;
import ru.practicum.main.entity.enums.StateAction;
import ru.practicum.main.exception.NotAvailableException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mapper.EventMapper;
import ru.practicum.main.mapper.LocationMapper;
import ru.practicum.main.repository.*;
import ru.practicum.main.service.EventService;
import ru.practicum.main.util.Pagination;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.main.exception.NotFoundException.notFoundException;

;

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
    public List<EventFullDto> getEventsByAdmin(List<Long> userIds,
                                               List<Long> categoryIds,
                                               List<EventStatus> states,
                                               LocalDateTime rangeStart,
                                               LocalDateTime rangeEnd,
                                               Integer from,
                                               Integer size) {
        List<Event> events = eventRepository.findAllForAdmin(
                userIds, states, categoryIds, rangeStart, rangeEnd, new Pagination(from, size, Sort.unsorted()));
        return mapToEventFullDto(events);
    }

    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventDto dto) {
        Event event = getEventOrThrowException(eventId);
        updateEventFields(event, dto);

        if (dto.getStateAction() != null) {
            if (event.getState().equals(EventStatus.PENDING)) {
                if (dto.getStateAction().equals(StateAction.REJECT_EVENT)) {
                    event.setState(EventStatus.CANCELED);
                }
                if (dto.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
                    event.setState(EventStatus.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                }
            } else {
                throw new NotAvailableException("Cannot publish or cancel the event because it's not in the right state: "
                        + event.getState());
            }
        }

        if (dto.getEventDate() != null && event.getState().equals(EventStatus.PUBLISHED)) {
            if (dto.getEventDate().isAfter(event.getPublishedOn().plusHours(1))) {
                event.setEventDate(dto.getEventDate());
            } else {
                throw new ValidationException("The event date must be at least 1 hour after the published date.");
            }
        }

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

        List<Event> events = eventRepository.findAllPublishStateAvailable(EventStatus.PUBLISHED, rangeStart,
                categoriesIds, paid, text, new Pagination(from, size, Sort.unsorted()));

        sendStats(request.getRequestURI(), request.getRemoteAddr());

        return mapToEventShortDto(events);
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventByIdPublic(Long eventId, HttpServletRequest request) {
        Event event = getEventOrThrowException(eventId);
        if (!event.getState().equals(EventStatus.PUBLISHED)) {
            throw new NotFoundException(String.format("Event %s not published", eventId));
        }

        sendStats(request.getRequestURI(), request.getRemoteAddr());
        return mapToEventFullDto(List.of(event)).get(0);
    }

///////////////////////////////////////////////// PRIVATE SERVICE //////////////////////////////////////////////////////

    @Override
    public EventFullDto save(Long userId, NewEventDto newEventDto) {
        validateEventDate(newEventDto.getEventDate());
        User user = getUserOrThrowException(userId);
        Category category = getCategoryOrThrowException(newEventDto.getCategory());
        Location location = locationRepository.save(
                locationMapper.toLocation(newEventDto.getLocation()));

        Event event = eventMapper.toEvent(newEventDto, location, category, EventStatus.PENDING, user);
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
                List.of(getEventOrThrowException(eventId))).get(0);
    }

    @Override
    public EventFullDto updateByUser(Long userId, Long eventId, UpdateEventDto dto) {
        getUserOrThrowException(userId);
        Event event = getEventOrThrowException(eventId);

        if (event.getState().equals(EventStatus.PUBLISHED)) {
            throw new NotAvailableException("Only canceled events or events pending moderation can be changed");
        }

        updateEventFields(event, dto);

        if (dto.getStateAction() != null) {
            if (dto.getStateAction().equals(StateAction.CANCEL_REVIEW)) {
                event.setState(EventStatus.CANCELED);
            }
            if (dto.getStateAction().equals(StateAction.SEND_TO_REVIEW)) {
                event.setState(EventStatus.PENDING);
            }
        }

        return mapToEventFullDto(List.of(event)).get(0);
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

    private void updateEventFields(Event event, UpdateEventDto dto) {
        if (dto.getAnnotation() != null && !dto.getAnnotation().isBlank()) {
            event.setAnnotation(dto.getAnnotation());
        }
        if (dto.getCategory() != null) {
            Category category = categoryRepository.findById(dto.getCategory()).orElseThrow(() ->
                    new NotFoundException(String.format("Category %s not found", dto.getCategory())));
            event.setCategory(category);
        }
        if (dto.getDescription() != null && !dto.getDescription().isBlank()) {
            event.setDescription(dto.getDescription());
        }
        if (dto.getEventDate() != null) {
            event.setEventDate(dto.getEventDate());
        }
        if (dto.getLocation() != null) {
            event.setLocation(locationMapper.toLocation(dto.getLocation()));
        }
        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }
        if (dto.getParticipantLimit() != null) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }
        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }
        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            event.setTitle(dto.getTitle());
        }
    }

    private void validateEventDate(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
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
        Map<Long, Integer> confirmedRequests = getConfirmedRequests(eventIds);

        dtos.forEach(event -> {
            event.setViews(eventsViews.getOrDefault(event.getId(), 0));
            event.setConfirmedRequests(confirmedRequests.getOrDefault(event.getId(), 0));
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
        Map<Long, Integer> confirmedRequests = getConfirmedRequests(eventIds);

        dtos.forEach(event -> {
            event.setViews(eventsViews.getOrDefault(event.getId(), 0));
            event.setConfirmedRequests(confirmedRequests.getOrDefault(event.getId(), 0));
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
            List<GetStatsDto> response = statsClient
                    .getStats(start.get(), LocalDateTime.now(), uris, true);

            response.forEach(dto -> {
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

    private Map<Long, Integer> getConfirmedRequests(Collection<Long> eventsId) {
        List<Request> confirmedRequests = requestRepository
                .findAllByStatusAndEventIdIn(RequestStatus.CONFIRMED, eventsId);

        return confirmedRequests.stream()
                .collect(Collectors.groupingBy(request -> request.getEvent().getId()))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().size()));
    }

    private void sendStats(String uri, String ip) {
        HitRequestDto endpointHitRequestDto = HitRequestDto.builder()
                .app(app)
                .uri(uri)
                .ip(ip)
                .timestamp(LocalDateTime.now())
                .build();

        statsClient.createHit(endpointHitRequestDto);
    }
}
