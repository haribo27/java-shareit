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
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.dto.NewCommentRequestDto;
import ru.practicum.shareit.item.dto.NewItemRequestDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;
import ru.practicum.shareit.util.HeaderConstant;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;  // Для преобразования объектов в JSON

    @MockBean
    private ItemService itemService;

    private NewItemRequestDto newItemRequestDto;
    private UpdateItemRequestDto updateItemRequestDto;
    private ItemDto itemDto;
    private ItemOwnerDto itemOwnerDto;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        // Инициализация newItemRequestDto с использованием сеттеров
        newItemRequestDto = new NewItemRequestDto();
        newItemRequestDto.setName("Item name");
        newItemRequestDto.setDescription("Item description");
        newItemRequestDto.setAvailable(true);
        newItemRequestDto.setRequestId(1L);  // Если requestId нужен, иначе уберите эту строку

        // Инициализация updateItemRequestDto с использованием сеттеров
        updateItemRequestDto = new UpdateItemRequestDto();
        updateItemRequestDto.setName("Updated name");
        updateItemRequestDto.setDescription("Updated description");
        updateItemRequestDto.setAvailable(true);

        // Инициализация itemDto с использованием сеттеров
        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Item name");
        itemDto.setDescription("Item description");
        itemDto.setAvailable(true);

        // Инициализация itemOwnerDto с использованием сеттеров
        itemOwnerDto = new ItemOwnerDto();
        itemOwnerDto.setId(1L);
        itemOwnerDto.setName("Item name");
        itemOwnerDto.setDescription("Item description");
        itemOwnerDto.setAvailable(true);

        // Инициализация commentDto с использованием сеттеров
        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Great item");
        commentDto.setAuthorName("User");
    }

    @Test
    void createItem_ShouldReturnCreatedItem() throws Exception {
        Mockito.when(itemService.createItem(any(NewItemRequestDto.class), anyLong())).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header(HeaderConstant.USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()));
    }

    @Test
    void updateItem_ShouldReturnUpdatedItem() throws Exception {
        Mockito.when(itemService.updateItem(any(UpdateItemRequestDto.class), anyLong(), anyLong())).thenReturn(itemDto);

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .header(HeaderConstant.USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateItemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()));
    }

    @Test
    void getItem_ShouldReturnItemWithOwnerDetails() throws Exception {
        Mockito.when(itemService.getItem(anyLong(), anyLong())).thenReturn(itemOwnerDto);

        mockMvc.perform(get("/items/{itemId}", 1L)
                        .header(HeaderConstant.USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemOwnerDto.getId()))
                .andExpect(jsonPath("$.name").value(itemOwnerDto.getName()))
                .andExpect(jsonPath("$.description").value(itemOwnerDto.getDescription()));
    }

    @Test
    void createComment_ShouldReturnCreatedComment() throws Exception {
        Mockito.when(itemService.createComment(any(NewCommentRequestDto.class), anyLong(), anyLong())).thenReturn(commentDto);

        NewCommentRequestDto newCommentRequestDto = new NewCommentRequestDto();
        newCommentRequestDto.setText("Great item");

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header(HeaderConstant.USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCommentRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentDto.getId()))
                .andExpect(jsonPath("$.text").value(commentDto.getText()))
                .andExpect(jsonPath("$.authorName").value(commentDto.getAuthorName()));
    }

    @Test
    void getOwnersItems_ShouldReturnListOfItems() throws Exception {
        Mockito.when(itemService.getOwnersItems(anyLong())).thenReturn(List.of(itemOwnerDto));

        mockMvc.perform(get("/items")
                        .header(HeaderConstant.USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemOwnerDto.getId()))
                .andExpect(jsonPath("$[0].name").value(itemOwnerDto.getName()))
                .andExpect(jsonPath("$[0].description").value(itemOwnerDto.getDescription()));
    }

    @Test
    void searchItemsByText_ShouldReturnItems() throws Exception {
        Mockito.when(itemService.searchItemsByText(any(String.class))).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "item"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$[0].name").value(itemDto.getName()))
                .andExpect(jsonPath("$[0].description").value(itemDto.getDescription()));
    }
}
