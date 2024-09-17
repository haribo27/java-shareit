package ru.practicum.shareit.exception;

public class NotEnoughRightsToChangeBooking extends RuntimeException {
    public NotEnoughRightsToChangeBooking(String message) {
        super(message);
    }
}
