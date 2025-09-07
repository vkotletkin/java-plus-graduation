package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.api.compilation.ConventionalCompilationApi;

@FeignClient(name = "compilation-service")
public interface CompilationFeignClient extends ConventionalCompilationApi {
}
