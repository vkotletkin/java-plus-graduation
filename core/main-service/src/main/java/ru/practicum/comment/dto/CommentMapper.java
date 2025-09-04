package ru.practicum.comment.dto;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import ru.practicum.comment.model.Comment;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {

    public static CommentDto mapToCommentDto(final Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .userId(comment.getUser().getId())
                .eventId(comment.getEvent().getId())
                .content(comment.getContent())
                .created(comment.getCreated())
                .isInitiator(comment.isInitiator())
                .build();
    }

    public static List<CommentDto> mapToCommentDto(final List<Comment> comments) {

        if (comments == null || comments.isEmpty()) {
            return List.of();
        }

        return comments.stream()
                .map(CommentMapper::mapToCommentDto)
                .toList();
    }

    public static Comment mapTo(final CommentDto comment, final User user, final Event event) {
        return Comment.builder()
                .id(comment.getId())
                .user(user)
                .event(event)
                .content(comment.getContent())
                .build();
    }
}