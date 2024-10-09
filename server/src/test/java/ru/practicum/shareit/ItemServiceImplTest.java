package ru.practicum.shareit;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.IncorrectArgumentException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.dto.NewCommentRequestDto;
import ru.practicum.shareit.item.dto.NewItemRequestDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ItemServiceImplTest {

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingService bookingService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private CommentMapper commentMapper;

    private User user;
    private Item item;
    private NewItemRequestDto newItemRequestDto;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setName("Test User");

        item = new Item();
        item.setId(1L);
        item.setOwner(user);
        item.setName("Test Item");

        newItemRequestDto = new NewItemRequestDto();
        newItemRequestDto.setName("Test Item");
        newItemRequestDto.setDescription("Item description");
        newItemRequestDto.setAvailable(true);

        itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
    }

    @Test
    void createItem_UserNotFound_ShouldThrowEntityNotFoundException() {
        // Arrange
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            itemService.createItem(newItemRequestDto, user.getId());
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void createComment_ValidRequest_ShouldReturnCommentDto() {
        // Arrange
        NewCommentRequestDto commentRequest = new NewCommentRequestDto();
        commentRequest.setText("Test comment");

        Booking booking = new Booking();
        booking.setEnd(LocalDateTime.now().minusDays(1));

        when(itemRepository.findByIdWithUser(item.getId())).thenReturn(Optional.of(item));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingService.isUserHadBookingOfItem(user.getId(), item.getId())).thenReturn(Optional.of(booking));
        when(commentMapper.mapToComment(commentRequest)).thenReturn(new Comment());

        // Act
        CommentDto commentDto = itemService.createComment(commentRequest, item.getId(), user.getId());

        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void createComment_ItemNotFound_ShouldThrowEntityNotFoundException() {
        // Arrange
        NewCommentRequestDto commentRequest = new NewCommentRequestDto();
        when(itemRepository.findByIdWithUser(item.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            itemService.createComment(commentRequest, item.getId(), user.getId());
        });
    }

    @Test
    void createComment_UserNotFound_ShouldThrowEntityNotFoundException() {
        // Arrange
        NewCommentRequestDto commentRequest = new NewCommentRequestDto();
        when(itemRepository.findByIdWithUser(item.getId())).thenReturn(Optional.of(item));
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            itemService.createComment(commentRequest, item.getId(), user.getId());
        });
    }

    @Test
    void updateItem_ItemNotFound_ShouldThrowEntityNotFoundException() {
        // Arrange
        UpdateItemRequestDto updateItemRequestDto = new UpdateItemRequestDto();
        when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            itemService.updateItem(updateItemRequestDto, user.getId(), item.getId());
        });
    }

    @Test
    void updateItem_NotOwner_ShouldThrowIncorrectArgumentException() {
        // Arrange
        UpdateItemRequestDto updateItemRequestDto = new UpdateItemRequestDto();
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        // Act & Assert
        assertThrows(IncorrectArgumentException.class, () -> {
            itemService.updateItem(updateItemRequestDto, 2L, item.getId());
        });
    }

    @Test
    void getItem_ItemExists_ShouldReturnItemOwnerDto() {
        // Arrange
        when(itemRepository.findByIdWithUser(item.getId())).thenReturn(Optional.of(item));
        when(itemMapper.toItemOwnerDto(item)).thenReturn(new ItemOwnerDto());

        // Act
        ItemOwnerDto itemOwnerDto = itemService.getItem(item.getId(), user.getId());

        // Assert
        assertNotNull(itemOwnerDto);
    }

    @Test
    void getItem_ItemNotFound_ShouldThrowEntityNotFoundException() {
        // Arrange
        when(itemRepository.findByIdWithUser(item.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            itemService.getItem(item.getId(), user.getId());
        });
    }

    @Test
    void searchItemsByText_NonBlankText_ShouldReturnItemDtoList() {
        // Arrange
        when(itemRepository.searchAvailableItemsByNameOrDescription("test")).thenReturn(Collections.singletonList(item));
        when(itemMapper.toItemDto(item)).thenReturn(itemDto);

        // Act
        List<ItemDto> result = itemService.searchItemsByText("test");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void searchItemsByText_BlankText_ShouldReturnEmptyList() {
        // Act
        List<ItemDto> result = itemService.searchItemsByText("   ");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
