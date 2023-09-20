package ru.practicum.main.service;

import ru.practicum.main.dto.compilation.CompilationDto;
import ru.practicum.main.dto.compilation.NewCompilationDto;
import ru.practicum.main.dto.request.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {

    CompilationDto save(NewCompilationDto newCompilationDto);

    CompilationDto update(Long compId, UpdateCompilationRequest updateCompilation);

    Boolean delete(Long compId);

    List<CompilationDto> getAll(Boolean pinned, Integer from, Integer size);

    CompilationDto getById(Long id);
}
