package ru.practicum.main.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main.dto.user.UserShortDto;

import java.time.LocalDateTime;

import static ru.practicum.constant.Constants.TIMESTAMP_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CommentDto {

    private Long id;

    private String content;

    private UserShortDto author;

    @JsonFormat(pattern = TIMESTAMP_PATTERN)
    private LocalDateTime created;

    @JsonFormat(pattern = TIMESTAMP_PATTERN)
    private LocalDateTime updated;
}
