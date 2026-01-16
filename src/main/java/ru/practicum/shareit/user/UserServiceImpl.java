package ru.practicum.shareit.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

/**
 * In-memory реализация {@link UserService}.
 */
@Service
public class UserServiceImpl implements UserService {
	private final Map<Long, User> users = new ConcurrentHashMap<>();
	private final AtomicLong nextId = new AtomicLong(1);

	@Override
	public UserDto create(UserDto userDto) {
		for (User existing : users.values()) {
			if (existing.getEmail() != null && existing.getEmail().equals(userDto.getEmail())) {
				throw new IllegalStateException("Email already in use: " + userDto.getEmail());
			}
		}

		User user = new User();
		Long id = nextId.getAndIncrement();
		user.setId(id);
		user.setName(userDto.getName());
		user.setEmail(userDto.getEmail());

		users.put(id, user);
		return UserMapper.toUserDto(user);
	}

	@Override
	public UserDto update(Long userId, UserDto userDto) {
		User existing = users.get(userId);
		if (existing == null) {
			throw new IllegalArgumentException("User with id=" + userId + " not found");
		}

		if (userDto.getName() != null) {
			existing.setName(userDto.getName());
		}
		if (userDto.getEmail() != null) {
			for (Map.Entry<Long, User> entry : users.entrySet()) {
				Long otherUserId = entry.getKey();
				User otherUser = entry.getValue();
				if (!otherUserId.equals(userId)
							&& otherUser.getEmail() != null
							&& otherUser.getEmail().equals(userDto.getEmail())) {
					throw new IllegalStateException("Email already in use: " + userDto.getEmail());
				}
			}
			existing.setEmail(userDto.getEmail());
		}

		return UserMapper.toUserDto(existing);
	}

	@Override
	public UserDto getById(Long userId) {
		User user = users.get(userId);
		if (user == null) {
			throw new IllegalArgumentException("User with id=" + userId + " not found");
		}
		return UserMapper.toUserDto(user);
	}

	@Override
	public List<UserDto> getAll() {
		List<UserDto> result = new ArrayList<>();
		for (User user : users.values()) {
			result.add(UserMapper.toUserDto(user));
		}
		return result;
	}

	@Override
	public void delete(Long userId) {
		User removed = users.remove(userId);
		if (removed == null) {
			throw new IllegalArgumentException("User with id=" + userId + " not found");
		}
	}
}
