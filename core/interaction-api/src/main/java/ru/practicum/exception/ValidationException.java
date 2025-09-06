package ru.practicum.exception;

import java.text.MessageFormat;
import java.util.function.Supplier;

public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Object... args) {
        super(MessageFormat.format(message, args));
    }

    public static Supplier<ValidationException> validationException(String message, Object... args) {
        return () -> new ValidationException(message, args);
    }

    public static Supplier<ValidationException> validationException(String message) {
        return () -> new ValidationException(message);
    }
}
