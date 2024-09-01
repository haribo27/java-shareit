package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.IncorrectArgumentException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequestDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto createItem(NewItemRequestDto requestDto, long userId) {
        log.info("Creating new Item with owner ID: {}", userId);
        checkUserExist(userId);
        Item item = ItemMapper.mapToItem(requestDto);
        item.setOwnerId(userId);
        item = itemRepository.createItem(item);
        log.info("Created new Item {}", item);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemDto updateItem(UpdateItemRequestDto requestDto, long userId, long itemId) {
        log.info("Updating Item ID: {}, Owner ID: {}", itemId, userId);
        checkUserExist(userId);
        Item updatedItem = itemRepository.findItemById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));
        log.info("Check owner is correct");
        checkItemOwner(userId, updatedItem.getOwnerId());
        log.info("Owner is CORRECT!");
        updatedItem = itemRepository.updateItem(ItemMapper.updateItemFields(requestDto, updatedItem));
        log.info("Updated item {}", updatedItem);
        return ItemMapper.mapToItemDto(updatedItem);
    }

    @Override
    public ItemDto getItemInfo(long itemId) {
        log.info("Getting item {} info", itemId);
        return itemRepository.findItemById(itemId)
                .map(ItemMapper::mapToItemDto)
                .orElseThrow(() -> new EntityNotFoundException("Item Not Found"));
    }

    @Override
    public List<ItemDto> getOwnersItems(long userId) {
        log.info("Getting owners items");
        return itemRepository.getOwnerItems(userId)
                .stream().map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> searchItemsByText(String text) {
        if (text == null || text.isBlank()) {
            log.info("Return empty result of search with blank query");
            return new ArrayList<>();
        }
        log.info("Searching item with query - {}", text);
        return itemRepository.searchItemByText(text.toLowerCase())
                .stream().map(ItemMapper::mapToItemDto)
                .toList();
    }

    private void checkUserExist(long userId) {
        log.info("Check is user {} exist", userId);
        userRepository.findUserById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    private void checkItemOwner(long userId, long itemUserId) {
        log.info("Check owner of item with ID: {}", itemUserId);
        if (userId != itemUserId) {
            throw new IncorrectArgumentException("Incorrect owner of this Item");
        }
    }
}
