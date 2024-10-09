package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.NewRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

public interface RequestService {

    RequestDto createItemRequest(NewRequestDto requestDto, long userId);

    List<RequestDto> getOwnItemRequests(long userId);

    List<RequestDto> getAllRequests();

    RequestDto getRequestInfo(long requestId);
}
