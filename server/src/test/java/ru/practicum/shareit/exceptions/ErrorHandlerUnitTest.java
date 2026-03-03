package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("ErrorHandler")
class ErrorHandlerUnitTest {

	private final ErrorHandler errorHandler = new ErrorHandler();

	@Test
	@DisplayName("handleNotFoundException: возвращает сообщение")
	void handleNotFoundException() {
		ErrorResponse response = errorHandler.handleNotFoundException(new NotFoundException("nf"));
		assertEquals("nf", response.getError());
	}

	@Test
	@DisplayName("handleSecurityException: возвращает сообщение")
	void handleSecurityException() {
		ErrorResponse response = errorHandler.handleSecurityException(new SecurityException("sec"));
		assertEquals("sec", response.getError());
	}

	@Test
	@DisplayName("handleIllegalStateException: возвращает сообщение")
	void handleIllegalStateException() {
		ErrorResponse response = errorHandler.handleIllegalStateException(new IllegalStateException("conflict"));
		assertEquals("conflict", response.getError());
	}

	@Test
	@DisplayName("handleIllegalArgumentException: возвращает сообщение")
	void handleIllegalArgumentException() {
		ErrorResponse response = errorHandler.handleIllegalArgumentException(new IllegalArgumentException("bad"));
		assertEquals("bad", response.getError());
	}

	@Test
	@DisplayName("handleTypeMismatch: unknown state")
	void handleTypeMismatch_state() {
		MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
				"UNKNOWN",
				String.class,
				"state",
				null,
				new IllegalArgumentException("m"));
		ErrorResponse response = errorHandler.handleTypeMismatch(ex);
		assertEquals("Unknown state: UNKNOWN", response.getError());
	}

	@Test
	@DisplayName("handleTypeMismatch: любой другой параметр")
	void handleTypeMismatch_otherParam() {
		MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
				"1",
				Long.class,
				"userId",
				null,
				new IllegalArgumentException("m"));
		ErrorResponse response = errorHandler.handleTypeMismatch(ex);
		assertNotNull(response.getError());
	}
}
