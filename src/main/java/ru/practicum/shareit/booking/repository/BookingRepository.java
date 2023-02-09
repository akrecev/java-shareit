package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :userId " +
            "AND b.start < :now " +
            "AND b.end > :now " +
            "ORDER BY b.start DESC")
    List<Booking> findBookingCurrent(Long userId, LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :userId " +
            "AND b.end < :now " +
            "ORDER BY b.start DESC")
    List<Booking> findBookingPast(Long userId, LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :userId " +
            "AND b.start > :now " +
            "ORDER BY b.start DESC")
    List<Booking> findBookingFuture(Long userId, LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :userId " +
            "AND b.status = :status " +
            "order by b.start DESC")
    List<Booking> findBookingStatus(Long userId, Status status);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long bookerId);


    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :userId " +
            "AND b.start < :now " +
            "AND b.end > :now " +
            "ORDER BY b.start DESC")
    List<Booking> findOwnerCurrent(Long userId, LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :userId " +
            "AND b.end < :now " +
            "ORDER BY b.start DESC")
    List<Booking> findOwnerPast(Long userId, LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :userId " +
            "AND b.start > :now " +
            "ORDER BY b.start DESC")
    List<Booking> findOwnerFuture(Long userId, LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :userId " +
            "AND b.status = :status " +
            "ORDER BY b.start DESC")
    List<Booking> findOwnerStatus(Long userId, Status status);

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
