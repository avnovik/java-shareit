package ru.practicum.shareit.booking.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

@JsonTest
class BookingCreateDtoJsonTest {

	@Autowired
	private JacksonTester<BookingCreateDto> json;

	@Test
	@DisplayName("serialize: пишет itemId/start/end")
	void serialize_ok() throws Exception {
		BookingCreateDto dto = new BookingCreateDto();
		dto.setItemId(1L);
		dto.setStart(LocalDateTime.of(2026, 1, 2, 3, 4, 5));
		dto.setEnd(LocalDateTime.of(2026, 1, 3, 3, 4, 5));

		var content = json.write(dto);

		assertThat(content).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
		assertThat(content).extractingJsonPathStringValue("$.start").isEqualTo("2026-01-02T03:04:05");
		assertThat(content).extractingJsonPathStringValue("$.end").isEqualTo("2026-01-03T03:04:05");
	}

	@Test
	@DisplayName("deserialize: читает itemId/start/end")
	void deserialize_ok() throws Exception {
		String body = "{\n" +
				"  \"itemId\": 2,\n" +
				"  \"start\": \"2026-02-01T10:00:00\",\n" +
				"  \"end\": \"2026-02-02T10:00:00\"\n" +
				"}";

		BookingCreateDto dto = json.parseObject(body);
		assertThat(dto.getItemId()).isEqualTo(2L);
		assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2026, 2, 1, 10, 0, 0));
		assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2026, 2, 2, 10, 0, 0));
	}
}
