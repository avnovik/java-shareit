package ru.practicum.shareit.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import ru.practicum.shareit.user.dto.UserDto;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerSpringBootTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private UserDto createUser(String name, String email) throws Exception {
		UserDto request = new UserDto();
		request.setName(name);
		request.setEmail(email);

		MvcResult result = mockMvc.perform(post("/users")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andReturn();

		return objectMapper.readValue(result.getResponse().getContentAsByteArray(), UserDto.class);
	}

	@Test
	@DisplayName("Users: создать и получить пользователя")
	void createAndGet() throws Exception {
		long suffix = System.nanoTime();
		UserDto created = createUser("Ivan", "ivan-" + suffix + "@ya.ru");

		mockMvc.perform(get("/users/{userId}", created.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(created.getId()))
				.andExpect(jsonPath("$.name").value("Ivan"))
				.andExpect(jsonPath("$.email").value("ivan-" + suffix + "@ya.ru"));
	}

	@Test
	@DisplayName("Users: обновить пользователя")
	void update() throws Exception {
		long suffix = System.nanoTime();
		UserDto created = createUser("Ivan", "ivan-" + suffix + "@ya.ru");

		UserDto update = new UserDto();
		update.setName("NewName");

		mockMvc.perform(patch("/users/{userId}", created.getId())
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(update)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(created.getId()))
				.andExpect(jsonPath("$.name").value("NewName"))
				.andExpect(jsonPath("$.email").value("ivan-" + suffix + "@ya.ru"));
	}
}
