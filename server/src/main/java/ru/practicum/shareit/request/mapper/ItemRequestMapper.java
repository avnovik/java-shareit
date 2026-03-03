package ru.practicum.shareit.request.mapper;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestItemDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

/**
 * Маппер для {@link ItemRequest}.
 */
@UtilityClass
public class ItemRequestMapper {

	/**
	 * Преобразует {@link ItemRequestCreateDto} в {@link ItemRequest}.
	 */
	public static ItemRequest toItemRequest(@NotNull ItemRequestCreateDto dto,
										@NotNull User requestor,
										@NotNull LocalDateTime created) {
		ItemRequest request = new ItemRequest();
		request.setDescription(dto.getDescription());
		request.setRequestor(requestor);
		request.setCreated(created);
		return request;
	}

	/**
	 * Преобразует {@link ItemRequest} в {@link ItemRequestResponseDto}.
	 */
	public static ItemRequestResponseDto toItemRequestResponseDto(@NotNull ItemRequest request,
													  @NotNull List<ItemRequestItemDto> items) {
		ItemRequestResponseDto dto = new ItemRequestResponseDto();
		dto.setId(request.getId());
		dto.setDescription(request.getDescription());
		dto.setCreated(request.getCreated());
		dto.setItems(items);
		return dto;
	}

	/**
	 * Преобразует {@link Item} в {@link ItemRequestItemDto}.
	 */
	public static ItemRequestItemDto toItemRequestItemDto(@NotNull Item item) {
		ItemRequestItemDto dto = new ItemRequestItemDto();
		dto.setId(item.getId());
		dto.setName(item.getName());
		dto.setOwnerId(item.getOwner() != null ? item.getOwner().getId() : null);
		return dto;
	}
}
