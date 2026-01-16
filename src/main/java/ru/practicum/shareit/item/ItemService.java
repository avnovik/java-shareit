package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

/**
 * Сервис для операций с вещами.
 */
public interface ItemService {

	/** Создаёт вещь. */
	ItemDto create(Long userId, ItemDto itemDto);
}
