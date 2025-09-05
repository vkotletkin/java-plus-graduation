package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.api.category.ConventialCategoryApi;

@FeignClient(name = "category-service")
public interface CategoryFeignClient extends ConventialCategoryApi {
}
