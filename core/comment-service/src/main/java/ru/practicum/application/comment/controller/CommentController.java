package ru.practicum.application.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.api.comment.PublicCommentApi;
import ru.practicum.application.comment.service.CommentService;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.exception.NotFoundException;

import java.util.Collection;

@RequiredArgsConstructor
@RestController
public class CommentController implements PublicCommentApi {

    private final CommentService service;

    @Override
    public Collection<CommentDto> getByEvent(Long eventId, Integer from, Integer size) throws NotFoundException {
        return service.getAllEventComments(eventId, from, size);
    }
}

