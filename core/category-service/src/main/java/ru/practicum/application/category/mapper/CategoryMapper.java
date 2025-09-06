package ru.practicum.application.category.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.application.category.model.Category;
import ru.practicum.dto.category.CategoryDto;

@UtilityClass
public class CategoryMapper {
    public static Category mapCategoryDto(CategoryDto categoryDto) {
        return new Category(null, categoryDto.getName());
    }

    public static CategoryDto mapCategory(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

}
