package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.NotEnoughRightsToChangeData;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewCommentRequestDto;
import ru.practicum.shareit.item.dto.NewItemRequestDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User user;
    private Item item;
    private Booking booking;
    private ItemRequest itemRequest;
    private NewItemRequestDto newItemRequestDto;
    private NewCommentRequestDto newCommentRequestDto;
    private Comment comment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Инициализация тестовых данных
        user = new User();
        user.setId(1L);
        user.setName("Test User");

        item = new Item();
        item.setId(1L);
        item.setAvailable(true);
        item.setOwner(user);

        booking = new Booking();
        booking.setId(1L);
        booking.setEnd(LocalDateTime.now().minusDays(1));

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);

        newItemRequestDto = new NewItemRequestDto();
        newItemRequestDto.setName("Test Item");
        newItemRequestDto.setDescription("Description of Test Item");
        newItemRequestDto.setAvailable(true);

        newCommentRequestDto = new NewCommentRequestDto();
        newCommentRequestDto.setText("Great item!");

        comment = new Comment();
        comment.setId(1L);
        comment.setText("Great item!");
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
    }

    @Test
    void createItem_Success() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemMapper.toItem(any(NewItemRequestDto.class))).thenReturn(item);
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(itemMapper.toItemDto(any(Item.class))).thenReturn(new ItemDto());

        ItemDto result = itemService.createItem(newItemRequestDto, user.getId());

        assertNotNull(result);
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void createItem_RequestExists_Success() {
        newItemRequestDto.setRequestId(itemRequest.getId());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemMapper.toItem(any(NewItemRequestDto.class))).thenReturn(item);
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(itemMapper.toItemDto(any(Item.class))).thenReturn(new ItemDto());

        ItemDto result = itemService.createItem(newItemRequestDto, user.getId());

        assertNotNull(result);
        assertEquals(itemRequest, item.getItemRequest());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void createItem_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> itemService.createItem(newItemRequestDto, user.getId()));

        assertEquals("User not found", exception.getMessage());
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void createComment_Success() {
        when(itemRepository.findByIdWithUser(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingService.isUserHadBookingOfItem(anyLong(), anyLong())).thenReturn(Optional.of(booking));
        when(commentMapper.mapToComment(any(NewCommentRequestDto.class))).thenReturn(comment);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.mapToCommentDto(any(Comment.class))).thenReturn(new CommentDto());

        CommentDto result = itemService.createComment(newCommentRequestDto, item.getId(), user.getId());

        assertNotNull(result);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void createComment_ItemNotFound() {
        when(itemRepository.findByIdWithUser(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> itemService.createComment(newCommentRequestDto, item.getId(), user.getId()));

        assertEquals("Item not found", exception.getMessage());
    }

    @Test
    void createComment_UserHasNoBooking() {
        when(itemRepository.findByIdWithUser(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingService.isUserHadBookingOfItem(anyLong(), anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> itemService.createComment(newCommentRequestDto, item.getId(), user.getId()));

        assertEquals("User doesn't have this booking", exception.getMessage());
    }

    @Test
    void createComment_BookingIsNotFinished() {
        booking.setEnd(LocalDateTime.now().plusDays(1));
        when(itemRepository.findByIdWithUser(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingService.isUserHadBookingOfItem(anyLong(), anyLong())).thenReturn(Optional.of(booking));

        NotEnoughRightsToChangeData exception = assertThrows(NotEnoughRightsToChangeData.class,
                () -> itemService.createComment(newCommentRequestDto, item.getId(), user.getId()));

        assertEquals("Booking must be past to create comment", exception.getMessage());
    }

    @Test
    void updateItem_Success() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        doNothing().when(itemMapper).updateItem(any(UpdateItemRequestDto.class), any(Item.class));
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(itemMapper.toItemDto(any(Item.class))).thenReturn(new ItemDto());

        ItemDto result = itemService.updateItem(new UpdateItemRequestDto(), user.getId(), item.getId());

        assertNotNull(result);
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void updateItem_ItemNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> itemService.updateItem(new UpdateItemRequestDto(), user.getId(), item.getId()));

        assertEquals("Item not found", exception.getMessage());
        verify(itemRepository, never()).save(any(Item.class));
    }
}
