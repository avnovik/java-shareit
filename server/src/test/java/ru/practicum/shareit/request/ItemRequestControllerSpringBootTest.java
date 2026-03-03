package ru.practicum.shareit.request;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.user.dto.UserDto;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ItemRequestControllerSpringBootTest {

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
	@DisplayName("Requests: создать запрос и получить его через /requests, /requests/all и /requests/{id}")
	void requests_endToEnd() throws Exception {
		long suffix = System.nanoTime();
		UserDto requestor = createUser("RqName", "rq-" + suffix + "@ya.ru");
		UserDto other = createUser("OtherName", "ot-" + suffix + "@ya.ru");

		ItemRequestCreateDto createDto = new ItemRequestCreateDto();
		createDto.setDescription("Need drill");

		MvcResult created = mockMvc.perform(post("/requests")
					.header("X-Sharer-User-Id", requestor.getId())
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(createDto)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.description").value("Need drill"))
				.andExpect(jsonPath("$.items").isArray())
				.andReturn();

		Long requestId = objectMapper.readTree(created.getResponse().getContentAsByteArray()).get("id").asLong();

		mockMvc.perform(get("/requests")
					.header("X-Sharer-User-Id", requestor.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(requestId))
				.andExpect(jsonPath("$[0].description").value("Need drill"));

		mockMvc.perform(get("/requests/all")
					.header("X-Sharer-User-Id", other.getId())
					.param("from", "0")
					.param("size", "10"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(requestId));

		mockMvc.perform(get("/requests/all")
					.header("X-Sharer-User-Id", requestor.getId())
					.param("from", "0")
					.param("size", "10"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$").isEmpty());

		mockMvc.perform(get("/requests/{requestId}", requestId)
					.header("X-Sharer-User-Id", other.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(requestId))
				.andExpect(jsonPath("$.description").value("Need drill"));
	}
}
