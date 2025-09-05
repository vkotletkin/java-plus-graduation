package ru.practicum.application.category.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.api.category.PublicCategoryApi;
import ru.practicum.application.category.service.CategoryService;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.exception.NotFoundException;

import java.util.List;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class PublicCategoryController implements PublicCategoryApi {
    final CategoryService categoryService;

    @Override
    public List<CategoryDto> getAllCategories(@RequestParam(defaultValue = "0") Integer from,
                                              @RequestParam(defaultValue = "10") Integer size) {
        return categoryService.getAllCategories(from, size);
    }

    @Override
    public CategoryDto getCategoryById(@PathVariable Long catId) throws NotFoundException {
        return categoryService.getCategoryById(catId);
    }
}
