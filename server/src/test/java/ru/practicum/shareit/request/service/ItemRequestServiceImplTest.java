package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.model.DataNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.utility.MyPageRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @Mock
    ItemRequestRepository requestRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ItemRepository itemRepository;

    @InjectMocks
    ItemRequestServiceImpl requestService;

    ItemRequest request = new ItemRequest(
            1L,
            "request description",
            User.builder().id(1L).build(),
            LocalDateTime.now().withSecond(0).withNano(0)
    );

    ItemRequestDto requestDto = new ItemRequestDto(
            1L,
            "request description",
            1L,
            LocalDateTime.now().withSecond(0).withNano(0),
            Collections.singletonList(ItemDto.builder().id(1L).build())
    );

    User requestor = new User().builder().id(1L).build();

    @Test
    void testCreateRequestOk() {
        ItemRequestDto initialRequestDto = new ItemRequestDto(
                1L,
                "request description",
                1L,
                LocalDateTime.now().withSecond(0).withNano(0),
                new ArrayList<>()
        );

        when(userRepository.findById(requestor.getId()))
                .thenReturn(Optional.ofNullable(requestor));

        when(requestRepository.save(any()))
                .thenReturn(request);

        ItemRequestDto actualRequestDto = requestService.create(requestor.getId(), initialRequestDto);

        assertNotNull(actualRequestDto);
        assertEquals(initialRequestDto, actualRequestDto);
        verify(userRepository, times(1)).findById(anyLong());
        verify(requestRepository, times(1)).save(any());
    }

    @Test
    void testCreateRequestRequestorNotFound() {
        when(userRepository.findById(requestor.getId()))
                .thenReturn(Optional.empty());

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> requestService.create(requestor.getId(), requestDto));

        assertEquals("User Id=1", exception.getMessage());
        verify(requestRepository, never()).save(any());
    }

    @Test
    void testGetRequestOk() {
        when(userRepository.findById(requestor.getId()))
                .thenReturn(Optional.ofNullable(requestor));

        when(requestRepository.findById(request.getId()))
                .thenReturn(Optional.ofNullable(request));

        when(itemRepository.findAllByRequestIdOrderById(1L))
                .thenReturn(Collections.singletonList(Item.builder().id(1L).build()));

        ItemRequestDto actualRequestDto = requestService.getRequest(requestor.getId(), requestDto.getId());

        assertNotNull(actualRequestDto);
        assertEquals(requestDto, actualRequestDto);
        verify(userRepository, times(1)).findById(anyLong());
        verify(requestRepository, times(1)).findById(anyLong());
    }

    @Test
    void testGetRequestRequestorNotFound() {
        when(userRepository.findById(requestor.getId()))
                .thenReturn(Optional.empty());

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> requestService.getRequest(requestor.getId(), request.getId()));

        assertEquals("User Id=1", exception.getMessage());
        verify(requestRepository, never()).findById(anyLong());
    }

    @Test
    void testGetRequestNotFound() {
        when(userRepository.findById(requestor.getId()))
                .thenReturn(Optional.ofNullable(requestor));

        when(requestRepository.findById(request.getId()))
                .thenReturn(Optional.empty());

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> requestService.getRequest(requestor.getId(), request.getId()));

        assertEquals("Request Id=1", exception.getMessage());
        verify(itemRepository, never()).findAllByRequestIdOrderById(anyLong());
    }

    @Test
    void testGetUserRequestsOk() {
        Page<ItemRequest> requests = new PageImpl<>(Collections.singletonList(request));
        List<ItemRequestDto> requestDtos = Collections.singletonList(requestDto);

        when(userRepository.findById(requestor.getId()))
                .thenReturn(Optional.ofNullable(requestor));

        when(requestRepository.findAllByRequestorId(requestor.getId(),
                new MyPageRequest(0, 10, Sort.by("id"))))
                .thenReturn(requests);

        when(itemRepository.findAllByRequestIdOrderById(1L))
                .thenReturn(Collections.singletonList(Item.builder().id(1L).build()));

        List<ItemRequestDto> actualRequestDtos = requestService.getUserRequests(requestor.getId(), 0, 10);

        assertNotNull(actualRequestDtos);
        assertEquals(requestDtos, actualRequestDtos);
        verify(userRepository, times(1)).findById(anyLong());
        verify(requestRepository, times(1)).findAllByRequestorId(anyLong(), any());
    }

    @Test
    void testGetUserRequestsUserNotFound() {
        when(userRepository.findById(requestor.getId()))
                .thenReturn(Optional.empty());

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> requestService.getUserRequests(requestor.getId(), 0, 10));

        assertEquals("User Id=1", exception.getMessage());
        verify(requestRepository, never()).findById(anyLong());
        verify(requestRepository, never()).findAllByRequestorId(anyLong(), any());
    }

    @Test
    void testGetAllRequestsOk() {
        Page<ItemRequest> requests = new PageImpl<>(Collections.singletonList(request));
        List<ItemRequestDto> requestDtos = Collections.singletonList(requestDto);

        when(userRepository.findById(requestor.getId()))
                .thenReturn(Optional.ofNullable(requestor));

        when(requestRepository.findAllByRequestorIdNot(requestor.getId(),
                new MyPageRequest(0, 10, Sort.by("created"))))
                .thenReturn(requests);

        when(itemRepository.findAllByRequestIdOrderById(1L))
                .thenReturn(Collections.singletonList(Item.builder().id(1L).build()));

        List<ItemRequestDto> actualRequestDtos = requestService.getAllRequests(requestor.getId(), 0, 10);

        assertNotNull(actualRequestDtos);
        assertEquals(requestDtos, actualRequestDtos);
        verify(userRepository, times(1)).findById(anyLong());
        verify(requestRepository, times(1)).findAllByRequestorIdNot(anyLong(), any());
    }

    @Test
    void testGetAllRequestsUserNotFound() {
        when(userRepository.findById(requestor.getId()))
                .thenReturn(Optional.empty());

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> requestService.getAllRequests(requestor.getId(), 0, 10));

        assertEquals("User Id=1", exception.getMessage());
        verify(requestRepository, never()).findById(anyLong());
        verify(requestRepository, never()).findAllByRequestorIdNot(anyLong(), any());
    }
}