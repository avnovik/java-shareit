package ru.practicum.shareit.item.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Короткий DTO бронирования для отображения в данных вещи владельцу.
 */
@Data
public class ItemBookingDto {
	private Long id;
	private Long bookerId;
	private LocalDateTime start;
	private LocalDateTime end;
}
