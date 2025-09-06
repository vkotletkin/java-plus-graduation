package ru.practicum.api.compilation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.dto.compilation.ResponseCompilationDto;
import ru.practicum.exception.NotFoundException;

import java.util.List;

public interface PublicCompilationApi {

    String COMPILATIONS_PATH = "/compilations";

    @GetMapping(COMPILATIONS_PATH)
    List<ResponseCompilationDto> getAll(@RequestParam(required = false) Boolean pinned,
                                        @RequestParam(defaultValue = "0") Integer from,
                                        @RequestParam(defaultValue = "10") Integer size);

    @GetMapping(COMPILATIONS_PATH + "/{compilation-id}")
    ResponseCompilationDto getCompilationById(@PathVariable(name = "compilation-id") Long compilationId) throws NotFoundException;
}
