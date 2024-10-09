package ru.practicum.shareit;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
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
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private CommentMapper commentMapper;

    private User testUser;
    private NewItemRequestDto newItemRequestDto;

    @BeforeEach
    void setUp() {
        // Setting up a test user and initial test data
        testUser = new User();
        testUser.setEmail("testuser@example.com");
        testUser.setName("Test User");
        userRepository.save(testUser);

        newItemRequestDto = new NewItemRequestDto();
        newItemRequestDto.setName("Test Item");
        newItemRequestDto.setDescription("Test Description");
        newItemRequestDto.setAvailable(true);


    }

    @Test
    void createItem_ShouldCreateNewItem() {
        // Act
        Item item = itemMapper.toItem(newItemRequestDto);
        item.setOwner(testUser);
        item.setAvailable(true);
        item.setName(newItemRequestDto.getName());
        item.setDescription(newItemRequestDto.getDescription());
        Item createdItem = itemRepository.save(item);

        // Assert
        assertThat(createdItem).isNotNull();
        assertThat(createdItem.getName()).isEqualTo(newItemRequestDto.getName());
        assertThat(createdItem.getDescription()).isEqualTo(newItemRequestDto.getDescription());
        assertThat(createdItem.getOwner().getId()).isEqualTo(testUser.getId());

        // Verify item is persisted
        Optional<Item> foundItem = itemRepository.findById(createdItem.getId());
        assertThat(foundItem).isPresent();
    }

    @Test
    void updateItem_ShouldUpdateExistingItem() {
        // Create an item first
        ItemDto createdItem = itemService.createItem(newItemRequestDto, testUser.getId());

        // Create an update request
        UpdateItemRequestDto updateItemRequestDto = new UpdateItemRequestDto();
        updateItemRequestDto.setName("Updated Item");
        updateItemRequestDto.setDescription("Updated Description");

        // Act
        ItemDto updatedItem = itemService.updateItem(updateItemRequestDto, testUser.getId(), createdItem.getId());

        // Assert
        assertThat(updatedItem).isNotNull();
        assertThat(updatedItem.getName()).isEqualTo(updateItemRequestDto.getName());
        assertThat(updatedItem.getDescription()).isEqualTo(updateItemRequestDto.getDescription());
    }

    @Test
    void getItem_ShouldReturnItemDetails() {
        // Create an item first
        ItemDto createdItem = itemService.createItem(newItemRequestDto, testUser.getId());

        // Act
        ItemOwnerDto foundItem = itemService.getItem(createdItem.getId(), testUser.getId());

        // Assert
        assertThat(foundItem).isNotNull();
        assertThat(foundItem.getId()).isEqualTo(createdItem.getId());
        assertThat(foundItem.getName()).isEqualTo(createdItem.getName());
    }

    @Test
    void searchItemsByText_ShouldReturnMatchingItems() {
        // Create an item first
        itemService.createItem(newItemRequestDto, testUser.getId());

        // Act
        List<ItemDto> foundItems = itemService.searchItemsByText("Test");

        // Assert
        assertThat(foundItems).isNotEmpty();
        assertThat(foundItems.get(0).getName()).contains("Test");
    }

    @Test
    void createComment_ShouldThrowExceptionIfNoBookingExists() {
        // Create an item first
        ItemDto createdItem = itemService.createItem(newItemRequestDto, testUser.getId());

        // Create comment request
        NewCommentRequestDto newCommentRequestDto = new NewCommentRequestDto();
        newCommentRequestDto.setText("Test Comment");

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            itemService.createComment(newCommentRequestDto, createdItem.getId(), testUser.getId());
        });
    }
}
