package ru.practicum.shareit.item.mapper;

import java.time.LocalDateTime;
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
	public static CommentDto toCommentDto(Comment comment) {
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
	public static Comment toComment(CommentCreateDto dto,
							  Item item,
							  User author,
							  LocalDateTime created) {
		Comment comment = new Comment();
		comment.setText(dto.getText());
		comment.setItem(item);
		comment.setAuthor(author);
		comment.setCreated(created);
		return comment;
	}
}
