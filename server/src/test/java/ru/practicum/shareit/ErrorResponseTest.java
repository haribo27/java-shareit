package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.ErrorResponse;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorResponseTest {

    @Test
    void constructor_ShouldInitializeFields() {
        // Arrange
        String expectedStatus = "404 Not Found";
        String expectedErrorMessage = "Entity not found";

        // Act
        ErrorResponse errorResponse = new ErrorResponse(expectedStatus, expectedErrorMessage);

        // Assert
        assertThat(errorResponse.getStatus()).isEqualTo(expectedStatus);
        assertThat(errorResponse.getErrorMessage()).isEqualTo(expectedErrorMessage);
        assertThat(errorResponse.getTimestamp()).isNotNull();  // Проверяем, что timestamp не null
    }

    @Test
    void timestamp_ShouldBeCurrentTime() {
        // Arrange
        String status = "400 Bad Request";
        String errorMessage = "Invalid input";
        ErrorResponse errorResponse = new ErrorResponse(status, errorMessage);

        // Act
        Instant timestamp = errorResponse.getTimestamp();

        // Calculate the acceptable range
        Instant now = Instant.now();
        Instant startRange = now.minus(Duration.ofSeconds(1));  // 1 second before now
        Instant endRange = now.plus(Duration.ofSeconds(1));      // 1 second after now

        // Assert
        assertThat(timestamp).isBetween(startRange, endRange); // Check if timestamp is within 1 second of the current time
    }
}
