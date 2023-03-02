package ru.practicum.shareit.exception;

public class NotFoundBookingException extends RuntimeException {
    public NotFoundBookingException(String message) {
        super(message);
    }
}
