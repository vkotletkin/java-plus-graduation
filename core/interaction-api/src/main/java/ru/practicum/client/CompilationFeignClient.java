package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.api.compilation.CommonCompilationApi;

@FeignClient(name = "compilation-service")
public interface CompilationFeignClient extends CommonCompilationApi {
}
