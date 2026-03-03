package ru.practicum.shareit.request.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

@JsonTest
class ItemRequestCreateDtoJsonTest {

	@Autowired
	private JacksonTester<ItemRequestCreateDto> json;

	@Test
	@DisplayName("serialize: пишет description")
	void serialize_ok() throws Exception {
		ItemRequestCreateDto dto = new ItemRequestCreateDto();
		dto.setDescription("Need item");

		var content = json.write(dto);
		assertThat(content).extractingJsonPathStringValue("$.description").isEqualTo("Need item");
	}

	@Test
	@DisplayName("deserialize: читает description")
	void deserialize_ok() throws Exception {
		String body = "{\"description\":\"Need item\"}";
		ItemRequestCreateDto dto = json.parseObject(body);
		assertThat(dto.getDescription()).isEqualTo("Need item");
	}
}
