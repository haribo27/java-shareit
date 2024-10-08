package ru.practicum.shareit.gateway.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.shareit.gateway.item.dto.NewCommentRequestDto;
import ru.practicum.shareit.gateway.item.dto.NewItemRequestDto;
import ru.practicum.shareit.gateway.item.dto.UpdateItemRequestDto;

import static ru.practicum.shareit.gateway.util.HeaderConstant.USER_ID_HEADER;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@Valid @RequestBody NewItemRequestDto requestDto,
                                             @RequestHeader(USER_ID_HEADER) long userId) {
        return itemClient.createItem(requestDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@Valid @RequestBody NewCommentRequestDto request,
                                                @PathVariable long itemId,
                                                @RequestHeader(USER_ID_HEADER) long userId) {
        return itemClient.createComment(request, itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@Valid @RequestBody UpdateItemRequestDto requestDto,
                                             @PathVariable long itemId,
                                             @RequestHeader(USER_ID_HEADER) long itemOwnerId) {
        return itemClient.updateItem(requestDto, itemOwnerId, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable long itemId,
                                          @RequestHeader(USER_ID_HEADER) long ownerItemId) {
        return itemClient.getItem(itemId, ownerItemId);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnersItems(@RequestHeader(USER_ID_HEADER) long userId) {
        return itemClient.getOwnersItems(userId);
    }

/*    @GetMapping("/search")
    public ResponseEntity<Object> searchItemsByText(@RequestParam(name = "text") String text) {
        return itemClient.searchItemsByText(text);
    }*/
}
