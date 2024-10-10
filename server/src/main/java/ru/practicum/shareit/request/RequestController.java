package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.NewRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

import static ru.practicum.shareit.util.HeaderConstant.USER_ID_HEADER;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    public RequestDto createRequest(@RequestBody NewRequestDto request,
                                    @RequestHeader(USER_ID_HEADER) long userId) {
        return requestService.createItemRequest(request, userId);
    }

    @GetMapping
    public List<RequestDto> getOwnItemRequests(@RequestHeader(USER_ID_HEADER) long userId) {
        return requestService.getOwnItemRequests(userId);
    }

    @GetMapping("/all")
    public List<RequestDto> getAllRequests() {
        return requestService.getAllRequests();
    }

    @GetMapping("/{requestId}")
    public RequestDto getRequestInfo(@PathVariable long requestId) {
        return requestService.getRequestInfo(requestId);
    }


}
