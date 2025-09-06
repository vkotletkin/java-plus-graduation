package ru.practicum.application.compilation.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.ResponseCompilationDto;
import ru.practicum.application.compilation.model.Compilation;

@UtilityClass
public class CompilationMapper {
    public static Compilation mapToCompilation(NewCompilationDto dto) {
        Compilation compilation = new Compilation();
        compilation.setTitle(dto.getTitle());
        compilation.setPinned(dto.getPinned());

        return compilation;
    }

    public static ResponseCompilationDto mapToResponseCompilation(Compilation compilation) {
        return ResponseCompilationDto.builder()
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }
}
