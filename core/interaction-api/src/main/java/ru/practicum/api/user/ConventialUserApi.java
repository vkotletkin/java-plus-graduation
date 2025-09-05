package ru.practicum.api.user;

import org.springframework.validation.annotation.Validated;

@Validated
public interface ConventialUserApi extends UserApi, InnerUserApi {
}
