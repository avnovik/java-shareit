package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
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
	@FutureOrPresent
	private LocalDateTime start;
	@NotNull
	@Future
	private LocalDateTime end;
}
