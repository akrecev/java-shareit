package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;


public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findAllByOwnerIdOrderById(Long ownerId, Pageable pageable);

    List<Item> findAllByRequestIdOrderById(Long requestId);

    @Query(" SELECT i FROM Item i " +
            "WHERE UPPER(i.name) LIKE UPPER(CONCAT('%', :text, '%') ) " +
            " OR UPPER(i.description) LIKE UPPER(CONCAT('%', :text, '%'))")
    Page<Item> search(String text, Pageable pageable);

}
