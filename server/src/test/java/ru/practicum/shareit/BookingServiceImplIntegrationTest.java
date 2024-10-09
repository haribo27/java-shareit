package ru.practicum.shareit;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.SearchBookingStates;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private User user;
    private Item item;

    @BeforeEach
    void setUp() {
        // Создание и сохранение пользователя и предмета для тестирования
        user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        userRepository.save(user);

        item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);
    }

    @Test
    void createBooking_ShouldCreateNewBooking() {
        NewBookingRequest bookingRequest = new NewBookingRequest();
        bookingRequest.setItemId(item.getId());
        bookingRequest.setStart(LocalDateTime.now().plusDays(1));
        bookingRequest.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto createdBooking = bookingService.createBooking(bookingRequest, user.getId());

        assertThat(createdBooking).isNotNull();
        assertThat(createdBooking.getId()).isNotNull();
        assertThat(createdBooking.getItem().getId()).isEqualTo(item.getId());
        assertThat(createdBooking.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void approveBooking_ShouldUpdateBookingStatus() {
        NewBookingRequest bookingRequest = new NewBookingRequest();
        bookingRequest.setItemId(item.getId());
        bookingRequest.setStart(LocalDateTime.now().plusDays(1));
        bookingRequest.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto createdBooking = bookingService.createBooking(bookingRequest, user.getId());

        // Обновляем статус бронирования
        BookingDto approvedBooking = bookingService.approveBooking(user.getId(), createdBooking.getId(), true);

        assertThat(approvedBooking).isNotNull();
        assertThat(approvedBooking.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void getBooking_ShouldReturnBooking() {
        NewBookingRequest bookingRequest = new NewBookingRequest();
        bookingRequest.setItemId(item.getId());
        bookingRequest.setStart(LocalDateTime.now().plusDays(1));
        bookingRequest.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto createdBooking = bookingService.createBooking(bookingRequest, user.getId());

        BookingDto retrievedBooking = bookingService.getBooking(user.getId(), createdBooking.getId());

        assertThat(retrievedBooking).isNotNull();
        assertThat(retrievedBooking.getId()).isEqualTo(createdBooking.getId());
    }

    @Test
    void getBookingsOfUser_ShouldReturnBookings() {
        NewBookingRequest bookingRequest = new NewBookingRequest();
        bookingRequest.setItemId(item.getId());
        bookingRequest.setStart(LocalDateTime.now().plusDays(1));
        bookingRequest.setEnd(LocalDateTime.now().plusDays(2));

        bookingService.createBooking(bookingRequest, user.getId());

        List<BookingDto> bookings = bookingService.getBookingsOfUser(user.getId(), SearchBookingStates.ALL);

        assertThat(bookings).isNotEmpty();
        assertThat(bookings.size()).isGreaterThan(0);
    }
}
