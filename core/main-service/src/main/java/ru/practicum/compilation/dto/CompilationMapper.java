package ru.practicum.compilation.dto;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import ru.practicum.compilation.model.Compilation;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
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
