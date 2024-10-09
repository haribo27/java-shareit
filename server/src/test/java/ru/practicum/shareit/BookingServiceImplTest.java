package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.SearchBookingStates;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingMapper bookingMapper;

    private User user;
    private Item item;
    private Booking booking;
    private NewBookingRequest bookingRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setName("username");
        item = new Item();
        item.setId(1L);
        item.setName("Item Name");
        item.setDescription("Item Description");
        item.setAvailable(true);
        item.setOwner(user);
        bookingRequest = new NewBookingRequest();
        bookingRequest.setId(1L);
        bookingRequest.setStart(LocalDateTime.now());
        bookingRequest.setEnd(LocalDateTime.now().plusDays(1));
        bookingRequest.setItemId(item.getId());
        booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setStatus(BookingStatus.WAITING);
    }

    @Test
    void createBooking_ValidRequest_ShouldReturnBookingDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findByIdWithUser(anyLong())).thenReturn(Optional.of(item));
        when(bookingMapper.mapToBooking(any(NewBookingRequest.class))).thenReturn(booking);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toBookingDto(any(Booking.class))).thenReturn(new BookingDto());

        BookingDto result = bookingService.createBooking(bookingRequest, user.getId());

        verify(userRepository).findById(user.getId());
        verify(itemRepository).findByIdWithUser(bookingRequest.getItemId());
        verify(bookingRepository).save(booking);
        assertThat(result).isNotNull();
    }

    @Test
    void createBooking_ItemNotFound_ShouldThrowEntityNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findByIdWithUser(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.createBooking(bookingRequest, user.getId()));
    }

    @Test
    void createBooking_UserNotFound_ShouldThrowEntityNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.createBooking(bookingRequest, user.getId()));
    }

    @Test
    void createBooking_ItemNotAvailable_ShouldThrowItemIsNotAvailable() {
        item.setAvailable(false);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findByIdWithUser(anyLong())).thenReturn(Optional.of(item));

        assertThrows(ItemIsNotAvailable.class, () -> bookingService.createBooking(bookingRequest, user.getId()));
    }

    @Test
    void approveBooking_UserNotOwner_ShouldThrowNotEnoughRightsToChangeData() {
        when(bookingRepository.findByIdWithUserAndItem(anyLong())).thenReturn(Optional.of(booking));
        User user1 = new User();
        user1.setId(2L);
        user1.setEmail("other@example.com");
        user1.setName("otheruser");
        booking.getItem().setOwner(user1); // другой владелец

        assertThrows(NotEnoughRightsToChangeData.class, () -> bookingService.approveBooking(user.getId(), booking.getId(), true));
    }

    @Test
    void getBooking_NonExistingBooking_ShouldThrowEntityNotFoundException() {
        when(bookingRepository.findByIdWithUserAndItem(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.getBooking(user.getId(), booking.getId()));
    }

    @Test
    void getBooking_UserNotBookerOrOwner_ShouldThrowNotEnoughRightsToChangeData() {
        when(bookingRepository.findByIdWithUserAndItem(anyLong())).thenReturn(Optional.of(booking));
        booking.getBooker().setId(2L); // другой забронировавший

        assertThrows(NotEnoughRightsToChangeData.class, () -> bookingService.getBooking(5, booking.getId()));
    }

    @Test
    void getBooking_ValidRequest_ShouldReturnBookingDto() {
        when(bookingRepository.findByIdWithUserAndItem(anyLong())).thenReturn(Optional.of(booking));
        when(bookingMapper.toBookingDto(any(Booking.class))).thenReturn(new BookingDto());

        BookingDto result = bookingService.getBooking(user.getId(), booking.getId());

        assertThat(result).isNotNull();
    }

    @Test
    void getBookingsOfUser_UserNotFound_ShouldThrowEntityNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.getBookingsOfUser(user.getId(), SearchBookingStates.ALL));
    }


    @Test
    void getOwnersBookings_UserNotFound_ShouldThrowEntityNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.getOwnersBookings(user.getId(), SearchBookingStates.ALL));
    }
}
