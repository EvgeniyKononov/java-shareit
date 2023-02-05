package ru.practicum.shareit.exception;

public class DuplicateUserException extends RuntimeException{
    public DuplicateUserException(String message) {
        super(message);
    }
}
