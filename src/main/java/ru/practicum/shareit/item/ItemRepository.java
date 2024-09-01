package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item createItem(Item item);

    Optional<Item> findItemById(long itemId);

    Item updateItem(Item updatedItem);

    List<Item> getOwnerItems(long userId);

    List<Item> searchItemByText(String text);
}
