package ru.practicum.main.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "Идентификатор",
            type = "integer",
            example = "1")
    private Long id;

    @Schema(description = "Краткое описание",
            type = "string",
            example = "Эксклюзивность нашего шоу гарантирует привлечение максимальной зрительской аудитории")
    private String annotation;

    @Schema(implementation = CategoryDto.class)
    private CategoryDto category;

    @Schema(description = "Дата и время создания события (в формате \"yyyy-MM-dd HH:mm:ss\")",
            type = "string",
            example = "2022-09-06 11:00:23")
    @JsonFormat(pattern = TIMESTAMP_PATTERN)
    private LocalDateTime createdOn;

    @Schema(description = "Полное описание события",
            type = "string",
            example = "Что получится, если соединить кукурузу и полёт? Создатели " +
                    "\"Шоу летающей кукурузы\" испытали эту идею на практике и воплотили в жизнь " +
                    "инновационный проект, предлагающий свежий взгляд на развлечения...")
    private String description;

    @Schema(description = "Дата и время на которые намечено событие (в формате \"yyyy-MM-dd HH:mm:ss\")",
            type = "string",
            example = "2024-12-31 15:10:05")
    @JsonFormat(pattern = TIMESTAMP_PATTERN)
    private LocalDateTime eventDate;

    @Schema(implementation = UserShortDto.class)
    private UserShortDto initiator;

    @Schema(implementation = LocationDtoCoordinates.class)
    private LocationDtoCoordinates location;

    @Schema(description = "Нужно ли оплачивать участие",
            type = "boolean",
            example = "true")
    private Boolean paid;

    @Schema(description = "Ограничение на количество участников. Значение 0 - означает отсутствие ограничения",
            type = "integer",
            example = "10",
            defaultValue = "0")
    private Long participantLimit;

    @Schema(description = "Дата и время публикации события (в формате \"yyyy-MM-dd HH:mm:ss\")",
            type = "string",
            example = "2022-09-06 15:10:05")
    @JsonFormat(pattern = TIMESTAMP_PATTERN)
    private LocalDateTime publishedOn;

    @Schema(description = "Нужна ли пре-модерация заявок на участие",
            type = "boolean",
            example = "true",
            defaultValue = "true")
    private Boolean requestModeration;

    @Schema(description = "Количество одобренных заявок на участие в данном событии",
            type = "integer",
            example = "5")
    private Integer confirmedRequests;

    @Schema(description = "Список состояний жизненного цикла события",
            type = "string",
            allowableValues = {"PENDING", "PUBLISHED", "CANCELED"})
    private EventStatus state;

    @Schema(description = "Заголовок",
            type = "string",
            example = "Знаменитое шоу 'Летающая кукуруза'")
    private String title;

    @Schema(description = "Количество просмотрев события",
            type = "integer",
            example = "598")
    private Integer views;
}
