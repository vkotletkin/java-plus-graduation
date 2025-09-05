package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.api.compilation.AdminCompilationApi;
import ru.practicum.api.compilation.PublicCompilationApi;

@FeignClient(name = "compilation-service")
public interface CompilationFeignClient extends AdminCompilationApi, PublicCompilationApi {
}
