package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.model.DataNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final Map<Long, List<Item>> items = new HashMap<>();
    private long generatedId = 0;

    @Override
    public Item save(Item item) {
        item.setId(newId());
        Long ownerId = item.getOwner().getId();
        if (items.get(ownerId) != null) {
            items.get(ownerId).add(item);
        } else {
            items.put(ownerId, new ArrayList<>(Collections.singleton(item)));
        }

        return item;
    }

    @Override
    public Optional<Item> find(long itemId) {
        return items.values().stream()
                .flatMap(Collection::stream)
                .filter(item -> item.getId() == itemId)
                .findFirst();
    }

    @Override
    public List<Item> findAll() {
        return items.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> findUserAllItem(long userId) {
        return items.get(userId);
    }

    @Override
    public Item update(Item updatedItem) {
        Long ownerId = updatedItem.getOwner().getId();
        Long itemId = updatedItem.getId();
        if (!items.containsKey(ownerId)) {
            throw new DataNotFoundException("User id=" + ownerId + " items list not found");
        }
        List<Item> userItems = items.get(ownerId);
        Item existingItem = userItems.stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst().orElseThrow(() -> new DataNotFoundException("Item not found"));
        if (updatedItem.getName() != null) {
            existingItem.setName(updatedItem.getName());
        }
        if (updatedItem.getDescription() != null) {
            existingItem.setDescription(updatedItem.getDescription());
        }
        if (updatedItem.getAvailable() != null) {
            existingItem.setAvailable(updatedItem.getAvailable());
        }

        return existingItem;
    }

    @Override
    public void delete(long userId, long itemId) {
        items.get(userId).remove(items.get(userId).stream()
                .filter(item -> item.getOwner().getId().equals(itemId))
                .findFirst());
    }

    private Long newId() {
        return ++generatedId;
    }

}
