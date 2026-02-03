package ru.practicum.shareit.item.mapper;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class CommentMapper {

	/**
	 * Преобразует {@link Comment} в {@link CommentDto}.
	 */
	public static CommentDto toCommentDto(@NotNull Comment comment) {
		CommentDto dto = new CommentDto();
		dto.setId(comment.getId());
		dto.setText(comment.getText());
		dto.setAuthorName(comment.getAuthor() != null ? comment.getAuthor().getName() : null);
		dto.setCreated(comment.getCreated());
		return dto;
	}

	/**
	 * Преобразует {@link CommentCreateDto} в {@link Comment}.
	 */
	public static Comment toComment(@NotNull CommentCreateDto dto,
							  @NotNull Item item,
							  @NotNull User author,
							  @NotNull LocalDateTime created) {
		Comment comment = new Comment();
		comment.setText(dto.getText());
		comment.setItem(item);
		comment.setAuthor(author);
		comment.setCreated(created);
		return comment;
	}
}
