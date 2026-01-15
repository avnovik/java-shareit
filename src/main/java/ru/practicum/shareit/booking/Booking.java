package ru.practicum.shareit.booking;

import java.time.LocalDateTime;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

/**
 * Модель бронирования вещи.
 * Описывает период бронирования, вещь, арендатора и текущий статус.
 */
@Data
public class Booking {
	private Long id;
	private LocalDateTime start;
	private LocalDateTime end;
	private Item item;
	private User booker;
	private BookingStatus status;
}
