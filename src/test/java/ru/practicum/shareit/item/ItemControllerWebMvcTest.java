package ru.practicum.shareit.item;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;

@WebMvcTest(ItemController.class)
class ItemControllerWebMvcTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private ItemService itemService;

	private ItemDto itemDto(Long id, String name, String description, Boolean available, Long ownerId, Long requestId) {
		ItemDto dto = new ItemDto();
		dto.setId(id);
		dto.setName(name);
		dto.setDescription(description);
		dto.setAvailable(available);
		dto.setOwnerId(ownerId);
		dto.setRequestId(requestId);
		return dto;
	}

	@Test
	@DisplayName("POST /items: создаёт вещь")
	void create_returnsItem() throws Exception {
		ItemDto request = itemDto(null, "Drill", "Cordless drill", true, null, null);
		ItemDto response = itemDto(1L, "Drill", "Cordless drill", true, 10L, null);

		given(itemService.create(eq(10L), eq(request))).willReturn(response);

		mockMvc.perform(post("/items")
						.header("X-Sharer-User-Id", 10L)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1L))
				.andExpect(jsonPath("$.name").value("Drill"))
				.andExpect(jsonPath("$.description").value("Cordless drill"))
				.andExpect(jsonPath("$.available").value(true))
				.andExpect(jsonPath("$.ownerId").value(10L));
	}

	@Test
	@DisplayName("PATCH /items/{id}: обновляет вещь")
	void update_returnsItem() throws Exception {
		ItemDto request = itemDto(null, "New", null, null, null, null);
		ItemDto response = itemDto(1L, "New", "Cordless drill", true, 10L, null);

		given(itemService.update(eq(10L), eq(1L), eq(request))).willReturn(response);

		mockMvc.perform(patch("/items/{itemId}", 1L)
						.header("X-Sharer-User-Id", 10L)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1L))
				.andExpect(jsonPath("$.name").value("New"))
				.andExpect(jsonPath("$.ownerId").value(10L));
	}

	@Test
	@DisplayName("PATCH /items/{id}: 403 если не владелец")
	void update_forbidden() throws Exception {
		ItemDto request = itemDto(null, "New", null, null, null, null);

		given(itemService.update(eq(11L), eq(1L), eq(request))).willThrow(new SecurityException("Only owner"));

		mockMvc.perform(patch("/items/{itemId}", 1L)
						.header("X-Sharer-User-Id", 11L)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("GET /items/{id}: возвращает вещь")
	void getById_returnsItem() throws Exception {
		ItemDto response = itemDto(1L, "Drill", "Cordless drill", true, 10L, null);
		given(itemService.getById(eq(1L))).willReturn(response);

		mockMvc.perform(get("/items/{itemId}", 1L))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1L))
				.andExpect(jsonPath("$.name").value("Drill"));
	}

	@Test
	@DisplayName("GET /items/{id}: 404 если вещи нет")
	void getById_notFound() throws Exception {
		given(itemService.getById(eq(999L))).willThrow(new NotFoundException("Item not found"));

		mockMvc.perform(get("/items/{itemId}", 999L))
				.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("GET /items: возвращает список вещей владельца")
	void getAllByOwner_returnsList() throws Exception {
		given(itemService.getAllByOwner(eq(10L))).willReturn(List.of(
				itemDto(1L, "Drill", "Cordless drill", true, 10L, null),
				itemDto(2L, "Saw", "Hand saw", true, 10L, null)
		));

		mockMvc.perform(get("/items")
						.header("X-Sharer-User-Id", 10L))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(1L))
				.andExpect(jsonPath("$[1].id").value(2L));
	}

	@Test
	@DisplayName("GET /items/search: возвращает найденные доступные вещи")
	void search_returnsList() throws Exception {
		given(itemService.search(eq("drill"))).willReturn(List.of(
				itemDto(1L, "Drill", "Cordless drill", true, 10L, null)
		));

		mockMvc.perform(get("/items/search")
						.param("text", "drill"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(1L))
				.andExpect(jsonPath("$[0].name").value("Drill"));
	}

	@Test
	@DisplayName("GET /items/search: пустой text возвращает пустой список")
	void search_emptyText_returnsEmptyList() throws Exception {
		given(itemService.search(eq(""))).willReturn(List.of());

		mockMvc.perform(get("/items/search")
						.param("text", ""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$").isEmpty());
	}
}
