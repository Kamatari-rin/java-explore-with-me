package ru.practicum.main.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.user.NewUserRequestDto;
import ru.practicum.main.dto.user.UserDto;
import ru.practicum.main.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Set;

import static ru.practicum.constant.Constants.PAGE_INDEX_FROM;
import static ru.practicum.constant.Constants.PAGE_INDEX_SIZE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
@Validated
@Slf4j
public class AdminUserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> save(@RequestBody @Valid NewUserRequestDto dto) {
        log.info("Create user with id= {}", dto);
        return new ResponseEntity<>(userService.create(dto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive Long userId) {
        log.info("Delete user with id= {}", userId);
        userService.delete(userId);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers(@RequestParam(required = false) Set<Long> ids,
                                                  @RequestParam(defaultValue = PAGE_INDEX_FROM) Integer from,
                                                  @RequestParam(defaultValue = PAGE_INDEX_SIZE) Integer size) {
        log.info("Get all users with ids: {}", ids);
        return new ResponseEntity<>(userService.getUsers(ids, from, size), HttpStatus.OK);
    }
}
