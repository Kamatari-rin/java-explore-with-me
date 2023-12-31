package ru.practicum.main.dto.compilation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main.dto.event.EventShortDto;

import java.util.Set;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class CompilationDto {

    private Long id;
    private Boolean pinned;
    private String title;
    private Set<EventShortDto> events;
}
