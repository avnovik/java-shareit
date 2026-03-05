package ru.practicum.shareit.item.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO комментария к вещи.
 */
@Data
public class CommentDto {
	private Long id;
	private String text;
	private String authorName;
	private LocalDateTime created;
}
