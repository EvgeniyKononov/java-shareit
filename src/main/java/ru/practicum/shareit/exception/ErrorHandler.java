package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice("ru.practicum.shareit")
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> handleValidationException(final AuthOwnerException e) {
        return Map.of("error", e.getMessage());
    }
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleDuplicateException(final DuplicateUserException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleNullPointerException(final NullPointerException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundUserException(final NotFoundUserException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleEmptyPointerException(final EmptyPointerException e) {
        return Map.of("error", e.getMessage());
    }
}
