package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory реализация {@link ItemService}.
 */
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
	private final UserService userService;
	private final Map<Long, Item> items = new ConcurrentHashMap<>();
	private final AtomicLong nextId = new AtomicLong(1);

	@Override
	public ItemDto create(Long userId, ItemDto itemDto) {
		userService.getById(userId);
		Item item = ItemMapper.toItem(itemDto, userId);
		Long id = nextId.getAndIncrement();
		item.setId(id);

		items.put(id, item);
		return ItemMapper.toItemDto(item);
	}

	@Override
	public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
		Item existing = items.get(itemId);
		if (existing == null) {
			throw new NotFoundException("Item with id=" + itemId + " not found");
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
			throw new NotFoundException("Item with id=" + itemId + " not found");
		}
		return ItemMapper.toItemDto(item);
	}

	@Override
	public List<ItemDto> getAllByOwner(Long userId) {
		return items.values().stream()
				.filter(item -> {
					Long ownerId = item.getOwner() != null ? item.getOwner().getId() : null;
					return userId.equals(ownerId);
				})
				.map(ItemMapper::toItemDto)
				.toList();
	}

	@Override
	public List<ItemDto> search(String text) {
		if (text == null || text.isBlank()) {
			return List.of();
		}

		String query = text.toLowerCase();
		return items.values().stream()
				.filter(item -> !Boolean.FALSE.equals(item.getAvailable()))
				.filter(item -> {
					String name = item.getName() != null ? item.getName().toLowerCase() : "";
					String description = item.getDescription() != null ? item.getDescription().toLowerCase() : "";
					return name.contains(query) || description.contains(query);
				})
				.map(ItemMapper::toItemDto)
				.toList();
	}
}
