package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.SearchBookingStates;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.util.HeaderConstant;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;  // Для сериализации/десериализации JSON

    @MockBean  // Изменяем это на @MockBean
    private BookingService bookingService;  // Это будет мок для BookingService

    private BookingDto bookingDto;
    private NewBookingRequest newBookingRequest;

    @BeforeEach
    void setUp() {
        // Инициализация DTO
        bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStatus(BookingStatus.APPROVED);

        newBookingRequest = new NewBookingRequest();
        newBookingRequest.setItemId(1L);
        newBookingRequest.setStart(LocalDateTime.now());
        newBookingRequest.setEnd(LocalDateTime.now().plusDays(2));
    }

    @Test
    void createBooking_ShouldReturnCreatedBooking() throws Exception {
        Mockito.when(bookingService.createBooking(any(NewBookingRequest.class), anyLong()))
                .thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBookingRequest))
                        .header(HeaderConstant.USER_ID_HEADER, 1L))
                .andExpect(status().isOk())  // Убедитесь, что статус - 200 (OK)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void approveBooking_ShouldReturnUpdatedBooking() throws Exception {
        bookingDto.setStatus(BookingStatus.APPROVED);
        Mockito.when(bookingService.approveBooking(anyLong(), anyLong(), eq(true)))
                .thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/1")
                        .param("approved", "true")
                        .header(HeaderConstant.USER_ID_HEADER, 1L))
                .andExpect(status().isOk())  // Убедитесь, что статус - 200 (OK)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getBooking_ShouldReturnBooking() throws Exception {
        Mockito.when(bookingService.getBooking(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/1")
                        .header(HeaderConstant.USER_ID_HEADER, 1L))
                .andExpect(status().isOk())  // Убедитесь, что статус - 200 (OK)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getBookingCurrentUser_ShouldReturnListOfBookings() throws Exception {
        Mockito.when(bookingService.getBookingsOfUser(anyLong(), any(SearchBookingStates.class)))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .header(HeaderConstant.USER_ID_HEADER, 1L))
                .andExpect(status().isOk())  // Убедитесь, что статус - 200 (OK)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(bookingDto.getId()))
                .andExpect(jsonPath("$[0].status").value("APPROVED"));
    }

    @Test
    void getOwnersBookings_ShouldReturnListOfBookings() throws Exception {
        Mockito.when(bookingService.getOwnersBookings(anyLong(), any(SearchBookingStates.class)))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .header(HeaderConstant.USER_ID_HEADER, 1L))
                .andExpect(status().isOk())  // Убедитесь, что статус - 200 (OK)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(bookingDto.getId()))
                .andExpect(jsonPath("$[0].status").value("APPROVED"));
    }
}
