package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import ru.practicum.shareit.utility.MyPageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.BookingMapper.toBooking;
import static ru.practicum.shareit.booking.BookingMapper.toBookingDtoResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingDtoResponse create(Long userId, BookingDto bookingDto) {

        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User Id=" + userId));

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new DataNotFoundException("Item Id=" + bookingDto.getItemId()));

        if (userId.equals(item.getOwner().getId())) {
            throw new DataNotFoundException("User id=" + userId + " can not booking item id=" + item.getId());
        }

        if (Boolean.FALSE.equals(item.getAvailable())) {
            throw new BadRequestException("Item id=" + item.getId() + " not available");
        }

        bookingDto.setBookerId(userId);
        Booking booking = toBooking(bookingDto);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        Booking savedBooking = bookingRepository.save(booking);

        return toBookingDtoResponse(savedBooking);
    }

    @Override
    public BookingDtoResponse get(Long userId, Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new DataNotFoundException("Booking Id=" + bookingId));

        if (!userId.equals(booking.getBooker().getId())
                && !userId.equals(booking.getItem().getOwner().getId())) {
            throw new DataNotFoundException("User id=" + userId);
        }

        return toBookingDtoResponse(booking);
    }

    @Override
    public List<BookingDtoResponse> getByBooker(Long userId, BookingState state, int from, int size) {

        userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User Id=" + userId));

        Page<Booking> bookingList;
        switch (state) {
            case ALL:
                bookingList = bookingRepository
                        .findAllByBookerIdOrderByStartDesc(userId, new MyPageRequest(from, size, Sort.unsorted()));
                break;
            case CURRENT:
                bookingList = bookingRepository
                        .findBookingCurrent(userId, LocalDateTime.now(), new MyPageRequest(from, size, Sort.unsorted()));
                break;
            case PAST:
                bookingList = bookingRepository
                        .findBookingPast(userId, LocalDateTime.now(), new MyPageRequest(from, size, Sort.unsorted()));
                break;
            case FUTURE:
                bookingList = bookingRepository
                        .findBookingFuture(userId, LocalDateTime.now(), new MyPageRequest(from, size, Sort.unsorted()));
                break;
            case WAITING:
                bookingList = bookingRepository
                        .findBookingStatus(userId, Status.WAITING, new MyPageRequest(from, size, Sort.unsorted()));
                break;
            case REJECTED:
                bookingList = bookingRepository
                        .findBookingStatus(userId, Status.REJECTED, new MyPageRequest(from, size, Sort.unsorted()));
                break;
            default:
                throw new BadRequestException("Unknown state: " + state);
        }

        return bookingList
                .stream()
                .map(BookingMapper::toBookingDtoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDtoResponse> getByOwner(Long userId, BookingState state, int from, int size) {

        userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User Id=" + userId));

        Page<Booking> bookingList;
        switch (state) {
            case ALL:
                bookingList = bookingRepository
                        .findAllByItemOwnerIdOrderByStartDesc(userId, new MyPageRequest(from, size, Sort.unsorted()));
                break;
            case CURRENT:
                bookingList = bookingRepository
                        .findOwnerCurrent(userId, LocalDateTime.now(), new MyPageRequest(from, size, Sort.unsorted()));
                break;
            case PAST:
                bookingList = bookingRepository
                        .findOwnerPast(userId, LocalDateTime.now(), new MyPageRequest(from, size, Sort.unsorted()));
                break;
            case FUTURE:
                bookingList = bookingRepository
                        .findOwnerFuture(userId, LocalDateTime.now(), new MyPageRequest(from, size, Sort.unsorted()));
                break;
            case WAITING:
                bookingList = bookingRepository
                        .findOwnerStatus(userId, Status.WAITING, new MyPageRequest(from, size, Sort.unsorted()));
                break;
            case REJECTED:
                bookingList = bookingRepository
                        .findOwnerStatus(userId, Status.REJECTED, new MyPageRequest(from, size, Sort.unsorted()));
                break;
            default:
                throw new BadRequestException("Unknown state: " + state);
        }

        return bookingList
                .stream()
                .map(BookingMapper::toBookingDtoResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingDtoResponse confirm(Long userId, Long bookingId, boolean approved) {

        userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User Id=" + userId));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new DataNotFoundException("Booking Id=" + bookingId));

        itemRepository.findById(booking.getItem().getId())
                .orElseThrow(() -> new DataNotFoundException("Item Id=" + booking.getItem().getId()));

        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new BadRequestException("Booking is checked");
        }

        if (!userId.equals(booking.getItem().getOwner().getId())) {
            throw new DataNotFoundException("User id=" + userId + " is not owner of item");
        }

        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        Booking saveBooking = bookingRepository.save(booking);

        return toBookingDtoResponse(saveBooking);
    }

}
