package ru.practicum.main.controller.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.main.dto.comment.CommentDto;
import ru.practicum.main.service.CommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.constant.Constants.PAGE_INDEX_FROM;
import static ru.practicum.constant.Constants.PAGE_INDEX_SIZE;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@Validated
public class CommentControllerPublic {

    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<List<CommentDto>> getCommentsByEventId(@RequestParam @Positive Long eventId,
                                                                 @RequestParam(defaultValue = PAGE_INDEX_FROM) @PositiveOrZero Integer from,
                                                                 @RequestParam(defaultValue = PAGE_INDEX_SIZE) @Positive Integer size) {
        return new ResponseEntity<>(commentService.getCommentsByEventId(eventId, from, size), HttpStatus.OK);
    }
}
