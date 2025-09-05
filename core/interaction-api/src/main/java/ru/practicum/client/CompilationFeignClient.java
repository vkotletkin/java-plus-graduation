package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.api.compilation.ConventialCompilationApi;

@FeignClient(name = "compilation-service")
public interface CompilationFeignClient extends ConventialCompilationApi {
}
