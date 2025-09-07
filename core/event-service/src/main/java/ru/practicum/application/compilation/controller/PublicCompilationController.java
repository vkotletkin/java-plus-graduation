package ru.practicum.application.compilation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.api.compilation.PublicCompilationApi;
import ru.practicum.application.compilation.service.CompilationService;
import ru.practicum.dto.compilation.ResponseCompilationDto;
import ru.practicum.exception.NotFoundException;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
public class PublicCompilationController implements PublicCompilationApi {

    private final CompilationService compilationService;

    @Override
    public List<ResponseCompilationDto> getAll(Boolean pinned, Integer from, Integer size) {
        return compilationService.getCompilations(pinned, from, size);
    }

    @Override
    public ResponseCompilationDto getCompilationById(Long compilationId) throws NotFoundException {
        return compilationService.getCompilationById(compilationId);
    }
}
