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

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserActionService {

    private final UserActionRepository repository;

    public void save(UserActionAvro avro) {

        log.info("Сохранение действия {} пользователя: {} для события с идентификатором: {}",
                avro.getActionType(), avro.getUserId(), avro.getEventId());

        UserActionId id = UserActionMapper.toUserActionId(avro);
        Optional<UserAction> existingAction = repository.findById(id);

        if (shouldSaveAction(existingAction, avro)) {
            UserAction userAction = UserActionMapper.toUserAction(avro);
            repository.save(userAction);
        }
    }

    private boolean shouldSaveAction(Optional<UserAction> existingAction, UserActionAvro newAction) {

        if (existingAction.isEmpty()) {
            return true;
        }

        double existingScore = existingAction.get().getScore();
        double requiredWeight = WeightConverter.getWeightOnAction(newAction.getActionType());

        return existingScore < requiredWeight;
    }
}
