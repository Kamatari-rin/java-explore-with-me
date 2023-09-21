package ru.practicum.main.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCompilationRequest {

    private Set<Long> events;

    private Boolean pinned;

    private String title;
}
