package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.util.HeaderConstant.USER_ID_HEADER;

class RequestControllerTest {

    @InjectMocks
    private RequestController requestController;

    @Mock
    private RequestService requestService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(requestController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void createItemRequest_ValidRequest_ShouldReturnCreated() throws Exception {
        NewRequestDto newRequestDto = new NewRequestDto();
        newRequestDto.setDescription("Request description");

        RequestDto requestDto = new RequestDto(); // Создайте экземпляр RequestDto с необходимыми полями

        when(requestService.createItemRequest(any(NewRequestDto.class), anyLong())).thenReturn(requestDto);

        mockMvc.perform(post("/requests")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(requestService, times(1)).createItemRequest(any(NewRequestDto.class), anyLong());
    }

    @Test
    void getOwnItemRequests_ValidRequest_ShouldReturnList() throws Exception {
        RequestDto requestDto = new RequestDto(); // Создайте экземпляр RequestDto с необходимыми полями
        List<RequestDto> requestDtoList = Collections.singletonList(requestDto);

        when(requestService.getOwnItemRequests(anyLong())).thenReturn(requestDtoList);

        mockMvc.perform(get("/requests")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)));

        verify(requestService, times(1)).getOwnItemRequests(anyLong());
    }

    @Test
    void getAllItemRequests_ShouldReturnList() throws Exception {
        RequestDto requestDto = new RequestDto(); // Создайте экземпляр RequestDto с необходимыми полями
        List<RequestDto> requestDtoList = Collections.singletonList(requestDto);

        when(requestService.getAllRequests()).thenReturn(requestDtoList);

        mockMvc.perform(get("/requests/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)));

        verify(requestService, times(1)).getAllRequests();
    }

    @Test
    void getRequestInfo_ValidRequest_ShouldReturnRequest() throws Exception {
        long requestId = 1L;
        RequestDto requestDto = new RequestDto(); // Создайте экземпляр RequestDto с необходимыми полями

        when(requestService.getRequestInfo(requestId)).thenReturn(requestDto);

        mockMvc.perform(get("/requests/{requestId}", requestId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(requestService, times(1)).getRequestInfo(requestId);
    }
}
