package ru.practicum.shareit.item.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

@JsonTest
class CommentDtoJsonTest {

	@Autowired
	private JacksonTester<CommentDto> json;

	@Test
	@DisplayName("serialize: пишет id/text/authorName/created")
	void serialize_ok() throws Exception {
		CommentDto dto = new CommentDto();
		dto.setId(1L);
		dto.setText("t");
		dto.setAuthorName("A");
		dto.setCreated(LocalDateTime.of(2026, 3, 1, 12, 0, 0));

		var content = json.write(dto);
		assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
		assertThat(content).extractingJsonPathStringValue("$.text").isEqualTo("t");
		assertThat(content).extractingJsonPathStringValue("$.authorName").isEqualTo("A");
		assertThat(content).extractingJsonPathStringValue("$.created").isEqualTo("2026-03-01T12:00:00");
	}

	@Test
	@DisplayName("deserialize: читает id/text/authorName/created")
	void deserialize_ok() throws Exception {
		String body = "{\n" +
				"  \"id\": 2,\n" +
				"  \"text\": \"tt\",\n" +
				"  \"authorName\": \"AA\",\n" +
				"  \"created\": \"2026-03-02T13:00:00\"\n" +
				"}";
		CommentDto dto = json.parseObject(body);
		assertThat(dto.getId()).isEqualTo(2L);
		assertThat(dto.getText()).isEqualTo("tt");
		assertThat(dto.getAuthorName()).isEqualTo("AA");
		assertThat(dto.getCreated()).isEqualTo(LocalDateTime.of(2026, 3, 2, 13, 0, 0));
	}
}
