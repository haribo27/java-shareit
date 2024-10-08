package ru.practicum.shareit.exception;

import lombok.Data;

import java.time.Instant;

@Data
public class ErrorResponse {

    private String status;
    private String errorMessage;
    private Instant timestamp;

    public ErrorResponse(String status, String errorMessage) {
        this.status = status;
        this.timestamp = Instant.now();
        this.errorMessage = errorMessage;
    }
}
