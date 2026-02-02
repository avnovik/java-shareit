package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * DTO вещи для отображения владельцу.
 * Содержит информацию о последнем и следующем бронировании.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ItemDtoWithBookings extends ItemDto {
	private ItemBookingDto lastBooking;
	private ItemBookingDto nextBooking;
}
