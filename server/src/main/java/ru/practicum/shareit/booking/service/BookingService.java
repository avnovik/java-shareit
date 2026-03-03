package ru.practicum.shareit.booking.service;

import java.util.List;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;

/**
 * Сервис для операций с бронированиями.
 */
public interface BookingService {
	BookingDto create(Long userId, BookingCreateDto bookingCreateDto);

	BookingDto approve(Long userId, Long bookingId, boolean approved);

	BookingDto getById(Long userId, Long bookingId);

	List<BookingDto> getAllByBooker(Long userId, BookingState state);

	List<BookingDto> getAllByOwner(Long userId, BookingState state);
}
