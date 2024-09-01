package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();
    private long currentId;

    @Override
    public Item createItem(Item item) {
        item.setId(currentId++);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> findItemById(long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public List<Item> getOwnerItems(long userId) {
        return new ArrayList<>(items.values()
                .stream()
                .filter(item -> item.getOwnerId() == userId)
                .toList());
    }

    @Override
    public List<Item> searchItemByText(String text) {
        return items.values().stream().filter(
                item -> (item.getName().toLowerCase().contains(text) ||
                        item.getDescription().toLowerCase().contains(text)) &&
                        item.getAvailable().equals(true)
        ).toList();
    }
}
