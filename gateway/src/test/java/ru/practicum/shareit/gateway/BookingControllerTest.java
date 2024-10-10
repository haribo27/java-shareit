package ru.practicum.shareit.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.gateway.booking.BookingClient;
import ru.practicum.shareit.gateway.booking.BookingController;
import ru.practicum.shareit.gateway.booking.dto.NewBookingRequest;
import ru.practicum.shareit.gateway.booking.dto.SearchBookingStates;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.gateway.util.HeaderConstant.USER_ID_HEADER;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingClient bookingClient;

    @Autowired
    private ObjectMapper objectMapper;

    private NewBookingRequest validBookingRequest;
    private NewBookingRequest invalidBookingRequest;

    @BeforeEach
    void setUp() {
        validBookingRequest = new NewBookingRequest();
        validBookingRequest.setItemId(1L);
        validBookingRequest.setStart(LocalDateTime.now().plusDays(1));
        validBookingRequest.setEnd(LocalDateTime.now().plusDays(2));

        invalidBookingRequest = new NewBookingRequest();
        invalidBookingRequest.setItemId(1L);
        invalidBookingRequest.setStart(LocalDateTime.now().plusDays(2));
        invalidBookingRequest.setEnd(LocalDateTime.now().plusDays(1));
    }

    @Test
    void createBookingTest_ValidRequest() throws Exception {
        Mockito.when(bookingClient.createBooking(any(NewBookingRequest.class), anyLong()))
                .thenReturn(ResponseEntity.ok().body("Booking created"));
        mockMvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validBookingRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Booking created"));
    }

    @Test
    void createBookingTest_InvalidRequest() throws Exception {
        mockMvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidBookingRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBookingTest_MissingStartDate() throws Exception {
        validBookingRequest.setStart(null);
        mockMvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validBookingRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void approvedBookingTest() throws Exception {
        Mockito.when(bookingClient.approveBooking(anyLong(), anyLong(), any(Boolean.class)))
                .thenReturn(ResponseEntity.ok().body("Booking approved"));
        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header(USER_ID_HEADER, 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk());
    }

    @Test
    void getBookingTest() throws Exception {
        Mockito.when(bookingClient.getBooking(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok().body("Booking details"));
        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk());

    }

    @Test
    void getBookingCurrentUserTest() throws Exception {
        Mockito.when(bookingClient.getBookingsOfUser(anyLong(), any(SearchBookingStates.class)))
                .thenReturn(ResponseEntity.ok().body("User bookings"));

        mockMvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk());
    }

    @Test
    void getOwnersBookingsTest() throws Exception {
        Mockito.when(bookingClient.getOwnersBookings(anyLong(), any(SearchBookingStates.class)))
                .thenReturn(ResponseEntity.ok().body("Owner bookings"));
        mockMvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk());
    }
}
