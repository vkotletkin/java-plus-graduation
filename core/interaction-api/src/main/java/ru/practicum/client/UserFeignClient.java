package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.api.user.InnerUserApi;
import ru.practicum.api.user.UserApi;

@FeignClient(name = "user-service")
public interface UserFeignClient extends UserApi, InnerUserApi {
}
