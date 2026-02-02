package ru.practicum.shareit.item.dto;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * DTO вещи для выдачи вместе с комментариями.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ItemDtoWithComments extends ItemDto {
	private List<CommentDto> comments;
}
