package ru.practicum.stats.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.stats.analyzer.mapper.SimilarityMapper;
import ru.practicum.stats.analyzer.repository.SimilarityRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimilarityService {

    private final SimilarityRepository repository;

    public void save(EventSimilarityAvro avro) {
        log.info("Сохранение сходства событий {} и {}. Показатель сходства: {}",
                avro.getEventA(), avro.getEventB(), avro.getScore());
        repository.deleteById(SimilarityMapper.toEventSimilarityId(avro));
        repository.save(SimilarityMapper.toEventSimilarity(avro));
    }
}