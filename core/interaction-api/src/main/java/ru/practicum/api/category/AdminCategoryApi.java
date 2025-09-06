package ru.practicum.api.category;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;

public interface AdminCategoryApi {

    String ADMIN_CATEGORIES_PATH = "/admin/categories";
    String ADMIN_CATEGORIES_ID_PATH = "/admin/categories/{category-id}";

    @PostMapping(ADMIN_CATEGORIES_PATH)
    @ResponseStatus(HttpStatus.CREATED)
    CategoryDto addCategory(@Valid @RequestBody CategoryDto newCategory) throws ConflictException;

    @PatchMapping(ADMIN_CATEGORIES_ID_PATH)
    CategoryDto updateCategory(@PathVariable(name = "category-id") Long categoryId,
                               @Valid @RequestBody CategoryDto categoryDto) throws ConflictException, NotFoundException;

    @DeleteMapping(ADMIN_CATEGORIES_ID_PATH)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    void deleteCategory(@PathVariable(name = "category-id") Long categoryId) throws ConflictException, NotFoundException;
}
