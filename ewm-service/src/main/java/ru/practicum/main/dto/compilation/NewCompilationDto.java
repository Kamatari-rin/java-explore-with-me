package ru.practicum.main.dto.compilation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.Set;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationDto {

    private Boolean pinned;

    @NotBlank
    private String title;

    private Set<Long> events;
}
