package ru.practicum.shareit.item.dto;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * DTO вещи для выдачи владельцу: содержит бронирования и комментарии.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ItemDtoWithBookingsAndComments extends ItemDtoWithBookings {
	private List<CommentDto> comments;
}
