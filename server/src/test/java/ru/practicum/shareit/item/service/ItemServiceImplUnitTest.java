package ru.practicum.shareit.item.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplUnitTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private ItemRepository itemRepository;

	@Mock
	private ItemRequestRepository itemRequestRepository;

	@Mock
	private BookingRepository bookingRepository;

	@Mock
	private CommentRepository commentRepository;

	@InjectMocks
	private ItemServiceImpl itemService;

	private User user(Long id) {
		User u = new User();
		u.setId(id);
		u.setName("U");
		u.setEmail("u" + id + "@mail");
		return u;
	}

	private Item item(Long id, Long ownerId) {
		Item i = new Item();
		i.setId(id);
		i.setName("Item");
		i.setDescription("Desc");
		i.setAvailable(true);
		i.setOwner(user(ownerId));
		return i;
	}

	private ItemDto itemDto(String name, String description, Boolean available, Long requestId) {
		ItemDto dto = new ItemDto();
		dto.setName(name);
		dto.setDescription(description);
		dto.setAvailable(available);
		dto.setRequestId(requestId);
		return dto;
	}

	@Test
	@DisplayName("create: 404 если пользователя нет")
	void create_userNotFound() {
		given(userRepository.findById(eq(1L))).willReturn(Optional.empty());
		assertThrows(NotFoundException.class, () -> itemService.create(1L, itemDto("n", "d", true, null)));
		verify(itemRepository, never()).save(any());
	}

	@Test
	@DisplayName("create: 404 если requestId передан и запроса нет")
	void create_requestNotFound() {
		given(userRepository.findById(eq(1L))).willReturn(Optional.of(user(1L)));
		given(itemRequestRepository.findById(eq(10L))).willReturn(Optional.empty());

		assertThrows(NotFoundException.class, () -> itemService.create(1L, itemDto("n", "d", true, 10L)));
		verify(itemRepository, never()).save(any());
	}

	@Test
	@DisplayName("create: сохраняет requestId если он существует")
	void create_withRequestId_ok() {
		given(userRepository.findById(eq(1L))).willReturn(Optional.of(user(1L)));
		given(itemRequestRepository.findById(eq(10L))).willReturn(Optional.of(new ru.practicum.shareit.request.model.ItemRequest()));

		given(itemRepository.save(any())).willAnswer(inv -> {
			Item saved = inv.getArgument(0, Item.class);
			saved.setId(100L);
			return saved;
		});

		ItemDto created = itemService.create(1L, itemDto("n", "d", true, 10L));
		assertEquals(100L, created.getId());
		assertEquals(1L, created.getOwnerId());
		assertEquals(10L, created.getRequestId());
	}

	@Test
	@DisplayName("update: 404 если вещи нет")
	void update_itemNotFound() {
		given(itemRepository.findById(eq(1L))).willReturn(Optional.empty());
		assertThrows(NotFoundException.class, () -> itemService.update(10L, 1L, itemDto("n", null, null, null)));
	}

	@Test
	@DisplayName("update: 403 если не владелец")
	void update_forbidden() {
		given(itemRepository.findById(eq(1L))).willReturn(Optional.of(item(1L, 99L)));
		assertThrows(SecurityException.class, () -> itemService.update(10L, 1L, itemDto("n", null, null, null)));
		verify(itemRepository, never()).save(any());
	}

	@Test
	@DisplayName("update: обновляет только переданные поля")
	void update_partial_ok() {
		Item existing = item(1L, 10L);
		existing.setName("Old");
		existing.setDescription("OldD");
		existing.setAvailable(true);
		given(itemRepository.findById(eq(1L))).willReturn(Optional.of(existing));
		given(itemRepository.save(any())).willAnswer(inv -> inv.getArgument(0, Item.class));

		ItemDto patch = new ItemDto();
		patch.setName("New");
		patch.setDescription(null);
		patch.setAvailable(false);

		ItemDto updated = itemService.update(10L, 1L, patch);
		assertEquals(1L, updated.getId());
		assertEquals("New", updated.getName());
		assertEquals("OldD", updated.getDescription());
		assertEquals(false, updated.getAvailable());
	}

	@Test
	@DisplayName("getById: 404 если вещи нет")
	void getById_notFound() {
		given(itemRepository.findById(eq(1L))).willReturn(Optional.empty());
		assertThrows(NotFoundException.class, () -> itemService.getById(10L, 1L));
	}

	@Test
	@DisplayName("getById: для не владельца не запрашивает бронирования")
	void getById_notOwner_noBookings() {
		given(itemRepository.findById(eq(1L))).willReturn(Optional.of(item(1L, 99L)));
		Sort sort = Sort.by(Sort.Direction.DESC, "created");
		given(commentRepository.findAllByItemId(eq(1L), eq(sort))).willReturn(List.of());

		ItemDto dto = itemService.getById(10L, 1L);
		assertEquals(1L, dto.getId());
		verify(bookingRepository, never()).findLastBookings(anyLong(), any(), any(), any());
		verify(bookingRepository, never()).findNextBookings(anyLong(), any(), any(), any());
	}

	@Test
	@DisplayName("getById: для владельца запрашивает бронирования")
	void getById_owner_withBookings() {
		given(itemRepository.findById(eq(1L))).willReturn(Optional.of(item(1L, 10L)));
		given(bookingRepository.findLastBookings(eq(1L), eq(BookingStatus.APPROVED), any(), eq(PageRequest.of(0, 1))))
				.willReturn(List.of());
		given(bookingRepository.findNextBookings(eq(1L), eq(BookingStatus.APPROVED), any(), eq(PageRequest.of(0, 1))))
				.willReturn(List.of());
		Sort sort = Sort.by(Sort.Direction.DESC, "created");
		given(commentRepository.findAllByItemId(eq(1L), eq(sort))).willReturn(List.of());

		ItemDto dto = itemService.getById(10L, 1L);
		assertEquals(1L, dto.getId());
		assertNull(dto.getLastBooking());
		assertNull(dto.getNextBooking());
	}

	@Test
	@DisplayName("getAllByOwner: 404 если пользователя нет")
	void getAllByOwner_userNotFound() {
		given(userRepository.findById(eq(10L))).willReturn(Optional.empty());
		assertThrows(NotFoundException.class, () -> itemService.getAllByOwner(10L));
	}

	@Test
	@DisplayName("search: пустой текст возвращает пустой список")
	void search_blank_returnsEmpty() {
		assertEquals(List.of(), itemService.search(""));
		assertEquals(List.of(), itemService.search(" "));
		assertEquals(List.of(), itemService.search(null));
		verify(itemRepository, never()).searchAvailableByText(any());
	}

	@Test
	@DisplayName("search: непустой текст вызывает репозиторий")
	void search_ok() {
		given(itemRepository.searchAvailableByText(eq("drill"))).willReturn(List.of(item(1L, 10L)));
		List<ItemDto> dtos = itemService.search("drill");
		assertEquals(1, dtos.size());
	}

	@Test
	@DisplayName("addComment: 404 если автора нет")
	void addComment_authorNotFound() {
		given(userRepository.findById(eq(1L))).willReturn(Optional.empty());
		CommentCreateDto dto = new CommentCreateDto();
		dto.setText("t");
		assertThrows(NotFoundException.class, () -> itemService.addComment(1L, 2L, dto));
	}

	@Test
	@DisplayName("addComment: 404 если вещи нет")
	void addComment_itemNotFound() {
		given(userRepository.findById(eq(1L))).willReturn(Optional.of(user(1L)));
		given(itemRepository.findById(eq(2L))).willReturn(Optional.empty());
		CommentCreateDto dto = new CommentCreateDto();
		dto.setText("t");
		assertThrows(NotFoundException.class, () -> itemService.addComment(1L, 2L, dto));
	}

	@Test
	@DisplayName("addComment: 400 если нет завершённого бронирования")
	void addComment_noFinishedBooking_badRequest() {
		given(userRepository.findById(eq(1L))).willReturn(Optional.of(user(1L)));
		given(itemRepository.findById(eq(2L))).willReturn(Optional.of(item(2L, 10L)));
		given(bookingRepository.hasFinishedBooking(eq(2L), eq(1L), eq(BookingStatus.APPROVED), any())).willReturn(false);
		CommentCreateDto dto = new CommentCreateDto();
		dto.setText("t");
		assertThrows(IllegalArgumentException.class, () -> itemService.addComment(1L, 2L, dto));
		verify(commentRepository, never()).save(any());
	}

	@Test
	@DisplayName("addComment: добавляет комментарий")
	void addComment_ok() {
		given(userRepository.findById(eq(1L))).willReturn(Optional.of(user(1L)));
		given(itemRepository.findById(eq(2L))).willReturn(Optional.of(item(2L, 10L)));
		given(bookingRepository.hasFinishedBooking(eq(2L), eq(1L), eq(BookingStatus.APPROVED), any())).willReturn(true);

		given(commentRepository.save(any())).willAnswer(inv -> {
			Comment c = inv.getArgument(0, Comment.class);
			c.setId(100L);
			c.setCreated(LocalDateTime.now());
			return c;
		});

		CommentCreateDto dto = new CommentCreateDto();
		dto.setText("Hello");
		CommentDto created = assertDoesNotThrow(() -> itemService.addComment(1L, 2L, dto));
		assertNotNull(created.getId());
	}
}
