package ru.practicum.api.category;

import org.springframework.validation.annotation.Validated;

@Validated
public interface ConventialCategoryApi extends AdminCategoryApi, InnerCategoryApi, PublicCategoryApi {
}
