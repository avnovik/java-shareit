package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

	List<Booking> findAllByBookerId(Long bookerId, Sort sort);

	@Query("select b from Booking b "
			+ "where b.booker.id = :bookerId "
			+ "and b.start <= :now "
			+ "and b.end >= :now")
	List<Booking> findCurrentByBookerId(@Param("bookerId") Long bookerId, @Param("now") LocalDateTime now, Sort sort);

	List<Booking> findAllByItemOwnerId(Long ownerId, Sort sort);

	@Query("select b from Booking b "
			+ "where b.item.owner.id = :ownerId "
			+ "and b.start <= :now "
			+ "and b.end >= :now")
	List<Booking> findCurrentByOwnerId(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now, Sort sort);

	List<Booking> findAllByBookerIdAndEndBefore(Long bookerId, LocalDateTime now, Sort sort);

	List<Booking> findAllByItemOwnerIdAndEndBefore(Long ownerId, LocalDateTime now, Sort sort);

	List<Booking> findAllByBookerIdAndStartAfter(Long bookerId, LocalDateTime now, Sort sort);

	List<Booking> findAllByItemOwnerIdAndStartAfter(Long ownerId, LocalDateTime now, Sort sort);

	List<Booking> findAllByBookerIdAndStatus(Long bookerId, BookingStatus status, Sort sort);

	List<Booking> findAllByItemOwnerIdAndStatus(Long ownerId, BookingStatus status, Sort sort);
}
