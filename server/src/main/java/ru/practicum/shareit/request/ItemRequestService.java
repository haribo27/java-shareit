package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

public interface ItemRequestService {

    RequestDto createItemRequest(NewItemRequestDto requestDto, long userId);

    List<RequestDto> getOwnItemRequests(long userId);

    List<RequestDto> getAllRequests();

    RequestDto getRequestInfo(long requestId);
}
