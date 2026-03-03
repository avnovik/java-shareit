package ru.practicum.shareit.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * DTO ошибки для возврата клиенту.
 */
@Getter
@RequiredArgsConstructor
public class ErrorResponse {
	private final String error;
}