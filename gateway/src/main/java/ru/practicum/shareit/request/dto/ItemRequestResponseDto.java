package ru.practicum.shareit.request.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

/**
 * DTO ответа на запрос вещи.
 */
@Data
public class ItemRequestResponseDto {
	private Long id;
	private String description;
	private LocalDateTime created;
	private List<ItemRequestItemDto> items;
}
