package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.BadRequestException;
import ru.practicum.shareit.exception.model.DataNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.utility.ItemMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        throwMissingUserInRequest(userId);
        User owner = userService.find(userId);
        Item savedItem = itemRepository.save(ItemMapper.toItem(itemDto, owner));

        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto get(Long itemId) {
        return ItemMapper.toItemDto(find(itemId));
    }

    @Override
    public List<ItemDto> getUserItems(Long userId) {
        throwMissingUserInRequest(userId);
        return itemRepository.findUserAllItem(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getSearchItems(String searchText) {
        List<ItemDto> result = new ArrayList<>();
        if (!searchText.isBlank()) {
            result = itemRepository.findAll().stream()
                    .filter(Item::getAvailable)
                    .filter(item -> item.getName().toLowerCase().contains(searchText.toLowerCase())
                            || item.getDescription().toLowerCase().contains(searchText.toLowerCase()))
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        }
        return result;
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        throwMissingUserInRequest(userId);
        if (!itemRepository.findOwners().contains(userId)) {
            throw new DataNotFoundException("User id=" + userId + " items list not found");
        }
        User owner = userService.find(userId);
        find(itemId);
        Item updatedItem = ItemMapper.toItem(itemDto, owner);
        updatedItem.setId(itemId);
        updatedItem = itemRepository.update(updatedItem);

        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public void delete(Long userId, Long itemId) {
        throwMissingUserInRequest(userId);
        itemRepository.delete(userId, itemId);
    }

    @Override
    public Item find(Long id) {
        return itemRepository.find(id).orElseThrow(() -> new DataNotFoundException("itemId:" + id));
    }

    private void throwMissingUserInRequest(Long userId) {
        if (userId == null) {
            throw new BadRequestException("Missing user id in request");
        }
    }
}
