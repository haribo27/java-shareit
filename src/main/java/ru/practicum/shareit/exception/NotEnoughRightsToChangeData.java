package ru.practicum.shareit.exception;

public class NotEnoughRightsToChangeData extends RuntimeException {
    public NotEnoughRightsToChangeData(String message) {
        super(message);
    }
}
