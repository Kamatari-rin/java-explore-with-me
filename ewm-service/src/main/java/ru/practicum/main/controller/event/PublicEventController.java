package ru.practicum.main.controller.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.event.EventFullDto;
import ru.practicum.main.dto.event.EventShortDto;
import ru.practicum.main.entity.enums.EventSort;
import ru.practicum.main.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static ru.practicum.constant.Constants.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
@Validated
@Slf4j
public class PublicEventController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getEventsPublic(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) Set<Long> categoriesIds,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = TIMESTAMP_PATTERN) LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = TIMESTAMP_PATTERN) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") boolean onlyAvailable,
            @RequestParam(required = false) EventSort sort,
            @RequestParam(defaultValue = PAGE_INDEX_FROM) @PositiveOrZero Integer from,
            @RequestParam(defaultValue = PAGE_INDEX_SIZE) @Positive Integer size, HttpServletRequest request) {
        log.info("Get public events with text {}, categories {} onlyAvailable {}, sort {}", text, categoriesIds,
                onlyAvailable, sort);

        return new ResponseEntity<>(eventService.getAllForPublic(text, categoriesIds, paid, rangeStart,
                rangeEnd, onlyAvailable, sort, from, size, request), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventFullDto> getEventByIdPublic(@PathVariable @Positive Long id, HttpServletRequest request) {
        log.info("Get event with id= {}", id);
        return new ResponseEntity<>(eventService.getEventByIdPublic(id, request), HttpStatus.OK);
    }
}
