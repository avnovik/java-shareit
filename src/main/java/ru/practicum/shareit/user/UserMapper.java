package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public final class UserMapper {
	private UserMapper() {
	}

	/**
	 * Преобразует {@link User} в {@link UserDto}.
	 */
	public static UserDto toUserDto(User user) {
		if (user == null) {
			return null;
		}

		UserDto dto = new UserDto();
		dto.setId(user.getId());
		dto.setName(user.getName());
		dto.setEmail(user.getEmail());
		return dto;
	}
}
