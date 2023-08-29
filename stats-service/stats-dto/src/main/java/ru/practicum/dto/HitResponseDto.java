package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static ru.practicum.constant.Constants.TIMESTAMP_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class HitResponseDto {

    private Long id;
    private String app;
    private String uri;
    private String ip;

    @JsonFormat(pattern = TIMESTAMP_PATTERN)
    private LocalDateTime timestamp;
}
