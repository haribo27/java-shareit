package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.request.RequestServiceImpl;
import ru.practicum.shareit.request.dto.NewRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RequestServiceTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private UserService userService;

    @Mock
    private RequestMapper mapper;

    @InjectMocks
    private RequestServiceImpl requestService;

    private User user;
    private ItemRequest itemRequest;
    private NewRequestDto newRequestDto;
    private RequestDto requestDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Инициализация тестовых данных
        user = new User();
        user.setId(1L);
        user.setName("Test User");

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Test request");
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());

        newRequestDto = new NewRequestDto();
        newRequestDto.setDescription("Test request");

        requestDto = new RequestDto();
        requestDto.setId(1L);
        requestDto.setDescription("Test request");
    }

    @Test
    void createItemRequest_Success() {
        when(userService.findById(anyLong())).thenReturn(user);
        when(mapper.mapToItemRequest(any(NewRequestDto.class))).thenReturn(itemRequest);
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);
        when(mapper.mapToItemRequestDto(any(ItemRequest.class))).thenReturn(requestDto);

        RequestDto result = requestService.createItemRequest(newRequestDto, user.getId());

        assertNotNull(result);
        assertEquals(requestDto.getId(), result.getId());
        verify(requestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void createItemRequest_UserNotFound() {
        when(userService.findById(anyLong())).thenThrow(new EntityNotFoundException("User not found"));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> requestService.createItemRequest(newRequestDto, user.getId()));

        assertEquals("User not found", exception.getMessage());
        verify(requestRepository, never()).save(any(ItemRequest.class));
    }

    @Test
    void getOwnItemRequests_Success() {
        when(requestRepository.findByRequestor_Id(anyLong())).thenReturn(List.of(itemRequest));
        when(mapper.mapToItemRequestDto(any(ItemRequest.class))).thenReturn(requestDto);

        List<RequestDto> result = requestService.getOwnItemRequests(user.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(requestDto.getId(), result.get(0).getId());
        verify(requestRepository, times(1)).findByRequestor_Id(anyLong());
    }

    @Test
    void getOwnItemRequests_EmptyList() {
        when(requestRepository.findByRequestor_Id(anyLong())).thenReturn(Collections.emptyList());

        List<RequestDto> result = requestService.getOwnItemRequests(user.getId());

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(requestRepository, times(1)).findByRequestor_Id(anyLong());
    }

    @Test
    void getAllRequests_Success() {
        when(requestRepository.findAllOrderByDate()).thenReturn(List.of(itemRequest));
        when(mapper.mapToItemRequestDto(any(ItemRequest.class))).thenReturn(requestDto);

        List<RequestDto> result = requestService.getAllRequests();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(requestDto.getId(), result.get(0).getId());
        verify(requestRepository, times(1)).findAllOrderByDate();
    }

    @Test
    void getAllRequests_EmptyList() {
        when(requestRepository.findAllOrderByDate()).thenReturn(Collections.emptyList());

        List<RequestDto> result = requestService.getAllRequests();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(requestRepository, times(1)).findAllOrderByDate();
    }
}
