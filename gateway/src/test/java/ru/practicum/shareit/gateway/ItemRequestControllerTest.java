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
import ru.practicum.shareit.gateway.request.ItemRequestController;
import ru.practicum.shareit.gateway.request.NewRequestDto;
import ru.practicum.shareit.gateway.request.RequestClient;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.gateway.util.HeaderConstant.USER_ID_HEADER;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RequestClient requestClient;

    @Autowired
    private ObjectMapper objectMapper;

    private NewRequestDto validNewRequestDto;

    @BeforeEach
    void setUp() {
        validNewRequestDto = new NewRequestDto();
        validNewRequestDto.setDescription("Request for an item");
    }

    @Test
    void createItemRequest_ValidRequest() throws Exception {
        Mockito.when(requestClient.createItemRequest(any(NewRequestDto.class), anyLong()))
                .thenReturn(ResponseEntity.ok().body("Request created"));

        mockMvc.perform(post("/requests")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validNewRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Request created"));
    }

    @Test
    void getAllItemRequests_ValidRequest() throws Exception {
        Mockito.when(requestClient.getAllRequests())
                .thenReturn(ResponseEntity.ok().body("All item requests"));

        mockMvc.perform(get("/requests/all")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("All item requests"));
    }
}
