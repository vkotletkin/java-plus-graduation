package ru.practicum.application.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.api.category.AdminCategoryApi;
import ru.practicum.application.category.service.CategoryService;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;


@Validated
@RestController
@RequiredArgsConstructor
public class AdminCategoryController implements AdminCategoryApi {

    private final CategoryService categoryService;

    @Override
    public CategoryDto addCategory(CategoryDto newCategory) throws ConflictException {
        return categoryService.addCategory(newCategory);
    }

    @Override
    public CategoryDto updateCategory(Long categoryId,
                                      CategoryDto categoryDto) throws ConflictException, NotFoundException {
        return categoryService.updateCategory(categoryId, categoryDto);
    }

    @Override
    public void deleteCategory(Long categoryId) throws ConflictException, NotFoundException {
        categoryService.deleteCategory(categoryId);
    }
}
