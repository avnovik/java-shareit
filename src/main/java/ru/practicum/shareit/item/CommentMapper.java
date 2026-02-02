package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

@UtilityClass
public class CommentMapper {
	public static CommentDto toCommentDto(@NotNull Comment comment) {
		CommentDto dto = new CommentDto();
		dto.setId(comment.getId());
		dto.setText(comment.getText());
		dto.setAuthorName(comment.getAuthor() != null ? comment.getAuthor().getName() : null);
		dto.setCreated(comment.getCreated());
		return dto;
	}
}
