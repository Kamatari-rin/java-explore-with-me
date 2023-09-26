package ru.practicum.main.controller.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.event.EventFullDto;
import ru.practicum.main.dto.event.EventShortDto;
import ru.practicum.main.dto.event.NewEventDto;
import ru.practicum.main.dto.request.EventRequestStatusUpdateRequestDto;
import ru.practicum.main.dto.request.EventRequestStatusUpdateResultDto;
import ru.practicum.main.dto.request.ParticipationRequestDto;
import ru.practicum.main.dto.request.UpdateEventDto;
import ru.practicum.main.service.EventService;
import ru.practicum.main.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.constant.Constants.PAGE_INDEX_FROM;
import static ru.practicum.constant.Constants.PAGE_INDEX_SIZE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
@Validated
@Slf4j
public class PrivateEventController {

    private final EventService eventService;
    private final RequestService requestService;

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getEvents(
            @PathVariable @Positive Long userId,
            @RequestParam(defaultValue = PAGE_INDEX_FROM) @PositiveOrZero Integer from,
            @RequestParam(defaultValue = PAGE_INDEX_SIZE) @Positive Integer size) {
        log.info("Get events of user with id= {}", userId);
        return new ResponseEntity<>(eventService.getAllByUserId(userId, from, size), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<EventFullDto> save(@PathVariable @Positive Long userId,
                                             @RequestBody @Valid NewEventDto dto) {
        log.info("Create event {} of user with id= {}", dto, userId);
        return new ResponseEntity<>(eventService.save(userId, dto), HttpStatus.CREATED);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventFullDto> getEvent(@PathVariable @Positive Long userId,
                                                 @PathVariable @Positive Long eventId) {
        log.info("Get event with id= {} of user with id= {}", eventId, userId);
        return new ResponseEntity<>(eventService.getOneByUserId(userId, eventId), HttpStatus.OK);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> update(@PathVariable @Positive Long userId,
                                               @PathVariable @Positive Long eventId,
                                               @RequestBody @Valid UpdateEventDto dto) {
        log.info("Updating event {} with id= {} of user with id= {}", dto, eventId, userId);
        return new ResponseEntity<>(eventService.updateByUser(userId, eventId, dto), HttpStatus.OK);
    }

    @PatchMapping("/{eventId}/requests")
    public ResponseEntity<EventRequestStatusUpdateResultDto> updateStatusOfRequest(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long eventId,
            @RequestBody @Valid EventRequestStatusUpdateRequestDto dto) {
        log.info("Update request for event with id= {} for participation for user with id{}", eventId, userId);
        return new ResponseEntity<>(requestService.updateEventRequestStatusPrivate(userId, eventId, dto), HttpStatus.OK);
    }

    @GetMapping("/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getRequests(@PathVariable @Positive Long userId,
                                                                     @PathVariable @Positive Long eventId) {
        log.info("Get request for event with id= {} for participation for user with id{}", eventId, userId);
        return new ResponseEntity<>(requestService.getParticipationRequestPrivate(userId, eventId), HttpStatus.OK);
    }
}
