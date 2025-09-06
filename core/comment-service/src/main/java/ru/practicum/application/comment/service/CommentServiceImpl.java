package ru.practicum.application.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.application.comment.mapper.CommentMapper;
import ru.practicum.application.comment.model.Comment;
import ru.practicum.application.comment.repository.CommentRepository;
import ru.practicum.client.EventFeignClient;
import ru.practicum.client.UserFeignClient;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.enums.EventState;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.comment.GetCommentsAdminRequest;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.exception.NotFoundException.notFoundException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final UserFeignClient userFeignClient;
    private final EventFeignClient eventFeignClient;

    @Override
    @Transactional
    public CommentDto addComment(CommentDto commentDto, Long userId, Long eventId) throws NotFoundException, ConflictException {

        commentDto.setUserId(userId);
        commentDto.setEventId(eventId);

        UserDto user = fetchUser(userId);
        EventFullDto event = fetchEvent(eventId);

        if (!EventState.PUBLISHED.equals(event.getState())) {
            throw new ConflictException("Невозможно сохранить комментарии для неопубликованного события. ID события: {0}", event.getId());
        }

        Comment comment = CommentMapper.toModel(commentDto, user, event);
        comment.setCreated(LocalDateTime.now());

        if (user.getId().equals(event.getInitiator().getId())) {
            comment.setInitiator(true);
        }

        Comment savedComment = commentRepository.save(comment);
        return CommentMapper.toDto(savedComment);

    }

    @Override
    @Transactional
    public void delete(Long userId, Long commentId) throws NotFoundException, ConflictException {

        Comment comment = fetchComment(commentId);

        if (!comment.getUser().equals(userId)) {
            throw new ConflictException("Пользователь может удалять только свои комментарии.");
        }

        commentRepository.delete(comment);
    }

    @Override
    @Transactional
    public void delete(final Long commentId) throws NotFoundException {
        Comment comment = fetchComment(commentId);
        commentRepository.delete(comment);
    }

    @Override
    @Transactional
    public CommentDto updateUserComment(Long userId, Long commentId,
                                        CommentDto commentDto) throws NotFoundException, ConflictException {

        Comment comment = fetchComment(commentId);
        fetchUser(userId);

        if (!comment.getUser().equals(userId)) {
            throw new ConflictException("Пользователь может удалять только свои комментарии.");
        }

        comment.setContent(commentDto.getContent());
        Comment updated = commentRepository.save(comment);

        return CommentMapper.toDto(updated);
    }

    @Override
    public List<CommentDto> getAllUserComments(Long userId) throws NotFoundException {
        UserDto user = fetchUser(userId);
        return CommentMapper.toDto(commentRepository.findByUser(user.getId()));
    }

    @Override
    public List<CommentDto> getAllEventComments(GetCommentsAdminRequest param) throws NotFoundException {
        final List<Comment> comments =
                getEventComments(param.getEventId(), param.getFrom(), param.getSize());
        return CommentMapper.toDto(comments);
    }

    @Override
    public List<CommentDto> getAllEventComments(Long eventId, int from, int size) throws NotFoundException {
        return CommentMapper.toDto(getEventComments(eventId, from, size));
    }

    private List<Comment> getEventComments(Long eventId, int from, int size) throws NotFoundException {

        if (!eventFeignClient.existsById(eventId)) {
            throw new NotFoundException("Событие не найдено. Идентификатор события: {0}", eventId);
        }

        final PageRequest page = PageRequest.of(from / size, size);
        return commentRepository.findAllByEvent(eventId, page).getContent();
    }

    private UserDto fetchUser(Long userId) throws NotFoundException {
        return userFeignClient.getById(userId);
    }

    private EventFullDto fetchEvent(Long eventId) throws NotFoundException {
        return eventFeignClient.getInnerEventById(eventId);
    }

    private Comment fetchComment(Long commentId) throws NotFoundException {
        return commentRepository.findById(commentId).orElseThrow(notFoundException("Комментарий с идентификатором {0} не найден.", commentId));
    }
}
