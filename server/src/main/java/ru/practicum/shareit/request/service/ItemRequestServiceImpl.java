package ru.practicum.shareit.request.service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestItemDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

/**
 * Реализация {@link ItemRequestService}.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
	private final ItemRequestRepository itemRequestRepository;
	private final UserRepository userRepository;
	private final ItemRepository itemRepository;

	@Override
	public ItemRequestResponseDto create(Long userId, ItemRequestCreateDto requestCreateDto) {
		User requestor = userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));

		LocalDateTime now = LocalDateTime.now();
		ItemRequest request = ItemRequestMapper.toItemRequest(requestCreateDto, requestor, now);

		ItemRequest saved = itemRequestRepository.save(request);
		log.debug("created requestId={} requestorId={}", saved.getId(), userId);
		return ItemRequestMapper.toItemRequestResponseDto(saved, List.of());
	}

	@Override
	public List<ItemRequestResponseDto> getAllByRequestor(Long userId) {
		userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));

		List<ItemRequest> requests = itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId);
		Map<Long, List<ItemRequestItemDto>> itemsByRequestId = getItemsByRequestId(requests);

		List<ItemRequestResponseDto> dtos = requests.stream()
				.map(r -> ItemRequestMapper.toItemRequestResponseDto(r,
						itemsByRequestId.getOrDefault(r.getId(), List.of())))
				.toList();
		log.debug("returned own requests requestorId={} count={}", userId, dtos.size());
		return dtos;
	}

	@Override
	public List<ItemRequestResponseDto> getAllOther(Long userId, Integer from, Integer size) {
		userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));

		int safeFrom = from != null ? from : 0;
		int safeSize = size != null ? size : 10;

		int page = safeFrom / safeSize;
		int offsetInPage = safeFrom % safeSize;
		int pageSizeWithOffset = safeSize + offsetInPage;
		PageRequest pageable = PageRequest.of(page, pageSizeWithOffset, Sort.by(Sort.Direction.DESC, "created"));

		List<ItemRequest> pageRequests = itemRequestRepository.findAllByRequestorIdNot(userId, pageable)
				.getContent();
		List<ItemRequest> requests = pageRequests.stream()
				.skip(offsetInPage)
				.limit(safeSize)
				.toList();

		Map<Long, List<ItemRequestItemDto>> itemsByRequestId = getItemsByRequestId(requests);

		List<ItemRequestResponseDto> dtos = requests.stream()
				.map(r -> ItemRequestMapper.toItemRequestResponseDto(r,
						itemsByRequestId.getOrDefault(r.getId(), List.of())))
				.toList();
		log.debug("returned other requests userId={} from={} size={} count={}", userId, safeFrom, safeSize, dtos.size());
		return dtos;
	}

	@Override
	public ItemRequestResponseDto getById(Long userId, Long requestId) {
		userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));

		ItemRequest request = itemRequestRepository.findById(requestId)
				.orElseThrow(() -> new NotFoundException("ItemRequest with id=" + requestId + " not found"));

		List<ItemRequestItemDto> items = itemRepository.findAllByRequestId(requestId).stream()
				.map(ItemRequestMapper::toItemRequestItemDto)
				.toList();

		log.debug("returned requestId={} forUserId={} itemsCount={}", requestId, userId, items.size());
		return ItemRequestMapper.toItemRequestResponseDto(request, items);
	}

	private Map<Long, List<ItemRequestItemDto>> getItemsByRequestId(List<ItemRequest> requests) {
		if (requests == null || requests.isEmpty()) {
			return Collections.emptyMap();
		}
		Collection<Long> requestIds = requests.stream()
				.map(ItemRequest::getId)
				.toList();

		return itemRepository.findAllByRequestIdIn(requestIds).stream()
				.filter(i -> i.getRequestId() != null)
				.collect(Collectors.groupingBy(Item::getRequestId,
						Collectors.mapping(ItemRequestMapper::toItemRequestItemDto, Collectors.toList())));
	}
}
