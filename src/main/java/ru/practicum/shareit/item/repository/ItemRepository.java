package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    Item save(Item item);

    Optional<Item> find(long itemId);

    List<Item> findAll();

    List<Item> findUserAllItem(long userId);

    List<Long> findOwners();

    Item update(Item updatedItem);

    void delete(long userId, long itemId);

}
