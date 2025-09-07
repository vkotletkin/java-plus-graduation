package ru.practicum.application.compilation.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.application.compilation.model.Compilation;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.ResponseCompilationDto;

@UtilityClass
public class CompilationMapper {

    public static Compilation toModel(NewCompilationDto dto) {
        return Compilation.builder().title(dto.getTitle()).pinned(dto.getPinned()).build();
    }

    public static ResponseCompilationDto toResponseCompilationDto(Compilation compilation) {
        return ResponseCompilationDto.builder()
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }
}
