package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.api.user.ConventionalUserApi;

@FeignClient(name = "user-service")
public interface UserFeignClient extends ConventionalUserApi {
}
