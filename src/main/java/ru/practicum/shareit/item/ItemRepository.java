package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerId(long ownerId);

    @Query("SELECT i FROM Item i " +
            "WHERE (LOWER(i.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(i.description) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND i.available = true")
    List<Item> searchAvailableItemsByNameOrDescription(@Param("query") String query);

    @Query("select it " +
            "from Item it " +
            "JOIN FETCH it.owner " +
            "where it.id = ?1")
    Optional<Item> findByIdWithUser(long itemId);
}
