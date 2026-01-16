package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO вещи.
 * Используется для обмена данными о вещи через REST.
 */
@Data
public class ItemDto {
	private Long id;

	@NotBlank
	private String name;

	@NotBlank
	private String description;

	@NotNull
	private Boolean available;
	private Long ownerId;
	private Long requestId;
}
