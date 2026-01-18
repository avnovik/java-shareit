package ru.practicum.shareit.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.InMemoryItemRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.InMemoryUserRepository;
import ru.practicum.shareit.user.repository.UserRepository;

class ItemServiceImplTest {

	private final UserRepository userRepository = new InMemoryUserRepository();
	private final ItemRepository itemRepository = new InMemoryItemRepository();
	private final ItemServiceImpl itemService = new ItemServiceImpl(userRepository, itemRepository);

	private ItemDto itemDto(Long id, String name, String description, Boolean available) {
		ItemDto dto = new ItemDto();
		dto.setId(id);
		dto.setName(name);
		dto.setDescription(description);
		dto.setAvailable(available);
		return dto;
	}

	@Test
	@DisplayName("create: создаёт вещь и назначает владельца")
	void create_setsIdAndOwner() {
		User owner = new User();
		owner.setName("Owner");
		owner.setEmail("owner@email");
		Long ownerId = userRepository.create(owner).getId();

		ItemDto request = itemDto(null, "Drill", "Cordless drill", true);

		ItemDto created = itemService.create(ownerId, request);

		assertNotNull(created.getId());
		assertEquals("Drill", created.getName());
		assertEquals("Cordless drill", created.getDescription());
		assertEquals(true, created.getAvailable());
		assertEquals(ownerId, created.getOwnerId());
	}

	@Test
	@DisplayName("update: обновляет только переданные поля")
	void update_updatesOnlyProvidedFields() {
		User owner = new User();
		owner.setName("Owner");
		owner.setEmail("owner@email");
		Long ownerId = userRepository.create(owner).getId();

		ItemDto created = itemService.create(ownerId, itemDto(null, "Old", "Old desc", true));

		ItemDto patch = itemDto(null, "New", null, null);
		ItemDto updated = itemService.update(ownerId, created.getId(), patch);

		assertEquals(created.getId(), updated.getId());
		assertEquals("New", updated.getName());
		assertEquals("Old desc", updated.getDescription());
		assertEquals(true, updated.getAvailable());
		assertEquals(ownerId, updated.getOwnerId());
	}

	@Test
	@DisplayName("update: бросает SecurityException если не владелец")
	void update_forbiddenForNotOwner() {
		User owner = new User();
		owner.setName("Owner");
		owner.setEmail("owner@email");
		Long ownerId = userRepository.create(owner).getId();

		ItemDto created = itemService.create(ownerId, itemDto(null, "Old", "Old desc", true));

		SecurityException ex = assertThrows(SecurityException.class,
				() -> itemService.update(ownerId + 1, created.getId(), itemDto(null, "New", null, null)));
		assertNotNull(ex.getMessage());
	}
}
