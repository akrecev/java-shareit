package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.model.BadRequestException;
import ru.practicum.shareit.exception.model.DataNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    ItemRepository itemRepository;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    BookingServiceImpl bookingService;

    User booker = new User(
            1L,
            "booker",
            "booker@mail.com"
    );

    User owner = new User(
            2L,
            "owner",
            "owner@mail.com"
    );

    Item item = new Item(
            1L,
            "item",
            "item description",
            true,
            owner,
            null
    );

    Booking booking = new Booking(
            1L,
            LocalDateTime.now().plusHours(1),
            LocalDateTime.now().plusHours(2),
            item,
            booker,
            Status.WAITING
    );

    BookingDto bookingDto = BookingMapper.toBookingDto(booking);

    BookingDtoResponse bookingDtoResponse = BookingMapper.toBookingDtoResponse(booking);

    Page<Booking> bookingPage = new PageImpl<>(Collections.singletonList(booking));

    @Test
    void testCreateBookingOk() {
        Booking expectedBooking = BookingMapper.toBooking(bookingDto);
        expectedBooking.setBooker(booker);
        expectedBooking.setItem(item);
        expectedBooking.setStatus(Status.WAITING);

        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.ofNullable(booker));

        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(booking))
                .thenReturn(expectedBooking);

        BookingDtoResponse actualBookingDtoResponse = bookingService.create(1L, bookingDto);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);

        assertNotNull(actualBookingDtoResponse);
        assertEquals(bookingDtoResponse, actualBookingDtoResponse);
    }

    @Test
    void testCreateBookingWhenUserNotFound() {
        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.empty());

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> bookingService.create(booker.getId(), bookingDto));

        assertEquals("User Id=1", exception.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void testCreateBookingWhenItemNotFound() {
        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.ofNullable(booker));

        when(itemRepository.findById(bookingDto.getItemId()))
                .thenReturn(Optional.empty());

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> bookingService.create(booker.getId(), bookingDto));

        assertEquals("Item Id=1", exception.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void testCreateBookingWhenOwnerIsBooker() {
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.ofNullable(owner));

        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.ofNullable(item));

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> bookingService.create(owner.getId(), bookingDto));

        assertEquals("User id=2 can not booking item id=1", exception.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void testCreateBookingWhenFalseAvailable() {
        Item itemFalse = item;
        itemFalse.setAvailable(false);

        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.ofNullable(booker));

        when(itemRepository.findById(itemFalse.getId()))
                .thenReturn(Optional.of(itemFalse));

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> bookingService.create(booker.getId(), bookingDto));

        assertEquals("Item id=1 not available", exception.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void testGetBookingOk() {
        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.ofNullable(booking));

        BookingDtoResponse actualBookingDtoResponse = bookingService.get(booker.getId(), booking.getId());

        assertNotNull(actualBookingDtoResponse);
        assertEquals(bookingDtoResponse, actualBookingDtoResponse);
        verify(bookingRepository, times(1)).findById(any());
    }

    @Test
    void testGetBookingWhenBookingNotFound() {
        when(bookingRepository.findById(bookingDto.getId()))
                .thenReturn(Optional.empty());

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> bookingService.get(booker.getId(), bookingDto.getId()));

        assertEquals("Booking Id=1", exception.getMessage());
    }

    @Test
    void testGetBookingWhenUserIcNotOwnerOrBooker() {
        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.ofNullable(booking));

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> bookingService.get(3L, bookingDto.getId()));

        assertEquals("User id=3", exception.getMessage());
    }

    @Test
    void testGetByBookerStatusAll() {
        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.ofNullable(booker));

        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(bookingPage);

        List<BookingDtoResponse> actualBookingDtoResponseList = bookingService
                .getByBooker(booker.getId(), BookingState.ALL, 0, 10);

        assertNotNull(actualBookingDtoResponseList);
        assertEquals(List.of(bookingDtoResponse), actualBookingDtoResponseList);
    }

    @Test
    void testGetByBookerStatusCURRENT() {
        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.ofNullable(booker));

        when(bookingRepository.findBookingCurrent(anyLong(), any(), any()))
                .thenReturn(bookingPage);

        List<BookingDtoResponse> actualBookingDtoResponseList = bookingService
                .getByBooker(booker.getId(), BookingState.CURRENT, 0, 10);

        assertNotNull(actualBookingDtoResponseList);
        assertEquals(List.of(bookingDtoResponse), actualBookingDtoResponseList);
    }

    @Test
    void testGetByBookerStatusPAST() {
        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.ofNullable(booker));

        when(bookingRepository.findBookingPast(anyLong(), any(), any()))
                .thenReturn(bookingPage);

        List<BookingDtoResponse> actualBookingDtoResponseList = bookingService
                .getByBooker(booker.getId(), BookingState.PAST, 0, 10);

        assertNotNull(actualBookingDtoResponseList);
        assertEquals(List.of(bookingDtoResponse), actualBookingDtoResponseList);
    }

    @Test
    void testGetByBookerStatusFUTURE() {
        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.ofNullable(booker));

        when(bookingRepository.findBookingFuture(anyLong(), any(), any()))
                .thenReturn(bookingPage);

        List<BookingDtoResponse> actualBookingDtoResponseList = bookingService
                .getByBooker(booker.getId(), BookingState.FUTURE, 0, 10);

        assertNotNull(actualBookingDtoResponseList);
        assertEquals(List.of(bookingDtoResponse), actualBookingDtoResponseList);
    }

    @Test
    void testGetByBookerStatusWAITING() {
        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.ofNullable(booker));

        when(bookingRepository.findBookingStatus(anyLong(), any(), any()))
                .thenReturn(bookingPage);

        List<BookingDtoResponse> actualBookingDtoResponseList = bookingService
                .getByBooker(booker.getId(), BookingState.WAITING, 0, 10);

        assertNotNull(actualBookingDtoResponseList);
        assertEquals(List.of(bookingDtoResponse), actualBookingDtoResponseList);
    }

    @Test
    void testGetByBookerStatusREJECTED() {
        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.ofNullable(booker));

        when(bookingRepository.findBookingStatus(anyLong(), any(), any()))
                .thenReturn(bookingPage);

        List<BookingDtoResponse> actualBookingDtoResponseList = bookingService
                .getByBooker(booker.getId(), BookingState.REJECTED, 0, 10);

        assertNotNull(actualBookingDtoResponseList);
        assertEquals(List.of(bookingDtoResponse), actualBookingDtoResponseList);
    }

    @Test
    void testGetByOwnerStatusAll() {
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.ofNullable(owner));

        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(bookingPage);

        List<BookingDtoResponse> actualBookingDtoResponseList = bookingService
                .getByOwner(owner.getId(), BookingState.ALL, 0, 10);

        assertNotNull(actualBookingDtoResponseList);
        assertEquals(List.of(bookingDtoResponse), actualBookingDtoResponseList);
    }

    @Test
    void testGetByOwnerStatusCURRENT() {
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.ofNullable(owner));

        when(bookingRepository.findOwnerCurrent(anyLong(), any(), any()))
                .thenReturn(bookingPage);

        List<BookingDtoResponse> actualBookingDtoResponseList = bookingService
                .getByOwner(owner.getId(), BookingState.CURRENT, 0, 10);

        assertNotNull(actualBookingDtoResponseList);
        assertEquals(List.of(bookingDtoResponse), actualBookingDtoResponseList);
    }

    @Test
    void testGetByOwnerStatusPAST() {
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.ofNullable(owner));

        when(bookingRepository.findOwnerPast(anyLong(), any(), any()))
                .thenReturn(bookingPage);

        List<BookingDtoResponse> actualBookingDtoResponseList = bookingService
                .getByOwner(owner.getId(), BookingState.PAST, 0, 10);

        assertNotNull(actualBookingDtoResponseList);
        assertEquals(List.of(bookingDtoResponse), actualBookingDtoResponseList);
    }

    @Test
    void testGetByOwnerStatusFUTURE() {
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.ofNullable(owner));

        when(bookingRepository.findOwnerFuture(anyLong(), any(), any()))
                .thenReturn(bookingPage);

        List<BookingDtoResponse> actualBookingDtoResponseList = bookingService
                .getByOwner(owner.getId(), BookingState.FUTURE, 0, 10);

        assertNotNull(actualBookingDtoResponseList);
        assertEquals(List.of(bookingDtoResponse), actualBookingDtoResponseList);
    }

    @Test
    void testGetByOwnerStatusWAITING() {
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.ofNullable(owner));

        when(bookingRepository.findOwnerStatus(anyLong(), any(), any()))
                .thenReturn(bookingPage);

        List<BookingDtoResponse> actualBookingDtoResponseList = bookingService
                .getByOwner(owner.getId(), BookingState.WAITING, 0, 10);

        assertNotNull(actualBookingDtoResponseList);
        assertEquals(List.of(bookingDtoResponse), actualBookingDtoResponseList);
    }

    @Test
    void testGetByOwnerStatusREJECTED() {
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.ofNullable(owner));

        when(bookingRepository.findOwnerStatus(anyLong(), any(), any()))
                .thenReturn(bookingPage);

        List<BookingDtoResponse> actualBookingDtoResponseList = bookingService
                .getByOwner(owner.getId(), BookingState.REJECTED, 0, 10);

        assertNotNull(actualBookingDtoResponseList);
        assertEquals(List.of(bookingDtoResponse), actualBookingDtoResponseList);
    }

    @Test
    void testConfirmOkThenApproved() {

        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.ofNullable(owner));

        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.ofNullable(item));

        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.ofNullable(booking));

        when(bookingRepository.save(booking))
                .thenReturn(booking);

        BookingDtoResponse actualBookingDtoResponse
                = bookingService.confirm(owner.getId(), booking.getId(), true);

        assertNotNull(actualBookingDtoResponse);
        assertEquals(Status.APPROVED, actualBookingDtoResponse.getStatus());
    }

    @Test
    void testConfirmOkThenRejected() {

        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.ofNullable(owner));

        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.ofNullable(item));

        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.ofNullable(booking));

        when(bookingRepository.save(booking))
                .thenReturn(booking);

        BookingDtoResponse actualBookingDtoResponse
                = bookingService.confirm(owner.getId(), booking.getId(), false);

        assertNotNull(actualBookingDtoResponse);
        assertEquals(Status.REJECTED, actualBookingDtoResponse.getStatus());
    }

    @Test
    void testConfirmWhenUserNotFound() {
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.empty());

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> bookingService.confirm(owner.getId(), booking.getId(), true));

        assertEquals("User Id=2", exception.getMessage());
    }

    @Test
    void testConfirmWhenItemNotFound() {
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.ofNullable(owner));

        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.ofNullable(booking));

        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.empty());

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> bookingService.confirm(owner.getId(), booking.getId(), true));

        assertEquals("Item Id=1", exception.getMessage());
    }

    @Test
    void testConfirmWhenBookingIsNotWaiting() {
        Booking bookingCanceled = booking;
        bookingCanceled.setStatus(Status.CANCELED);

        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.ofNullable(owner));

        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.ofNullable(item));

        when(bookingRepository.findById(bookingCanceled.getId()))
                .thenReturn(Optional.of(bookingCanceled));

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> bookingService.confirm(owner.getId(), bookingCanceled.getId(), true));

        assertEquals("Booking is checked", exception.getMessage());
    }

    @Test
    void testConfirmWhenUserIsNotOwner() {
        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.ofNullable(booker));

        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.ofNullable(item));

        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.ofNullable(booking));

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> bookingService.confirm(booker.getId(), booking.getId(), true));

        assertEquals("User id=1 is not owner of item", exception.getMessage());
    }
}