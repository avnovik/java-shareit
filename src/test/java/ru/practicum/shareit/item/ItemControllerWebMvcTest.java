package ru.practicum.shareit.item;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

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
}
