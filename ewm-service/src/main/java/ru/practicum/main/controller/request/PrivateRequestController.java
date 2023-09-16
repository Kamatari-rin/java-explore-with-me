package ru.practicum.main.controller.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.request.ParticipationRequestDto;
import ru.practicum.main.service.RequestService;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class PrivateRequestController {

    private final RequestService requestService;

    @GetMapping
    public ResponseEntity<List<ParticipationRequestDto>> getRequestsToParticipateInOtherEvents(@PathVariable @Positive Long userId) {
        return new ResponseEntity<>(requestService.getRequestsToParticipate(userId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ParticipationRequestDto> save(@PathVariable @Positive Long userId,
                                                        @RequestParam @Positive Long eventId) {
        return new ResponseEntity<>(requestService.save(userId, eventId), HttpStatus.CREATED);
    }

    @PatchMapping("/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancelOwnEvent(@PathVariable @Positive Long userId,
                                                                  @PathVariable @Positive Long requestId) {
        return new ResponseEntity<>(requestService.cancelEvent(userId, requestId), HttpStatus.OK);
    }
}
