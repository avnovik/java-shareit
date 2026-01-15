package ru.practicum.shareit.item.dto;

import lombok.Data;

/**
 * DTO вещи.
 * Используется для обмена данными о вещи через REST.
 */
@Data
public class ItemDto {
	private Long id;
	private String name;
	private String description;
	private Boolean available;
	private Long ownerId;
	private Long requestId;
}
