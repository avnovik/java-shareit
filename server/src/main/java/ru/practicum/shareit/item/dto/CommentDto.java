package ru.practicum.shareit.item.dto;

import java.time.LocalDateTime;

import lombok.Data;

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
