package ru.practicum.shareit.item;

import java.util.List;

import ru.practicum.shareit.item.dto.ItemDto;

/**
 * Сервис для операций с вещами.
 */
public interface ItemService {

	/** Создаёт вещь. */
	ItemDto create(Long userId, ItemDto itemDto);

	/** Обновляет вещь. */
	ItemDto update(Long userId, Long itemId, ItemDto itemDto);

	/** Возвращает вещь по id. */
	ItemDto getById(Long itemId);

	/** Возвращает список вещей владельца. */
	List<ItemDto> getAllByOwner(Long userId);

	/** Ищет доступные вещи по тексту в названии или описании. */
	List<ItemDto> search(String text);
}
