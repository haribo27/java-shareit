package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ItemIsNotAvailable;
import ru.practicum.shareit.exception.NotEnoughRightsToChangeData;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BookingServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User user;
    private Item item;
    private Booking booking;
    private NewBookingRequest newBookingRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Инициализация тестовых данных
        user = new User();
        user.setId(1L);
        user.setName("Test User");

        item = new Item();
        item.setId(1L);
        item.setAvailable(true);
        item.setOwner(user);

        booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        newBookingRequest = new NewBookingRequest();
        newBookingRequest.setItemId(item.getId());
        newBookingRequest.setStart(LocalDateTime.now().plusDays(1));
        newBookingRequest.setEnd(LocalDateTime.now().plusDays(2));
    }

    @Test
    void createBooking_Success() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findByIdWithUser(anyLong())).thenReturn(Optional.of(item));
        when(bookingMapper.mapToBooking(eq(newBookingRequest))).thenReturn(booking);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toBookingDto(any(Booking.class))).thenReturn(new BookingDto());

        BookingDto result = bookingService.createBooking(newBookingRequest, user.getId());

        assertNotNull(result);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void createBooking_ItemNotAvailable() {
        item.setAvailable(false);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findByIdWithUser(anyLong())).thenReturn(Optional.of(item));

        ItemIsNotAvailable exception = assertThrows(ItemIsNotAvailable.class,
                () -> bookingService.createBooking(newBookingRequest, user.getId()));

        assertEquals("Item is not available", exception.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> bookingService.createBooking(newBookingRequest, user.getId()));

        assertEquals("User not found", exception.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void approveBooking_Success() {
        when(bookingRepository.findByIdWithUserAndItem(anyLong())).thenReturn(Optional.of(booking));
        when(bookingMapper.toBookingDto(any(Booking.class))).thenReturn(new BookingDto());

        BookingDto result = bookingService.approveBooking(user.getId(), booking.getId(), true);

        assertNotNull(result);
        assertEquals(BookingStatus.APPROVED, booking.getStatus());
        verify(bookingRepository, times(1)).findByIdWithUserAndItem(anyLong());
    }

    @Test
    void approveBooking_WrongOwner() {
        User anotherUser = new User();
        anotherUser.setId(2L);
        item.setOwner(anotherUser);
        when(bookingRepository.findByIdWithUserAndItem(anyLong())).thenReturn(Optional.of(booking));

        NotEnoughRightsToChangeData exception = assertThrows(NotEnoughRightsToChangeData.class,
                () -> bookingService.approveBooking(user.getId(), booking.getId(), true));

        assertEquals("Wrong booking owner", exception.getMessage());
    }

    @Test
    void getBooking_Success() {
        when(bookingRepository.findByIdWithUserAndItem(anyLong())).thenReturn(Optional.of(booking));
        when(bookingMapper.toBookingDto(any(Booking.class))).thenReturn(new BookingDto());

        BookingDto result = bookingService.getBooking(user.getId(), booking.getId());

        assertNotNull(result);
        verify(bookingRepository, times(1)).findByIdWithUserAndItem(anyLong());
    }

    @Test
    void getBooking_BookingNotFound() {
        when(bookingRepository.findByIdWithUserAndItem(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> bookingService.getBooking(user.getId(), booking.getId()));

        assertEquals("Booking not found", exception.getMessage());
    }
}
