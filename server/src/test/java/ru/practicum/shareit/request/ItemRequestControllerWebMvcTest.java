package ru.practicum.shareit.request;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestItemDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerWebMvcTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private ItemRequestService itemRequestService;

	private ItemRequestCreateDto createDto(String description) {
		ItemRequestCreateDto dto = new ItemRequestCreateDto();
		dto.setDescription(description);
		return dto;
	}

	private ItemRequestResponseDto responseDto(Long id, String description, LocalDateTime created,
									List<ItemRequestItemDto> items) {
		ItemRequestResponseDto dto = new ItemRequestResponseDto();
		dto.setId(id);
		dto.setDescription(description);
		dto.setCreated(created);
		dto.setItems(items);
		return dto;
	}

	private ItemRequestItemDto itemDto(Long id, String name, Long ownerId) {
		ItemRequestItemDto dto = new ItemRequestItemDto();
		dto.setId(id);
		dto.setName(name);
		dto.setOwnerId(ownerId);
		return dto;
	}

	@Test
	@DisplayName("POST /requests: создаёт запрос")
	void create_returnsRequest() throws Exception {
		ItemRequestCreateDto request = createDto("Need drill");
		ItemRequestResponseDto response = responseDto(1L, "Need drill", LocalDateTime.now(), List.of());

		given(itemRequestService.create(eq(10L), eq(request))).willReturn(response);

		mockMvc.perform(post("/requests")
					.header("X-Sharer-User-Id", 10L)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1L))
				.andExpect(jsonPath("$.description").value("Need drill"));
	}

	@Test
	@DisplayName("GET /requests: возвращает список своих запросов")
	void getAllByRequestor_returnsList() throws Exception {
		ItemRequestResponseDto response = responseDto(1L, "Need drill", LocalDateTime.now(), List.of(
				itemDto(33L, "Drill", 20L)
		));

		given(itemRequestService.getAllByRequestor(eq(10L))).willReturn(List.of(response));

		mockMvc.perform(get("/requests")
					.header("X-Sharer-User-Id", 10L))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(1L))
				.andExpect(jsonPath("$[0].items[0].id").value(33L));
	}

	@Test
	@DisplayName("GET /requests/all: возвращает список чужих запросов")
	void getAllOther_returnsList() throws Exception {
		ItemRequestResponseDto response = responseDto(1L, "Need drill", LocalDateTime.now(), List.of());
		given(itemRequestService.getAllOther(eq(10L), eq(0), eq(10))).willReturn(List.of(response));

		mockMvc.perform(get("/requests/all")
					.header("X-Sharer-User-Id", 10L)
					.param("from", "0")
					.param("size", "10"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(1L));
	}

	@Test
	@DisplayName("GET /requests/{id}: возвращает запрос по id")
	void getById_returnsRequest() throws Exception {
		ItemRequestResponseDto response = responseDto(1L, "Need drill", LocalDateTime.now(), List.of());
		given(itemRequestService.getById(eq(10L), eq(1L))).willReturn(response);

		mockMvc.perform(get("/requests/{requestId}", 1L)
					.header("X-Sharer-User-Id", 10L))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1L));
	}
}
