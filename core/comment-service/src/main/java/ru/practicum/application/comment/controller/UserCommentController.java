package ru.practicum.application.comment.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.api.comment.UserCommentApi;
import ru.practicum.application.comment.service.CommentService;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;

import java.util.Collection;

@Validated
@RestController
@RequiredArgsConstructor
public class UserCommentController implements UserCommentApi {

    private final CommentService service;

    @Override
    public CommentDto addComments(Long userId, Long eventId, CommentDto commentDto) throws ConflictException, NotFoundException {
        return service.addComment(commentDto, userId, eventId);
    }

    @Override
    public void deleteComment(@NonNull Long commentId, @NonNull Long userId) throws ConflictException, NotFoundException {
        service.delete(userId, commentId);
    }

    @Override
    public CommentDto updateComment(Long userId, Long commentId, CommentDto commentDto) throws ConflictException, NotFoundException {
        return service.updateUserComment(userId, commentId, commentDto);
    }

    @Override
    public Collection<CommentDto> getByUserComment(Long userId) throws NotFoundException {
        return service.getAllUserComments(userId);
    }
}