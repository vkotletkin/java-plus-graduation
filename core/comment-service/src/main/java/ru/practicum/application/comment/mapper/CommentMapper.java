package ru.practicum.application.comment.mapper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import ru.practicum.application.comment.model.Comment;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.user.UserDto;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {

    public static CommentDto toDto(final Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .userId(comment.getUser())
                .eventId(comment.getEvent())
                .content(comment.getContent())
                .created(comment.getCreated())
                .isInitiator(comment.isInitiator())
                .build();
    }

    public static List<CommentDto> toDto(final List<Comment> comments) {

        if (comments == null || comments.isEmpty()) {
            return List.of();
        }
        return comments.stream()
                .map(CommentMapper::toDto)
                .toList();
    }

    public static Comment toModel(final CommentDto comment, final UserDto user, final EventFullDto event) {
        return Comment.builder()
                .id(comment.getId())
                .user(user.getId())
                .event(event.getId())
                .content(comment.getContent())
                .build();
    }
}
