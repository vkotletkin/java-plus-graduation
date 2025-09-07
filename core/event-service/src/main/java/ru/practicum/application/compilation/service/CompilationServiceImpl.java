package ru.practicum.application.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.application.compilation.mapper.CompilationMapper;
import ru.practicum.application.compilation.model.Compilation;
import ru.practicum.application.compilation.repository.CompilationRepository;
import ru.practicum.application.event.mapper.EventMapper;
import ru.practicum.application.event.model.Event;
import ru.practicum.application.event.repository.EventRepository;
import ru.practicum.client.CategoryFeignClient;
import ru.practicum.client.UserFeignClient;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.ResponseCompilationDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.request.compilation.UpdateCompilationRequest;

import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.exception.NotFoundException.notFoundException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {

    private static final String COMPILATION_NOT_FOUND_MESSAGE = "Указанная подборка не найдена. ID: {0}";

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    private final UserFeignClient userFeignClient;
    private final CategoryFeignClient categoryFeignClient;

    @Override
    @Transactional
    public ResponseCompilationDto addCompilation(NewCompilationDto dto) throws NotFoundException {

        Compilation compilation = CompilationMapper.toModel(dto);

        if (compilation.getPinned() == null) {
            compilation.setPinned(false);
        }

        List<Event> events = getEventsFromDto(dto);
        compilation.setEvents(events);

        ResponseCompilationDto responseCompilationDto = CompilationMapper.toResponseCompilationDto(
                compilationRepository.save(compilation)
        );

        List<EventShortDto> eventDtos = new ArrayList<>();

        for (Event event : compilation.getEvents()) {
            eventDtos.add(EventMapper.mapEventToShortDto(event,
                    categoryFeignClient.getCategoryById(event.getCategory()),
                    userFeignClient.getById(event.getInitiator())));
        }

        responseCompilationDto.setEvents(eventDtos);

        return responseCompilationDto;
    }

    @Override
    @Transactional
    public ResponseCompilationDto updateCompilation(Long compId, UpdateCompilationRequest compilation) throws NotFoundException {

        Compilation old = compilationRepository.findById(compId)
                .orElseThrow(notFoundException(COMPILATION_NOT_FOUND_MESSAGE, compId));

        Compilation update = new Compilation();
        update.setId(compId);
        update.setPinned(compilation.getPinned() == null ? old.getPinned() : compilation.getPinned());
        update.setTitle(compilation.getTitle() == null ? old.getTitle() : compilation.getTitle());

        List<Event> events = getEventsFromDto(compilation);
        update.setEvents(events == null ? old.getEvents() : events);

        return CompilationMapper.toResponseCompilationDto(compilationRepository.save(update));
    }

    @Override
    public ResponseCompilationDto getCompilationById(Long id) throws NotFoundException {

        Compilation compilation = compilationRepository.findById(id)
                .orElseThrow(notFoundException(COMPILATION_NOT_FOUND_MESSAGE, id));

        List<Long> usersIds = compilation.getEvents().stream().map(Event::getInitiator).toList();
        Set<Long> categoriesIds = compilation.getEvents().stream().map(Event::getCategory).collect(Collectors.toSet());

        Map<Long, UserDto> users = userFeignClient.getUsersList(usersIds, 0, Math.max(compilation.getEvents().size(), 1)
        ).stream().collect(Collectors.toMap(UserDto::getId, userDto -> userDto));

        Map<Long, CategoryDto> categories = categoryFeignClient.getCategoriesByIds(categoriesIds).stream()
                .collect(Collectors.toMap(CategoryDto::getId, categoryDto -> categoryDto));

        return compileDtoWithEvents(compilation, categories, users);
    }

    @Override
    public List<ResponseCompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        List<Compilation> allWithPinned = compilationRepository.findAllWithPinned(pinned, Pageable.ofSize(size + from));
        return compileDtosWithEvents(allWithPinned);
    }

    @Override
    @Transactional
    public void deleteCompilation(Long id) throws ValidationException, NotFoundException {

        Compilation compilation = compilationRepository.findById(id)
                .orElseThrow(notFoundException("Категория с идентификатором: {0} - не найдена", id));

        try {
            compilationRepository.delete(compilation);
        } catch (Exception e) {
            throw new ValidationException("Невозможно удаление используемой категории события");
        }
    }

    private List<Event> getEventsFromDto(NewCompilationDto compilation) {

        List<Event> events = Collections.emptyList();

        if (compilation.getEvents() != null) {
            events = eventRepository.findAllByIdIn(compilation.getEvents());
        }

        return events;
    }

    private List<Event> getEventsFromDto(UpdateCompilationRequest compilation) {

        List<Event> events = new ArrayList<>();

        if (compilation.getEvents() != null) {
            events = eventRepository.findAllByIdIn(compilation.getEvents());
        }

        return events;
    }

    private List<ResponseCompilationDto> compileDtosWithEvents(List<Compilation> compilations) {

        List<Event> events = new ArrayList<>();

        for (Compilation compilation : compilations) {
            events.addAll(compilation.getEvents());
        }

        List<Long> usersIds = events.stream().map(Event::getInitiator).toList();
        Set<Long> categoriesIds = events.stream().map(Event::getCategory).collect(Collectors.toSet());

        Map<Long, UserDto> users = userFeignClient.getUsersList(usersIds, 0, Math.max(events.size(), 1)).stream()
                .collect(Collectors.toMap(UserDto::getId, userDto -> userDto));

        Map<Long, CategoryDto> categories = categoryFeignClient.getCategoriesByIds(categoriesIds).stream()
                .collect(Collectors.toMap(CategoryDto::getId, categoryDto -> categoryDto));

        return compilations.stream()
                .map(compilation -> compileDtoWithEvents(compilation, categories, users))
                .toList();
    }

    private ResponseCompilationDto compileDtoWithEvents(Compilation compilation,
                                                        Map<Long, CategoryDto> categories,
                                                        Map<Long, UserDto> users) {

        ResponseCompilationDto result = CompilationMapper.toResponseCompilationDto(compilation);

        List<EventShortDto> eventDtos = new ArrayList<>();

        for (Event event : compilation.getEvents()) {
            eventDtos.add(EventMapper.mapEventToShortDto(event,
                    categories.get(event.getCategory()), users.get(event.getInitiator())));
        }

        result.setEvents(eventDtos);
        return result;
    }
}
