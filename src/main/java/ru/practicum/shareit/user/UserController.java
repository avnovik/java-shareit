package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

/**
 * REST-контроллер для работы с пользователями.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
	private final UserService userService;

	@PostMapping
	public UserDto create(@Valid @RequestBody UserDto userDto) {
		log.debug("POST /users");
		return userService.create(userDto);
	}

	@PatchMapping("/{userId}")
	public UserDto update(@PathVariable Long userId, @RequestBody UserDto userDto) {
		log.debug("PATCH /users/{}", userId);
		return userService.update(userId, userDto);
	}

	@GetMapping("/{userId}")
	public UserDto getById(@PathVariable Long userId) {
		log.debug("GET /users/{}", userId);
		return userService.getById(userId);
	}

	@GetMapping
	public List<UserDto> getAll() {
		log.debug("GET /users");
		return userService.getAll();
	}

	@DeleteMapping("/{userId}")
	public void delete(@PathVariable Long userId) {
		log.debug("DELETE /users/{}", userId);
		userService.delete(userId);
	}
}
