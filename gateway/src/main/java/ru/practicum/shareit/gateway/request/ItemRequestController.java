package ru.practicum.shareit.gateway.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.shareit.gateway.item.dto.NewItemRequestDto;

import static ru.practicum.shareit.gateway.util.HeaderConstant.USER_ID_HEADER;


@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestBody NewItemRequestDto request,
                                                    @RequestHeader(USER_ID_HEADER) long userId) {
        return requestClient.createItemRequest(request, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnItemRequests(@RequestHeader(USER_ID_HEADER) long userId) {
        return requestClient.getOwnItemRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests() {
        return requestClient.getAllRequests();
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestInfo(@PathVariable long requestId) {
        return requestClient.getRequestInfo(requestId);
    }
}
