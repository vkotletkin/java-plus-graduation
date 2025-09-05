package ru.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.StatsRequestDto;
import ru.practicum.dto.StatsResponseDto;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.Mapper;
import ru.practicum.model.Response;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    @Transactional
    public StatsRequestDto save(StatsRequestDto requestDto) {
        try {
            var savedRequest = statsRepository.save(Mapper.toRequest(requestDto));
            return Mapper.toRequestDto(savedRequest);
        } catch (Exception e) {
            throw new ValidationException(e.getMessage());
        }
    }

    public List<StatsResponseDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (start.isAfter(end)) {
            throw new ValidationException("Время окончания позже начала");
        }

        List<Response> statistic;
        if ((uris == null) || (uris.isEmpty())) {
            statistic = unique ? statsRepository.findAllUnique(start, end) : statsRepository.findAll(start, end);
        } else {
            statistic = unique ? statsRepository.findUrisUnique(start, end, uris) :
                    statsRepository.findUris(start, end, uris);
        }
        return statistic.stream()
                .map(Mapper::toResponseDto)
                .collect(Collectors.toList());
    }
}