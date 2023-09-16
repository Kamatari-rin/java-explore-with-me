package ru.practicum.main.controller.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.compilation.CompilationDto;
import ru.practicum.main.service.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.constant.Constants.PAGE_INDEX_FROM;
import static ru.practicum.constant.Constants.PAGE_INDEX_SIZE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/compilations")
@Validated
public class PublicCompilationController {

    private final CompilationService compilationService;

    @GetMapping
    public ResponseEntity<List<CompilationDto>> getAll(
                @RequestParam(defaultValue = PAGE_INDEX_FROM) @PositiveOrZero Integer from,
                @RequestParam(defaultValue = PAGE_INDEX_SIZE) @Positive Integer size,
                @RequestParam(required = false) Boolean pinned) {
        return new ResponseEntity<>(compilationService.getAll(pinned, from, size), HttpStatus.OK);
    }

    @GetMapping("/{compId}")
    public ResponseEntity<CompilationDto> getById(@Positive @PathVariable Long compId) {
        return new ResponseEntity<>(compilationService.getById(compId), HttpStatus.OK);
    }
}
