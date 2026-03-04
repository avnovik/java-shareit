package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO пользователя.
 * Используется для обмена данными о пользователе через REST.
 */
@Data
public class UserDto {
	private Long id;

	@NotBlank
	@Size(max = 100)
	private String name;

	@NotBlank
	@Email
	private String email;
}
