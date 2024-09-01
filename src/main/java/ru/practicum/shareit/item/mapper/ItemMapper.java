package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequestDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {

    public static Item mapToItem(NewItemRequestDto requestDto) {
        Item item = new Item();
        item.setName(requestDto.getName());
        item.setAvailable(requestDto.getAvailable());
        item.setDescription(requestDto.getDescription());
        return item;
    }

    public static ItemDto mapToItemDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setAvailable(item.getAvailable());
        dto.setDescription(item.getDescription());
        return dto;
    }

    public static Item updateItemFields(UpdateItemRequestDto requestDto, Item item) {
        if (requestDto.getAvailable() != null) {
            item.setAvailable(requestDto.getAvailable());
        }
        if (requestDto.getName() != null) {
            item.setName(requestDto.getName());
        }
        if (requestDto.getDescription() != null) {
            item.setDescription(requestDto.getDescription());
        }
        return item;
    }
}
