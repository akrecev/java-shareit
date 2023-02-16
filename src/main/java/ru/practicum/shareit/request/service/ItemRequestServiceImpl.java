package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.DataNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.utility.MyPageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDtoResponse create(Long userId, ItemRequestDto requestDto) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User:" + userId));

        ItemRequest request = ItemRequestMapper.toRequest(requestDto);
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now());
        ItemRequest savedRequest = requestRepository.save(request);

        return ItemRequestMapper.toRequestDtoResponse(savedRequest);
    }

    @Override
    public ItemRequestDtoResponse getRequest(Long userId, Long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User Id=" + userId));

        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new DataNotFoundException("Request:" + requestId));

        List<ItemDto> itemDtos = itemRepository.findAllByRequestIdOrderById(requestId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        ItemRequestDtoResponse requestDtoResponse = ItemRequestMapper.toRequestDtoResponse(request);
        requestDtoResponse.setItems(itemDtos);

        return requestDtoResponse;
    }

    @Override
    public List<ItemRequestDtoResponse> getUserRequests(Long userId, int from, int size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User Id=" + userId));

        Page<ItemRequest> requests = requestRepository
                .findAllByRequestorId(userId, new MyPageRequest(from, size, Sort.by("id")));

        return requests.stream()
                .map(ItemRequestMapper::toRequestDtoResponse)
                .peek(itemRequestDtoResponse -> itemRequestDtoResponse.setItems(
                        itemRepository.findAllByRequestIdOrderById(itemRequestDtoResponse.getId())
                                .stream()
                                .map(ItemMapper::toItemDto)
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDtoResponse> getAllRequests(Long userId, int from, int size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User Id=" + userId));

        Page<ItemRequest> requests = requestRepository
                .findAllByRequestorIdNot(userId, new MyPageRequest(from, size, Sort.by("created")));

        return requests.stream()
                .map(ItemRequestMapper::toRequestDtoResponse)
                .peek(itemRequestDtoResponse -> itemRequestDtoResponse.setItems(
                        itemRepository.findAllByRequestIdOrderById(itemRequestDtoResponse.getId())
                                .stream()
                                .map(ItemMapper::toItemDto)
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }
}
