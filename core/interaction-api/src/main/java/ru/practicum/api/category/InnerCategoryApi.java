package ru.practicum.api.category;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.dto.category.CategoryDto;

import java.util.List;
import java.util.Set;

public interface InnerCategoryApi {

    String INNER_CATEGORY_PATH = "/inner/category";

    @GetMapping(INNER_CATEGORY_PATH + "/exist/{category-id}")
    boolean existById(@PathVariable(name = "category-id") Long categoryId);

    @GetMapping(INNER_CATEGORY_PATH + "/all")
    List<CategoryDto> getCategoriesByIds(@RequestParam Set<Long> ids);
}
