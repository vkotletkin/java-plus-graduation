package ru.practicum.stats.analyzer.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.stats.analyzer.model.UserAction;
import ru.practicum.stats.analyzer.model.UserActionId;

@UtilityClass
public class UserActionMapper {
    public UserAction mapAvroToEntity(UserActionAvro avro) {
        return new UserAction(avro.getUserId(), avro.getEventId(),
                convertActionToWeight(avro.getActionType()), avro.getTimestamp());
    }

    public UserActionId mapAvroToKey(UserActionAvro avro) {
        return new UserActionId(avro.getUserId(), avro.getEventId());
    }

    public double convertActionToWeight(ActionTypeAvro action) {
        switch (action) {
            case VIEW -> {
                return 0.4;
            }
            case REGISTER -> {
                return 0.8;
            }
            case LIKE -> {
                return 1.0;
            }
            default -> {
                return 0.0;
            }
        }
    }
}
