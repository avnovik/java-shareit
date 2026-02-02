package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO для создания комментария.
 */
@Data
public class CommentCreateDto {
	@NotBlank
	private String text;
}
