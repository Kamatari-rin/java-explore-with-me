package ru.practicum.main.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main.dto.location.LocationDtoCoordinates;

import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventDto {

    private String annotation;

    private Long category;

    private String description;

    private LocalDateTime eventDate;

    private LocationDtoCoordinates location;

    private Boolean paid;

    @PositiveOrZero
    private Long participantLimit;

    private Boolean requestModeration;

    private String title;

    private StateAction stateAction;

    public enum StateAction {
        PUBLISH_EVENT,
        REJECT_EVENT,
        SEND_TO_REVIEW,
        CANCEL_REVIEW
    }
}
