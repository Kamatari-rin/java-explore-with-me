package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.GetStatsDto;
import ru.practicum.dto.HitRequestDto;
import ru.practicum.dto.HitResponseDto;
import ru.practicum.entity.HitEntity;
import ru.practicum.mapper.HitMapper;
import ru.practicum.repository.HitRepository;

import javax.validation.ValidationException;
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
        if (start.isAfter(end)) {
            throw new ValidationException("End cannot be earlier than start");
        }
        return hitRepository.getStats(uriSet, start, end, unique);
    }

    @Override
    public HitResponseDto save(HitRequestDto dto) {
        HitEntity hitResponse = hitRepository
                .save(hitMapper.toHitEntity(dto));
        return hitMapper.toHitResponseDto(hitResponse);
    }
}