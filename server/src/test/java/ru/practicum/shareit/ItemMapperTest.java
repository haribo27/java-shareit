package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.dto.NewItemRequestDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

class ItemMapperTest {

    private ItemMapper itemMapper;

    @BeforeEach
    void setUp() {
        itemMapper = Mappers.getMapper(ItemMapper.class);
    }

    @Test
    void toItem_ShouldMapNewItemRequestDtoToItem() {
        // Arrange
        NewItemRequestDto requestDto = new NewItemRequestDto();
        requestDto.setName("Item Name");
        requestDto.setDescription("Item Description");
        requestDto.setAvailable(true);

        // Act
        Item item = itemMapper.toItem(requestDto);

        // Assert
        assertThat(item).isNotNull();
        assertThat(item.getName()).isEqualTo("Item Name");
        assertThat(item.getDescription()).isEqualTo("Item Description");
        assertThat(item.getAvailable()).isTrue();
    }

    @Test
    void toItemDto_ShouldMapItemToItemDto() {
        // Arrange
        Item item = new Item();
        item.setId(1L);
        item.setName("Item Name");
        item.setDescription("Item Description");
        item.setAvailable(true);

        // Act
        ItemDto itemDto = itemMapper.toItemDto(item);

        // Assert
        assertThat(itemDto).isNotNull();
        assertThat(itemDto.getId()).isEqualTo(1L);
        assertThat(itemDto.getName()).isEqualTo("Item Name");
        assertThat(itemDto.getDescription()).isEqualTo("Item Description");
    }

    @Test
    void toItemOwnerDto_ShouldMapItemToItemOwnerDto() {
        // Arrange
        Item item = new Item();
        item.setId(1L);
        item.setName("Item Name");
        item.setDescription("Item Description");
        item.setAvailable(true);
        // Assuming the owner field is set as well
        User owner = new User();
        owner.setId(2L);
        owner.setName("Owner Name");
        item.setOwner(owner);

        // Act
        ItemOwnerDto itemOwnerDto = itemMapper.toItemOwnerDto(item);

        // Assert
        assertThat(itemOwnerDto).isNotNull();
        assertThat(itemOwnerDto.getId()).isEqualTo(1L);
        assertThat(itemOwnerDto.getName()).isEqualTo("Item Name");
        assertThat(itemOwnerDto.getDescription()).isEqualTo("Item Description");
    }

    @Test
    void updateItem_ShouldUpdateItemFields() {
        // Arrange
        Item item = new Item();
        item.setName("Old Name");
        item.setDescription("Old Description");
        item.setAvailable(false);

        UpdateItemRequestDto requestDto = new UpdateItemRequestDto();
        requestDto.setName("New Name");
        // Not setting description to check if it remains unchanged
        requestDto.setAvailable(true);

        // Act
        itemMapper.updateItem(requestDto, item);

        // Assert
        assertThat(item.getName()).isEqualTo("New Name");
        assertThat(item.getDescription()).isEqualTo("Old Description");
        assertThat(item.getAvailable()).isTrue();
    }
}
