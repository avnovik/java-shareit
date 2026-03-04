package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class BookingMapper {

	/**
	 * Преобразует {@link BookingCreateDto} в {@link Booking}.
	 */
	public static Booking toBooking(BookingCreateDto dto,
							  Item item,
							  User booker,
							  BookingStatus status) {
		Booking booking = new Booking();
		booking.setStart(dto.getStart());
		booking.setEnd(dto.getEnd());
		booking.setItem(item);
		booking.setBooker(booker);
		booking.setStatus(status);
		return booking;
	}

	/**
	 * Преобразует {@link Booking} в {@link BookingDto}.
	 */
	public static BookingDto toBookingDto(Booking booking) {
		BookingDto dto = new BookingDto();
		dto.setId(booking.getId());
		dto.setStart(booking.getStart());
		dto.setEnd(booking.getEnd());
		dto.setStatus(booking.getStatus());

		if (booking.getItem() != null) {
			BookingDto.ItemInfo item = new BookingDto.ItemInfo();
			item.setId(booking.getItem().getId());
			item.setName(booking.getItem().getName());
			dto.setItem(item);
		}
		if (booking.getBooker() != null) {
			BookingDto.BookerInfo booker = new BookingDto.BookerInfo();
			booker.setId(booking.getBooker().getId());
			dto.setBooker(booker);
		}
		return dto;
	}
}
