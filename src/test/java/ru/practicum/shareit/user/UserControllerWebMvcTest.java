package ru.practicum.shareit.user;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import ru.practicum.shareit.user.dto.UserDto;

@WebMvcTest(UserController.class)
class UserControllerWebMvcTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private UserService userService;

	private ResultActions performGetById(long userId) throws Exception {
		return mockMvc.perform(get("/users/{userId}", userId));
	}

	private UserDto userDto(Long id, String name, String email) {
		UserDto dto = new UserDto();
		dto.setId(id);
		dto.setName(name);
		dto.setEmail(email);
		return dto;
	}

	@Test
	@DisplayName("GET /users/{id}: возвращает пользователя по id")
	void getById_returnsUser() throws Exception {
		UserDto userDto = userDto(1L, "Ivan", "ivan@example.com");
		given(userService.getById(eq(1L))).willReturn(userDto);

		performGetById(1L)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1L))
				.andExpect(jsonPath("$.name").value("Ivan"))
				.andExpect(jsonPath("$.email").value("ivan@example.com"));
	}

	@Test
	@DisplayName("GET /users/{id}: 404 если пользователя нет")
	void getById_notFound() throws Exception {
		given(userService.getById(eq(999L))).willThrow(new IllegalArgumentException("User not found"));

		performGetById(999L)
				.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("GET /users: возвращает список пользователей")
	void getAll_returnsList() throws Exception {
		given(userService.getAll()).willReturn(List.of(
				userDto(1L, "Ivan", "ivan@example.com"),
				userDto(2L, "Petr", "petr@example.com")
		));

		mockMvc.perform(get("/users"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(1L))
				.andExpect(jsonPath("$[1].id").value(2L));
	}

	@Test
	@DisplayName("POST /users: создаёт пользователя")
	void create_returnsUser() throws Exception {
		UserDto request = userDto(null, "Ivan", "ivan@example.com");
		UserDto response = userDto(1L, "Ivan", "ivan@example.com");

		given(userService.create(eq(request))).willReturn(response);

		mockMvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1L))
				.andExpect(jsonPath("$.name").value("Ivan"))
				.andExpect(jsonPath("$.email").value("ivan@example.com"));
	}

	@Test
	@DisplayName("PATCH /users/{id}: обновляет пользователя")
	void update_returnsUser() throws Exception {
		UserDto request = userDto(null, "New", null);
		UserDto response = userDto(1L, "New", "ivan@example.com");

		given(userService.update(eq(1L), eq(request))).willReturn(response);

		mockMvc.perform(patch("/users/{userId}", 1L)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1L))
				.andExpect(jsonPath("$.name").value("New"))
				.andExpect(jsonPath("$.email").value("ivan@example.com"));
	}

	@Test
	@DisplayName("DELETE /users/{id}: удаляет пользователя")
	void delete_ok() throws Exception {
		willDoNothing().given(userService).delete(eq(1L));

		mockMvc.perform(delete("/users/{userId}", 1L))
				.andExpect(status().isOk());
	}

	@Test
	@DisplayName("DELETE /users/{id}: 404 если пользователя нет")
	void delete_notFound() throws Exception {
		willThrow(new IllegalArgumentException("User not found")).given(userService).delete(eq(999L));

		mockMvc.perform(delete("/users/{userId}", 999L))
				.andExpect(status().isNotFound());
	}
}
