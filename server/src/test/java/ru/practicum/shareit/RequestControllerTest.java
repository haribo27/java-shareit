package ru.practicum.shareit;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.request.RequestController;
import ru.practicum.shareit.request.RequestService;
import ru.practicum.shareit.request.dto.NewRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.util.HeaderConstant.USER_ID_HEADER;

class RequestControllerTest {

    @InjectMocks
    private RequestController requestController;

    @Mock
    private RequestService requestService;

    private MockMvc mockMvc;

    private NewRequestDto newRequestDto;
    private RequestDto requestDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(requestController).build();

        // Prepare test data
        newRequestDto = new NewRequestDto();
        newRequestDto.setDescription("Test request");

        requestDto = new RequestDto();
        requestDto.setId(1L);
        requestDto.setDescription("Test request");
    }

    @Test
    void createRequest_ShouldReturnCreatedRequestDto() throws Exception {
        // Arrange
        when(requestService.createItemRequest(any(NewRequestDto.class), any(Long.class))).thenReturn(requestDto);

        // Act & Assert
        mockMvc.perform(post("/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\": \"Test request\"}")
                .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Test request"));
    }

    @Test
    void getOwnItemRequests_ShouldReturnListOfRequestDto() throws Exception {
        // Arrange
        List<RequestDto> requests = Arrays.asList(requestDto);
        when(requestService.getOwnItemRequests(any(Long.class))).thenReturn(requests);

        // Act & Assert
        mockMvc.perform(get("/requests")
                .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Test request"));
    }

    @Test
    void getAllRequests_ShouldReturnListOfRequestDto() throws Exception {
        // Arrange
        List<RequestDto> requests = Arrays.asList(requestDto);
        when(requestService.getAllRequests()).thenReturn(requests);

        // Act & Assert
        mockMvc.perform(get("/requests/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Test request"));
    }

    @Test
    void getRequestInfo_ShouldReturnRequestDto() throws Exception {
        // Arrange
        when(requestService.getRequestInfo(1L)).thenReturn(requestDto);

        // Act & Assert
        mockMvc.perform(get("/requests/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Test request"));
    }
}
