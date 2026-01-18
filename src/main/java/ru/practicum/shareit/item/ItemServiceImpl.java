package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

/**
 * In-memory реализация {@link ItemService}.
 */
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        User owner = userRepository.getById(userId);
        if (owner == null) {
            throw new NotFoundException("User with id=" + userId + " not found");
        }
        Item item = ItemMapper.toItem(itemDto, owner);
        Item created = itemRepository.create(item);
        return ItemMapper.toItemDto(created);
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        Item existing = itemRepository.getById(itemId);
        if (existing == null) {
            throw new NotFoundException("Item with id=" + itemId + " not found");
        }

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
        itemRepository.update(existing);
        return ItemMapper.toItemDto(existing);
    }

    @Override
    public ItemDto getById(Long itemId) {
        Item item = itemRepository.getById(itemId);
        if (item == null) {
            throw new NotFoundException("Item with id=" + itemId + " not found");
        }
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getAllByOwner(Long userId) {
        return itemRepository.getAll().stream()
                .filter(item -> {
                    Long ownerId = item.getOwner() != null ? item.getOwner().getId() : null;
                    return userId.equals(ownerId);
                })
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        String query = text.toLowerCase();
        return itemRepository.getAll().stream()
                .filter(item -> !Boolean.FALSE.equals(item.getAvailable()))
                .filter(item -> {
                    String name = item.getName() != null ? item.getName().toLowerCase() : "";
                    String description = item.getDescription() != null ? item.getDescription().toLowerCase() : "";
                    return name.contains(query) || description.contains(query);
                })
                .map(ItemMapper::toItemDto)
                .toList();
    }
}
