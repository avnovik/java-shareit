package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * In-memory реализация {@link ItemService}.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
	private final BookingRepository bookingRepository;
	private final CommentRepository commentRepository;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));
        Item item = ItemMapper.toItem(itemDto, owner);
        Item created = itemRepository.save(item);
		log.debug("created itemId={} ownerId={}", created.getId(), userId);
        return ItemMapper.toItemDto(created);
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        Item existing = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id=" + itemId + " not found"));

        Long ownerId = existing.getOwner() != null ? existing.getOwner().getId() : null;
        if (ownerId == null || !ownerId.equals(userId)) {
            throw new SecurityException("Only owner can update item with id=" + itemId);
        }

        if (itemDto.getName() != null) {
            existing.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            existing.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existing.setAvailable(itemDto.getAvailable());
        }

        Item saved = itemRepository.save(existing);
		log.debug("updated itemId={} ownerId={}", itemId, userId);
        return ItemMapper.toItemDto(saved);
    }

	@Override
	public ItemDto getById(Long userId, Long itemId) {
		Item item = itemRepository.findById(itemId)
				.orElseThrow(() -> new NotFoundException("Item with id=" + itemId + " not found"));

		Long ownerId = item.getOwner() != null ? item.getOwner().getId() : null;
		if (ownerId == null || !ownerId.equals(userId)) {
			ItemDto dto = addComments(ItemMapper.toItemDto(item));
			log.debug("returned itemId={} forUserId={} ownerView=false", itemId, userId);
			return dto;
		}
		ItemDto dto = addBookingsAndCommentsForOwner(ItemMapper.toItemDto(item));
		log.debug("returned itemId={} forUserId={} ownerView=true", itemId, userId);
		return dto;
	}

    @Override
    public List<ItemDto> getAllByOwner(Long userId) {
		userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));

		List<ItemDto> items = itemRepository.findAllByOwnerId(userId).stream()
				.map(ItemMapper::toItemDto)
				.map(this::addBookingsAndCommentsForOwner)
				.toList();
		log.debug("returned itemsCount={} ownerId={}", items.size(), userId);
		return items;
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
		List<ItemDto> items = itemRepository.searchAvailableByText(text).stream()
                .map(ItemMapper::toItemDto)
                .toList();
		log.debug("search returned itemsCount={}", items.size());
		return items;
    }

	@Override
	public CommentDto addComment(Long userId, Long itemId, CommentCreateDto commentCreateDto) {
		User author = userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));
		Item item = itemRepository.findById(itemId)
				.orElseThrow(() -> new NotFoundException("Item with id=" + itemId + " not found"));

		LocalDateTime now = LocalDateTime.now();
		boolean hasFinishedApprovedBooking = bookingRepository.hasFinishedBooking(itemId, userId, BookingStatus.APPROVED, now);
		if (!hasFinishedApprovedBooking) {
			throw new IllegalArgumentException("User with id=" + userId
					+ " has no finished approved booking for item with id=" + itemId);
		}

		Comment comment = new Comment();
		comment.setText(commentCreateDto.getText());
		comment.setItem(item);
		comment.setAuthor(author);
		comment.setCreated(now);

		Comment saved = commentRepository.save(comment);
		log.debug("comment added commentId={} itemId={} authorId={}", saved.getId(), itemId, userId);
		return CommentMapper.toCommentDto(saved);
	}

	private ItemDto addBookingsAndCommentsForOwner(ItemDto itemDto) {
		ItemDto dto = new ItemDto();
		dto.setId(itemDto.getId());
		dto.setName(itemDto.getName());
		dto.setDescription(itemDto.getDescription());
		dto.setAvailable(itemDto.getAvailable());
		dto.setOwnerId(itemDto.getOwnerId());
		dto.setRequestId(itemDto.getRequestId());

		LocalDateTime now = LocalDateTime.now();
		dto.setLastBooking(bookingRepository
				.findLastBookings(dto.getId(), BookingStatus.APPROVED, now, PageRequest.of(0, 1)).stream()
				.findFirst()
				.map(this::toItemBookingDto)
				.orElse(null));
		dto.setNextBooking(bookingRepository
				.findNextBookings(dto.getId(), BookingStatus.APPROVED, now, PageRequest.of(0, 1)).stream()
				.findFirst()
				.map(this::toItemBookingDto)
				.orElse(null));

		Sort newestFirst = Sort.by(Sort.Direction.DESC, "created");
		dto.setComments(commentRepository.findAllByItemId(dto.getId(), newestFirst).stream()
				.map(CommentMapper::toCommentDto)
				.toList());

		return dto;
	}

	private ItemDto addComments(ItemDto itemDto) {
		ItemDto dto = new ItemDto();
		dto.setId(itemDto.getId());
		dto.setName(itemDto.getName());
		dto.setDescription(itemDto.getDescription());
		dto.setAvailable(itemDto.getAvailable());
		dto.setOwnerId(itemDto.getOwnerId());
		dto.setRequestId(itemDto.getRequestId());

		Sort newestFirst = Sort.by(Sort.Direction.DESC, "created");
		dto.setComments(commentRepository.findAllByItemId(dto.getId(), newestFirst).stream()
				.map(CommentMapper::toCommentDto)
				.toList());
		return dto;
	}

	private ItemBookingDto toItemBookingDto(Booking booking) {
		ItemBookingDto dto = new ItemBookingDto();
		dto.setId(booking.getId());
		dto.setStart(booking.getStart());
		dto.setEnd(booking.getEnd());
		dto.setBookerId(booking.getBooker() != null ? booking.getBooker().getId() : null);
		return dto;
	}
}
