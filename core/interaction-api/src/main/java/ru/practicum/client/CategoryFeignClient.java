package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.api.category.ConventionalCategoryApi;

@FeignClient(name = "category-service")
public interface CategoryFeignClient extends ConventionalCategoryApi {
}
