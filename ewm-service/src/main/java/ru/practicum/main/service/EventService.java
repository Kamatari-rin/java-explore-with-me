package ru.practicum.main.service;

import ru.practicum.main.dto.event.EventFullDto;
import ru.practicum.main.dto.event.EventShortDto;
import ru.practicum.main.dto.event.NewEventDto;
import ru.practicum.main.dto.request.*;
import ru.practicum.main.entity.enums.EventSort;
import ru.practicum.main.entity.enums.EventStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface EventService {

    EventFullDto save(Long userId, NewEventDto dto);

    List<EventShortDto> getAllByUserId(Long userId, Integer from, Integer size);

    EventFullDto getOneByUserId(Long userId, Long eventId);

    EventFullDto updateByUser(Long userId, Long eventId, UpdateEventDto dto);

    List<EventFullDto> getEventsByAdmin(List<Long> userIds,
                                        List<Long> categoryIds,
                                        List<EventStatus> states,
                                        LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd,
                                        Integer from,
                                        Integer size);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventDto dto);

    List<EventShortDto> getAllForPublic(String text,
                                        Set<Long> categoriesIds,
                                        Boolean paid,
                                        LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd,
                                        Boolean onlyAvailable,
                                        EventSort sort,
                                        Integer from,
                                        Integer size,
                                        HttpServletRequest httpServletRequest);

    EventFullDto getEventByIdPublic(Long eventId, HttpServletRequest httpServletRequest);
}
