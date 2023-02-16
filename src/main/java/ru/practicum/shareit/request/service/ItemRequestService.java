package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDtoResponse create(Long userId, ItemRequestDto requestDto);

    ItemRequestDtoResponse getRequest(Long userId, Long requestId);

    List<ItemRequestDtoResponse> getUserRequests(Long userId, int from, int size);

    List<ItemRequestDtoResponse> getAllRequests(Long userId, int from, int size);
}
