package ru.practicum.main.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main.dto.location.LocationDtoCoordinates;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

import static ru.practicum.constant.Constants.TIMESTAMP_PATTERN;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDto {

    @NotBlank
    private String annotation;

    @NotNull
    private Long category;

    @NotBlank
    private String description;

    @NotNull
    @JsonFormat(pattern = TIMESTAMP_PATTERN)
    private LocalDateTime eventDate;

    @NotNull
    private LocationDtoCoordinates location;

    private Boolean paid;

    @PositiveOrZero
    private Integer participantLimit;

    private Boolean requestModeration = true;

    @NotBlank
    private String title;
}
