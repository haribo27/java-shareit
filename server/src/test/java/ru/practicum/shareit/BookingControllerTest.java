package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BookingControllerTest {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    private MockMvc mockMvc;
    private BookingDto bookingDto;
    private NewBookingRequest newBookingRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();

        // Инициализация тестовых данных
        bookingDto = new BookingDto();
        bookingDto.setId(1L);

        newBookingRequest = new NewBookingRequest();
        newBookingRequest.setItemId(1L);
    }

    @Test
    void createBooking_Success() throws Exception {
        when(bookingService.createBooking(any(NewBookingRequest.class), anyLong())).thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\":1, \"start\":\"2024-10-15T10:00:00\", \"end\":\"2024-10-16T10:00:00\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()));

        verify(bookingService, times(1)).createBooking(any(NewBookingRequest.class), anyLong());
    }

    @Test
    void approveBooking_Success() throws Exception {
        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/1")
                        .header(USER_ID_HEADER, 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()));

        verify(bookingService, times(1)).approveBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void approveBooking_InvalidRequest() throws Exception {
        mockMvc.perform(patch("/bookings/1")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).approveBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void getBooking_Success() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong())).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/1")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()));

        verify(bookingService, times(1)).getBooking(anyLong(), anyLong());
    }

    @Test
    void getBooking_InvalidBookingId() throws Exception {
        mockMvc.perform(get("/bookings/abc")  // Некорректный id бронирования
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).getBooking(anyLong(), anyLong());
    }
}
