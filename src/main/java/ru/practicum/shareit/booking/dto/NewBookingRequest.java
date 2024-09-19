package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Data
public class NewBookingRequest {

    private Long id;
    @NotNull
    @FutureOrPresent(message = "Дата начала должна быть либо в настоящем времени, либо в будущем")
    private LocalDateTime start;
    @NotNull
    @Future(message = "Дата окончания должна быть в будущем времени")
    private LocalDateTime end;
    private BookingStatus status;
    @Positive
    @NotNull
    private Long itemId;
}
