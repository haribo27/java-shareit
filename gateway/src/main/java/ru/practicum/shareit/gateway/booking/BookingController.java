package ru.practicum.shareit.gateway.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.gateway.booking.dto.NewBookingRequest;
import ru.practicum.shareit.gateway.booking.dto.SearchBookingStates;

import static ru.practicum.shareit.gateway.util.HeaderConstant.USER_ID_HEADER;


@Controller
@RequestMapping(path = "/bookings")
@Validated
@RequiredArgsConstructor
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@Valid @RequestBody NewBookingRequest bookingRequest,
                                                @RequestHeader(USER_ID_HEADER) long userId) {
        return bookingClient.createBooking(bookingRequest, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approvedBooking(@PathVariable @Positive long bookingId,
                                                  @RequestParam("approved") Boolean isApproved,
                                                  @RequestHeader(USER_ID_HEADER) long userId) {
        return bookingClient.approveBooking(userId, bookingId, isApproved);
    }

    @GetMapping("{bookingId}")
    public ResponseEntity<Object> getBooking(@PathVariable @Positive long bookingId,
                                             @RequestHeader(USER_ID_HEADER) long userId) {
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingCurrentUser(@RequestParam(value = ("state"),
            defaultValue = "ALL") SearchBookingStates state,
                                                        @RequestHeader(USER_ID_HEADER) long userId) {
        return bookingClient.getBookingsOfUser(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnersBookings(@RequestParam(value = ("state"),
            defaultValue = "ALL") SearchBookingStates state,
                                                    @RequestHeader(USER_ID_HEADER) long userId) {
        return bookingClient.getOwnersBookings(userId, state);
    }
}
