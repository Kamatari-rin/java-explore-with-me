package ru.practicum.main.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main.dto.location.LocationDtoCoordinates;
import ru.practicum.main.entity.enums.StateAction;

import javax.validation.constraints.Future;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static ru.practicum.constant.Constants.TIMESTAMP_PATTERN;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventDto {

    @Size(max = 2000, min = 20)
    private String annotation;

    private Long category;

    @Size(max = 7000, min = 20)
    private String description;

    @Future
    @JsonFormat(pattern = TIMESTAMP_PATTERN)
    private LocalDateTime eventDate;

    private LocationDtoCoordinates location;

    private Boolean paid;

    @PositiveOrZero
    private Long participantLimit;

    private Boolean requestModeration;

    @Size(max = 120, min = 3)
    private String title;

    private StateAction stateAction;
}
