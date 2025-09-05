package ru.practicum.application.compilation.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import ru.practicum.api.compilation.PublicCompilationApi;
import ru.practicum.application.compilation.service.CompilationService;
import ru.practicum.dto.compilation.ResponseCompilationDto;
import ru.practicum.exception.NotFoundException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PublicCompilationController implements PublicCompilationApi {

    final CompilationService compilationService;

    @Override
    public List<ResponseCompilationDto> getAll(Boolean pinned, Integer from, Integer size) {
        return compilationService.getCompilations(pinned, from, size);
    }

    @Override
    public ResponseCompilationDto getCompilationById(Long compId) throws NotFoundException {
        return compilationService.getCompilationById(compId);
    }


}
