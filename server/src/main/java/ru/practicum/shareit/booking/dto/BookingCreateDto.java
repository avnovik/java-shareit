package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * DTO для создания бронирования.
 */
@Data
public class BookingCreateDto {
	private Long itemId;
	private LocalDateTime start;
	private LocalDateTime end;
}
