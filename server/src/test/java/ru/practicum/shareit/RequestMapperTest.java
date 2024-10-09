package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.NewRequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

class RequestMapperTest {

    private RequestMapper requestMapper;

    @BeforeEach
    void setUp() {
        requestMapper = Mappers.getMapper(RequestMapper.class);
    }

    @Test
    void mapToItemRequest_ShouldMapNewRequestDtoToItemRequest() {
        // Arrange
        NewRequestDto requestDto = new NewRequestDto();
        requestDto.setDescription("Request description");

        // Act
        ItemRequest itemRequest = requestMapper.mapToItemRequest(requestDto);

        // Assert
        assertThat(itemRequest).isNotNull();
        assertThat(itemRequest.getDescription()).isEqualTo("Request description");
    }

    @Test
    void maptoItemRequestDto_ShouldMapItemToItemRequestDto() {
        // Arrange
        User user = new User();
        user.setId(3L);

        Item item = new Item();
        item.setId(2L);
        item.setName("Item Name");
        item.setOwner(user);

        // Act
        ItemRequestDto itemRequestDto = requestMapper.maptoItemRequestDto(item);

        // Assert
        assertThat(itemRequestDto).isNotNull();
        assertThat(itemRequestDto.getOwnerId()).isEqualTo(3L);
        assertThat(itemRequestDto.getId()).isEqualTo(2L);
        assertThat(itemRequestDto.getName()).isEqualTo("Item Name");
    }
}
