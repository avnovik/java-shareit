package ru.practicum.shareit.user;

import java.util.List;

import ru.practicum.shareit.user.dto.UserDto;

/**
 * Сервис для операций с пользователями.
 */
public interface UserService {

	/** Создаёт пользователя. */
	UserDto create(UserDto userDto);

	/** Обновляет пользователя. */
	UserDto update(Long userId, UserDto userDto);

	/** Возвращает пользователя по id. */
	UserDto getById(Long userId);

	/** Возвращает всех пользователей. */
	List<UserDto> getAll();

	/** Удаляет пользователя по id. */
	void delete(Long userId);
}
