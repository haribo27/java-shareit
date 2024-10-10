package ru.practicum.shareit.gateway.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import ru.practicum.shareit.gateway.util.StartBeforeEndDateValid;

import java.time.LocalDateTime;

@Data
@StartBeforeEndDateValid(message = "Start date must be before end date")
public class NewBookingRequest {

    private Long id;
    @NotNull
    @FutureOrPresent(message = "Дата начала должна быть либо в настоящем времени, либо в будущем")
    private LocalDateTime start;
    @NotNull
    @Future(message = "Дата окончания должна быть в будущем времени")
    private LocalDateTime end;
    @Positive
    @NotNull
    private Long itemId;
}
