package ru.practicum.shareit.item.repository;

import java.util.Collection;
import ru.practicum.shareit.item.model.Item;

/**
 * Репозиторий для хранения и получения вещей.
 */
public interface ItemRepository {
    Item create(Item item);

    Item update(Item item);

	Item getById(Long itemId);

	Collection<Item> getAll();

	Item delete(Long itemId);
}