package ru.practicum.shareit.item;

import java.util.ArrayList;
import java.util.List;
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

	@Override
	public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
		Item existing = items.get(itemId);
		if (existing == null) {
			throw new IllegalArgumentException("Item with id=" + itemId + " not found");
		}

		Long ownerId = existing.getOwner() != null ? existing.getOwner().getId() : null;
		if (ownerId == null || !ownerId.equals(userId)) {
			throw new SecurityException("Only owner can update item with id=" + itemId);
		}

		if (itemDto.getName() != null) {
			existing.setName(itemDto.getName());
		}
		if (itemDto.getDescription() != null) {
			existing.setDescription(itemDto.getDescription());
		}
		if (itemDto.getAvailable() != null) {
			existing.setAvailable(itemDto.getAvailable());
		}

		return ItemMapper.toItemDto(existing);
	}

	@Override
	public ItemDto getById(Long itemId) {
		Item item = items.get(itemId);
		if (item == null) {
			throw new IllegalArgumentException("Item with id=" + itemId + " not found");
		}
		return ItemMapper.toItemDto(item);
	}

	@Override
	public List<ItemDto> getAllByOwner(Long userId) {
		List<ItemDto> result = new ArrayList<>();
		for (Item item : items.values()) {
			Long ownerId = item.getOwner() != null ? item.getOwner().getId() : null;
			if (userId.equals(ownerId)) {
				result.add(ItemMapper.toItemDto(item));
			}
		}
		return result;
	}

	@Override
	public List<ItemDto> search(String text) {
		if (text == null || text.isBlank()) {
			return List.of();
		}

		String query = text.toLowerCase();
		List<ItemDto> result = new ArrayList<>();
		for (Item item : items.values()) {
			if (Boolean.FALSE.equals(item.getAvailable())) {
				continue;
			}

			String name = item.getName() != null ? item.getName().toLowerCase() : "";
			String description = item.getDescription() != null ? item.getDescription().toLowerCase() : "";
			if (name.contains(query) || description.contains(query)) {
				result.add(ItemMapper.toItemDto(item));
			}
		}
		return result;
	}
}
