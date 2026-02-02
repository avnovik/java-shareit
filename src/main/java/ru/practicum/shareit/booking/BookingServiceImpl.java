package ru.practicum.shareit.booking;

import java.time.LocalDateTime;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

/**
 * Реализация {@link BookingService}.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
	private final BookingRepository bookingRepository;
	private final UserRepository userRepository;
	private final ItemRepository itemRepository;

	@Override
	public BookingDto create(Long userId, BookingCreateDto bookingCreateDto) {
		User booker = userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));

		Long itemId = bookingCreateDto.getItemId();

		Item item = itemRepository.findById(itemId)
				.orElseThrow(() -> new NotFoundException("Item with id=" + itemId + " not found"));

		if (item.getOwner() != null && item.getOwner().getId() != null && item.getOwner().getId().equals(userId)) {
			throw new IllegalArgumentException("Owner cannot book own item with id=" + itemId);
		}
		if (Boolean.FALSE.equals(item.getAvailable())) {
			throw new IllegalArgumentException("Item with id=" + itemId + " is not available");
		}

		LocalDateTime start = bookingCreateDto.getStart();
		LocalDateTime end = bookingCreateDto.getEnd();
		LocalDateTime now = LocalDateTime.now();
		if (start.isBefore(now) || end.isBefore(now)) {
			throw new IllegalArgumentException("start and end must be in the future");
		}
		if (!start.isBefore(end)) {
			throw new IllegalArgumentException("start must be before end");
		}

		Booking booking = new Booking();
		booking.setStart(start);
		booking.setEnd(end);
		booking.setItem(item);
		booking.setBooker(booker);
		booking.setStatus(BookingStatus.WAITING);

		Booking saved = bookingRepository.save(booking);
		log.debug("created bookingId={} itemId={} bookerId={}", saved.getId(), itemId, userId);
		return toDto(saved);
	}

	@Override
	public BookingDto approve(Long userId, Long bookingId, boolean approved) {
		Booking booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new NotFoundException("Booking with id=" + bookingId + " not found"));

		Long ownerId = booking.getItem() != null && booking.getItem().getOwner() != null ? booking.getItem().getOwner().getId() : null;
		if (ownerId == null || !ownerId.equals(userId)) {
			throw new SecurityException("Only owner can approve booking with id=" + bookingId);
		}

		if (booking.getStatus() != BookingStatus.WAITING) {
			throw new IllegalStateException("Booking with id=" + bookingId + " is not in WAITING status");
		}

		booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
		Booking saved = bookingRepository.save(booking);
		log.debug("approved bookingId={} ownerId={} status={}", bookingId, userId, saved.getStatus());
		return toDto(saved);
	}

	@Override
	public BookingDto getById(Long userId, Long bookingId) {
		Booking booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new NotFoundException("Booking with id=" + bookingId + " not found"));

		Long ownerId = booking.getItem() != null && booking.getItem().getOwner() != null ? booking.getItem().getOwner().getId() : null;
		Long bookerId = booking.getBooker() != null ? booking.getBooker().getId() : null;
		if ((ownerId == null || !ownerId.equals(userId)) && (bookerId == null || !bookerId.equals(userId))) {
			throw new SecurityException("Only owner or booker can view booking with id=" + bookingId);
		}

		BookingDto dto = toDto(booking);
		log.debug("returned bookingId={} forUserId={}", bookingId, userId);
		return dto;
	}

	@Override
	public List<BookingDto> getAllByBooker(Long userId, BookingState state) {
		userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));

		BookingState safeState = state != null ? state : BookingState.ALL;
		LocalDateTime now = LocalDateTime.now();

		List<Booking> bookings = switch (safeState) {
			case ALL -> bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
			case CURRENT -> bookingRepository.findCurrentByBookerId(userId, now);
			case PAST -> bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
			case FUTURE -> bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, now);
			case WAITING -> bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
			case REJECTED -> bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
		};

		List<BookingDto> dtos = bookings.stream()
				.map(this::toDto)
				.toList();
		log.debug("returned bookingsCount={} forBookerId={} state={}", dtos.size(), userId, safeState);
		return dtos;
	}

	@Override
	public List<BookingDto> getAllByOwner(Long userId, BookingState state) {
		userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));

		BookingState safeState = state != null ? state : BookingState.ALL;
		LocalDateTime now = LocalDateTime.now();

		List<Booking> bookings = switch (safeState) {
			case ALL -> bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
			case CURRENT -> bookingRepository.findCurrentByOwnerId(userId, now);
			case PAST -> bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, now);
			case FUTURE -> bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, now);
			case WAITING -> bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
			case REJECTED -> bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
		};

		List<BookingDto> dtos = bookings.stream()
				.map(this::toDto)
				.toList();
		log.debug("returned bookingsCount={} forOwnerId={} state={}", dtos.size(), userId, safeState);
		return dtos;
	}

	private BookingDto toDto(Booking booking) {
		BookingDto dto = new BookingDto();
		dto.setId(booking.getId());
		dto.setStart(booking.getStart());
		dto.setEnd(booking.getEnd());
		dto.setStatus(booking.getStatus());

		if (booking.getItem() != null) {
			BookingDto.ItemInfo item = new BookingDto.ItemInfo();
			item.setId(booking.getItem().getId());
			item.setName(booking.getItem().getName());
			dto.setItem(item);
		}
		if (booking.getBooker() != null) {
			BookingDto.BookerInfo booker = new BookingDto.BookerInfo();
			booker.setId(booking.getBooker().getId());
			dto.setBooker(booker);
		}
		return dto;
	}
}
