package ru.practicum.shareit.user.dto;

import lombok.Data;

/**
 * DTO пользователя.
 * Используется для обмена данными о пользователе через REST.
 */
@Data
public class UserDto {
	private Long id;
	private String name;
	private String email;
}
