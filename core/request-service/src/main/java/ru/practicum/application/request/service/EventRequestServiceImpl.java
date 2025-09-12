package ru.practicum.application.request.service;

import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.application.request.mapper.EventRequestMapper;
import ru.practicum.application.request.model.EventRequest;
import ru.practicum.application.request.repository.RequestRepository;
import ru.practicum.stats.client.CollectorGrpcClient;
import ru.practicum.stats.client.EventFeignClient;
import ru.practicum.stats.client.UserFeignClient;
import ru.practicum.dto.enums.EventState;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.request.EventRequestDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.ewm.stats.proto.ActionTypeProto;
import ru.practicum.ewm.stats.proto.UserActionProto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.dto.enums.EventRequestStatus.*;
import static ru.practicum.exception.NotFoundException.notFoundException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventRequestServiceImpl implements EventRequestService {

    private final RequestRepository requestRepository;
    private final UserFeignClient userFeignClient;
    private final EventFeignClient eventFeignClient;
    private final CollectorGrpcClient collectorGrpcClient;

    @Override
    @Transactional
    public EventRequestDto addRequest(Long userId, Long eventId) throws ConflictException, NotFoundException {

        collectorGrpcClient.sendUserAction(createUserAction(eventId, userId, ActionTypeProto.ACTION_REGISTER, Instant.now()));

        UserDto user = userFeignClient.getById(userId);
        EventFullDto event = getEventById(eventId);

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Создатель события не может подать заявку на участие");
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Событие не опубликовано");
        }

        List<EventRequest> requests = getEventRequestsByEventId(event.getId());
        participationLimitIsFull(event);

        for (EventRequest request : requests) {
            if (request.getRequester().equals(userId)) {
                throw new ConflictException("Повторная заявка на участие в событии");
            }
        }

        EventRequest newRequest = createNewEventRequest(user, event);
        EventRequest eventRequest = requestRepository.save(newRequest);

        return EventRequestMapper.mapRequest(eventRequest);
    }

    @Override
    public List<EventRequestDto> getUserRequests(Long userId) throws NotFoundException {

        if (!userFeignClient.existsById(userId)) {
            throw new NotFoundException("Пользователь с идентификатором: {0} - не найден", userId);
        }

        return requestRepository.findByUserId(userId).stream()
                .map(EventRequestMapper::mapRequest)
                .toList();
    }

    @Override
    public List<EventRequestDto> getRequestsByEventId(Long userId, Long eventId) throws ValidationException, NotFoundException {
        List<EventRequest> requests = getEventRequests(userId, eventId);
        return requests.stream()
                .map(EventRequestMapper::mapRequest)
                .toList();
    }

    @Override
    @Transactional
    public EventRequestDto updateRequest(Long userId,
                                         Long eventId,
                                         EventRequestDto updateRequest) throws ConflictException, ValidationException, NotFoundException {

        EventFullDto event = getEventById(eventId);
        List<EventRequest> requests = getEventRequestsByEventId(eventId);

        long confirmedRequestsCounter = requests.stream()
                .filter(r -> r.getStatus().equals(CONFIRMED_REQUEST))
                .count();

        List<EventRequestDto> confirmedRequests = new ArrayList<>();
        List<EventRequestDto> rejectedRequests = new ArrayList<>();

        List<EventRequest> result = new ArrayList<>();

        List<EventRequest> pending = requests.stream()
                .filter(p -> p.getStatus().equals(PENDING_REQUEST))
                .toList();

        for (EventRequest request : requests) {
            if (request.getStatus().equals(CONFIRMED_REQUEST) ||
                    request.getStatus().equals(REJECTED_REQUEST) ||
                    request.getStatus().equals(PENDING_REQUEST)) {

                if (updateRequest.getStatus().equals(CONFIRMED_REQUEST) && event.getParticipantLimit() != 0 &&
                        event.getParticipantLimit() < confirmedRequestsCounter) {

                    throw new ConflictException("Превышено число возможных заявок на участие");
                }


                if (updateRequest.getStatus().equals(REJECTED_REQUEST) && request.getStatus().equals(CONFIRMED_REQUEST)) {
                    throw new ConflictException("Нельзя отменить подтверждённую заявку");
                }

                request.setStatus(updateRequest.getStatus());
                EventRequestDto participationRequestDto = EventRequestMapper.mapRequest(request);

                if (CONFIRMED_REQUEST.equals(participationRequestDto.getStatus())) {
                    confirmedRequests.add(participationRequestDto);
                } else if (REJECTED_REQUEST.equals(participationRequestDto.getStatus())) {
                    rejectedRequests.add(participationRequestDto);
                }

                result.add(request);
                confirmedRequestsCounter++;

            } else {
                throw new ValidationException("Неверный статус заявки");
            }
        }

        requestRepository.saveAll(pending);

        requestRepository.saveAll(result);

        return EventRequestMapper.mapRequestWithConfirmedAndRejected(confirmedRequests, rejectedRequests);
    }

    @Override
    @Transactional
    public EventRequestDto cancelRequest(Long userId, Long requestId) throws NotFoundException, ValidationException {

        if (!userFeignClient.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден. Идентификатор пользователя: {0}", userId);
        }

        EventRequest request = requestRepository.findById(requestId).orElseThrow(notFoundException("Запрос не существует"));

        if (!request.getRequester().equals(userId)) {
            throw new ValidationException("Создатель заявки другой пользователь");
        }

        request.setStatus(CANCELED_REQUEST);
        return EventRequestMapper.mapRequest(requestRepository.save(request));
    }

    @Override
    public Long countByEventAndStatuses(Long eventId, List<String> statuses) {
        return requestRepository.countByEventAndStatuses(eventId, statuses);
    }

    @Override
    public List<EventRequestDto> getByEventAndStatus(List<Long> eventId, String status) {
        return requestRepository.findByEventIdsAndStatus(eventId, status).stream()
                .map(EventRequestMapper::mapRequest)
                .toList();
    }

    @Override
    public List<EventRequestDto> findByEventIds(List<Long> id) {
        return requestRepository.findByEventIds(id).stream()
                .map(EventRequestMapper::mapRequest)
                .toList();
    }

    @Override
    @Transactional
    public boolean isUserTakePart(Long userId, Long eventId) {
        return requestRepository.userTakePart(userId, eventId);
    }

    private UserActionProto createUserAction(Long eventId, Long userId, ActionTypeProto type, Instant timestamp) {
        return UserActionProto.newBuilder()
                .setUserId(userId)
                .setEventId(eventId)
                .setActionType(type)
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(timestamp.getEpochSecond())
                        .setNanos(timestamp.getNano())
                        .build())
                .build();
    }

    private EventRequest createNewEventRequest(UserDto user, EventFullDto event) {

        EventRequest.EventRequestBuilder builder = EventRequest.builder()
                .requester(user.getId())
                .created(LocalDateTime.now())
                .status(event.getParticipantLimit() == 0 ? CONFIRMED_REQUEST : PENDING_REQUEST)
                .event(event.getId());

        if (Boolean.FALSE.equals(event.getRequestModeration())) {
            builder.status(ACCEPTED_REQUEST);
        }

        return builder.build();
    }

    private void participationLimitIsFull(EventFullDto event) throws ConflictException {

        Long confirmedRequestsCounter = requestRepository.countByEventAndStatuses(event.getId(), List.of("CONFIRMED", "ACCEPTED"));

        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= confirmedRequestsCounter) {
            throw new ConflictException("Превышено число заявок на участие");
        }
    }

    private List<EventRequest> getEventRequests(Long userId, Long eventId) throws ValidationException, NotFoundException {

        UserDto user = userFeignClient.getById(userId);
        EventFullDto event = getEventById(eventId);

        if (!user.getId().equals(event.getInitiator().getId())) {
            throw new ValidationException("Пользователь не инициатор события с ID: {0}", eventId);
        }

        return requestRepository.findByEventId(eventId);
    }

    private List<EventRequest> getEventRequestsByEventId(Long eventId) throws NotFoundException {

        if (!eventFeignClient.existsById(eventId)) {
            throw new NotFoundException("Событие с идентификатором: {0} - не найдено", eventId);
        }

        return requestRepository.findByEventId(eventId);
    }

    private EventFullDto getEventById(Long eventId) throws NotFoundException {
        return eventFeignClient.getInnerEventById(eventId);
    }
}
