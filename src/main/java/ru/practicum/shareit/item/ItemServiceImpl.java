package ru.practicum.shareit.item;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

/**
 * In-memory реализация {@link ItemService}.
 */
@Service
public class ItemServiceImpl implements ItemService {
	private final Map<Long, Item> items = new ConcurrentHashMap<>();
	private final AtomicLong nextId = new AtomicLong(1);

	@Override
	public ItemDto create(Long userId, ItemDto itemDto) {
		Item item = new Item();
		Long id = nextId.getAndIncrement();
		item.setId(id);
		item.setName(itemDto.getName());
		item.setDescription(itemDto.getDescription());
		item.setAvailable(itemDto.getAvailable());

		User owner = new User();
		owner.setId(userId);
		item.setOwner(owner);

		items.put(id, item);
		return ItemMapper.toItemDto(item);
	}
}
