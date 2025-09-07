package ru.practicum.exception;

import java.text.MessageFormat;
import java.util.function.Supplier;

public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String message, Object... args) {
        super(MessageFormat.format(message, args));
    }

    public static Supplier<ConflictException> conflictException(String message, Object... args) {
        return () -> new ConflictException(message, args);
    }

    public static Supplier<ConflictException> conflictException(String message) {
        return () -> new ConflictException(message);
    }
}
