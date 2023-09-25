package ru.practicum.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
public class GetStatsDto {

    private String app;

    private String uri;

    private long hits;
}