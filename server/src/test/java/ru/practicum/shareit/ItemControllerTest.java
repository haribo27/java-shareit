package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.dto.NewCommentRequestDto;
import ru.practicum.shareit.item.dto.NewItemRequestDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.util.HeaderConstant.USER_ID_HEADER;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    private NewItemRequestDto newItemRequestDto;
    private UpdateItemRequestDto updateItemRequestDto;
    private NewCommentRequestDto newCommentRequestDto;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        newItemRequestDto = new NewItemRequestDto();
        newItemRequestDto.setName("Item Name");
        newItemRequestDto.setDescription("Item Description");
        newItemRequestDto.setAvailable(true);

        updateItemRequestDto = new UpdateItemRequestDto();
        updateItemRequestDto.setName("Updated Item Name");
        updateItemRequestDto.setDescription("Updated Description");
        updateItemRequestDto.setAvailable(false);

        newCommentRequestDto = new NewCommentRequestDto();
        newCommentRequestDto.setText("This is a comment.");

        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Item Name");
        itemDto.setDescription("Item Description");
        itemDto.setAvailable(true);
    }

    @Test
    void createItem_ValidRequest_ShouldReturnOk() throws Exception {
        when(itemService.createItem(any(NewItemRequestDto.class), anyLong())).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItemRequestDto)))
                .andExpect(status().isOk());

        verify(itemService, times(1)).createItem(any(NewItemRequestDto.class), anyLong());
    }

    @Test
    void updateItem_ValidRequest_ShouldReturnOk() throws Exception {
        when(itemService.updateItem(any(UpdateItemRequestDto.class), anyLong(), anyLong())).thenReturn(itemDto);

        mockMvc.perform(patch("/items/1")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateItemRequestDto)))
                .andExpect(status().isOk());

        verify(itemService, times(1)).updateItem(any(UpdateItemRequestDto.class), anyLong(), anyLong());
    }

    @Test
    void createComment_ValidRequest_ShouldReturnOk() throws Exception {
        CommentDto commentDto = new CommentDto(); // Предполагаем, что есть CommentDto
        when(itemService.createComment(any(NewCommentRequestDto.class), anyLong(), anyLong())).thenReturn(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCommentRequestDto)))
                .andExpect(status().isOk());

        verify(itemService, times(1)).createComment(any(NewCommentRequestDto.class), anyLong(), anyLong());
    }

    @Test
    void getOwnersItems_ValidRequest_ShouldReturnOk() throws Exception {
        List<ItemOwnerDto> itemOwnerDtos = Arrays.asList(new ItemOwnerDto());
        when(itemService.getOwnersItems(anyLong())).thenReturn(itemOwnerDtos);

        mockMvc.perform(get("/items")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk());

        verify(itemService, times(1)).getOwnersItems(anyLong());
    }

    @Test
    void searchItemsByText_ValidRequest_ShouldReturnOk() throws Exception {
        List<ItemDto> itemDtos = Arrays.asList(itemDto);
        when(itemService.searchItemsByText(any(String.class))).thenReturn(itemDtos);

        mockMvc.perform(get("/items/search?text=Item")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemService, times(1)).searchItemsByText(any(String.class));
    }
}
