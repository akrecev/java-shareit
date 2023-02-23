package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    User booker;

    User owner;

    Item item;

    Booking booking;

    @BeforeEach
    void beforeEach() {

        booker = userRepository.save(new User(
                1L,
                "booker",
                "booker@mail.com"
        ));

        owner = userRepository.save(new User(
                2L,
                "owner",
                "owner@mail.com"
        ));

        item = itemRepository.save(new Item(
                1L,
                "item",
                "item description",
                true,
                owner,
                null
        ));

        booking = bookingRepository.save(new Booking(
                1L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item,
                booker,
                Status.WAITING
        ));
    }

    @Test
    void findAllByBookerIdOrderByStartDesc() {
        Page<Booking> bookingPage = bookingRepository.findAllByBookerIdOrderByStartDesc(
                booker.getId(), Pageable.unpaged()
        );

        assertFalse(bookingPage.toList().isEmpty());
        assertEquals(booking, bookingPage.toList().get(0));
    }

    @Test
    void findBookingCurrent() {
        Page<Booking> bookingPage = bookingRepository.findBookingCurrent(
                booker.getId(), LocalDateTime.now().plusHours(1), Pageable.unpaged()
        );

        assertFalse(bookingPage.toList().isEmpty());
        assertEquals(booking, bookingPage.toList().get(0));
    }

    @Test
    void findBookingPast() {
        Page<Booking> bookingPage = bookingRepository.findBookingPast(
                booker.getId(), LocalDateTime.now().plusHours(3), Pageable.unpaged()
        );

        assertFalse(bookingPage.toList().isEmpty());
        assertEquals(booking, bookingPage.toList().get(0));
    }

    @Test
    void findBookingFuture() {
        Page<Booking> bookingPage = bookingRepository.findBookingFuture(
                booker.getId(), LocalDateTime.now(), Pageable.unpaged()
        );

        assertFalse(bookingPage.toList().isEmpty());
        assertEquals(booking, bookingPage.toList().get(0));
    }

    @Test
    void findBookingStatus() {
        Page<Booking> bookingPage = bookingRepository.findBookingStatus(
                booker.getId(), Status.WAITING, Pageable.unpaged()
        );

        assertFalse(bookingPage.toList().isEmpty());
        assertEquals(booking, bookingPage.toList().get(0));
    }

    @Test
    void findAllByItemOwnerIdOrderByStartDesc() {
        Page<Booking> bookingPage = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(
                owner.getId(), Pageable.unpaged()
        );

        assertFalse(bookingPage.toList().isEmpty());
        assertEquals(booking, bookingPage.toList().get(0));
    }

    @Test
    void findOwnerCurrent() {
        Page<Booking> bookingPage = bookingRepository.findOwnerCurrent(
                owner.getId(), LocalDateTime.now().plusHours(1), Pageable.unpaged()
        );

        assertFalse(bookingPage.toList().isEmpty());
        assertEquals(booking, bookingPage.toList().get(0));
    }

    @Test
    void findOwnerPast() {
        Page<Booking> bookingPage = bookingRepository.findOwnerPast(
                owner.getId(), LocalDateTime.now().plusHours(3), Pageable.unpaged()
        );

        assertFalse(bookingPage.toList().isEmpty());
        assertEquals(booking, bookingPage.toList().get(0));
    }

    @Test
    void findOwnerFuture() {
        Page<Booking> bookingPage = bookingRepository.findOwnerFuture(
                owner.getId(), LocalDateTime.now(), Pageable.unpaged()
        );

        assertFalse(bookingPage.toList().isEmpty());
        assertEquals(booking, bookingPage.toList().get(0));
    }

    @Test
    void findOwnerStatus() {
        Page<Booking> bookingPage = bookingRepository.findOwnerStatus(
                owner.getId(), Status.WAITING, Pageable.unpaged()
        );

        assertFalse(bookingPage.toList().isEmpty());
        assertEquals(booking, bookingPage.toList().get(0));
    }

    @Test
    void findLast() {
        Booking lastBooking = bookingRepository.findLast(
                item.getId(), LocalDateTime.now().plusHours(3), PageRequest.of(0, 1)
        );

        assertNotNull(lastBooking);
        assertEquals(booking, lastBooking);
    }

    @Test
    void findNext() {
        Booking nextBooking = bookingRepository.findNext(
                item.getId(), LocalDateTime.now(), PageRequest.of(0, 1)
        );

        assertNotNull(nextBooking);
        assertEquals(booking, nextBooking);
    }
}