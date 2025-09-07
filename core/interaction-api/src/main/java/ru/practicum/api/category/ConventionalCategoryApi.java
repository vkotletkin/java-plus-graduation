package ru.practicum.api.category;

import org.springframework.validation.annotation.Validated;

@Validated
public interface ConventionalCategoryApi extends AdminCategoryApi, InternalCategoryApi, PublicCategoryApi {
}
