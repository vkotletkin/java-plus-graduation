package ru.practicum.api.category;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.dto.category.CategoryDto;

import java.util.List;
import java.util.Set;

@Validated
public interface InternalCategoryApi {

    String INTERNAL_CATEGORY_PATH = "/internal/category";

    @GetMapping(INTERNAL_CATEGORY_PATH + "/all")
    List<CategoryDto> getCategoriesByIds(@RequestParam Set<Long> ids);

    @GetMapping(INTERNAL_CATEGORY_PATH + "/exist/{category-id}")
    boolean existById(@PathVariable(name = "category-id") Long categoryId);
}
