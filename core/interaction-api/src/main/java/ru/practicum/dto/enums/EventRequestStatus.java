package ru.practicum.dto.enums;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class EventRequestStatus {
    public static final String PENDING_REQUEST = "PENDING";
    public static final String CONFIRMED_REQUEST = "CONFIRMED";
    public static final String REJECTED_REQUEST = "REJECTED";
    public static final String ACCEPTED_REQUEST = "ACCEPTED";
    public static final String CANCELED_REQUEST = "CANCELED";
}
