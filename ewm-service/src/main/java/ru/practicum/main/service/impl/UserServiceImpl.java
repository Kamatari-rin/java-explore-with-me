package ru.practicum.main.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.dto.user.NewUserRequestDto;
import ru.practicum.main.dto.user.UserDto;
import ru.practicum.main.mapper.UserMapper;
import ru.practicum.main.repository.UserRepository;
import ru.practicum.main.service.UserService;
import ru.practicum.main.util.Pagination;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.practicum.main.exception.NotFoundException.notFoundException;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto create(NewUserRequestDto newUserRequest) {
        return userMapper.toUserDto(
                userRepository.save(userMapper.toUser(newUserRequest))
        );
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getUsers(Set<Long> ids, Integer from, Integer size) {
        return ids == null || ids.isEmpty()
                ? userRepository.findAll(new Pagination(from, size, Sort.unsorted()))
                    .stream()
                    .map(userMapper::toUserDto)
                    .collect(Collectors.toList())
                : userRepository.findAllByIdIn(ids, new Pagination(from, size, Sort.unsorted()))
                    .stream()
                    .map(userMapper::toUserDto)
                    .collect(Collectors.toList());
    }

    @Override
    public void delete(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(notFoundException("User with id={userId} hasn't found", userId));
        userRepository.deleteById(userId);
    }
}
