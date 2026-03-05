package ru.practicum.shareit.booking;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;

@WebMvcTest(BookingController.class)
class BookingControllerWebMvcTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private BookingService bookingService;

	private BookingCreateDto bookingCreateDto(Long itemId, LocalDateTime start, LocalDateTime end) {
		BookingCreateDto dto = new BookingCreateDto();
		dto.setItemId(itemId);
		dto.setStart(start);
		dto.setEnd(end);
		return dto;
	}

	private BookingDto bookingDto(Long id, Long itemId, String itemName, Long bookerId,
						 LocalDateTime start, LocalDateTime end, BookingStatus status) {
		BookingDto dto = new BookingDto();
		dto.setId(id);

		BookingDto.ItemInfo itemInfo = new BookingDto.ItemInfo();
		itemInfo.setId(itemId);
		itemInfo.setName(itemName);
		dto.setItem(itemInfo);

		BookingDto.BookerInfo bookerInfo = new BookingDto.BookerInfo();
		bookerInfo.setId(bookerId);
		dto.setBooker(bookerInfo);

		dto.setStart(start);
		dto.setEnd(end);
		dto.setStatus(status);
		return dto;
	}

	@Test
	@DisplayName("POST /bookings: создаёт бронирование")
	void create_returnsBooking() throws Exception {
		BookingCreateDto request = bookingCreateDto(33L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
		BookingDto response = bookingDto(1L, 33L, "Drill", 10L, request.getStart(), request.getEnd(), BookingStatus.WAITING);

		given(bookingService.create(eq(10L), eq(request))).willReturn(response);

		mockMvc.perform(post("/bookings")
					.header("X-Sharer-User-Id", 10L)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1L))
				.andExpect(jsonPath("$.item.id").value(33L))
				.andExpect(jsonPath("$.booker.id").value(10L))
				.andExpect(jsonPath("$.status").value("WAITING"));
	}

	@Test
	@DisplayName("PATCH /bookings/{id}: подтверждает бронирование")
	void approve_returnsBooking() throws Exception {
		BookingDto response = bookingDto(1L, 33L, "Drill", 10L,
				LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), BookingStatus.APPROVED);

		given(bookingService.approve(eq(20L), eq(1L), eq(true))).willReturn(response);

		mockMvc.perform(patch("/bookings/{bookingId}", 1L)
					.header("X-Sharer-User-Id", 20L)
					.param("approved", "true"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1L))
				.andExpect(jsonPath("$.status").value("APPROVED"));
	}

	@Test
	@DisplayName("GET /bookings/{id}: возвращает бронирование")
	void getById_returnsBooking() throws Exception {
		BookingDto response = bookingDto(1L, 33L, "Drill", 10L,
				LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), BookingStatus.WAITING);

		given(bookingService.getById(eq(10L), eq(1L))).willReturn(response);

		mockMvc.perform(get("/bookings/{bookingId}", 1L)
					.header("X-Sharer-User-Id", 10L))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1L));
	}

	@Test
	@DisplayName("GET /bookings: возвращает список бронирований пользователя")
	void getAllByBooker_returnsList() throws Exception {
		BookingDto response = bookingDto(1L, 33L, "Drill", 10L,
				LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), BookingStatus.WAITING);

		given(bookingService.getAllByBooker(eq(10L), eq(BookingState.ALL))).willReturn(List.of(response));

		mockMvc.perform(get("/bookings")
					.header("X-Sharer-User-Id", 10L)
					.param("state", "ALL"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(1L));
	}

	@Test
	@DisplayName("GET /bookings: 400 если state неизвестный")
	void getAllByBooker_unknownState_badRequest() throws Exception {
		mockMvc.perform(get("/bookings")
					.header("X-Sharer-User-Id", 10L)
					.param("state", "UNKNOWN"))
				.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("GET /bookings/owner: возвращает список бронирований владельца")
	void getAllByOwner_returnsList() throws Exception {
		BookingDto response = bookingDto(1L, 33L, "Drill", 10L,
				LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), BookingStatus.WAITING);

		given(bookingService.getAllByOwner(eq(20L), eq(BookingState.ALL))).willReturn(List.of(response));

		mockMvc.perform(get("/bookings/owner")
					.header("X-Sharer-User-Id", 20L)
					.param("state", "ALL"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(1L));
	}
}
