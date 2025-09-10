package ru.practicum.stats.analyzer.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.stats.proto.RecommendedEventProto;

@UtilityClass
public class RecommendationsMapper {
    public RecommendedEventProto mapSimilarityToRecommendation(Long eventId, Double score) {
        return RecommendedEventProto.newBuilder()
                .setEventId(eventId)
                .setScore(score)
                .build();
    }
}
