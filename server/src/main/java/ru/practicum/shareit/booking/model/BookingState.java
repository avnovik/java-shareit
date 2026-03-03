package ru.practicum.shareit.booking.model;

/**
 * Состояния бронирований для фильтрации в API.
 */
public enum BookingState {
	ALL,
	CURRENT,
	PAST,
	FUTURE,
	WAITING,
	REJECTED
}
