package ru.practicum.shareit.booking;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.QBooking;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ItemIsNotAvailable;
import ru.practicum.shareit.exception.NotEnoughRightsToChangeData;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static ru.practicum.shareit.booking.model.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.model.BookingStatus.REJECTED;
import static ru.practicum.shareit.booking.model.BookingStatus.WAITING;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingDto createBooking(NewBookingRequest bookingRequest, long userId) {
        log.info("User {}, creating new booking {}", userId, bookingRequest);
        Booking booking = bookingMapper.mapToBooking(bookingRequest);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Item item = itemRepository.findByIdWithUser(bookingRequest.getItemId())
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));
        if (!item.getAvailable()) {
            log.info("Item is not available to booking");
            throw new ItemIsNotAvailable("Item is not available");
        }
        log.info("Set booker to booking {}", user);
        booking.setBooker(user);
        log.info("Set item to booking {}", item);
        booking.setItem(item);
        log.info("Set status: WAITING to booking");
        booking.setStatus(WAITING);
        booking = bookingRepository.save(booking);
        log.info("Booking saved success {}", booking);
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDto approveBooking(long userId, long bookingId, boolean isApproved) {
        log.info("User: {} wants to approve Booking: {}", userId, bookingId);
        Booking booking = bookingRepository.findByIdWithUserAndItem(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));
        if (booking.getItem().getOwner().getId() != userId) {
            log.info("Booking: {} has another item owner.", booking);
            throw new NotEnoughRightsToChangeData("Wrong booking owner");
        }
        log.info("Set status to booking {}", isApproved);
        booking.setStatus(isApproved ? APPROVED : REJECTED);
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto getBooking(long userId, long bookingId) {
        log.info("Owner of item or booking id: {} getting booking id: {}", userId, bookingId);
        Booking booking = bookingRepository.findByIdWithUserAndItem(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));
        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            log.info("User with id: {} dont have rights to see booking", userId);
            throw new NotEnoughRightsToChangeData("You must be owner of booking or item");
        }
        log.info("Getting booking {}", booking);
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getBookingsOfUser(long userId, SearchBookingStates state) {
        log.info("User id: {}, trying to get own bookings. State: {}", userId, state);
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        BooleanExpression byUserId = QBooking.booking.booker.id.eq(userId);
        BooleanExpression byState = getStateExpression(state);
        Iterable<Booking> foundItems = bookingRepository.findAll(
                byUserId.and(byState),
                Sort.by(Sort.Direction.DESC, "start"));
        log.info("Getting User: {} bookings, sort by date", userId);
        return StreamSupport.stream(foundItems.spliterator(), false)
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getOwnersBookings(long userId, SearchBookingStates state) {
        log.info("User id: {}, trying to get his own item's bookings. State: {}", userId, state);
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        BooleanExpression byUserId = QBooking.booking.item.owner.id.eq(userId);
        BooleanExpression byState = getStateExpression(state);
        Iterable<Booking> foundItems = bookingRepository.findAll(
                byUserId.and(byState),
                Sort.by(Sort.Direction.DESC, "start"));
        log.info("Getting User: {} own item's bookings, sort by date", userId);
        return StreamSupport.stream(foundItems.spliterator(), false)
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private BooleanExpression getStateExpression(SearchBookingStates state) {
        return switch (state) {
            case ALL -> null;
            case CURRENT -> QBooking.booking.end.after(LocalDateTime.now());
            case PAST -> QBooking.booking.end.before(LocalDateTime.now());
            case FUTURE -> QBooking.booking.start.after(LocalDateTime.now());
            case WAITING -> QBooking.booking.status.eq(WAITING);
            case REJECTED -> QBooking.booking.status.eq(REJECTED);
        };
    }

    public Optional<Booking> isUserHadBookingOfItem(long userId, long itemId) {
        return bookingRepository.findByBooker_IdAndItem_Id(userId, itemId);
    }
}
