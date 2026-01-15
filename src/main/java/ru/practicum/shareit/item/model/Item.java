package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

/**
 * Модель вещи.
 * Описывает вещь, которую владелец может сдавать в аренду.
 */
@Data
public class Item {
	private Long id;
	private String name;
	private String description;
	private Boolean available;
	private User owner;
	private ItemRequest request;
}
