package ru.practicum.shareit.booking;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BookingControllerSpringBootTest {

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

	private long createItem(long ownerId) throws Exception {
		ItemDto item = new ItemDto();
		item.setName("Drill");
		item.setDescription("Cordless drill");
		item.setAvailable(true);

		MvcResult result = mockMvc.perform(post("/items")
					.header("X-Sharer-User-Id", ownerId)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(item)))
				.andExpect(status().isOk())
				.andReturn();

		JsonNode node = objectMapper.readTree(result.getResponse().getContentAsByteArray());
		return node.get("id").asLong();
	}

	@Test
	@DisplayName("Bookings: создать бронирование и подтвердить")
	void createAndApprove() throws Exception {
		long suffix = System.nanoTime();
		UserDto owner = createUser("Owner", "owner-" + suffix + "@ya.ru");
		UserDto booker = createUser("Booker", "booker-" + suffix + "@ya.ru");

		long itemId = createItem(owner.getId());

		BookingCreateDto bookingCreateDto = new BookingCreateDto();
		bookingCreateDto.setItemId(itemId);
		bookingCreateDto.setStart(LocalDateTime.now().plusDays(1));
		bookingCreateDto.setEnd(LocalDateTime.now().plusDays(2));

		MvcResult created = mockMvc.perform(post("/bookings")
					.header("X-Sharer-User-Id", booker.getId())
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(bookingCreateDto)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("WAITING"))
				.andReturn();

		long bookingId = objectMapper.readTree(created.getResponse().getContentAsByteArray()).get("id").asLong();

		mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
					.header("X-Sharer-User-Id", owner.getId())
					.param("approved", "true"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(bookingId))
				.andExpect(jsonPath("$.status").value("APPROVED"));

		mockMvc.perform(get("/bookings/{bookingId}", bookingId)
					.header("X-Sharer-User-Id", booker.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(bookingId))
				.andExpect(jsonPath("$.status").value("APPROVED"));
	}
}
