package ru.practicum.application.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.api.category.PublicCategoryApi;
import ru.practicum.application.category.service.CategoryService;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.exception.NotFoundException;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
public class PublicCategoryController implements PublicCategoryApi {

    private final CategoryService categoryService;

    @Override
    public List<CategoryDto> getAllCategories(@RequestParam(defaultValue = "0") Integer from,
                                              @RequestParam(defaultValue = "10") Integer size) {
        return categoryService.getAllCategories(from, size);
    }

    @Override
    public CategoryDto getCategoryById(Long categoryId) throws NotFoundException {
        return categoryService.getCategoryById(categoryId);
    }
}
