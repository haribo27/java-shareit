package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingService {

    BookingDto createBooking(NewBookingRequest bookingRequest, long userId);

    BookingDto approveBooking(long userId, long bookingId, boolean isApproved);

    BookingDto getBooking(long userId, long bookingId);

    List<BookingDto> getBookingsOfUser(long userId, SearchBookingStates state);

    List<BookingDto> getOwnersBookings(long userId, SearchBookingStates state);

    Optional<Booking> isUserHadBookingOfItem(long userId, long itemId);
}
