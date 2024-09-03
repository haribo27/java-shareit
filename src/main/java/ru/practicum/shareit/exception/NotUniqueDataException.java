package ru.practicum.shareit.exception;

public class NotUniqueDataException extends RuntimeException {
    public NotUniqueDataException(String message) {
        super(message);
    }
}
