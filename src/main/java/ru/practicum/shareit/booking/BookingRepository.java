package ru.practicum.shareit.booking;

import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.shareit.booking.model.Booking;

import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long>, QuerydslPredicateExecutor<Booking> {

    @Query("select bk " +
            "from Booking bk " +
            "JOIN FETCH bk.booker " +
            "JOIN FETCH bk.item " +
            "JOIN FETCH bk.item.owner " +
            "where bk.id = ?1")
    Optional<Booking> findByIdWithUserAndItem(long bookingId);

    @EntityGraph(attributePaths = {"booker", "item", "item.owner"})
    Iterable<Booking> findAll(Predicate predicate, Sort sort);
}
