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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class RequestServiceImplTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private UserService userService;

    @Mock
    private RequestMapper mapper;

    @InjectMocks
    private RequestServiceImpl requestService;

    private NewRequestDto newRequestDto;
    private RequestDto requestDto;
    private ItemRequest itemRequest;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize test data
        newRequestDto = new NewRequestDto();
        newRequestDto.setDescription("Test description");

        requestDto = new RequestDto();
        requestDto.setDescription("Test description");
        requestDto.setId(1L);

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Test description");

        user = new User();
        user.setId(1L);
        user.setName("Test User");
    }

    @Test
    void createItemRequest_ValidRequest_ShouldReturnRequestDto() {
        // Arrange
        when(mapper.mapToItemRequest(newRequestDto)).thenReturn(itemRequest);
        when(userService.findById(1L)).thenReturn(user);
        when(requestRepository.save(itemRequest)).thenReturn(itemRequest);
        when(mapper.mapToItemRequestDto(itemRequest)).thenReturn(requestDto);

        // Act
        RequestDto createdRequestDto = requestService.createItemRequest(newRequestDto, 1L);

        // Assert
        assertNotNull(createdRequestDto);
        assertEquals(requestDto.getId(), createdRequestDto.getId());
        assertEquals(requestDto.getDescription(), createdRequestDto.getDescription());
        assertEquals(requestDto.getId(), createdRequestDto.getId());
        verify(requestRepository).save(itemRequest);
    }

    @Test
    void getOwnItemRequests_ExistingUser_ShouldReturnRequestList() {
        // Arrange
        when(userService.findById(1L)).thenReturn(user);
        when(requestRepository.findByRequestor_Id(1L)).thenReturn(Collections.singletonList(itemRequest));
        when(mapper.mapToItemRequestDto(itemRequest)).thenReturn(requestDto);

        // Act
        List<RequestDto> requests = requestService.getOwnItemRequests(1L);

        // Assert
        assertEquals(1, requests.size());
        assertEquals(requestDto.getId(), requests.get(0).getId());
    }

    @Test
    void getAllRequests_ShouldReturnAllRequests() {
        // Arrange
        when(requestRepository.findAllOrderByDate()).thenReturn(Collections.singletonList(itemRequest));
        when(mapper.mapToItemRequestDto(itemRequest)).thenReturn(requestDto);

        // Act
        List<RequestDto> allRequests = requestService.getAllRequests();

        // Assert
        assertEquals(1, allRequests.size());
        assertEquals(requestDto.getId(), allRequests.get(0).getId());
    }

    @Test
    void getRequestInfo_ExistingRequest_ShouldReturnRequestDto() {
        // Arrange
        when(requestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));
        when(mapper.mapToItemRequestDto(itemRequest)).thenReturn(requestDto);

        // Act
        RequestDto foundRequestDto = requestService.getRequestInfo(1L);

        // Assert
        assertNotNull(foundRequestDto);
        assertEquals(requestDto.getId(), foundRequestDto.getId());
    }

    @Test
    void getRequestInfo_NonExistingRequest_ShouldThrowEntityNotFoundException() {
        // Arrange
        when(requestRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> requestService.getRequestInfo(1L));
    }
}
