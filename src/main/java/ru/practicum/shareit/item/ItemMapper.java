package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public final class ItemMapper {
	private ItemMapper() {
	}

	/**
	 * Преобразует {@link Item} в {@link ItemDto}.
	 */
	public static ItemDto toItemDto(Item item) {
		if (item == null) {
			return null;
		}

		ItemDto dto = new ItemDto();
		dto.setId(item.getId());
		dto.setName(item.getName());
		dto.setDescription(item.getDescription());
		dto.setAvailable(item.getAvailable());
		dto.setOwnerId(item.getOwner() != null ? item.getOwner().getId() : null);
		dto.setRequestId(item.getRequest() != null ? item.getRequest().getId() : null);
		return dto;
	}
}
