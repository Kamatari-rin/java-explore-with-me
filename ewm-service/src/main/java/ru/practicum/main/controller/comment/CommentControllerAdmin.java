package ru.practicum.main.controller.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.service.CommentService;

import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
@Validated
public class CommentControllerAdmin {

    private final CommentService commentService;

    @DeleteMapping()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestParam @Positive Long commentId) {
        commentService.deleteCommentByAdmin(commentId);
    }
}
