package ru.practicum.shareit.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.shareit.booking.dto.NewBookingRequest;

import java.time.LocalDateTime;

public class CheckDateValidator implements ConstraintValidator<StartBeforeEndDateValid, NewBookingRequest> {

    @Override
    public void initialize(StartBeforeEndDateValid constraintAnnotation) {
    }

    @Override
    public boolean isValid(NewBookingRequest bookingRequest, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime start = bookingRequest.getStart();
        LocalDateTime end = bookingRequest.getEnd();
        if (start == null || end == null) {
            return false;
        }
        return start.isBefore(end);
    }
}