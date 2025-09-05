package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.api.category.AdminCategoryApi;
import ru.practicum.api.category.InnerCategoryApi;
import ru.practicum.api.category.PublicCategoryApi;

@FeignClient(name = "category-service")
public interface CategoryFeignClient extends PublicCategoryApi, AdminCategoryApi, InnerCategoryApi {
}
