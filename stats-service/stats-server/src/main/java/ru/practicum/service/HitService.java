package ru.practicum.service;

import ru.practicum.dto.GetStatsDto;
import ru.practicum.dto.HitResponseDto;
import ru.practicum.entity.HitEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface HitService {

    HitEntity save(HitEntity hitEntity);

    List<GetStatsDto> getStats(LocalDateTime start, LocalDateTime end, Set<String> uriSet, boolean unique);
}
