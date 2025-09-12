package ru.practicum.stats.analyzer.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.stats.analyzer.model.UserAction;
import ru.practicum.stats.analyzer.model.embedded.UserActionId;
import ru.practicum.stats.analyzer.util.WeightConverter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserActionMapper {

    public static UserAction toUserAction(UserActionAvro avro) {
        return new UserAction(avro.getUserId(), avro.getEventId(),
                WeightConverter.getWeightOnAction(avro.getActionType()), avro.getTimestamp());
    }

    public static UserActionId toUserActionId(UserActionAvro avro) {
        return new UserActionId(avro.getUserId(), avro.getEventId());
    }
}
