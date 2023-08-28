package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.HitRequestDto;
import ru.practicum.dto.GetStatsDto;
import ru.practicum.dto.HitResponseDto;
import ru.practicum.entity.HitEntity;
import ru.practicum.mapper.HitMapper;
import ru.practicum.service.HitService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static ru.practicum.constant.Constants.TIMESTAMP_PATTERN;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class StatsServerController {

    private final HitService hitService;
    private final HitMapper hitMapper;

    @GetMapping("/stats")
    public ResponseEntity<List<GetStatsDto>> getStats(
            @RequestParam @DateTimeFormat(pattern = TIMESTAMP_PATTERN) LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = TIMESTAMP_PATTERN) LocalDateTime end,
            @RequestParam(required = false) Set<String> uriSet,
            @RequestParam(defaultValue = "false") boolean unique) {

        return new ResponseEntity<>(hitService.getStats(start, end, uriSet, unique), HttpStatus.OK);
    }

    @PostMapping("/hit")
    public ResponseEntity<HitResponseDto> saveHit(@RequestBody @Valid HitRequestDto hitRequestDto) {
        HitEntity hit = hitService.save(hitMapper.toHitEntity(hitRequestDto));
        return new ResponseEntity<>(hitMapper.toHitResponseDto(hit), HttpStatus.OK);
    }
}
