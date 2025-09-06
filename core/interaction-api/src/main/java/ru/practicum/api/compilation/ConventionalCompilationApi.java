package ru.practicum.api.compilation;

import org.springframework.validation.annotation.Validated;

@Validated
public interface ConventionalCompilationApi extends AdminCompilationApi, PublicCompilationApi {
}
