package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.dto.NewCommentRequestDto;
import ru.practicum.shareit.item.dto.NewItemRequestDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;

import java.util.List;

import static ru.practicum.shareit.util.HeaderConstant.USER_ID_HEADER;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestBody NewItemRequestDto requestDto,
                              @RequestHeader(USER_ID_HEADER) long userId) {
        return itemService.createItem(requestDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody UpdateItemRequestDto requestDto,
                              @PathVariable long itemId,
                              @RequestHeader(USER_ID_HEADER) long userId) {
        return itemService.updateItem(requestDto, userId,  itemId);
    }

    @GetMapping("/{itemId}")
    public ItemOwnerDto getItem(@PathVariable long itemId,
                                @RequestHeader(USER_ID_HEADER) long ownerItemId) {
        return itemService.getItem(itemId, ownerItemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestBody NewCommentRequestDto request,
                                    @PathVariable long itemId,
                                    @RequestHeader(USER_ID_HEADER) long userId) {
        return itemService.createComment(request, itemId, userId);
    }

    @GetMapping
    public List<ItemOwnerDto> getOwnersItems(@RequestHeader(USER_ID_HEADER) long userId) {
        return itemService.getOwnersItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemsByText(@RequestParam(name = "text") String text) {
        return itemService.searchItemsByText(text);
    }
}