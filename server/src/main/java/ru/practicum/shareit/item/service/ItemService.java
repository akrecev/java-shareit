package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;

import java.util.List;

public interface ItemService {
    ItemDto create(Long userId, ItemDto itemDto);

    CommentDtoResponse commentCreate(Long userId, Long itemId, CommentDto commentDto);

    ItemDtoResponse get(Long userId, Long itemId);

    List<ItemDto> getSearchItems(String searchText, Integer from, Integer size);

    List<ItemDtoResponse> getUserItems(Long userId, Integer from, Integer size);

    ItemDto update(Long userId, Long itemId, ItemDto itemDto);

    void delete(Long userId, Long itemId);

}
