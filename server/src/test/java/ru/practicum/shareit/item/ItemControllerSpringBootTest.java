package ru.practicum.shareit.item;

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
import com.fasterxml.jackson.databind.JsonNode;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.user.dto.UserDto;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ItemControllerSpringBootTest {

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

	private long createRequest(long userId, String description) throws Exception {
		ItemRequestCreateDto dto = new ItemRequestCreateDto();
		dto.setDescription(description);

		MvcResult result = mockMvc.perform(post("/requests")
					.header("X-Sharer-User-Id", userId)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(dto)))
				.andExpect(status().isOk())
				.andReturn();

		JsonNode node = objectMapper.readTree(result.getResponse().getContentAsByteArray());
		return node.get("id").asLong();
	}

	private long createItem(long ownerId, ItemDto dto) throws Exception {
		MvcResult result = mockMvc.perform(post("/items")
					.header("X-Sharer-User-Id", ownerId)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(dto)))
				.andExpect(status().isOk())
				.andReturn();

		JsonNode node = objectMapper.readTree(result.getResponse().getContentAsByteArray());
		return node.get("id").asLong();
	}

	@Test
	@DisplayName("Items: создать вещь с requestId и без requestId")
	void create_withAndWithoutRequestId() throws Exception {
		long suffix = System.nanoTime();
		UserDto owner = createUser("Owner", "owner-" + suffix + "@ya.ru");
		UserDto requestor = createUser("Req", "req-" + suffix + "@ya.ru");

		long requestId = createRequest(requestor.getId(), "Need drill");

		ItemDto withoutRequest = new ItemDto();
		withoutRequest.setName("Saw");
		withoutRequest.setDescription("Hand saw");
		withoutRequest.setAvailable(true);
		withoutRequest.setRequestId(null);
		long itemId1 = createItem(owner.getId(), withoutRequest);

		mockMvc.perform(get("/items/{itemId}", itemId1)
					.header("X-Sharer-User-Id", owner.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(itemId1))
				.andExpect(jsonPath("$.requestId").doesNotExist());

		ItemDto withRequest = new ItemDto();
		withRequest.setName("Drill");
		withRequest.setDescription("Cordless drill");
		withRequest.setAvailable(true);
		withRequest.setRequestId(requestId);
		long itemId2 = createItem(owner.getId(), withRequest);

		mockMvc.perform(get("/items/{itemId}", itemId2)
					.header("X-Sharer-User-Id", owner.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(itemId2))
				.andExpect(jsonPath("$.requestId").value(requestId));

		mockMvc.perform(get("/requests/{requestId}", requestId)
					.header("X-Sharer-User-Id", requestor.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(requestId))
				.andExpect(jsonPath("$.items").isArray())
				.andExpect(jsonPath("$.items[0].id").value(itemId2))
				.andExpect(jsonPath("$.items[0].name").value("Drill"))
				.andExpect(jsonPath("$.items[0].ownerId").value(owner.getId()));
	}
}
