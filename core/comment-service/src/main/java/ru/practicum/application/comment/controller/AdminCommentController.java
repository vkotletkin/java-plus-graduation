package ru.practicum.application.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.api.comment.AdminCommentApi;
import ru.practicum.application.comment.service.CommentService;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.comment.GetCommentsAdminRequest;


import java.util.Collection;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminCommentController implements AdminCommentApi {

    private final CommentService service;

    @Override
    public Collection<CommentDto> getComments(Long eventId, Integer from, Integer size) throws NotFoundException {
        return service.getAllEventComments(new GetCommentsAdminRequest(eventId, from, size));
    }

    @Override
    public void removeComment(@PathVariable("commentId") Long commentId) throws NotFoundException {
        log.info("удаление комментария с id {}", commentId);
        service.delete(commentId);
    }

}