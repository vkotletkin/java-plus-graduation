package ru.practicum.category.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryService;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;


@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addCategory(@Valid @RequestBody CategoryDto newCategory) throws ConflictException {
        return categoryService.addCategory(newCategory);
    }

    @PatchMapping("/{category-id}")
    public CategoryDto updateCategory(@PathVariable(name = "category-id") Long categoryId,
                                      @Valid @RequestBody CategoryDto categoryDto) throws ConflictException, NotFoundException {
        return categoryService.updateCategory(categoryId, categoryDto);
    }

    @DeleteMapping("/{category-id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable(name = "category-id") Long categoryId) throws ConflictException, NotFoundException {
        categoryService.deleteCategory(categoryId);
    }
}
