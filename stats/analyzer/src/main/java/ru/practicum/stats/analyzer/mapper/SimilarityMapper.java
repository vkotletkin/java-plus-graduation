package ru.practicum.stats.analyzer.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.stats.analyzer.model.EventSimilarity;
import ru.practicum.stats.analyzer.model.embedded.EventSimilarityId;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SimilarityMapper {

    public static EventSimilarity toEventSimilarity(EventSimilarityAvro avro) {
        return new EventSimilarity(avro.getEventA(), avro.getEventB(), avro.getScore());
    }

    public static EventSimilarityId toEventSimilarityId(EventSimilarityAvro avro) {
        return new EventSimilarityId(avro.getEventA(), avro.getEventB());
    }
}
