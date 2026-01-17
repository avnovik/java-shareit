package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserMapper {

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

	/**
	 * Преобразует {@link UserDto} в {@link User}.
	 */
	public static User toUser(UserDto dto) {
		if (dto == null) {
			return null;
		}

		User user = new User();
		user.setName(dto.getName());
		user.setEmail(dto.getEmail());
		return user;
	}
}
