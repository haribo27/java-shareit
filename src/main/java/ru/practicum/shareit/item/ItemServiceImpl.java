package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.IncorrectArgumentException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequestDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

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
    public ItemDto updateItem(UpdateItemRequestDto requestDto, long itemOwnerId, long itemId) {
        log.info("Updating Item ID: {}, Owner ID: {}", itemId, itemOwnerId);
        Item updatedItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));
        log.info("Check owner is correct");
        if (updatedItem.getOwner().getId() != itemOwnerId) {
            throw new IncorrectArgumentException("Incorrect owner id");
        }
        log.info("Owner is CORRECT!");
        itemMapper.updateItem(requestDto, updatedItem);
        updatedItem.setId(itemId);
        itemRepository.save(updatedItem);
        log.info("Updated item {}", updatedItem);
        return itemMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemDto getItemInfo(long itemId) {
        log.info("Getting item {} info", itemId);
        return itemRepository.findById(itemId)
                .map(itemMapper::toItemDto)
                .orElseThrow(() -> new EntityNotFoundException("Item Not Found"));
    }

    @Override
    public List<ItemDto> getOwnersItems(long userId) {
        log.info("Getting owners items");
        return itemRepository.findAllByOwnerId(userId)
                .stream()
                .map(itemMapper::toItemDto)
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
}
