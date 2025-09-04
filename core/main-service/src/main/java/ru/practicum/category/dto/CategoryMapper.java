package ru.practicum.category.dto;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import ru.practicum.category.model.Category;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryMapper {

    public static Category mapCategoryDto(CategoryDto categoryDto) {
        return new Category(null, categoryDto.getName());
    }

    public static CategoryDto mapCategory(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }
}
