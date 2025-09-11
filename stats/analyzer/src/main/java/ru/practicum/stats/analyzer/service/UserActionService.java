package ru.practicum.stats.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.stats.analyzer.mapper.UserActionMapper;
import ru.practicum.stats.analyzer.model.UserAction;
import ru.practicum.stats.analyzer.model.embedded.UserActionId;
import ru.practicum.stats.analyzer.repository.UserActionRepository;
import ru.practicum.stats.analyzer.util.WeightConverter;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserActionService {

    private final UserActionRepository repository;

    public void save(UserActionAvro avro) {

        log.info("Сохранение действия {} пользователя: {} для события с идентификатором: {}", avro.getActionType(),
                avro.getUserId(), avro.getEventId());
        UserActionId id = UserActionMapper.toUserActionId(avro);

        if (!repository.existsById(id) ||
                repository.findById(id).get().getScore() < WeightConverter.getWeightOnAction(avro.getActionType())) {
            repository.save(UserActionMapper.toUserAction(avro));
        }

       // UserAction userAction = repository.findById(id).orElseThrow(notFoundException
    }
}
