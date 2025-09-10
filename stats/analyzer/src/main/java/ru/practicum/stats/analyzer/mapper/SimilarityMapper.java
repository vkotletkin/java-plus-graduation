package ru.practicum.stats.analyzer.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.stats.analyzer.model.EventSimilarity;
import ru.practicum.stats.analyzer.model.EventSimilarityId;

@UtilityClass
public class SimilarityMapper {
    public EventSimilarity mapAvroToEntity(EventSimilarityAvro avro) {
        return new EventSimilarity(avro.getEventA(), avro.getEventB(), avro.getScore());
    }

    public EventSimilarityId mapAvroToKey(EventSimilarityAvro avro) {
        return new EventSimilarityId(avro.getEventA(), avro.getEventB());
    }
}
