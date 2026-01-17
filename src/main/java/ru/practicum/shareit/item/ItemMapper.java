package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class ItemMapper {

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

	/**
	 * Преобразует {@link ItemDto} в {@link Item}.
	 */
	public static Item toItem(ItemDto dto, Long ownerId) {
		if (dto == null) {
			return null;
		}

		Item item = new Item();
		item.setName(dto.getName());
		item.setDescription(dto.getDescription());
		item.setAvailable(dto.getAvailable());

		if (ownerId != null) {
			User owner = new User();
			owner.setId(ownerId);
			item.setOwner(owner);
		}

		return item;
	}
}
