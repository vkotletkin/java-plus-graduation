package ru.practicum.api.comment;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.exception.NotFoundException;

import java.util.Collection;

@Validated
public interface AdminCommentApi {

    String ADMIN_COMMENTS_PATH = "/admin/comments";

    @GetMapping(ADMIN_COMMENTS_PATH)
    Collection<CommentDto> getComments(@RequestParam @Positive Long eventId,
                                       @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                       @RequestParam(defaultValue = "10") @Positive Integer size) throws NotFoundException;

    @DeleteMapping(ADMIN_COMMENTS_PATH + "/{comment-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void removeComment(@PathVariable(name = "comment-id") Long commentId) throws NotFoundException;
}
