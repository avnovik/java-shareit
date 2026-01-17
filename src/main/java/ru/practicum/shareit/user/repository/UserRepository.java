package ru.practicum.shareit.user.repository;

import java.util.Collection;

import ru.practicum.shareit.user.model.User;

/**
 * Репозиторий для хранения и получения пользователей.
 */
public interface UserRepository {
    User create(User user);

    User update(User user);

    User getById(Long userId);

	Collection<User> getAll();

	User delete(Long userId);

	boolean existsByEmail(String email);

	boolean existsByEmailExcludingId(String email, Long excludedUserId);
}