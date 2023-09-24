package ru.practicum.service;

import ru.practicum.dto.GetStatsDto;
import ru.practicum.dto.HitRequestDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface HitService {

    void save(HitRequestDto dto);

    List<GetStatsDto> getStats(LocalDateTime start, LocalDateTime end, Set<String> uriSet, Boolean unique);
}
