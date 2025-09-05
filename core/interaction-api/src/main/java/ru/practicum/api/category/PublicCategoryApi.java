package ru.practicum.api.category;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.exception.NotFoundException;

import java.util.List;

@Validated
public interface PublicCategoryApi {

    String CATEGORIES_PATH = "/categories";

    @GetMapping(CATEGORIES_PATH)
    List<CategoryDto> getAllCategories(@RequestParam(defaultValue = "0") Integer from,
                                       @RequestParam(defaultValue = "10") Integer size);

    @GetMapping(CATEGORIES_PATH + "/{category-id}")
    CategoryDto getCategoryById(@PathVariable(name = "category-id") Long catId) throws NotFoundException;
}
