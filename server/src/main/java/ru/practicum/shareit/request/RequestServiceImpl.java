package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.request.dto.NewRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserService userService;
    private final RequestMapper mapper;

    @Override
    @Transactional
    public RequestDto createItemRequest(NewRequestDto requestDto, long userId) {
        log.info("Creating new item request userId: {}", userId);
        ItemRequest itemRequest = mapper.mapToItemRequest(requestDto);
        User requester = userService.findById(userId);
        itemRequest.setRequestor(requester);
        log.info("Set requestor to item request");
        itemRequest.setCreated(LocalDateTime.now());
        log.info("Set date to request");
        itemRequest = requestRepository.save(itemRequest);
        return mapper.mapToItemRequestDto(itemRequest);
    }

    @Override
    public List<RequestDto> getOwnItemRequests(long userId) {
        log.info("Getting user own item requests");
        return requestRepository.findByRequestor_Id(userId)
                .stream()
                .map(mapper::mapToItemRequestDto).toList();
    }

    @Override
    public List<RequestDto> getAllRequests() {
        log.info("Getting all item requests");
        return requestRepository.findAllOrderByDate()
                .stream()
                .map(mapper::mapToItemRequestDto).toList();
    }

    @Override
    public RequestDto getRequestInfo(long requestId) {
        log.info("Getting info about item request");
        return requestRepository.findById(requestId)
                .map(mapper::mapToItemRequestDto)
                .orElseThrow(() -> new EntityNotFoundException("Item request not found"));
    }
}
