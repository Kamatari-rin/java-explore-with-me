package ru.practicum.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.entity.Compilation;
import ru.practicum.main.util.Pagination;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    List<Compilation> findAllByPinned(Boolean pinned, Pagination pagination);
}
