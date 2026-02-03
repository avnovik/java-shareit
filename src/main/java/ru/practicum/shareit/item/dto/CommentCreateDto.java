package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO для создания комментария.
 */
@Data
public class CommentCreateDto {
	@NotBlank
	@Size(max = 1024)
	private String text;
}
