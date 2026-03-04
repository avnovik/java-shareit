package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * REST-контроллер для работы с вещами.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
	private final ItemService itemService;

	@PostMapping
	public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemDto itemDto) {
		log.debug("POST /items userId={}", userId);
		return itemService.create(userId, itemDto);
	}

	@PatchMapping("/{itemId}")
	public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
						@PathVariable Long itemId,
						@RequestBody ItemDto itemDto) {
		log.debug("PATCH /items/{} userId={}", itemId, userId);
		return itemService.update(userId, itemId, itemDto);
	}

	@GetMapping("/{itemId}")
	public ItemDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
				   @PathVariable Long itemId) {
		log.debug("GET /items/{} userId={}", itemId, userId);
		return itemService.getById(userId, itemId);
	}

	@GetMapping
	public List<ItemDto> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
		log.debug("GET /items userId={}", userId);
		return itemService.getAllByOwner(userId);
	}

	@GetMapping("/search")
	public List<ItemDto> search(@RequestParam String text) {
		log.debug("GET /items/search textLength={}", text != null ? text.length() : null);
		return itemService.search(text);
	}

	@PostMapping("/{itemId}/comment")
	public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
						@PathVariable Long itemId,
						@RequestBody CommentCreateDto commentCreateDto) {
		log.debug("POST /items/{}/comment userId={}", itemId, userId);
		return itemService.addComment(userId, itemId, commentCreateDto);
	}
}
