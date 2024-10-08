package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;


public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    @Query("select distinct ir " +
            "from ItemRequest ir " +
            "left join fetch ir.requestor " +
            "left join fetch ir.items i " +
            "left join fetch i.owner " +
            "where ir.requestor.id = ?1 " +
            "order by ir.created asc ")
    List<ItemRequest> findByRequestor_Id(long requestorId);

    @Query("select ir " +
            "from ItemRequest ir " +
            "order by ir.created asc ")
    List<ItemRequest> findAllOrderByDate();

}
