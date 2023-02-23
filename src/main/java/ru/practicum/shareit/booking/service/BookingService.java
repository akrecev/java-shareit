package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.util.List;

public interface BookingService {
    BookingDtoResponse create(Long userId, BookingDto bookingDto);

    BookingDtoResponse get(Long userId, Long bookingId);

    List<BookingDtoResponse> getByBooker(Long userId, BookingState state, int from, int size);

    List<BookingDtoResponse> getByOwner(Long userId, BookingState state, int from, int size);

    BookingDtoResponse confirm(Long userId, Long bookingId, boolean approved);
}
