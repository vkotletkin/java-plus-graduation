package ru.practicum.api.comment;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.exception.NotFoundException;

import java.util.Collection;

public interface PublicCommentApi {

    @GetMapping("/events/{event-id}/comments")
    Collection<CommentDto> getByEvent(@PathVariable(name = "event-id") Long eventId,
                                      @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                      @RequestParam(defaultValue = "10") @Positive Integer size) throws NotFoundException;
}