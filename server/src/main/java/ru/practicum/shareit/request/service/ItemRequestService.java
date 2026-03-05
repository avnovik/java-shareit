package ru.practicum.shareit.request.service;

import java.util.List;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

/**
 * Сервис для операций с запросами на вещи.
 */
public interface ItemRequestService {

	/** Создаёт новый запрос вещи. */
	ItemRequestResponseDto create(Long userId, ItemRequestCreateDto requestCreateDto);

	/** Возвращает список своих запросов вместе с ответами на них. */
	List<ItemRequestResponseDto> getAllByRequestor(Long userId);

	/** Возвращает список запросов других пользователей с пагинацией. */
	List<ItemRequestResponseDto> getAllOther(Long userId, Integer from, Integer size);

	/** Возвращает один запрос по id вместе с ответами на него. */
	ItemRequestResponseDto getById(Long userId, Long requestId);
}
