package ru.practicum.main.controller.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.compilation.CompilationDto;
import ru.practicum.main.dto.compilation.NewCompilationDto;
import ru.practicum.main.dto.request.UpdateCompilationDto;
import ru.practicum.main.service.CompilationService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
@Validated
@Slf4j
public class AdminCompilationController {

    private final CompilationService compilationService;

    @PostMapping
    public ResponseEntity<CompilationDto> save(@RequestBody @Valid NewCompilationDto dto) {
        log.info("Create compilation {}", dto);
        return new ResponseEntity<>(compilationService.save(dto), HttpStatus.CREATED);
    }

    @PatchMapping("/{compId}")
    public ResponseEntity<CompilationDto> update(
                    @PathVariable(value = "compId") @Positive Long compId,
                    @RequestBody @Valid UpdateCompilationDto dto) {
        log.info("Update compilation {} with id = {}", dto, compId);
        return new ResponseEntity<>(compilationService.update(compId, dto), HttpStatus.OK);
    }

    @DeleteMapping("/{compId}")
    public ResponseEntity<Boolean> deleteById(@PathVariable(value = "compId") @Positive Long compId) {
        log.info("Delete compilation  with id = {}", compId);
        return new ResponseEntity<>(compilationService.delete(compId), HttpStatus.NO_CONTENT);
    }
}
