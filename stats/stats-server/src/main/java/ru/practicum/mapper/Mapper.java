package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.StatsRequestDto;
import ru.practicum.dto.StatsResponseDto;
import ru.practicum.model.Requests;
import ru.practicum.model.Response;

@UtilityClass
public class Mapper {
    public StatsRequestDto toRequestDto(Requests request) {
        StatsRequestDto statsDto = new StatsRequestDto();
        statsDto.setIp(request.getIp());
        statsDto.setApp(request.getApplication());
        statsDto.setUri(request.getUri());
        statsDto.setTimestamp(request.getMoment());
        return statsDto;
    }

    public Requests toRequest(StatsRequestDto requestDto) {
        Requests request = new Requests();
        request.setIp(requestDto.getIp());
        request.setApplication(requestDto.getApp());
        request.setUri(requestDto.getUri());
        request.setMoment(requestDto.getTimestamp());
        return request;
    }

    public StatsResponseDto toResponseDto(Response stats) {
        StatsResponseDto statsDto = new StatsResponseDto();
        statsDto.setApp(stats.getApplication());
        statsDto.setHits(stats.getTotal());
        statsDto.setUri(stats.getUri());
        return statsDto;
    }
}
