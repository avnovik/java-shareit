package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

/**
 * Внешний REST-контроллер запросов вещей (gateway).
 * Выполняет валидацию входных данных и проксирует запросы в shareIt-server.
 */
@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
	private final ItemRequestClient itemRequestClient;

	@PostMapping
	public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long userId,
									@RequestBody @Valid ItemRequestCreateDto requestCreateDto) {
		log.debug("POST /requests userId={}", userId);
		return itemRequestClient.create(userId, requestCreateDto);
	}

	@GetMapping
	public ResponseEntity<Object> getAllByRequestor(@RequestHeader("X-Sharer-User-Id") long userId) {
		log.debug("GET /requests userId={}", userId);
		return itemRequestClient.getAllByRequestor(userId);
	}

	@GetMapping("/all")
	public ResponseEntity<Object> getAllOther(@RequestHeader("X-Sharer-User-Id") long userId,
										@RequestParam(defaultValue = "0") @PositiveOrZero int from,
										@RequestParam(defaultValue = "10") @Positive int size) {
		log.debug("GET /requests/all userId={} from={} size={}", userId, from, size);
		return itemRequestClient.getAllOther(userId, from, size);
	}

	@GetMapping("/{requestId}")
	public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") long userId,
										 @PathVariable @Positive long requestId) {
		log.debug("GET /requests/{} userId={}", requestId, userId);
		return itemRequestClient.getById(userId, requestId);
	}
}
