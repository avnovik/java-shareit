package ru.practicum.shareit.item.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

@JsonTest
class ItemBookingDtoJsonTest {

	@Autowired
	private JacksonTester<ItemBookingDto> json;

	@Test
	@DisplayName("serialize: пишет id/bookerId/start/end")
	void serialize_ok() throws Exception {
		ItemBookingDto dto = new ItemBookingDto();
		dto.setId(1L);
		dto.setBookerId(2L);
		dto.setStart(LocalDateTime.of(2026, 1, 1, 0, 0, 0));
		dto.setEnd(LocalDateTime.of(2026, 1, 2, 0, 0, 0));

		var content = json.write(dto);
		assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
		assertThat(content).extractingJsonPathNumberValue("$.bookerId").isEqualTo(2);
		assertThat(content).extractingJsonPathStringValue("$.start").isEqualTo("2026-01-01T00:00:00");
		assertThat(content).extractingJsonPathStringValue("$.end").isEqualTo("2026-01-02T00:00:00");
	}

	@Test
	@DisplayName("deserialize: читает id/bookerId/start/end")
	void deserialize_ok() throws Exception {
		String body = "{\n" +
				"  \"id\": 10,\n" +
				"  \"bookerId\": 11,\n" +
				"  \"start\": \"2026-04-01T01:02:03\",\n" +
				"  \"end\": \"2026-04-02T01:02:03\"\n" +
				"}";
		ItemBookingDto dto = json.parseObject(body);
		assertThat(dto.getId()).isEqualTo(10L);
		assertThat(dto.getBookerId()).isEqualTo(11L);
		assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2026, 4, 1, 1, 2, 3));
		assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2026, 4, 2, 1, 2, 3));
	}
}
