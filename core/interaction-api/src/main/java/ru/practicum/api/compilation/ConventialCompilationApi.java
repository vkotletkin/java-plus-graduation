package ru.practicum.api.compilation;

import org.springframework.validation.annotation.Validated;

@Validated
public interface ConventialCompilationApi extends AdminCompilationApi, PublicCompilationApi {
}
