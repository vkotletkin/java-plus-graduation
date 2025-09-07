package ru.practicum.application.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;

import java.util.Map;

@Slf4j
@RestControllerAdvice(basePackages = {"ru.practicum.application.event", "ru.practicum.application.compilation"})
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getAllErrors().getFirst().getDefaultMessage();
        assert errorMessage != null;
        Map<String, String> error = Map.of("error", errorMessage);
        log.warn("Validation error: {}", errorMessage);
        return error;
    }

    @ExceptionHandler({MissingServletRequestParameterException.class, ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(final ValidationException e) {
        log.error("Validation error: {}", e.getMessage());
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(final NotFoundException e) {
        log.error("Not found exception: {}", e.getMessage());
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleConflictException(final ConflictException e) {
        log.error("Conflict exception: {}", e.getMessage());
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleError(final Throwable e) {
        log.error("Unprocessed exception: {}", e.getMessage());
        return Map.of("error", e.getMessage());
    }
}
