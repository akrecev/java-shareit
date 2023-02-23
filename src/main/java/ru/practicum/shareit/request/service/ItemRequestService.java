package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(Long userId, ItemRequestDto requestDto);

    ItemRequestDto getRequest(Long userId, Long requestId);

    List<ItemRequestDto> getUserRequests(Long userId, int from, int size);

    List<ItemRequestDto> getAllRequests(Long userId, int from, int size);
}
