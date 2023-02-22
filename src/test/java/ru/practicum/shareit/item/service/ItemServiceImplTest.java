package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.model.BadRequestException;
import ru.practicum.shareit.exception.model.DataNotFoundException;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.utility.MyPageRequest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    ItemRepository itemRepository;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    CommentRepository commentRepository;

    @Mock
    ItemRequestRepository requestRepository;

    @InjectMocks
    ItemServiceImpl itemService;

    @Captor
    ArgumentCaptor<Item> argumentCaptor;

    User owner = new User().builder().id(1L).build();
    User author = new User().builder().id(2L).name("commentator").build();
    ItemRequest request = new ItemRequest().builder().id(1L).build();

    Item item = new Item(
            1L,
            "item name",
            "item description",
            true,
            owner,
            request
    );
    ItemDto itemDto = ItemMapper.toItemDto(item);
    ItemDtoResponse itemDtoResponse = ItemMapper.toItemDtoResponse(item);
    Comment comment = new Comment(
            1L,
            "text comment",
            item,
            author,
            LocalDateTime.now()
    );
    CommentDto commentDto = CommentMapper.toCommentDto(comment);
    CommentDtoResponse commentDtoResponse = CommentMapper.toCommentDtoResponse(comment);
    Item itemNotRequest = new Item(
            1L,
            "item name",
            "item description",
            true,
            owner,
            request
    );
    ItemDto itemDtoNotRequest = ItemMapper.toItemDto(itemNotRequest);

    @Test
    void testCreateItemOk() {
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.ofNullable(owner));

        when(requestRepository.findById(request.getId()))
                .thenReturn(Optional.ofNullable(request));

        when(itemRepository.save(item))
                .thenReturn(item);

        ItemDto actualItemDto = itemService.create(owner.getId(), itemDto);

        assertNotNull(actualItemDto);
        assertEquals(itemDto, actualItemDto);
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    void testCreateItemNotRequest() {
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.ofNullable(owner));

        when(requestRepository.findById(request.getId()))
                .thenReturn(Optional.ofNullable(request));

        when(itemRepository.save(itemNotRequest))
                .thenReturn(itemNotRequest);

        ItemDto actualItemDto = itemService.create(owner.getId(), itemDto);

        assertNotNull(actualItemDto);
        assertEquals(itemDtoNotRequest, actualItemDto);
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    void testCreateItemWhenOwnerNotFound() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> itemService.create(owner.getId(), itemDto));

        assertEquals("User Id=1", exception.getMessage());
        verify(itemRepository, never()).save(any());
    }

    @Test
    void testCreateItemWhenRequestNotFound() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(owner));

        when(requestRepository.findById(request.getId()))
                .thenReturn(Optional.empty());

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> itemService.create(owner.getId(), itemDto));

        assertEquals("Request Id=1", exception.getMessage());
        verify(itemRepository, never()).save(any());
    }

    @Test
    void testCommentCreateOk() {
        Page<Booking> bookingPage = new PageImpl<>(
                Collections.singletonList(Booking.builder().id(1L).item(item).build())
        );

        when(userRepository.findById(author.getId()))
                .thenReturn(Optional.ofNullable(author));

        when(itemRepository.findById(1L))
                .thenReturn(Optional.ofNullable(item));

        when(bookingRepository.findBookingPast(anyLong(), any(), any()))
                .thenReturn(bookingPage);

        when(commentRepository.save(any()))
                .thenReturn(comment);

        CommentDtoResponse actualCommentDtoResponse = itemService.commentCreate(author.getId(), item.getId(), commentDto);

        assertNotNull(actualCommentDtoResponse);
        assertEquals(commentDtoResponse, actualCommentDtoResponse);
        verify(commentRepository, times(1)).save(any());
    }

    @Test
    void testCommentCreateWhenAuthorNotFound() {
        when(userRepository.findById(2L))
                .thenReturn(Optional.empty());

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> itemService.commentCreate(author.getId(), item.getId(), commentDto));

        assertEquals("User Id=2", exception.getMessage());
        verify(commentRepository, never()).save(any());
    }

    @Test
    void testCommentCreateWhenItemNotFound() {
        when(userRepository.findById(author.getId()))
                .thenReturn(Optional.ofNullable(author));

        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.empty());

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> itemService.commentCreate(author.getId(), item.getId(), commentDto));

        assertEquals("Item Id=1", exception.getMessage());
        verify(commentRepository, never()).save(any());
    }

    @Test
    void testCommentCreateWhenAuthorDidNotUseItem() {
        Page<Booking> bookingPage = new PageImpl<>(
                Collections.singletonList(Booking.builder().id(1L).item(Item.builder().id(2L).build()).build())
        );

        when(userRepository.findById(author.getId()))
                .thenReturn(Optional.ofNullable(author));

        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.ofNullable(item));

        when(bookingRepository.findBookingPast(anyLong(), any(), any()))
                .thenReturn(bookingPage);

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> itemService.commentCreate(author.getId(), item.getId(), commentDto));

        assertEquals("User id=2 did not use item id=1", exception.getMessage());
        verify(commentRepository, never()).save(any());
    }

    @Test
    void testGetOk() {
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.ofNullable(item));

        when(bookingRepository.findLast(anyLong(), any(), any()))
                .thenReturn(Booking.builder().id(1L).item(item).booker(author).build());

        when(bookingRepository.findNext(anyLong(), any(), any()))
                .thenReturn(Booking.builder().id(2L).item(item).booker(author).build());

        ItemDtoResponse actualItemDtoResponse = itemService.get(owner.getId(), item.getId());
        ItemDtoResponse expectedDtoResponse = itemDtoResponse;
        expectedDtoResponse.setLastBooking(
                BookingMapper.toBookingDto(Booking.builder().id(1L).item(item).booker(author).build())
        );
        expectedDtoResponse.setNextBooking(
                BookingMapper.toBookingDto(Booking.builder().id(2L).item(item).booker(author).build())
        );


        assertNotNull(actualItemDtoResponse);
        assertEquals(expectedDtoResponse, actualItemDtoResponse);
    }

    @Test
    void testGetWhenItemNotFound() {
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.empty());

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> itemService.get(owner.getId(), item.getId()));

        assertEquals("Item id=1", exception.getMessage());
    }

    @Test
    void testGetUserItems() {
        Page<Item> itemPage = new PageImpl<>(Collections.singletonList(item));

        when(itemRepository
                .findAllByOwnerIdOrderById(owner.getId(), new MyPageRequest(0, 10, Sort.unsorted()))
        )
                .thenReturn(itemPage);

        when(bookingRepository.findLast(anyLong(), any(), any()))
                .thenReturn(Booking.builder().id(1L).item(item).booker(author).build());

        when(bookingRepository.findNext(anyLong(), any(), any()))
                .thenReturn(Booking.builder().id(2L).item(item).booker(author).build());

        List<ItemDtoResponse> actualItemDtoResponseList = itemService.getUserItems(owner.getId(), 0, 10);
        ItemDtoResponse expectedDtoResponse = itemDtoResponse;
        expectedDtoResponse.setLastBooking(
                BookingMapper.toBookingDto(Booking.builder().id(1L).item(item).booker(author).build())
        );
        expectedDtoResponse.setNextBooking(
                BookingMapper.toBookingDto(Booking.builder().id(2L).item(item).booker(author).build())
        );

        assertNotNull(actualItemDtoResponseList);
        assertEquals(List.of(expectedDtoResponse), actualItemDtoResponseList);
    }

    @Test
    void getSearchItems() {
        Page<Item> itemPage = new PageImpl<>(Collections.singletonList(item));

        when(itemRepository.search("search", new MyPageRequest(0, 10, Sort.unsorted())))
                .thenReturn(itemPage);

        List<ItemDto> actualItemDtoList = itemService.getSearchItems("search", 0, 10);

        assertNotNull(actualItemDtoList);
        assertEquals(List.of(itemDto), actualItemDtoList);
    }

    @Test
    void getSearchItemsIsBlanc() {
        List<ItemDto> actualItemDtoList = itemService.getSearchItems("", 0, 10);

        assertNotNull(actualItemDtoList);
        assertTrue(actualItemDtoList.isEmpty());
    }

    @Test
    void testUpdateItemOk() {
        ItemDto updateItemDto = new ItemDto(
                1L,
                "item of Vasily Fortochkin",
                "item description update",
                false,
                null
        );

        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.ofNullable(owner));

        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.ofNullable(item));

        when(itemRepository.save(item))
                .thenReturn(item);

        itemService.update(owner.getId(), item.getId(), updateItemDto);

        verify(itemRepository).save(argumentCaptor.capture());
        Item savedItem = argumentCaptor.getValue();

        assertEquals(updateItemDto.getName(), savedItem.getName());
        assertEquals(updateItemDto.getDescription(), savedItem.getDescription());
        assertEquals(updateItemDto.getAvailable(), savedItem.getAvailable());
    }

    @Test
    void testUpdateItemNameWithoutChanges() {
        ItemDto updateItemDto = new ItemDto(
                1L,
                item.getName(),
                "item description update",
                false,
                null
        );

        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.ofNullable(owner));

        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.ofNullable(item));

        when(itemRepository.save(item))
                .thenReturn(item);

        itemService.update(owner.getId(), item.getId(), updateItemDto);

        verify(itemRepository).save(argumentCaptor.capture());
        Item savedItem = argumentCaptor.getValue();

        assertEquals(item.getName(), savedItem.getName());
        assertEquals(updateItemDto.getDescription(), savedItem.getDescription());
        assertEquals(updateItemDto.getAvailable(), savedItem.getAvailable());
    }

    @Test
    void testUpdateItemDescriptionWithoutChanges() {
        ItemDto updateItemDto = new ItemDto(
                1L,
                "update name",
                item.getDescription(),
                false,
                null
        );

        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.ofNullable(owner));

        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.ofNullable(item));

        when(itemRepository.save(item))
                .thenReturn(item);

        itemService.update(owner.getId(), item.getId(), updateItemDto);

        verify(itemRepository).save(argumentCaptor.capture());
        Item savedItem = argumentCaptor.getValue();

        assertEquals(updateItemDto.getName(), savedItem.getName());
        assertEquals(item.getDescription(), savedItem.getDescription());
        assertEquals(updateItemDto.getAvailable(), savedItem.getAvailable());
    }

    @Test
    void testUpdateItemAvailableWithoutChanges() {
        ItemDto updateItemDto = new ItemDto(
                1L,
                "update name",
                "update description",
                item.getAvailable(),
                null
        );

        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.ofNullable(owner));

        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.ofNullable(item));

        when(itemRepository.save(item))
                .thenReturn(item);

        itemService.update(owner.getId(), item.getId(), updateItemDto);

        verify(itemRepository).save(argumentCaptor.capture());
        Item savedItem = argumentCaptor.getValue();

        assertEquals(updateItemDto.getName(), savedItem.getName());
        assertEquals(updateItemDto.getDescription(), savedItem.getDescription());
        assertEquals(item.getAvailable(), savedItem.getAvailable());
    }

    @Test
    void testUpdateItemOkOnlyName() {
        ItemDto updateItemDto = new ItemDto(
                1L,
                "item of Vasily Fortochkin",
                null,
                null,
                null
        );

        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.ofNullable(owner));

        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.ofNullable(item));

        when(itemRepository.save(item))
                .thenReturn(item);

        itemService.update(owner.getId(), item.getId(), updateItemDto);

        verify(itemRepository).save(argumentCaptor.capture());
        Item savedItem = argumentCaptor.getValue();

        assertEquals(updateItemDto.getName(), savedItem.getName());
        assertEquals(item.getDescription(), savedItem.getDescription());
        assertEquals(item.getAvailable(), savedItem.getAvailable());
    }

    @Test
    void testUpdateItemOkOnlyDescription() {
        ItemDto updateItemDto = new ItemDto(
                1L,
                null,
                "update description",
                null,
                null
        );

        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.ofNullable(owner));

        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.ofNullable(item));

        when(itemRepository.save(item))
                .thenReturn(item);

        itemService.update(owner.getId(), item.getId(), updateItemDto);

        verify(itemRepository).save(argumentCaptor.capture());
        Item savedItem = argumentCaptor.getValue();

        assertEquals(item.getName(), savedItem.getName());
        assertEquals(updateItemDto.getDescription(), savedItem.getDescription());
        assertEquals(item.getAvailable(), savedItem.getAvailable());
    }

    @Test
    void testUpdateItemOkOnlyAvailable() {
        ItemDto updateItemDto = new ItemDto(
                1L,
                null,
                null,
                false,
                null
        );

        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.ofNullable(owner));

        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.ofNullable(item));

        when(itemRepository.save(item))
                .thenReturn(item);

        itemService.update(owner.getId(), item.getId(), updateItemDto);

        verify(itemRepository).save(argumentCaptor.capture());
        Item savedItem = argumentCaptor.getValue();

        assertEquals(item.getName(), savedItem.getName());
        assertEquals(item.getDescription(), savedItem.getDescription());
        assertEquals(updateItemDto.getAvailable(), savedItem.getAvailable());
    }

    @Test
    void testUpdateItemWhenUserNotFound() {
        ItemDto updateItemDto = new ItemDto(
                1L,
                "item of Vasily Fortochkin",
                "item description update",
                false,
                null
        );

        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.empty());

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> itemService.update(owner.getId(), item.getId(), updateItemDto));

        assertEquals("User Id=1", exception.getMessage());
        verify(itemRepository, never()).save(any());
    }


    @Test
    void testUpdateItemWhenItemNotFound() {
        ItemDto updateItemDto = new ItemDto(
                1L,
                "item of Vasily Fortochkin",
                "item description update",
                false,
                null
        );

        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.ofNullable(owner));

        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.empty());

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> itemService.update(owner.getId(), item.getId(), updateItemDto));

        assertEquals("Item Id=1", exception.getMessage());
        verify(itemRepository, never()).save(any());
    }

    @Test
    void testDeleteItemOk() {
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.ofNullable(item));

        itemService.delete(owner.getId(), item.getId());

        verify(itemRepository, times(1))
                .deleteById(1L);
    }

    @Test
    void testDeleteItemWhenItemNotFound() {
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.empty());

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> itemService.delete(owner.getId(), item.getId()));

        assertEquals("Item Id=1", exception.getMessage());
    }

    @Test
    void testDeleteItemWhenUserIsNotOwnerOfIem() {
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.ofNullable(item));

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> itemService.delete(author.getId(), item.getId()));

        assertEquals("User id=2 is not owner of item " + item, exception.getMessage());
    }
}