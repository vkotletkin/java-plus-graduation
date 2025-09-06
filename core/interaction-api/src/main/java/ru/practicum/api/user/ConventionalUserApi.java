package ru.practicum.api.user;

import org.springframework.validation.annotation.Validated;

@Validated
public interface ConventionalUserApi extends InternalUserApi, UserApi {
}
