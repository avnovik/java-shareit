package ru.practicum.shareit.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceImplUnitTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserServiceImpl userService;

	private UserDto dto(String name, String email) {
		UserDto dto = new UserDto();
		dto.setName(name);
		dto.setEmail(email);
		return dto;
	}

	private User user(Long id, String name, String email) {
		User u = new User();
		u.setId(id);
		u.setName(name);
		u.setEmail(email);
		return u;
	}

	@Test
	@DisplayName("create: 409 если email занят")
	void create_emailConflict() {
		given(userRepository.existsByEmail(eq("a@b"))).willReturn(true);
		assertThrows(IllegalStateException.class, () -> userService.create(dto("n", "a@b")));
		verify(userRepository, never()).save(any());
	}

	@Test
	@DisplayName("create: создаёт пользователя")
	void create_ok() {
		given(userRepository.existsByEmail(eq("a@b"))).willReturn(false);
		given(userRepository.save(any())).willReturn(user(1L, "n", "a@b"));
		UserDto created = userService.create(dto("n", "a@b"));
		assertEquals(1L, created.getId());
		assertEquals("a@b", created.getEmail());
	}

	@Test
	@DisplayName("update: 404 если пользователя нет")
	void update_notFound() {
		given(userRepository.findById(eq(1L))).willReturn(Optional.empty());
		assertThrows(NotFoundException.class, () -> userService.update(1L, dto("n", null)));
	}

	@Test
	@DisplayName("update: 409 если email занят")
	void update_emailConflict() {
		given(userRepository.findById(eq(1L))).willReturn(Optional.of(user(1L, "old", "old@b")));
		given(userRepository.existsByEmailAndIdNot(eq("new@b"), eq(1L))).willReturn(true);
		UserDto patch = new UserDto();
		patch.setEmail("new@b");
		assertThrows(IllegalStateException.class, () -> userService.update(1L, patch));
		verify(userRepository, never()).save(any());
	}

	@Test
	@DisplayName("update: обновляет имя и email")
	void update_ok() {
		given(userRepository.findById(eq(1L))).willReturn(Optional.of(user(1L, "old", "old@b")));
		given(userRepository.existsByEmailAndIdNot(eq("new@b"), eq(1L))).willReturn(false);
		given(userRepository.save(any())).willAnswer(inv -> inv.getArgument(0, User.class));

		UserDto patch = new UserDto();
		patch.setName("new");
		patch.setEmail("new@b");

		UserDto updated = userService.update(1L, patch);
		assertEquals(1L, updated.getId());
		assertEquals("new", updated.getName());
		assertEquals("new@b", updated.getEmail());
	}

	@Test
	@DisplayName("getById: 404 если пользователя нет")
	void getById_notFound() {
		given(userRepository.findById(eq(1L))).willReturn(Optional.empty());
		assertThrows(NotFoundException.class, () -> userService.getById(1L));
	}

	@Test
	@DisplayName("getAll: возвращает список")
	void getAll_ok() {
		given(userRepository.findAll()).willReturn(List.of(user(1L, "n", "a@b")));
		List<UserDto> dtos = userService.getAll();
		assertEquals(1, dtos.size());
		assertEquals(1L, dtos.get(0).getId());
	}

	@Test
	@DisplayName("delete: 404 если пользователя нет")
	void delete_notFound() {
		given(userRepository.existsById(eq(1L))).willReturn(false);
		assertThrows(NotFoundException.class, () -> userService.delete(1L));
		verify(userRepository, never()).deleteById(any());
	}

	@Test
	@DisplayName("delete: удаляет пользователя")
	void delete_ok() {
		given(userRepository.existsById(eq(1L))).willReturn(true);
		userService.delete(1L);
		verify(userRepository).deleteById(eq(1L));
	}
}
