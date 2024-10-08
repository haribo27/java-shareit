package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.dto.NewCommentRequestDto;
import ru.practicum.shareit.item.dto.NewItemRequestDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;

import java.util.List;

public interface ItemService {

    ItemDto createItem(NewItemRequestDto requestDto, long userId);

    ItemDto updateItem(UpdateItemRequestDto requestDto, long userId, long itemId);

    ItemOwnerDto getItem(long userId, long ownerItemId);

    List<ItemOwnerDto> getOwnersItems(long userId);

    List<ItemDto> searchItemsByText(String text);

    CommentDto createComment(NewCommentRequestDto request, long itemId, long userId);
}
