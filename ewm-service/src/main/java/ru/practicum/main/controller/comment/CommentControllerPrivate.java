package ru.practicum.main.controller.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.comment.CommentDto;
import ru.practicum.main.dto.comment.NewCommentDto;
import ru.practicum.main.dto.comment.UpdateCommentDto;
import ru.practicum.main.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/comments/{userId}")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CommentControllerPrivate {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentDto> save(@PathVariable @Positive Long userId,
                                           @RequestParam @Positive Long eventId,
                                           @RequestBody @Valid NewCommentDto dto) {
        log.info("Create comment {} of user with id= {} and event with id = {}", dto, userId, eventId);
        return new ResponseEntity<>(commentService.save(userId, dto, eventId), HttpStatus.CREATED);
    }

    @DeleteMapping()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentAddedCurrentUser(@PathVariable @Positive Long userId,
                                              @RequestParam @Positive Long commentId) {
        log.info("Delete comment with id= {} of user with id= {}", commentId, userId);
        commentService.deleteCommentByUser(commentId, userId);
    }

    @PatchMapping
    public ResponseEntity<CommentDto> updateCommentByAuthor(@PathVariable @Positive Long userId,
                                                            @RequestParam @Positive Long commentId,
                                                            @RequestBody @Valid UpdateCommentDto dto) {
        log.info("Update comment with id={} and userId={}", commentId, userId);
        return new ResponseEntity<>(commentService.update(commentId, userId, dto), HttpStatus.OK);
    }
}
