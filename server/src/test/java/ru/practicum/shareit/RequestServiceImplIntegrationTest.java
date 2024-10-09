package ru.practicum.shareit;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureTestDatabase
public class RequestServiceImplIntegrationTest {

    @Autowired
    private RequestServiceImpl requestService;

    @MockBean
    private RequestRepository requestRepository;

    @MockBean
    private UserService userService;

    @Mock
    private RequestMapper mapper;

    private User user;
    private ItemRequest itemRequest;
    private NewRequestDto newRequestDto;
    private RequestDto requestDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");

        newRequestDto = new NewRequestDto();
        newRequestDto.setDescription("Test description");

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Test description");
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());

        requestDto = new RequestDto();
        requestDto.setId(itemRequest.getId());
        requestDto.setDescription(itemRequest.getDescription());
    }

    @Test
    void getOwnItemRequests_ExistingUser_ShouldReturnRequestList() {
        // Arrange
        when(userService.findById(1L)).thenReturn(user);
        when(requestRepository.findByRequestor_Id(user.getId())).thenReturn(Collections.singletonList(itemRequest));
        when(mapper.mapToItemRequestDto(itemRequest)).thenReturn(requestDto);

        // Act
        List<RequestDto> requests = requestService.getOwnItemRequests(user.getId());

        // Assert
        assertEquals(1, requests.size());
        assertEquals(requestDto.getId(), requests.get(0).getId());
        assertEquals(requestDto.getDescription(), requests.get(0).getDescription()); // Added this line
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
        assertEquals(requestDto.getDescription(), allRequests.get(0).getDescription()); // Added this line
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
        assertEquals(requestDto.getDescription(), foundRequestDto.getDescription()); // Added this line
    }

    @Test
    void getRequestInfo_NonExistingRequest_ShouldThrowEntityNotFoundException() {
        // Arrange
        when(requestRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> requestService.getRequestInfo(1L));
    }
}