package ru.practicum.shareit.user.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

@JsonTest
class UserDtoJsonTest {

	@Autowired
	private JacksonTester<UserDto> json;

	@Test
	@DisplayName("serialize: пишет id/name/email")
	void serialize_ok() throws Exception {
		UserDto dto = new UserDto();
		dto.setId(1L);
		dto.setName("N");
		dto.setEmail("a@b");

		var content = json.write(dto);
		assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
		assertThat(content).extractingJsonPathStringValue("$.name").isEqualTo("N");
		assertThat(content).extractingJsonPathStringValue("$.email").isEqualTo("a@b");
	}

	@Test
	@DisplayName("deserialize: читает id/name/email")
	void deserialize_ok() throws Exception {
		String body = "{\n" +
				"  \"id\": 2,\n" +
				"  \"name\": \"NN\",\n" +
				"  \"email\": \"x@y\"\n" +
				"}";
		UserDto dto = json.parseObject(body);
		assertThat(dto.getId()).isEqualTo(2L);
		assertThat(dto.getName()).isEqualTo("NN");
		assertThat(dto.getEmail()).isEqualTo("x@y");
	}
}
