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

    @Query("select b from Booking b " +
            "where b.booker.id = :userId " +
            "and b.start < :now " +
            "and b.end > :now " +
            "order by b.start desc")
    List<Booking> findBookingCurrent(Long userId, LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.booker.id = :userId " +
            "and b.end < :now " +
            "order by b.start desc")
    List<Booking> findBookingPast(Long userId, LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.booker.id = :userId " +
            "and b.start > :now " +
            "order by b.start desc")
    List<Booking> findBookingFuture(Long userId, LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.booker.id = :userId " +
            "and b.status = :status " +
            "order by b.start desc")
    List<Booking> findBookingStatus(Long userId, Status status);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long bookerId);


    @Query("select b from Booking b " +
            "where b.item.owner.id = :userId " +
            "and b.start < :now " +
            "and b.end > :now " +
            "order by b.start desc")
    List<Booking> findOwnerCurrent(Long userId, LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.item.owner.id = :userId " +
            "and b.end < :now " +
            "order by b.start desc")
    List<Booking> findOwnerPast(Long userId, LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.item.owner.id = :userId " +
            "and b.start > :now " +
            "order by b.start desc")
    List<Booking> findOwnerFuture(Long userId, LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.item.owner.id = :userId " +
            "and b.status = :status " +
            "order by b.start desc")
    List<Booking> findOwnerStatus(Long userId, Status status);

    @Query("select b from Booking b " +
            "where b.item.id = :itemId " +
            "and b.end < :now " +
            "order by b.end desc")
    Booking findLast(Long itemId, LocalDateTime now, PageRequest pageable);

    @Query("select b from Booking b " +
            "where b.item.id = :itemId " +
            "and b.start > :now " +
            "order by b.start asc")
    Booking findNext(Long itemId, LocalDateTime now, PageRequest pageable);
}
