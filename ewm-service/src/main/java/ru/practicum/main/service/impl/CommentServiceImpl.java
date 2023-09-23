package ru.practicum.main.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.dto.comment.CommentDto;
import ru.practicum.main.dto.comment.NewCommentDto;
import ru.practicum.main.dto.comment.UpdateCommentDto;
import ru.practicum.main.entity.Comment;
import ru.practicum.main.entity.Event;
import ru.practicum.main.entity.User;
import ru.practicum.main.entity.enums.EventStatus;
import ru.practicum.main.mapper.CommentMapper;
import ru.practicum.main.repository.CommentRepository;
import ru.practicum.main.repository.EventRepository;
import ru.practicum.main.repository.UserRepository;
import ru.practicum.main.service.CommentService;
import ru.practicum.main.util.Pagination;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.main.exception.NotFoundException.notFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    @Override
    public CommentDto save(Long userId, NewCommentDto newCommentDto, Long eventId) {
        User user = getUserOrThrowException(userId);
        Event event = getEventOrThrowException(eventId);

        if (!event.getState().equals(EventStatus.PUBLISHED)) {
            throw new ValidationException(String.format("Event %s isn't published", eventId));
        }

        Comment comment = Comment.builder()
                .content(newCommentDto.getContent())
                .created(LocalDateTime.now())
                .event(event)
                .author(user)
                .build();

        return commentMapper.toCommentDto(
                commentRepository.save(comment));
    }

    @Override
    public CommentDto update(Long commentId, Long authorId, UpdateCommentDto dto) {
        User user = getUserOrThrowException(authorId);
        Comment comment = getCommentOrThrowException(commentId);

        if (!comment.getAuthor().getId().equals(user.getId())) {
            throw new ValidationException(String.format("User %s isn't author of comment %s", authorId, commentId));
        }

        comment.setContent(dto.getContent());
        comment.setUpdated(LocalDateTime.now());
        return commentMapper.toCommentDto(
                commentRepository.save(comment)
        );
    }

    @Override
    public List<CommentDto> getCommentsByEventId(Long eventId, Integer from, Integer size) {
        return commentRepository.findAllByEventId(eventId, new Pagination(from, size, Sort.unsorted()))
                .stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCommentByUser(Long commentId, Long authorId) {
        User user = getUserOrThrowException(authorId);
        Comment comment = getCommentOrThrowException(commentId);

        if (!comment.getAuthor().getId().equals(user.getId())) {
            throw new ValidationException(String.format("User %s isn't author of comment %s", authorId, commentId));
        }

        commentRepository.deleteById(commentId);
    }

    @Override
    public void deleteCommentByAdmin(Long commentId) {
        getCommentOrThrowException(commentId);
        commentRepository.deleteById(commentId);
    }

    private Comment getCommentOrThrowException(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(notFoundException("Comment {} not found", commentId)
                );
    }

    private Event getEventOrThrowException(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(notFoundException("Event {} not found", eventId)
                );
    }

    private User getUserOrThrowException(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(notFoundException("User {} not found.", userId)
                );
    }
}
