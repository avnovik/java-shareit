package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import lombok.Data;

import ru.practicum.shareit.booking.model.BookingStatus;

/**
 * DTO для ответа по бронированию.
 */
@Data
public class BookingDto {
	private Long id;
	private ItemInfo item;
	private BookerInfo booker;
	private LocalDateTime start;
	private LocalDateTime end;
	private BookingStatus status;

	@Data
	public static class ItemInfo {
		private Long id;
		private String name;
	}

	@Data
	public static class BookerInfo {
		private Long id;
	}
}
