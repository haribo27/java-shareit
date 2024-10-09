package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingMapperTest {

    private BookingMapper bookingMapper;

    @BeforeEach
    void setUp() {
        bookingMapper = Mappers.getMapper(BookingMapper.class);
    }

    @Test
    void mapToBooking_ShouldMapNewBookingRequestToBooking() {
        // Arrange
        NewBookingRequest request = new NewBookingRequest();
        request.setStart(LocalDateTime.now());
        request.setEnd(LocalDateTime.now().plusDays(2));

        // Act
        Booking booking = bookingMapper.mapToBooking(request);

        // Assert
        assertEquals(request.getStart(), booking.getStart());
        assertEquals(request.getEnd(), booking.getEnd());
    }

    @Test
    void toBookingDto_ShouldMapBookingToBookingDto() {
        // Arrange
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(2));

        // Act
        BookingDto bookingDto = bookingMapper.toBookingDto(booking);

        // Assert
        assertEquals(1L, bookingDto.getId());
        assertEquals(BookingStatus.APPROVED, bookingDto.getStatus());
        assertEquals(booking.getStart(), bookingDto.getStart());
        assertEquals(booking.getEnd(), bookingDto.getEnd());
    }
}
