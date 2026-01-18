package ru.practicum.shareit.request;

import java.time.LocalDateTime;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

/**
 * Модель запроса вещи.
 * Используется, когда нужной вещи нет и пользователь оставляет запрос.
 */
@Data
public class ItemRequest {
	private Long id;
	private String description;
	private User requestor;
	private LocalDateTime created;
}
