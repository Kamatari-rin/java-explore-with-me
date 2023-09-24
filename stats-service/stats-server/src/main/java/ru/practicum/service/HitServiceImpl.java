package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.GetStatsDto;
import ru.practicum.dto.HitRequestDto;
import ru.practicum.mapper.HitMapper;
import ru.practicum.repository.HitRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class HitServiceImpl implements HitService {

    private final HitRepository hitRepository;
    private final HitMapper hitMapper;

    @Override
    @Transactional(readOnly = true)
    public List<GetStatsDto> getStats(LocalDateTime start, LocalDateTime end, Set<String> uriSet, Boolean unique) {
        return hitRepository.getStats(uriSet, start, end, unique);
    }

    @Override
    public void save(HitRequestDto dto) {
        hitRepository.save(hitMapper.toHitEntity(dto));
    }
}