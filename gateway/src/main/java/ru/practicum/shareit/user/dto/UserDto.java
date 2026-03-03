package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO пользователя.
 * Используется для обмена данными о пользователе через REST.
 */
@Data
public class UserDto {
	private Long id;
	private String name;

	@NotBlank
	@Email
	private String email;
}
