package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * JPA-репозиторий для {@link Booking}.
 * Spring Data JPA создаёт реализацию автоматически: доступны CRUD-операции и
 * запросы из имени методов.
 */
public interface BookingRepository extends JpaRepository<Booking, Long> {
	@Query("select b from Booking b "
			+ "where b.item.id = :itemId "
			+ "and b.status = :status "
			+ "and b.end < :now "
			+ "order by b.end desc")
	List<Booking> findLastBookings(@Param("itemId") Long itemId,
						  @Param("status") BookingStatus status,
						  @Param("now") LocalDateTime now,
						  Pageable pageable);

	@Query("select b from Booking b "
			+ "where b.item.id = :itemId "
			+ "and b.status = :status "
			+ "and b.start > :now "
			+ "order by b.start asc")
	List<Booking> findNextBookings(@Param("itemId") Long itemId,
						  @Param("status") BookingStatus status,
						  @Param("now") LocalDateTime now,
						  Pageable pageable);

	@Query("select (count(b) > 0) from Booking b "
			+ "where b.item.id = :itemId "
			+ "and b.booker.id = :userId "
			+ "and b.status = :status "
			+ "and b.end < :now")
	boolean hasFinishedBooking(@Param("itemId") Long itemId,
						 @Param("userId") Long userId,
						 @Param("status") BookingStatus status,
						 @Param("now") LocalDateTime now);

	List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

	@Query("select b from Booking b "
			+ "where b.booker.id = :bookerId "
			+ "and b.start <= :now "
			+ "and b.end >= :now "
			+ "order by b.start desc")
	List<Booking> findCurrentByBookerId(@Param("bookerId") Long bookerId, @Param("now") LocalDateTime now);

	List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId);

	@Query("select b from Booking b "
			+ "where b.item.owner.id = :ownerId "
			+ "and b.start <= :now "
			+ "and b.end >= :now "
			+ "order by b.start desc")
	List<Booking> findCurrentByOwnerId(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now);

	List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime now);

	List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime now);

	List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime now);

	List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime now);

	List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

	List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);
}
