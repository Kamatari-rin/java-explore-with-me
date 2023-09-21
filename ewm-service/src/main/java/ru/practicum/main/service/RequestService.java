package ru.practicum.main.service;

import ru.practicum.main.dto.request.EventRequestStatusUpdateRequestDto;
import ru.practicum.main.dto.request.EventRequestStatusUpdateResultDto;
import ru.practicum.main.dto.request.ParticipationRequestDto;

import java.util.List;

public interface RequestService {

    List<ParticipationRequestDto> getRequestsToParticipate(Long userId);

    ParticipationRequestDto save(Long userId, Long eventId);

    ParticipationRequestDto cancelEvent(Long userId, Long requestId);

    List<ParticipationRequestDto> getParticipationRequestPrivate(Long userId, Long eventId);

    EventRequestStatusUpdateResultDto updateEventRequestStatusPrivate(Long userId,
                                                                      Long eventId,
                                                                      EventRequestStatusUpdateRequestDto dto);
}
