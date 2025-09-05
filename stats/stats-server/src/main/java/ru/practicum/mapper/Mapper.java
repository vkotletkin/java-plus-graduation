package ru.practicum.mapper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import ru.practicum.dto.StatsRequestDto;
import ru.practicum.dto.StatsResponseDto;
import ru.practicum.model.Requests;
import ru.practicum.model.Response;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Mapper {

    public static StatsRequestDto toRequestDto(Requests request) {
        return StatsRequestDto.builder()
                .ip(request.getIp())
                .app(request.getApplication())
                .uri(request.getUri())
                .timestamp(request.getMoment())
                .build();
    }

    public static Requests toRequest(StatsRequestDto requestDto) {
        return Requests.builder()
                .ip(requestDto.getIp())
                .application(requestDto.getApp())
                .uri(requestDto.getUri())
                .moment(requestDto.getTimestamp())
                .build();
    }

    public static StatsResponseDto toResponseDto(Response stats) {
        return StatsResponseDto.builder()
                .app(stats.getApplication())
                .hits(stats.getTotal())
                .uri(stats.getUri())
                .build();
    }
}
