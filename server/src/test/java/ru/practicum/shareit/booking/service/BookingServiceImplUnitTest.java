package ru.practicum.shareit.booking.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplUnitTest {

	@Mock
	private BookingRepository bookingRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private ItemRepository itemRepository;

	@InjectMocks
	private BookingServiceImpl bookingService;

	private User user(Long id) {
		User u = new User();
		u.setId(id);
		u.setName("U");
		u.setEmail("u" + id + "@mail");
		return u;
	}

	private Item item(Long id, Long ownerId, boolean available) {
		Item i = new Item();
		i.setId(id);
		User owner = user(ownerId);
		i.setOwner(owner);
		i.setAvailable(available);
		i.setName("Item");
		i.setDescription("Desc");
		return i;
	}

	private BookingCreateDto createDto(Long itemId, LocalDateTime start, LocalDateTime end) {
		BookingCreateDto dto = new BookingCreateDto();
		dto.setItemId(itemId);
		dto.setStart(start);
		dto.setEnd(end);
		return dto;
	}

	@Test
	@DisplayName("create: 404 если пользователя нет")
	void create_userNotFound() {
		given(userRepository.findById(eq(1L))).willReturn(java.util.Optional.empty());

		assertThrows(NotFoundException.class,
				() -> bookingService.create(1L, createDto(2L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2))));
		verify(itemRepository, never()).findById(any());
	}

	@Test
	@DisplayName("create: 404 если вещи нет")
	void create_itemNotFound() {
		given(userRepository.findById(eq(1L))).willReturn(java.util.Optional.of(user(1L)));
		given(itemRepository.findById(eq(2L))).willReturn(java.util.Optional.empty());

		assertThrows(NotFoundException.class,
				() -> bookingService.create(1L, createDto(2L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2))));
	}

	@Test
	@DisplayName("create: 400 если владелец бронирует свою вещь")
	void create_ownerBooksOwnItem() {
		given(userRepository.findById(eq(1L))).willReturn(java.util.Optional.of(user(1L)));
		given(itemRepository.findById(eq(2L))).willReturn(java.util.Optional.of(item(2L, 1L, true)));

		assertThrows(IllegalArgumentException.class,
				() -> bookingService.create(1L, createDto(2L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2))));
		verify(bookingRepository, never()).save(any());
	}

	@Test
	@DisplayName("create: 400 если вещь недоступна")
	void create_itemNotAvailable() {
		given(userRepository.findById(eq(1L))).willReturn(java.util.Optional.of(user(1L)));
		given(itemRepository.findById(eq(2L))).willReturn(java.util.Optional.of(item(2L, 10L, false)));

		assertThrows(IllegalArgumentException.class,
				() -> bookingService.create(1L, createDto(2L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2))));
		verify(bookingRepository, never()).save(any());
	}

	@Test
	@DisplayName("create: 400 если start >= end")
	void create_startNotBeforeEnd() {
		given(userRepository.findById(eq(1L))).willReturn(java.util.Optional.of(user(1L)));
		given(itemRepository.findById(eq(2L))).willReturn(java.util.Optional.of(item(2L, 10L, true)));

		LocalDateTime t = LocalDateTime.now().plusDays(1);
		assertThrows(IllegalArgumentException.class,
				() -> bookingService.create(1L, createDto(2L, t, t)));
		verify(bookingRepository, never()).save(any());
	}

	@Test
	@DisplayName("create: создаёт бронирование")
	void create_ok() {
		User booker = user(1L);
		Item item = item(2L, 10L, true);
		LocalDateTime start = LocalDateTime.now().plusDays(1);
		LocalDateTime end = LocalDateTime.now().plusDays(2);
		BookingCreateDto request = createDto(2L, start, end);

		given(userRepository.findById(eq(1L))).willReturn(java.util.Optional.of(booker));
		given(itemRepository.findById(eq(2L))).willReturn(java.util.Optional.of(item));

		Booking saved = new Booking();
		saved.setId(100L);
		saved.setItem(item);
		saved.setBooker(booker);
		saved.setStart(start);
		saved.setEnd(end);
		saved.setStatus(BookingStatus.WAITING);
		given(bookingRepository.save(any())).willReturn(saved);

		BookingDto dto = bookingService.create(1L, request);
		assertEquals(100L, dto.getId());
		assertEquals(2L, dto.getItem().getId());
		assertEquals(1L, dto.getBooker().getId());
		assertEquals(BookingStatus.WAITING, dto.getStatus());
	}

	@Test
	@DisplayName("approve: 404 если бронирования нет")
	void approve_notFound() {
		given(bookingRepository.findById(eq(1L))).willReturn(java.util.Optional.empty());
		assertThrows(NotFoundException.class, () -> bookingService.approve(10L, 1L, true));
	}

	@Test
	@DisplayName("approve: 403 если не владелец")
	void approve_notOwner_forbidden() {
		Booking booking = new Booking();
		booking.setId(1L);
		booking.setStatus(BookingStatus.WAITING);
		booking.setItem(item(2L, 99L, true));
		given(bookingRepository.findById(eq(1L))).willReturn(java.util.Optional.of(booking));

		assertThrows(SecurityException.class, () -> bookingService.approve(10L, 1L, true));
		verify(bookingRepository, never()).save(any());
	}

	@Test
	@DisplayName("approve: 409 если статус не WAITING")
	void approve_notWaiting_conflict() {
		Booking booking = new Booking();
		booking.setId(1L);
		booking.setStatus(BookingStatus.APPROVED);
		booking.setItem(item(2L, 10L, true));
		given(bookingRepository.findById(eq(1L))).willReturn(java.util.Optional.of(booking));

		assertThrows(IllegalStateException.class, () -> bookingService.approve(10L, 1L, true));
		verify(bookingRepository, never()).save(any());
	}

	@Test
	@DisplayName("approve: подтверждает бронирование")
	void approve_ok_approved() {
		Booking booking = new Booking();
		booking.setId(1L);
		booking.setStatus(BookingStatus.WAITING);
		booking.setItem(item(2L, 10L, true));
		given(bookingRepository.findById(eq(1L))).willReturn(java.util.Optional.of(booking));
		given(bookingRepository.save(any())).willAnswer(inv -> inv.getArgument(0, Booking.class));

		BookingDto dto = bookingService.approve(10L, 1L, true);
		assertEquals(BookingStatus.APPROVED, dto.getStatus());
	}

	@Test
	@DisplayName("approve: отклоняет бронирование")
	void approve_ok_rejected() {
		Booking booking = new Booking();
		booking.setId(1L);
		booking.setStatus(BookingStatus.WAITING);
		booking.setItem(item(2L, 10L, true));
		given(bookingRepository.findById(eq(1L))).willReturn(java.util.Optional.of(booking));
		given(bookingRepository.save(any())).willAnswer(inv -> inv.getArgument(0, Booking.class));

		BookingDto dto = bookingService.approve(10L, 1L, false);
		assertEquals(BookingStatus.REJECTED, dto.getStatus());
	}

	@Test
	@DisplayName("getById: 403 если не владелец и не букер")
	void getById_forbidden() {
		Booking booking = new Booking();
		booking.setId(1L);
		booking.setStatus(BookingStatus.WAITING);
		booking.setItem(item(2L, 10L, true));
		booking.setBooker(user(20L));
		given(bookingRepository.findById(eq(1L))).willReturn(java.util.Optional.of(booking));

		assertThrows(SecurityException.class, () -> bookingService.getById(99L, 1L));
	}

	@Test
	@DisplayName("getById: возвращает для владельца")
	void getById_owner_ok() {
		Booking booking = new Booking();
		booking.setId(1L);
		booking.setStatus(BookingStatus.WAITING);
		booking.setItem(item(2L, 10L, true));
		booking.setBooker(user(20L));
		given(bookingRepository.findById(eq(1L))).willReturn(java.util.Optional.of(booking));

		BookingDto dto = bookingService.getById(10L, 1L);
		assertEquals(1L, dto.getId());
		assertNotNull(dto.getItem());
	}

	@Test
	@DisplayName("getAllByBooker: покрывает все state ветки")
	void getAllByBooker_switchBranches() {
		given(userRepository.findById(eq(1L))).willReturn(java.util.Optional.of(user(1L)));
		Sort sort = Sort.by(Sort.Direction.DESC, "start");
		given(bookingRepository.findAllByBookerId(eq(1L), eq(sort))).willReturn(List.of());
		given(bookingRepository.findAllByBookerIdAndEndBefore(eq(1L), any(), eq(sort))).willReturn(List.of());
		given(bookingRepository.findAllByBookerIdAndStartAfter(eq(1L), any(), eq(sort))).willReturn(List.of());
		given(bookingRepository.findAllByBookerIdAndStatus(eq(1L), eq(BookingStatus.WAITING), eq(sort))).willReturn(List.of());
		given(bookingRepository.findAllByBookerIdAndStatus(eq(1L), eq(BookingStatus.REJECTED), eq(sort))).willReturn(List.of());
		given(bookingRepository.findCurrentByBookerId(eq(1L), any(), eq(sort))).willReturn(List.of());

		bookingService.getAllByBooker(1L, BookingState.ALL);
		bookingService.getAllByBooker(1L, BookingState.CURRENT);
		bookingService.getAllByBooker(1L, BookingState.PAST);
		bookingService.getAllByBooker(1L, BookingState.FUTURE);
		bookingService.getAllByBooker(1L, BookingState.WAITING);
		bookingService.getAllByBooker(1L, BookingState.REJECTED);

		verify(bookingRepository).findAllByBookerId(eq(1L), eq(sort));
		verify(bookingRepository).findCurrentByBookerId(eq(1L), any(), eq(sort));
		verify(bookingRepository).findAllByBookerIdAndEndBefore(eq(1L), any(), eq(sort));
		verify(bookingRepository).findAllByBookerIdAndStartAfter(eq(1L), any(), eq(sort));
		verify(bookingRepository).findAllByBookerIdAndStatus(eq(1L), eq(BookingStatus.WAITING), eq(sort));
		verify(bookingRepository).findAllByBookerIdAndStatus(eq(1L), eq(BookingStatus.REJECTED), eq(sort));
	}

	@Test
	@DisplayName("getAllByOwner: покрывает все state ветки")
	void getAllByOwner_switchBranches() {
		given(userRepository.findById(eq(1L))).willReturn(java.util.Optional.of(user(1L)));
		Sort sort = Sort.by(Sort.Direction.DESC, "start");
		given(bookingRepository.findAllByItemOwnerId(eq(1L), eq(sort))).willReturn(List.of());
		given(bookingRepository.findAllByItemOwnerIdAndEndBefore(eq(1L), any(), eq(sort))).willReturn(List.of());
		given(bookingRepository.findAllByItemOwnerIdAndStartAfter(eq(1L), any(), eq(sort))).willReturn(List.of());
		given(bookingRepository.findAllByItemOwnerIdAndStatus(eq(1L), eq(BookingStatus.WAITING), eq(sort))).willReturn(List.of());
		given(bookingRepository.findAllByItemOwnerIdAndStatus(eq(1L), eq(BookingStatus.REJECTED), eq(sort))).willReturn(List.of());
		given(bookingRepository.findCurrentByOwnerId(eq(1L), any(), eq(sort))).willReturn(List.of());

		bookingService.getAllByOwner(1L, BookingState.ALL);
		bookingService.getAllByOwner(1L, BookingState.CURRENT);
		bookingService.getAllByOwner(1L, BookingState.PAST);
		bookingService.getAllByOwner(1L, BookingState.FUTURE);
		bookingService.getAllByOwner(1L, BookingState.WAITING);
		bookingService.getAllByOwner(1L, BookingState.REJECTED);

		verify(bookingRepository).findAllByItemOwnerId(eq(1L), eq(sort));
		verify(bookingRepository).findCurrentByOwnerId(eq(1L), any(), eq(sort));
		verify(bookingRepository).findAllByItemOwnerIdAndEndBefore(eq(1L), any(), eq(sort));
		verify(bookingRepository).findAllByItemOwnerIdAndStartAfter(eq(1L), any(), eq(sort));
		verify(bookingRepository).findAllByItemOwnerIdAndStatus(eq(1L), eq(BookingStatus.WAITING), eq(sort));
		verify(bookingRepository).findAllByItemOwnerIdAndStatus(eq(1L), eq(BookingStatus.REJECTED), eq(sort));
	}
}
