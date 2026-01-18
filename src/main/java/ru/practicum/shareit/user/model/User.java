package ru.practicum.shareit.user.model;

import lombok.Data;

/**
 * Модель пользователя.
 * Хранит базовую информацию о пользователе: идентификатор, имя и email.
 */
@Data
public class User {
	private Long id;
	private String name;
	private String email;
}
