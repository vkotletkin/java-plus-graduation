package ru.practicum.application.event.mapper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import ru.practicum.application.event.model.Event;
import ru.practicum.application.event.model.Location;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.enums.EventState;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.LocationDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.util.JsonFormatPattern;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class EventMapper {

    public static EventFullDto mapEventToFullDto(Event event, Long confirmed, CategoryDto category, UserDto user) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(category)
                .confirmedRequests(confirmed)
                .createdOn(getLocalDateTime(event.getCreatedOn()))
                .description(event.getDescription())
                .eventDate(getLocalDateTime(event.getEventDate()))
                .initiator(user)
                .location(mapLocationToDto(event.getLocation()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(getLocalDateTime(event.getPublishedOn()))
                .requestModeration(event.getRequestModeration())
                .state(event.getState() == null ? EventState.PENDING : event.getState())
                .title(event.getTitle())
                .build();
    }

    public static EventShortDto mapEventToShortDto(Event event, CategoryDto category, UserDto user) {
        return EventShortDto.builder()
                .annotation(event.getAnnotation())
                .title(event.getTitle())
                .category(category)
                .initiator(user)
                .id(event.getId())
                .paid(event.getPaid())
                .eventDate(event.getEventDate())
                .build();
    }

    public static Event mapNewEventDtoToEvent(NewEventDto newEvent, CategoryDto category) {
        return Event.builder()
                .annotation(newEvent.getAnnotation())
                .category(category.getId())
                .description(newEvent.getDescription())
                .eventDate(getFromString(newEvent.getEventDate()))
                .location(mapDtoToLocation(newEvent.getLocation()))
                .paid(newEvent.getPaid())
                .participantLimit(newEvent.getParticipantLimit())
                .requestModeration(newEvent.getRequestModeration())
                .title(newEvent.getTitle())
                .build();
    }

    public static LocationDto mapLocationToDto(Location location) {
        return LocationDto.builder()
                .id(location.getId())
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }

    public static Location mapDtoToLocation(LocationDto locationDto) {
        return new Location(locationDto.getId(), locationDto.getLat(), locationDto.getLon());
    }

    static String getLocalDateTime(LocalDateTime time) {
        if (time == null) {
            return null;
        }
        return time.format(DateTimeFormatter.ofPattern(JsonFormatPattern.TIME_PATTERN));
    }

    static LocalDateTime getFromString(String time) {

        if (time == null) {
            return null;
        }

        return LocalDateTime.parse(time, DateTimeFormatter.ofPattern(JsonFormatPattern.TIME_PATTERN));
    }
}
