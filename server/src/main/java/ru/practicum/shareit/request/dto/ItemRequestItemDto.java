package ru.practicum.shareit.request.dto;

import lombok.Data;

/**
 * Короткий DTO вещи для ответа на запрос.
 */
@Data
public class ItemRequestItemDto {
	private Long id;
	private String name;
	private Long ownerId;
}
