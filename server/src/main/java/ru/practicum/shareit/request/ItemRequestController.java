package ru.practicum.shareit.request;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

/**
 * REST-контроллер для операций с запросами на вещи.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
	private final ItemRequestService itemRequestService;

	@PostMapping
	public ItemRequestResponseDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
									@RequestBody ItemRequestCreateDto requestCreateDto) {
		log.debug("POST /requests userId={}", userId);
		return itemRequestService.create(userId, requestCreateDto);
	}

	@GetMapping
	public List<ItemRequestResponseDto> getAllByRequestor(@RequestHeader("X-Sharer-User-Id") Long userId) {
		log.debug("GET /requests userId={}", userId);
		return itemRequestService.getAllByRequestor(userId);
	}

	@GetMapping("/all")
	public List<ItemRequestResponseDto> getAllOther(@RequestHeader("X-Sharer-User-Id") Long userId,
											@RequestParam(defaultValue = "0") Integer from,
											@RequestParam(defaultValue = "10") Integer size) {
		log.debug("GET /requests/all userId={} from={} size={}", userId, from, size);
		return itemRequestService.getAllOther(userId, from, size);
	}

	@GetMapping("/{requestId}")
	public ItemRequestResponseDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
										 @PathVariable Long requestId) {
		log.debug("GET /requests/{} userId={}", requestId, userId);
		return itemRequestService.getById(userId, requestId);
	}
}
