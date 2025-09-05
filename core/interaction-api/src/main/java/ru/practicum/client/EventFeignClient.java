package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.api.event.AdminEventApi;
import ru.practicum.api.event.InnerEventApi;
import ru.practicum.api.event.PublicEventApi;
import ru.practicum.api.event.UserEventApi;

@FeignClient(name = "event-service")
public interface EventFeignClient extends AdminEventApi, InnerEventApi, PublicEventApi, UserEventApi {
}
