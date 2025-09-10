package ru.practicum.stats.aggregator.service.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.stats.aggregator.exception.IncorrectActionTypeException;

import java.time.Instant;
import java.util.*;

@Slf4j
@Service
public class UserActionDomainService {

    private final Map<Long, Double> eventWeightSumMap;
    private final Map<PairEvent, Double> eventsSimilarityMap;
    private final Map<Long, Double> sqrtCacheMap;
    private final Map<Long, Map<Long, Double>> usersFeedbackMap;
    private final Map<PairEvent, Double> eventsMinWeightSumMap;

    public UserActionDomainService() {
        usersFeedbackMap = new HashMap<>();
        eventsMinWeightSumMap = new HashMap<>();
        eventWeightSumMap = new HashMap<>();
        eventsSimilarityMap = new HashMap<>();
        sqrtCacheMap = new HashMap<>();
    }

    public List<EventSimilarityAvro> calculateSimilarityEvents(UserActionAvro avro) throws IncorrectActionTypeException {

        Long userId = avro.getUserId();
        Long eventId = avro.getEventId();
        log.info("Действие пользователя: {} с событием: {}", userId, eventId);

        Double newWeight = convertActionToWeight(avro.getActionType());
        log.info("Вес действия пользователя: {}", newWeight);

        Map<Long, Double> userRatings = usersFeedbackMap.computeIfAbsent(eventId, k -> new HashMap<>());
        Double oldWeight = userRatings.getOrDefault(userId, 0.0);

        log.info("Сравнение старого веса и нового: {} и {}", oldWeight, newWeight);
        if (oldWeight < newWeight) {
            log.info("Новый вес больше старого");
            userRatings.put(userId, newWeight);
            return determineSimilarity(eventId, userId, oldWeight, newWeight, avro.getTimestamp());
        } else {
            return Collections.emptyList();
        }
    }


    private List<EventSimilarityAvro> determineSimilarity(Long eventId, Long userId, Double oldWeight, Double newWeight, Instant timestamp) {

        double newSum = eventWeightSumMap.getOrDefault(eventId, 0.0) - oldWeight + newWeight;
        eventWeightSumMap.put(eventId, newSum);
        sqrtCacheMap.remove(eventId);

        List<EventSimilarityAvro> eventSimilarityAvros = new ArrayList<>();

        for (Map.Entry<Long, Map<Long, Double>> entry : usersFeedbackMap.entrySet()) {
            Long otherEventId = entry.getKey();
            Map<Long, Double> feedback = entry.getValue();
            if (!feedback.containsKey(userId) || Objects.equals(otherEventId, eventId)) continue;
            double convergenceWeight = feedback.get(userId);
            PairEvent pair = PairEvent.create(eventId, otherEventId);
            double oldMinSum = eventsMinWeightSumMap.getOrDefault(pair, 0.0);
            double newMinSum = oldMinSum - Math.min(oldWeight, convergenceWeight) + Math.min(newWeight, convergenceWeight);
            eventsMinWeightSumMap.put(pair, newMinSum);
            double similarity = calculateSimilarity(pair, newMinSum);
            eventsSimilarityMap.put(pair, similarity);
            EventSimilarityAvro message = EventSimilarityAvro.newBuilder()
                    .setEventA(pair.first())
                    .setEventB(pair.second())
                    .setScore(similarity)
                    .setTimestamp(timestamp)
                    .build();
            eventSimilarityAvros.add(message);
            log.info(eventSimilarityAvros.toString());
        }

        return eventSimilarityAvros;
    }

    private double calculateSimilarity(PairEvent pair, double sumCommon) {
        return sumCommon / (getSumOfSqrt(pair.first()) * getSumOfSqrt(pair.second()));
    }

    private double getSumOfSqrt(Long eventId) {
        return sqrtCacheMap.computeIfAbsent(eventId, id -> Math.sqrt(eventWeightSumMap.getOrDefault(id, 0.0)));
    }

    // Функция для конвертации веса просмотра, регистрации и лайка. В соответствии с теоретической частью рекомендательных систем
    // 0.4 - просмотр, 0.8 - регистрация, 1 - лайк
    private Double convertActionToWeight(ActionTypeAvro action) throws IncorrectActionTypeException {
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
            default ->
                    throw new IncorrectActionTypeException("Типа действия пользователя: {0} - не существует", action);
        }
    }
}
