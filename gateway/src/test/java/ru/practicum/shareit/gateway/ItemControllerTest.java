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
import ru.practicum.shareit.gateway.item.ItemClient;
import ru.practicum.shareit.gateway.item.ItemController;
import ru.practicum.shareit.gateway.item.dto.NewItemRequestDto;
import ru.practicum.shareit.gateway.item.dto.UpdateItemRequestDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.gateway.util.HeaderConstant.USER_ID_HEADER;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemClient itemClient;

    @Autowired
    private ObjectMapper objectMapper;

    private NewItemRequestDto validNewItemRequestDto;
    private UpdateItemRequestDto validUpdateItemRequestDto;

    @BeforeEach
    void setUp() {
        validNewItemRequestDto = new NewItemRequestDto();
        validNewItemRequestDto.setName("Test Item");
        validNewItemRequestDto.setDescription("Description for Test Item");
        validNewItemRequestDto.setAvailable(true);
        validNewItemRequestDto.setRequestId(1L);

        validUpdateItemRequestDto = new UpdateItemRequestDto();
        validUpdateItemRequestDto.setName("Updated Test Item");
        validUpdateItemRequestDto.setDescription("Updated description for Test Item");
        validUpdateItemRequestDto.setAvailable(false);
    }

    @Test
    void createItem_ValidRequest() throws Exception {
        Mockito.when(itemClient.createItem(any(NewItemRequestDto.class), anyLong()))
                .thenReturn(ResponseEntity.ok().body("Item created"));

        mockMvc.perform(post("/items")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validNewItemRequestDto)))
                .andExpect(status().isOk());
    }

    @Test
    void createItem_InvalidRequest() throws Exception {
        validNewItemRequestDto.setName("");

        mockMvc.perform(post("/items")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validNewItemRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateItem_ValidRequest() throws Exception {
        Mockito.when(itemClient.updateItem(any(UpdateItemRequestDto.class), anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok().body("Item updated"));

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUpdateItemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Item updated"));
    }

    @Test
    void updateItem_InvalidRequest() throws Exception {
        validUpdateItemRequestDto.setDescription("A".repeat(2001));

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUpdateItemRequestDto)))
                .andExpect(status().isBadRequest());

    }

    @Test
    void getItem_ValidRequest() throws Exception {
        Mockito.when(itemClient.getItem(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok().body("Item details"));

        mockMvc.perform(get("/items/{itemId}", 1L)
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Item details"));
    }
}
