package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequestDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;

import java.util.List;

public interface ItemService {

    ItemDto createItem(NewItemRequestDto requestDto, long userId);

    ItemDto updateItem(UpdateItemRequestDto requestDto, long userId, long itemId);

    ItemDto getItemInfo(long itemId);

    List<ItemDto> getOwnersItems(long userId);

    List<ItemDto> searchItemsByText(String text);
}
