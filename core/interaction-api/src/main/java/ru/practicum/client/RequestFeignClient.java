package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.api.request.ConventialRequestApi;

@FeignClient(name = "request-service")
public interface RequestFeignClient extends ConventialRequestApi {
}
