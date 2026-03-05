package ru.practicum.shareit.booking;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

/**
 * REST-контроллер для операций с бронированиями.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
	private final BookingService bookingService;

	@PostMapping
	public BookingDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
						 @RequestBody BookingCreateDto bookingCreateDto) {
		log.debug("POST /bookings userId={}", userId);
		return bookingService.create(userId, bookingCreateDto);
	}

	@PatchMapping("/{bookingId}")
	public BookingDto approve(@RequestHeader("X-Sharer-User-Id") Long userId,
						 @PathVariable Long bookingId,
						 @RequestParam boolean approved) {
		log.debug("PATCH /bookings/{} userId={} approved={}", bookingId, userId, approved);
		return bookingService.approve(userId, bookingId, approved);
	}

	@GetMapping("/{bookingId}")
	public BookingDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
					  @PathVariable Long bookingId) {
		log.debug("GET /bookings/{} userId={}", bookingId, userId);
		return bookingService.getById(userId, bookingId);
	}

	@GetMapping
	public List<BookingDto> getAllByBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
						  @RequestParam(defaultValue = "ALL") BookingState state) {
		log.debug("GET /bookings userId={} state={}", userId, state);
		return bookingService.getAllByBooker(userId, state);
	}

	@GetMapping("/owner")
	public List<BookingDto> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
							 @RequestParam(defaultValue = "ALL") BookingState state) {
		log.debug("GET /bookings/owner userId={} state={}", userId, state);
		return bookingService.getAllByOwner(userId, state);
	}
}
