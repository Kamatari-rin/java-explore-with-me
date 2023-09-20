package ru.practicum.main.controller.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.compilation.CompilationDto;
import ru.practicum.main.dto.compilation.NewCompilationDto;
import ru.practicum.main.dto.request.UpdateCompilationRequest;
import ru.practicum.main.service.CompilationService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
@Validated
public class AdminCompilationController {

    private final CompilationService compilationService;

    @PostMapping
    public ResponseEntity<CompilationDto> save(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        return new ResponseEntity<>(compilationService.save(newCompilationDto), HttpStatus.CREATED);
    }

    @PatchMapping("/{compId}")
    public ResponseEntity<CompilationDto> update(
                    @PathVariable @Positive Long compId,
                    @RequestBody @Valid UpdateCompilationRequest updateCompilationRequest) {
        return new ResponseEntity<>(compilationService.update(compId, updateCompilationRequest), HttpStatus.OK);
    }

    @DeleteMapping("/{compId}")
    public ResponseEntity<Boolean> deleteById(@Positive @PathVariable Long compId) {
        return new ResponseEntity<>(compilationService.delete(compId), HttpStatus.NO_CONTENT);
    }
}
