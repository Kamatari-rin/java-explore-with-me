package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.GetStatsDto;
import ru.practicum.dto.HitRequestDto;
import ru.practicum.service.HitService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static ru.practicum.constant.Constants.TIMESTAMP_PATTERN;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class StatsServerController {

    private final HitService hitService;

    @GetMapping("/stats")
    public ResponseEntity<List<GetStatsDto>> getStats(
            @RequestParam @DateTimeFormat(pattern = TIMESTAMP_PATTERN) LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = TIMESTAMP_PATTERN) LocalDateTime end,
            @RequestParam(required = false) Set<String> uris,
            @RequestParam(defaultValue = "false") Boolean unique) {

        return new ResponseEntity<>(hitService.getStats(start, end, uris, unique), HttpStatus.OK);
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveHit(@RequestBody @Valid HitRequestDto dto) {
        hitService.save(dto);
    }
}
