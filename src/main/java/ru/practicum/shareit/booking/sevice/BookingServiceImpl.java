package ru.practicum.shareit.booking.sevice;

import lombok.RequiredArgsConstructor;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
        bookingDto.setBookerId(userId);
        User booker = findUser(userId);
        Item item = findItem(bookingDto.getItemId());
        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setBooker(booker);
        booking.setItem(item);
        if (userId.equals(item.getOwner().getId())) {
            throw new DataNotFoundException("User id=" + userId);
        }
        if (item.getAvailable()) {
            booking.setStatus(Status.WAITING);
            return BookingMapper.toBookingDtoResponse(bookingRepository.save(booking));
        } else {
            throw new BadRequestException("Item id=" + item.getId() + " not available");
        }
    }

    @Override
    public BookingDtoResponse get(Long userId, Long bookingId) {
        findUser(userId);
        Booking booking = findBooking(bookingId);
        if (userId.equals(booking.getBooker().getId()) || userId.equals(booking.getItem().getOwner().getId())) {
            return BookingMapper.toBookingDtoResponse(booking);
        }
        throw new DataNotFoundException("User id= " + userId);
    }

    @Override
    public List<BookingDtoResponse> getByBooker(Long userId, BookingState state) {
        findUser(userId);
        List<Booking> bookingList;
        switch (state) {
            case ALL:
                bookingList = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                bookingList = bookingRepository.findBookingCurrent(userId, LocalDateTime.now());
                break;
            case PAST:
                bookingList = bookingRepository.findBookingPast(userId, LocalDateTime.now());
                break;
            case FUTURE:
                bookingList = bookingRepository.findBookingFuture(userId, LocalDateTime.now());
                break;
            case WAITING:
                bookingList = bookingRepository.findBookingStatus(userId, Status.WAITING);
                break;
            case REJECTED:
                bookingList = bookingRepository.findBookingStatus(userId, Status.REJECTED);
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
    public List<BookingDtoResponse> getByOwner(Long userId, BookingState state) {
        findUser(userId);
        List<Booking> bookingList;
        switch (state) {
            case ALL:
                bookingList = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                bookingList = bookingRepository.findOwnerCurrent(userId, LocalDateTime.now());
                break;
            case PAST:
                bookingList = bookingRepository.findOwnerPast(userId, LocalDateTime.now());
                break;
            case FUTURE:
                bookingList = bookingRepository.findOwnerFuture(userId, LocalDateTime.now());
                break;
            case WAITING:
                bookingList = bookingRepository.findOwnerStatus(userId, Status.WAITING);
                break;
            case REJECTED:
                bookingList = bookingRepository.findOwnerStatus(userId, Status.REJECTED);
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
        findUser(userId);
        Booking booking = findBooking(bookingId);
        findItem(booking.getItem().getId());
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new BadRequestException("Booking is checked");
        }
        if (userId.equals(booking.getItem().getOwner().getId())) {
            if (approved) {
                booking.setStatus(Status.APPROVED);
            } else {
                booking.setStatus(Status.REJECTED);
            }
        } else {
            throw new DataNotFoundException("User id=" + userId);
        }
        Booking saveBooking = bookingRepository.save(booking);

        return BookingMapper.toBookingDtoResponse(saveBooking);
    }

    private Booking findBooking(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(
                () -> new DataNotFoundException("Booking Id=" + bookingId));
    }

    private Item findItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(
                () -> new DataNotFoundException("Item Id=" + itemId));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new DataNotFoundException("User Id=" + userId));
    }
}
