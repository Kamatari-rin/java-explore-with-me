package ru.practicum.main.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.event.EventFullDto;
import ru.practicum.main.dto.request.UpdateEventDto;
import ru.practicum.main.entity.enums.EventStatus;
import ru.practicum.main.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static ru.practicum.constant.Constants.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
@Validated
public class AdminEventController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventFullDto>> getEvents(
            @RequestParam(required = false) Set<Long> users,
            @RequestParam(required = false) Set<Long> categories,
            @RequestParam(required = false) List<EventStatus> states,
            @RequestParam(required = false) @DateTimeFormat(pattern = TIMESTAMP_PATTERN) LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = TIMESTAMP_PATTERN) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = PAGE_INDEX_FROM) @PositiveOrZero Integer from,
            @RequestParam(defaultValue = PAGE_INDEX_SIZE) @Positive Integer size) {
        return new ResponseEntity<>(eventService.getEventsByAdmin(
                users, categories, states, rangeStart, rangeEnd, from, size), HttpStatus.OK);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> update(@PathVariable @Positive Long eventId,
                                               @RequestBody @Valid UpdateEventDto dto) {
        return new ResponseEntity<>(eventService.updateEventByAdmin(eventId, dto), HttpStatus.OK);
    }
}
