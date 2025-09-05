package ru.practicum.application.category.mapper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import ru.practicum.application.category.model.Category;
import ru.practicum.dto.category.CategoryDto;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryMapper {

    public static Category mapCategoryDto(CategoryDto categoryDto) {
        return new Category(null, categoryDto.getName());
    }

    public static CategoryDto mapCategory(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }

}
