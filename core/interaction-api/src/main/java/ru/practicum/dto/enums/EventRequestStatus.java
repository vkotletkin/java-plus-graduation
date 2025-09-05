package ru.practicum.dto.enums;

import lombok.experimental.UtilityClass;

@UtilityClass
public class EventRequestStatus {
    public final String PENDING_REQUEST = "PENDING";
    public final String CONFIRMED_REQUEST = "CONFIRMED";
    public final String REJECTED_REQUEST = "REJECTED";
    public final String ACCEPTED_REQUEST = "ACCEPTED";
    public final String CANCELED_REQUEST = "CANCELED";
}
