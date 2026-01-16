package ru.practicum.shareit.item;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import ru.practicum.shareit.item.dto.ItemDto;

/**
 * REST-контроллер для работы с вещами.
 */
@RestController
@RequestMapping("/items")
public class ItemController {
	private final ItemService itemService;

	public ItemController(ItemService itemService) {
		this.itemService = itemService;
	}

	@PostMapping
	public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemDto itemDto) {
		return itemService.create(userId, itemDto);
	}

	@PatchMapping("/{itemId}")
	public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
							@PathVariable Long itemId,
							@RequestBody ItemDto itemDto) {
		try {
			return itemService.update(userId, itemId, itemDto);
		} catch (IllegalArgumentException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		} catch (SecurityException e) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
		}
	}

	@GetMapping("/{itemId}")
	public ItemDto getById(@PathVariable Long itemId) {
		try {
			return itemService.getById(itemId);
		} catch (IllegalArgumentException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	@GetMapping
	public List<ItemDto> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
		return itemService.getAllByOwner(userId);
	}

	@GetMapping("/search")
	public List<ItemDto> search(@RequestParam String text) {
		return itemService.search(text);
	}
}
