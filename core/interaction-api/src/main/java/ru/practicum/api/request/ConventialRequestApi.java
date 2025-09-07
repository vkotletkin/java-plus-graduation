package ru.practicum.api.request;

import org.springframework.validation.annotation.Validated;

@Validated
public interface ConventialRequestApi extends RequestApi, InternalEventRequestApi {
}
