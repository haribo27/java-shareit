package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;

import java.util.List;

import static ru.practicum.shareit.util.HeaderConstant.USER_ID_HEADER;

@RestController
@RequestMapping(path = "/bookings")
@Validated
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@Valid @RequestBody NewBookingRequest bookingRequest,
                                    @RequestHeader(USER_ID_HEADER) long userId) {
        return bookingService.createBooking(bookingRequest, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approvedBooking(@PathVariable @Positive long bookingId,
                                      @RequestParam("approved") boolean isApproved,
                                      @RequestHeader(USER_ID_HEADER) long userId) {
        return bookingService.approveBooking(userId, bookingId, isApproved);
    }

    @GetMapping("{bookingId}")
    public BookingDto getBooking(@PathVariable @Positive long bookingId,
                                 @RequestHeader(USER_ID_HEADER) long userId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getBookingCurrentUser(@RequestParam(value = ("state"), required = false,
            defaultValue = "ALL") SearchBookingStates state,
                                                  @RequestHeader(USER_ID_HEADER) long userId) {
        return bookingService.getBookingsOfUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnersBookings(@RequestParam(value = ("state"), required = false,
            defaultValue = "ALL") SearchBookingStates state,
                                              @RequestHeader(USER_ID_HEADER) long userId) {
        return bookingService.getOwnersBookings(userId, state);
    }
}
