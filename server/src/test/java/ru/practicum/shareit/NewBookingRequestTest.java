package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.NewBookingRequest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class NewBookingRequestTest {

    private NewBookingRequest newBookingRequest;

    @BeforeEach
    void setUp() {
        newBookingRequest = new NewBookingRequest();
    }

    @Test
    void testIdSetterAndGetter() {
        newBookingRequest.setId(1L);
        assertThat(newBookingRequest.getId()).isEqualTo(1L);
    }

    @Test
    void testStartSetterAndGetter() {
        LocalDateTime startTime = LocalDateTime.of(2024, 10, 10, 10, 0);
        newBookingRequest.setStart(startTime);
        assertThat(newBookingRequest.getStart()).isEqualTo(startTime);
    }

    @Test
    void testEndSetterAndGetter() {
        LocalDateTime endTime = LocalDateTime.of(2024, 10, 12, 10, 0);
        newBookingRequest.setEnd(endTime);
        assertThat(newBookingRequest.getEnd()).isEqualTo(endTime);
    }

    @Test
    void testItemIdSetterAndGetter() {
        newBookingRequest.setItemId(2L);
        assertThat(newBookingRequest.getItemId()).isEqualTo(2L);
    }

    @Test
    void testBookingRequestCreation() {
        LocalDateTime startTime = LocalDateTime.of(2024, 10, 10, 10, 0);
        LocalDateTime endTime = LocalDateTime.of(2024, 10, 12, 10, 0);
        newBookingRequest = new NewBookingRequest();
        newBookingRequest.setId(1L);
        newBookingRequest.setStart(startTime);
        newBookingRequest.setEnd(endTime);
        newBookingRequest.setItemId(2L);

        assertThat(newBookingRequest.getId()).isEqualTo(1L);
        assertThat(newBookingRequest.getStart()).isEqualTo(startTime);
        assertThat(newBookingRequest.getEnd()).isEqualTo(endTime);
        assertThat(newBookingRequest.getItemId()).isEqualTo(2L);
    }

    @Test
    void testEqualsAndHashCode() {
        NewBookingRequest bookingRequest1 = new NewBookingRequest();
        bookingRequest1.setId(1L);
        bookingRequest1.setStart(LocalDateTime.of(2024, 10, 10, 10, 0));
        bookingRequest1.setEnd(LocalDateTime.of(2024, 10, 12, 10, 0));
        bookingRequest1.setItemId(2L);

        NewBookingRequest bookingRequest2 = new NewBookingRequest();
        bookingRequest2.setId(1L);
        bookingRequest2.setStart(LocalDateTime.of(2024, 10, 10, 10, 0));
        bookingRequest2.setEnd(LocalDateTime.of(2024, 10, 12, 10, 0));
        bookingRequest2.setItemId(2L);

        assertThat(bookingRequest1).isEqualTo(bookingRequest2);
        assertThat(bookingRequest1.hashCode()).isEqualTo(bookingRequest2.hashCode());
    }
}
