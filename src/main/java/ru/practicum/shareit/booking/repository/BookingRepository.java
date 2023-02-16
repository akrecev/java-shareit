package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :userId " +
            "AND b.start < :now " +
            "AND b.end > :now " +
            "ORDER BY b.start DESC")
    Page<Booking> findBookingCurrent(Long userId, LocalDateTime now, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :userId " +
            "AND b.end < :now " +
            "ORDER BY b.start DESC")
    Page<Booking> findBookingPast(Long userId, LocalDateTime now, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :userId " +
            "AND b.start > :now " +
            "ORDER BY b.start DESC")
    Page<Booking> findBookingFuture(Long userId, LocalDateTime now, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :userId " +
            "AND b.status = :status " +
            "order by b.start DESC")
    Page<Booking> findBookingStatus(Long userId, Status status, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdOrderByStartDesc(Long bookerId, Pageable pageable);


    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :userId " +
            "AND b.start < :now " +
            "AND b.end > :now " +
            "ORDER BY b.start DESC")
    Page<Booking> findOwnerCurrent(Long userId, LocalDateTime now, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :userId " +
            "AND b.end < :now " +
            "ORDER BY b.start DESC")
    Page<Booking> findOwnerPast(Long userId, LocalDateTime now, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :userId " +
            "AND b.start > :now " +
            "ORDER BY b.start DESC")
    Page<Booking> findOwnerFuture(Long userId, LocalDateTime now, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :userId " +
            "AND b.status = :status " +
            "ORDER BY b.start DESC")
    Page<Booking> findOwnerStatus(Long userId, Status status, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.end < :now " +
            "ORDER BY b.end DESC")
    Booking findLast(Long itemId, LocalDateTime now, PageRequest pageable);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.start > :now " +
            "ORDER BY b.start ASC")
    Booking findNext(Long itemId, LocalDateTime now, PageRequest pageable);
}
