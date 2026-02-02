package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO для создания бронирования.
 */
@Data
public class BookingCreateDto {
	@NotNull
	private Long itemId;
	@NotNull
	private LocalDateTime start;
	@NotNull
	private LocalDateTime end;
}
