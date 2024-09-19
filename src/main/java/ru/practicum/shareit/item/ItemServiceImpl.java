package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.IncorrectArgumentException;
import ru.practicum.shareit.exception.NotEnoughRightsToChangeData;
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
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.SearchBookingStates.ALL;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingService bookingService;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public ItemDto createItem(NewItemRequestDto requestDto, long userId) {
        log.info("Creating new Item with owner ID: {}", userId);
        Item item = itemMapper.toItem(requestDto);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        item.setOwner(user);
        item = itemRepository.save(item);
        log.info("Created new Item {}", item);
        return itemMapper.toItemDto(item);
    }

    @Override
    @Transactional
    public CommentDto createComment(NewCommentRequestDto request, long itemId, long userId) {
        Item item = itemRepository.findByIdWithUser(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Booking booking = bookingService.isUserHadBookingOfItem(userId, itemId)
                .orElseThrow(() -> new EntityNotFoundException("User doesn't have this booking"));
        if (booking.getEnd().isAfter(LocalDateTime.now())) {
            throw new NotEnoughRightsToChangeData("Booking must be past to create comment");
        }
        Comment comment = commentMapper.mapToComment(request);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        comment = commentRepository.save(comment);
        return commentMapper.mapToCommentDto(comment);
    }

    @Override
    @Transactional
    public ItemDto updateItem(UpdateItemRequestDto requestDto, long itemOwnerId, long itemId) {
        log.info("Updating Item ID: {}, Owner ID: {}", itemId, itemOwnerId);
        Item updatedItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));
        log.info("Check owner is correct");
        isOwnerOfItemCorrect(updatedItem.getOwner().getId(), itemOwnerId);
        log.info("Owner is CORRECT!");
        itemMapper.updateItem(requestDto, updatedItem);
        updatedItem.setId(itemId);
        itemRepository.save(updatedItem);
        log.info("Updated item {}", updatedItem);
        return itemMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemOwnerDto getItem(long itemId, long ownerItemId) {
        log.info("Getting item {} info", itemId);
        Item item = itemRepository.findByIdWithUser(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item Not Found"));
        List<BookingDto> hisBookings = bookingService.getOwnersBookings(ownerItemId, ALL);
        BookingDates bookingDates = getBookingDates(hisBookings);
        ItemOwnerDto itemDto = itemMapper.toItemOwnerDto(item);
        itemDto.setComments(commentRepository.findByItemId(itemId)
                .stream()
                .map(commentMapper::mapToCommentDto)
                .collect(Collectors.toSet()));
        itemDto.setLastBooking(bookingDates.lastBooking());
        itemDto.setNextBooking(bookingDates.nextBooking());

        return itemDto;
    }

    @Override
    public List<ItemOwnerDto> getOwnersItems(long userId) {
        log.info("Getting owners items");
        List<BookingDto> hisBookings = bookingService.getOwnersBookings(userId, ALL);
        BookingDates bookingDates = getBookingDates(hisBookings);

        return itemRepository.findAllByOwnerId(userId)
                .stream()
                .map(itemMapper::toItemOwnerDto)
                .peek(itemDto -> {
                    itemDto.setLastBooking(bookingDates.lastBooking());
                    itemDto.setNextBooking(bookingDates.nextBooking());
                })
                .toList();
    }

    @Override
    public List<ItemDto> searchItemsByText(String text) {
        if (text.isBlank()) {
            log.info("Return empty result of search with blank query");
            return Collections.emptyList();
        }
        log.info("Searching item with query - {}", text);
        return itemRepository.searchAvailableItemsByNameOrDescription(text.toLowerCase())
                .stream()
                .map(itemMapper::toItemDto)
                .toList();
    }

    private void isOwnerOfItemCorrect(long itemIdOwner, long requestUserId) {
        if (!(itemIdOwner == requestUserId)) {
            throw new IncorrectArgumentException("Incorrect owner id");
        }
    }

    private BookingDates getBookingDates(List<BookingDto> bookings) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastBooking = bookings.stream()
                .map(BookingDto::getEnd)
                .filter(end -> end.isBefore(now))
                .max(LocalDateTime::compareTo)
                .orElse(null);
        LocalDateTime nextBooking = bookings.stream()
                .map(BookingDto::getStart)
                .filter(start -> start.isAfter(now))
                .min(LocalDateTime::compareTo)
                .orElse(null);
        return new BookingDates(lastBooking, nextBooking);
    }

    private record BookingDates(LocalDateTime lastBooking, LocalDateTime nextBooking) { }
}
