package ru.practicum.main.service;

import ru.practicum.main.dto.user.NewUserRequestDto;
import ru.practicum.main.dto.user.UserDto;

import java.util.List;
import java.util.Set;

public interface UserService {

    UserDto create(NewUserRequestDto newUserRequest);

    List<UserDto> getUsers(Set<Long> ids, Integer from, Integer size);

    void delete(Long userId);
}
