package ru.practicum.shareit.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ru.practicum.shareit.item.dto.ItemDto;

class ItemServiceImplTest {

	private final ItemServiceImpl itemService = new ItemServiceImpl();

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
		ItemDto request = itemDto(null, "Drill", "Cordless drill", true);

		ItemDto created = itemService.create(10L, request);

		assertNotNull(created.getId());
		assertEquals("Drill", created.getName());
		assertEquals("Cordless drill", created.getDescription());
		assertEquals(true, created.getAvailable());
		assertEquals(10L, created.getOwnerId());
	}

	@Test
	@DisplayName("update: обновляет только переданные поля")
	void update_updatesOnlyProvidedFields() {
		ItemDto created = itemService.create(10L, itemDto(null, "Old", "Old desc", true));

		ItemDto patch = itemDto(null, "New", null, null);
		ItemDto updated = itemService.update(10L, created.getId(), patch);

		assertEquals(created.getId(), updated.getId());
		assertEquals("New", updated.getName());
		assertEquals("Old desc", updated.getDescription());
		assertEquals(true, updated.getAvailable());
		assertEquals(10L, updated.getOwnerId());
	}

	@Test
	@DisplayName("update: бросает SecurityException если не владелец")
	void update_forbiddenForNotOwner() {
		ItemDto created = itemService.create(10L, itemDto(null, "Old", "Old desc", true));

		SecurityException ex = assertThrows(SecurityException.class,
				() -> itemService.update(11L, created.getId(), itemDto(null, "New", null, null)));
		assertNotNull(ex.getMessage());
	}
}
