package ru.practicum.main.service;

import ru.practicum.main.dto.comment.CommentDto;
import ru.practicum.main.dto.comment.NewCommentDto;
import ru.practicum.main.dto.comment.UpdateCommentDto;

import java.util.List;

public interface CommentService {

    CommentDto save(Long userId, NewCommentDto newCommentDto, Long eventId);

    CommentDto update(Long commentId, Long authorId, UpdateCommentDto dto);

    List<CommentDto> getCommentsByEventId(Long eventId, Integer from, Integer size);

    void deleteCommentByUser(Long commentId, Long authorId);

    void deleteCommentByAdmin(Long commentId);
}