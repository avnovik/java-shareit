package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Глобальный обработчик исключений.
 */
@Slf4j
@RestControllerAdvice
public class ErrorHandler {

	@ExceptionHandler({MethodArgumentNotValidException.class})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleValidationException(final Exception e) {
		log.warn("Ошибка валидации объекта");
		return new ErrorResponse(e.getMessage());
	}

	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorResponse handleNotFoundException(final NotFoundException e) {
		log.warn("Объект не найден");
		return new ErrorResponse(e.getMessage());
	}

	@ExceptionHandler(SecurityException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ErrorResponse handleSecurityException(final SecurityException e) {
		log.warn("Доступ запрещён");
		return new ErrorResponse(e.getMessage());
	}

	@ExceptionHandler(IllegalStateException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ErrorResponse handleIllegalStateException(final IllegalStateException e) {
		log.warn("Конфликт данных");
		return new ErrorResponse(e.getMessage());
	}

	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleIllegalArgumentException(final IllegalArgumentException e) {
		log.warn("Некорректный запрос");
		return new ErrorResponse(e.getMessage());
	}
}
