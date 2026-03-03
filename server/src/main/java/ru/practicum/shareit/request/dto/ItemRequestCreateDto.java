package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO для создания запроса вещи.
 */
@Data
public class ItemRequestCreateDto {
	@NotBlank
	@Size(max = 1024)
	private String description;
}
