package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;

public class ItemRequestMapper {
    private ItemRequestMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static ItemRequestDtoResponse toRequestDtoResponse(ItemRequest request) {
        if (request == null) {
            return null;
        }
        return new ItemRequestDtoResponse(
                request.getId(),
                request.getDescription(),
                request.getRequestor(),
                request.getCreated(),
                new ArrayList<>()
        );
    }

    public static ItemRequest toRequest(ItemRequestDto requestDto) {
        return new ItemRequest(
                requestDto.getId(),
                requestDto.getDescription(),
                requestDto.getRequestor(),
                requestDto.getCreated()
        );
    }
}
