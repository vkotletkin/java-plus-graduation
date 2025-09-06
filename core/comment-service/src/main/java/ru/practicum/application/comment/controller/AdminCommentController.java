package ru.practicum.application.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.api.comment.AdminCommentApi;
import ru.practicum.application.comment.service.CommentService;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.comment.GetCommentsAdminRequest;

import java.util.Collection;

@Validated
@RestController
@RequiredArgsConstructor
public class AdminCommentController implements AdminCommentApi {

    private final CommentService service;

    @Override
    public Collection<CommentDto> getComments(Long eventId, Integer from, Integer size) throws NotFoundException {

        GetCommentsAdminRequest getCommentsAdminRequest = GetCommentsAdminRequest.builder()
                .eventId(eventId)
                .from(from)
                .size(size)
                .build();

        return service.getAllEventComments(getCommentsAdminRequest);
    }

    @Override
    public void removeComment(Long commentId) throws NotFoundException {
        service.delete(commentId);
    }
}