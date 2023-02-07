package ru.practicum.shareit.booking.sevice;

import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.util.List;

public interface BookingService {
    BookingDtoResponse create(Long userId, BookingDto bookingDto);
    BookingDtoResponse get(Long userId, Long bookingId);
    List<BookingDtoResponse> getByBooker(Long userId, BookingState state);
    List<BookingDtoResponse> getByOwner(Long userId, BookingState state);
    BookingDtoResponse confirm(Long userId, Long bookingId, boolean approved);
}
