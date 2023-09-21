package ru.practicum.main.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.dto.request.EventRequestStatusUpdateRequestDto;
import ru.practicum.main.dto.request.EventRequestStatusUpdateResultDto;
import ru.practicum.main.dto.request.ParticipationRequestDto;
import ru.practicum.main.entity.Event;
import ru.practicum.main.entity.Request;
import ru.practicum.main.entity.User;
import ru.practicum.main.entity.enums.EventStatus;
import ru.practicum.main.entity.enums.RequestStatus;
import ru.practicum.main.mapper.RequestMapper;
import ru.practicum.main.repository.EventRepository;
import ru.practicum.main.repository.RequestRepository;
import ru.practicum.main.repository.UserRepository;
import ru.practicum.main.service.RequestService;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.main.exception.NotFoundException.notFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    private final RequestMapper requestMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getRequestsToParticipate(Long userId) {
        getUserOrThrowException(userId);
        return requestRepository.findAllByRequesterId(userId)
                .stream()
                .map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto save(Long userId, Long eventId) {
        User requester = getUserOrThrowException(userId);
        Event event = getEventOrThrowException(eventId);

        if (event.getInitiator().getId().equals(userId)) {
            throw new ValidationException("Event initiator cannot add a request to participate in their event");
        }
        if (!event.getState().equals(EventStatus.PUBLISHED)) {
            throw new ValidationException("It isn't possible participate if event isn't published.");
        }

        Long confirmedRequests = requestRepository.countAllByEventIdAndStatus(eventId,
                RequestStatus.CONFIRMED);

        if (event.getParticipantLimit() <= confirmedRequests && event.getParticipantLimit() != 0) {
            throw new ValidationException("Limit of requests for participation has been exceeded");
        }

        Request request = Request.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(requester)
                .status(!event.getRequestModeration() || event.getParticipantLimit() == 0
                        ? RequestStatus.CONFIRMED
                        : RequestStatus.PENDING)
                .build();

        return requestMapper.toParticipationRequestDto(
                requestRepository.save(request));
    }

    @Override
    public ParticipationRequestDto cancelEvent(Long userId, Long requestId) {
        getUserOrThrowException(userId);
        Request request = requestRepository.findById(requestId)
                .orElseThrow(notFoundException("Request {requestId} not found", requestId));

        if (!request.getRequester().getId().equals(userId)) {
            throw new ValidationException(
                    String.format("User %s didn't apply for participation %s", userId, requestId));
        }
        request.setStatus(RequestStatus.CANCELED);

        return requestMapper.toParticipationRequestDto(
                requestRepository.save(request));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getParticipationRequestPrivate(Long userId, Long eventId) {
        getUserOrThrowException(userId);
        getEventOrThrowException(eventId);
        if (eventRepository.findByIdAndInitiatorId(eventId, userId).isPresent()) {
            return requestRepository.findAllByEventId(eventId).stream()
                    .map(requestMapper::toParticipationRequestDto)
                    .collect(Collectors.toList());
        } else return Collections.emptyList();
    }

    @Override
    public EventRequestStatusUpdateResultDto updateEventRequestStatusPrivate(Long userId,
                                                                             Long eventId,
                                                                             EventRequestStatusUpdateRequestDto dto) {
        getUserOrThrowException(userId);
        Event event = getEventOrThrowException(eventId);
        Long confirmedRequests = requestRepository.countAllByEventIdAndStatus(eventId,
                RequestStatus.CONFIRMED);

        Long freePlaces = event.getParticipantLimit() - confirmedRequests;

        RequestStatus status = RequestStatus.valueOf(String.valueOf(dto.getStatus()));

        if (status.equals(RequestStatus.CONFIRMED) && freePlaces <= 0) {
            throw new ValidationException("The limit of requests to participate in the event has been reached");
        }

        List<Request> requests = requestRepository.findAllByEventIdAndEventInitiatorIdAndIdIn(eventId,
                userId, dto.getRequestIds());

        setStatus(requests, status, freePlaces);

        List<ParticipationRequestDto> requestsDto = requests
                .stream()
                .map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());

        List<ParticipationRequestDto> confirmedRequestsDto = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequestsDto = new ArrayList<>();

        requestsDto.forEach(el -> {
            if (status.equals(RequestStatus.CONFIRMED)) {
                confirmedRequestsDto.add(el);
            } else {
                rejectedRequestsDto.add(el);
            }
        });

        return EventRequestStatusUpdateResultDto.builder()
                .confirmedRequests(confirmedRequestsDto)
                .rejectedRequests(rejectedRequestsDto)
                .build();
    }

    private User getUserOrThrowException(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(notFoundException("User {userId} not found.", userId)
                );
    }

    private Event getEventOrThrowException(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(notFoundException("Event {eventId} not found", eventId)
                );
    }

    private void setStatus(Collection<Request> requests, RequestStatus status, long freePlaces) {
        if (status.equals(RequestStatus.CONFIRMED)) {
            for (Request request : requests) {
                if (!request.getStatus().equals(RequestStatus.PENDING)) {
                    throw new ValidationException("Request's status has to be PENDING");
                }
                if (freePlaces > 0) {
                    request.setStatus(RequestStatus.CONFIRMED);
                    freePlaces--;
                } else {
                    request.setStatus(RequestStatus.REJECTED);
                }
            }
        } else if (status.equals(RequestStatus.REJECTED)) {
            requests.forEach(request -> {
                if (!request.getStatus().equals(RequestStatus.PENDING)) {
                    throw new ValidationException("Request's status has to be PENDING");
                }
                request.setStatus(RequestStatus.REJECTED);
            });
        } else {
            throw new ValidationException("You must either approve - CONFIRMED" +
                    " or reject - REJECTED the application");
        }
    }
}
