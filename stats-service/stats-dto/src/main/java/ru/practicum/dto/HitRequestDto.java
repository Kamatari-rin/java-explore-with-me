package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;

import static ru.practicum.constant.Constants.TIMESTAMP_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class HitRequestDto {

    @NotBlank
    private String app;

    @NotBlank
    private String uri;

    @NotBlank
    private String ip;

    @JsonFormat(pattern = TIMESTAMP_PATTERN)
    @DateTimeFormat(pattern = TIMESTAMP_PATTERN)
    private LocalDateTime timestamp;
}
