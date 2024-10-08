package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

import static ru.practicum.shareit.util.HeaderConstant.USER_ID_HEADER;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public RequestDto createItemRequest(@RequestBody NewItemRequestDto request,
                                        @RequestHeader(USER_ID_HEADER) long userId) {
        return itemRequestService.createItemRequest(request, userId);
    }

    @GetMapping
    public List<RequestDto> getOwnItemRequests(@RequestHeader(USER_ID_HEADER) long userId) {
        return itemRequestService.getOwnItemRequests(userId);
    }

    @GetMapping("/all")
    public List<RequestDto> getAllItemRequests() {
        return itemRequestService.getAllRequests();
    }

    @GetMapping("/{requestId}")
    public RequestDto getRequestInfo(@PathVariable long requestId) {
        return itemRequestService.getRequestInfo(requestId);
    }


}
