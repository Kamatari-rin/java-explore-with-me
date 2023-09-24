package ru.practicum.main.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main.dto.category.CategoryDto;
import ru.practicum.main.dto.location.LocationDtoCoordinates;
import ru.practicum.main.dto.user.UserShortDto;
import ru.practicum.main.entity.enums.EventStatus;

import java.time.LocalDateTime;

import static ru.practicum.constant.Constants.TIMESTAMP_PATTERN;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class EventFullDto {

    private Long id;

    private String annotation;

    private CategoryDto category;

    @JsonFormat(pattern = TIMESTAMP_PATTERN)
    private LocalDateTime createdOn;

    private String description;

    @JsonFormat(pattern = TIMESTAMP_PATTERN)
    private LocalDateTime eventDate;

    private UserShortDto initiator;

    private LocationDtoCoordinates location;

    private Boolean paid;

    private Long participantLimit;

    @JsonFormat(pattern = TIMESTAMP_PATTERN)
    private LocalDateTime publishedOn;

    private Boolean requestModeration;

    private Long confirmedRequests;

    private EventStatus state;

    private String title;

    private Integer views;
}
