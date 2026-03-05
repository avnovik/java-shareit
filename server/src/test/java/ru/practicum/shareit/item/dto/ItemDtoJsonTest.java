package ru.practicum.shareit.item.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

@JsonTest
class ItemDtoJsonTest {

	@Autowired
	private JacksonTester<ItemDto> json;

	private ItemBookingDto booking(Long id) {
		ItemBookingDto dto = new ItemBookingDto();
		dto.setId(id);
		dto.setBookerId(99L);
		dto.setStart(LocalDateTime.of(2026, 1, 1, 0, 0, 0));
		dto.setEnd(LocalDateTime.of(2026, 1, 2, 0, 0, 0));
		return dto;
	}

	private CommentDto comment(Long id) {
		CommentDto dto = new CommentDto();
		dto.setId(id);
		dto.setText("t" + id);
		dto.setAuthorName("a");
		dto.setCreated(LocalDateTime.of(2026, 2, 1, 1, 1, 1));
		return dto;
	}

	@Test
	@DisplayName("serialize: пишет lastBooking/nextBooking/comments")
	void serialize_ok() throws Exception {
		ItemDto dto = new ItemDto();
		dto.setId(1L);
		dto.setName("n");
		dto.setDescription("d");
		dto.setAvailable(true);
		dto.setOwnerId(10L);
		dto.setRequestId(11L);
		dto.setLastBooking(booking(2L));
		dto.setNextBooking(booking(3L));
		dto.setComments(List.of(comment(4L)));

		var content = json.write(dto);

		assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
		assertThat(content).extractingJsonPathNumberValue("$.ownerId").isEqualTo(10);
		assertThat(content).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(2);
		assertThat(content).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(3);
		assertThat(content).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(4);
		assertThat(content).extractingJsonPathStringValue("$.comments[0].created").isEqualTo("2026-02-01T01:01:01");
	}

	@Test
	@DisplayName("deserialize: читает lastBooking/nextBooking/comments")
	void deserialize_ok() throws Exception {
		String body = "{\n" +
				"  \"id\": 1,\n" +
				"  \"name\": \"n\",\n" +
				"  \"description\": \"d\",\n" +
				"  \"available\": true,\n" +
				"  \"ownerId\": 10,\n" +
				"  \"requestId\": 11,\n" +
				"  \"lastBooking\": {\"id\": 2, \"bookerId\": 99, \"start\": \"2026-01-01T00:00:00\", \"end\": \"2026-01-02T00:00:00\"},\n" +
				"  \"nextBooking\": {\"id\": 3, \"bookerId\": 99, \"start\": \"2026-01-01T00:00:00\", \"end\": \"2026-01-02T00:00:00\"},\n" +
				"  \"comments\": [{\"id\": 4, \"text\": \"t4\", \"authorName\": \"a\", \"created\": \"2026-02-01T01:01:01\"}]\n" +
				"}";

		ItemDto dto = json.parseObject(body);
		assertThat(dto.getId()).isEqualTo(1L);
		assertThat(dto.getLastBooking().getId()).isEqualTo(2L);
		assertThat(dto.getNextBooking().getId()).isEqualTo(3L);
		assertThat(dto.getComments()).hasSize(1);
		assertThat(dto.getComments().get(0).getCreated()).isEqualTo(LocalDateTime.of(2026, 2, 1, 1, 1, 1));
	}
}
