package ru.practicum.shareit.item.dto;

import java.time.LocalDateTime;

import lombok.Data;

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
