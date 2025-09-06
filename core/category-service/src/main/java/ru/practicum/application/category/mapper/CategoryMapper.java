package ru.practicum.application.category.mapper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import ru.practicum.application.category.model.Category;
import ru.practicum.dto.category.CategoryDto;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryMapper {

    public static Category toModel(CategoryDto categoryDto) {
        return Category.builder()
                .id(categoryDto.getId())
                .name(categoryDto.getName())
                .build();
    }

    public static CategoryDto toDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
