package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.api.event.ConventialEventApi;

@FeignClient(name = "event-service")
public interface EventFeignClient extends ConventialEventApi {
}
