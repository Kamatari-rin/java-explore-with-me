package ru.practicum.main.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.dto.compilation.CompilationDto;
import ru.practicum.main.dto.compilation.NewCompilationDto;
import ru.practicum.main.dto.request.UpdateCompilationRequest;
import ru.practicum.main.entity.Compilation;
import ru.practicum.main.entity.Event;
import ru.practicum.main.mapper.CompilationMapper;
import ru.practicum.main.repository.CompilationRepository;
import ru.practicum.main.repository.EventRepository;
import ru.practicum.main.service.CompilationService;
import ru.practicum.main.util.Pagination;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.practicum.main.exception.NotFoundException.notFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;

    @Override
    public CompilationDto save(NewCompilationDto newCompilationDto) {
        Compilation compilation = compilationMapper.toCompilation(newCompilationDto);

        Set<Long> eventsId = newCompilationDto.getEvents();
        if (eventsId != null) {
            Set<Event> events = new HashSet<>(eventRepository.findAllByIdIn(eventsId));
            compilation.setEvents(events);
        }

        return compilationMapper.toCompilationDto(
                compilationRepository.save(compilation)
        );
    }

    @Override
    public CompilationDto update(Long compId, UpdateCompilationRequest updateDto) {
        Compilation updatedCompilation = getCompilation(compId);

        if (updateDto.getTitle() != null && !updateDto.getTitle().isBlank()) {
            updatedCompilation.setTitle(updateDto.getTitle());
        }
        if (updateDto.getPinned() != null) {
            updatedCompilation.setPinned(updateDto.getPinned());
        }

        if (updateDto.getEvents() != null && !updateDto.getEvents().isEmpty()) {
            Set<Long> eventsId = updateDto.getEvents();
            List<Event> events = eventRepository.findAllByIdIn(eventsId);
            updatedCompilation.setEvents(new HashSet<>(events));
        }

        return compilationMapper.toCompilationDto(updatedCompilation);
    }

    @Override
    public Boolean delete(Long compId) {
        Compilation compilation = getCompilation(compId);
        compilationRepository.delete(compilation);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getAll(Boolean pinned, Integer from, Integer size) {
        return pinned != null
                ? compilationRepository.findAllByPinned(pinned, new Pagination(from, size, Sort.unsorted()))
                        .stream()
                        .map(compilationMapper::toCompilationDto)
                        .collect(Collectors.toList())
                : compilationRepository.findAll(new Pagination(from, size, Sort.unsorted()))
                        .getContent()
                        .stream()
                        .map(compilationMapper::toCompilationDto)
                        .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getById(Long id) {
        return compilationMapper.toCompilationDto(getCompilation(id));
    }

    private Compilation getCompilation(Long compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(notFoundException("Compilation {0} not found", compId)
               );
    }
}
