package ru.practicum.main.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main.dto.event.EventFullDto;
import ru.practicum.main.entity.enums.EventStatus;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class RequestResponseDto {

    private Long id;

    private LocalDateTime created;

    private EventFullDto eventResponseDto;

    private Long requester;

    private EventStatus status;
}
