package ru.practicum.comment.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.service.CommentService;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;

import java.util.Collection;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{user-id}/comments")
public class UserCommentController {

    private final CommentService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComments(
            @PathVariable(name = "user-id") Long userId,
            @RequestParam @Positive Long eventId,
            @RequestBody @Validated CommentDto commentDto) throws ConflictException, NotFoundException {
        return service.addComment(commentDto, userId, eventId);
    }

    @DeleteMapping("/{comment-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(
            @PathVariable(name = "comment-id") @NonNull Long commentId,
            @PathVariable(name = "user-id") @NonNull Long userId) throws ConflictException, NotFoundException {
        service.delete(userId, commentId);
    }

    @PatchMapping("/{comment-id}")
    public CommentDto updateComment(
            @PathVariable(name = "user-id") Long userId,
            @PathVariable(name = "comment-id") Long commentId,
            @RequestBody @Valid CommentDto commentDto) throws ConflictException, NotFoundException {
        return service.updateUserComment(userId, commentId, commentDto);
    }

    @GetMapping
    public Collection<CommentDto> getByUserComment(@PathVariable(name = "user-id") Long userId) throws NotFoundException {
        return service.getAllUserComments(userId);
    }
}