package ru.practicum.api.compilation;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.ResponseCompilationDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.request.compilation.UpdateCompilationRequest;

@Validated
public interface AdminCompilationApi {

    String ADMIN_COMPILATIONS_PATH = "/admin/compilations";
    String ADMIN_COMPILATION_BY_ID_PATH = "/admin/compilations/{compilation-id}";

    @PostMapping(ADMIN_COMPILATIONS_PATH)
    @ResponseStatus(HttpStatus.CREATED)
    ResponseCompilationDto add(@Valid @RequestBody NewCompilationDto compilationDto) throws NotFoundException;

    @DeleteMapping(ADMIN_COMPILATION_BY_ID_PATH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable(name = "compilation-id") Long compilationId) throws ValidationException, NotFoundException;

    @PatchMapping(ADMIN_COMPILATION_BY_ID_PATH)
    ResponseCompilationDto update(@PathVariable(name = "compilation-id") Long compilationId,
                                  @Valid @RequestBody UpdateCompilationRequest compilationDto) throws NotFoundException;
}
