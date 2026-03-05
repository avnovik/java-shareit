package ru.practicum.shareit.request.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

@JsonTest
class ItemRequestResponseDtoJsonTest {

	@Autowired
	private JacksonTester<ItemRequestResponseDto> json;

	private ItemRequestItemDto item(Long id, String name, Long ownerId) {
		ItemRequestItemDto dto = new ItemRequestItemDto();
		dto.setId(id);
		dto.setName(name);
		dto.setOwnerId(ownerId);
		return dto;
	}

	@Test
	@DisplayName("serialize: пишет created и items")
	void serialize_ok() throws Exception {
		ItemRequestResponseDto dto = new ItemRequestResponseDto();
		dto.setId(1L);
		dto.setDescription("d");
		dto.setCreated(LocalDateTime.of(2026, 1, 1, 1, 1, 1));
		dto.setItems(List.of(item(2L, "n", 10L)));

		var content = json.write(dto);

		assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
		assertThat(content).extractingJsonPathStringValue("$.created").isEqualTo("2026-01-01T01:01:01");
		assertThat(content).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(2);
		assertThat(content).extractingJsonPathStringValue("$.items[0].name").isEqualTo("n");
		assertThat(content).extractingJsonPathNumberValue("$.items[0].ownerId").isEqualTo(10);
	}

	@Test
	@DisplayName("deserialize: читает created и items")
	void deserialize_ok() throws Exception {
		String body = "{\n" +
				"  \"id\": 1,\n" +
				"  \"description\": \"d\",\n" +
				"  \"created\": \"2026-01-01T01:01:01\",\n" +
				"  \"items\": [\n" +
				"    {\"id\": 2, \"name\": \"n\", \"ownerId\": 10}\n" +
				"  ]\n" +
				"}";

		ItemRequestResponseDto dto = json.parseObject(body);
		assertThat(dto.getId()).isEqualTo(1L);
		assertThat(dto.getCreated()).isEqualTo(LocalDateTime.of(2026, 1, 1, 1, 1, 1));
		assertThat(dto.getItems()).hasSize(1);
		assertThat(dto.getItems().get(0).getId()).isEqualTo(2L);
		assertThat(dto.getItems().get(0).getOwnerId()).isEqualTo(10L);
	}
}
