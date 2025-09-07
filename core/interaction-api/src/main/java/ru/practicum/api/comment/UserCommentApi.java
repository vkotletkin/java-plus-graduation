package ru.practicum.api.comment;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;

import java.util.Collection;

@Validated
public interface UserCommentApi {

    String USER_COMMENTS_PATH = "/users/{user-id}/comments";
    String USER_COMMENTS_BY_ID_PATH = "/users/{user-id}/comments/{comment-id}";

    @GetMapping(USER_COMMENTS_PATH)
    Collection<CommentDto> getByUserComment(@PathVariable(name = "user-id") Long userId) throws NotFoundException;

    @PostMapping(USER_COMMENTS_PATH)
    @ResponseStatus(HttpStatus.CREATED)
    CommentDto addComments(@PathVariable(name = "user-id") Long userId,
                           @RequestParam @Positive Long eventId,
                           @RequestBody @Validated CommentDto commentDto) throws ConflictException, NotFoundException;

    @PatchMapping(USER_COMMENTS_BY_ID_PATH)
    CommentDto updateComment(@PathVariable(name = "user-id") Long userId,
                             @PathVariable(name = "comment-id") Long commentId,
                             @RequestBody @Valid CommentDto commentDto) throws ConflictException, NotFoundException;

    @DeleteMapping(USER_COMMENTS_BY_ID_PATH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteComment(@PathVariable(name = "comment-id") @NonNull Long commentId,
                       @PathVariable(name = "user-id") @NonNull Long userId
    ) throws ConflictException, NotFoundException;
}
