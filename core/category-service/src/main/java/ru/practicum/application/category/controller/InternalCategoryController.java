package ru.practicum.application.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.api.category.InternalCategoryApi;
import ru.practicum.application.category.service.CategoryService;
import ru.practicum.dto.category.CategoryDto;

import java.util.List;
import java.util.Set;

@Validated
@RestController
@RequiredArgsConstructor
public class InternalCategoryController implements InternalCategoryApi {

    private final CategoryService categoryService;

    @Override
    public boolean existById(Long categoryId) {
        return categoryService.existById(categoryId);
    }

    @Override
    public List<CategoryDto> getCategoriesByIds(Set<Long> ids) {
        return categoryService.getCategoriesByIds(ids);
    }
}
