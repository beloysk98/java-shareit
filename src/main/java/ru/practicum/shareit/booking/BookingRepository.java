package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    @Query("select b from Booking b " +
            "where b.booker.id = :bookerId and b.start <= :now and b.end >= :now " +
            "order by b.start desc")
    List<Booking> findCurrentByBooker(@Param("bookerId") Long bookerId, @Param("now") LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.booker.id = :bookerId and b.end < :now " +
            "order by b.start desc")
    List<Booking> findPastByBooker(@Param("bookerId") Long bookerId, @Param("now") LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.booker.id = :bookerId and b.start > :now " +
            "order by b.start desc")
    List<Booking> findFutureByBooker(@Param("bookerId") Long bookerId, @Param("now") LocalDateTime now);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId);

    @Query("select b from Booking b " +
            "where b.item.owner.id = :ownerId and b.start <= :now and b.end >= :now " +
            "order by b.start desc")
    List<Booking> findCurrentByOwner(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.item.owner.id = :ownerId and b.end < :now " +
            "order by b.start desc")
    List<Booking> findPastByOwner(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.item.owner.id = :ownerId and b.start > :now " +
            "order by b.start desc")
    List<Booking> findFutureByOwner(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);

    Optional<Booking> findFirstByItemIdAndStatusAndEndBeforeOrderByEndDesc(
            Long itemId, BookingStatus status, LocalDateTime now);

    Optional<Booking> findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
            Long itemId, BookingStatus status, LocalDateTime now);

    boolean existsByBookerIdAndItemIdAndStatusAndEndBefore(
            Long bookerId, Long itemId, BookingStatus status, LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.item.id in :itemIds and b.status = :status and b.end < :now " +
            "order by b.end desc")
    List<Booking> findAllPastByItemIds(@Param("itemIds") Collection<Long> itemIds,
                                       @Param("status") BookingStatus status,
                                       @Param("now") LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.item.id in :itemIds and b.status = :status and b.start > :now " +
            "order by b.start asc")
    List<Booking> findAllNextByItemIds(@Param("itemIds") Collection<Long> itemIds,
                                       @Param("status") BookingStatus status,
                                       @Param("now") LocalDateTime now);
}