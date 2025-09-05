package ru.practicum.application.compilation.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import ru.practicum.api.compilation.AdminCompilationApi;
import ru.practicum.application.compilation.service.CompilationService;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.ResponseCompilationDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.request.compilation.UpdateCompilationRequest;


@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminCompilationController implements AdminCompilationApi {

    final CompilationService compilationService;

    @Override
    public ResponseCompilationDto add(NewCompilationDto compilationDto) throws NotFoundException {
        return compilationService.addCompilation(compilationDto);
    }

    @Override
    public ResponseCompilationDto update(Long compId, UpdateCompilationRequest compilationDto) throws NotFoundException {
        return compilationService.updateCompilation(compId, compilationDto);
    }

    @Override
    public void delete(Long compId) throws ValidationException, NotFoundException {
        compilationService.deleteCompilation(compId);
    }
}
