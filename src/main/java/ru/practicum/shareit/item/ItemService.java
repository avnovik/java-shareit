package ru.practicum.shareit.item;

import java.util.List;

import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

/**
 * Сервис для операций с вещами.
 */
public interface ItemService {

	/** Создаёт вещь. */
	ItemDto create(Long userId, ItemDto itemDto);

	/** Обновляет вещь. */
	ItemDto update(Long userId, Long itemId, ItemDto itemDto);

	/** Возвращает вещь по id с учётом пользователя (для отображения бронирований владельцу). */
	ItemDto getById(Long userId, Long itemId);

	/** Возвращает список вещей владельца. */
	List<ItemDto> getAllByOwner(Long userId);

	/** Ищет доступные вещи по тексту в названии или описании. */
	List<ItemDto> search(String text);

	/** Добавляет комментарий к вещи. */
	CommentDto addComment(Long userId, Long itemId, CommentCreateDto commentCreateDto);
}
