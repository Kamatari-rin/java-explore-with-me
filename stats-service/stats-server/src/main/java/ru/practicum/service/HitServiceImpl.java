package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.GetStatsDto;
import ru.practicum.entity.HitEntity;
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

    @Override
    public List<GetStatsDto> getStats(LocalDateTime start, LocalDateTime end, Set<String> uriSet, boolean unique) {
        if (unique) {
            return hitRepository.findStatsBetweenTimestampUniqUri(uriSet, start, end);
        } else {
            return hitRepository.findStatsBetweenTimestampNotUniqUri(uriSet, start, end);
        }
    }

    @Override
    public HitEntity save(HitEntity hitEntity) {
        return hitRepository.save(hitEntity);
    }


}