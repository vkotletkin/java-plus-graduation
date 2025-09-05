package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.api.user.ConventialUserApi;

@FeignClient(name = "user-service")
public interface UserFeignClient extends ConventialUserApi {
}
