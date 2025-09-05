package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.client.StatsInterface;
import ru.practicum.dto.StatsRequestDto;
import ru.practicum.dto.StatsResponseDto;
import ru.practicum.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatsController implements StatsInterface {

    private final StatsService statsService;

    @Override
    public List<StatsResponseDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        log.info("Получен запрос сбора статистики StartDate: {}, EndDate: {}, Uris: {}, Unique: {}", start, end, uris, unique);
        return statsService.getStats(start, end, uris, unique);
    }

    @Override
    public StatsRequestDto save(StatsRequestDto statsRequestDto) {
        log.info("Получен запрос на добавление статистики: StatsRequestDto: {}", statsRequestDto);
        return statsService.save(statsRequestDto);
    }
}