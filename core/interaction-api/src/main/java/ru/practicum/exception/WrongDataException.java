package ru.practicum.exception;

import java.text.MessageFormat;
import java.util.function.Supplier;

public class WrongDataException extends RuntimeException {

    public WrongDataException(String message) {
        super(message);
    }

    public WrongDataException(String message, Object... args) {
        super(MessageFormat.format(message, args));
    }

    public static Supplier<WrongDataException> wrongDataException(String message, Object... args) {
        return () -> new WrongDataException(message, args);
    }

    public static Supplier<WrongDataException> wrongDataException(String message) {
        return () -> new WrongDataException(message);
    }
}