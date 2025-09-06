package ru.practicum.api.event;

import org.springframework.validation.annotation.Validated;

@Validated
public interface ConventialEventApi extends PublicEventApi, AdminEventApi, InternalEventApi, UserEventApi {
}
