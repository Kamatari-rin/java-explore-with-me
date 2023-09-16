package ru.practicum.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.entity.User;
import ru.practicum.main.util.Pagination;

import java.util.List;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAllByIdIn(Set<Long> ids, Pagination pagination);
}
