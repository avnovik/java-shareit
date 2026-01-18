package ru.practicum.shareit.user.repository;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import ru.practicum.shareit.user.model.User;

/**
 * In-memory реализация {@link UserRepository}.
 */
@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    @Override
    public User create(User user) {
        Long id = nextId.getAndIncrement();
        user.setId(id);
        users.put(id, user);
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getById(Long userId) {
        return users.get(userId);
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public User delete(Long userId) {
        return users.remove(userId);
    }

    @Override
    public boolean existsByEmail(String email) {
        return users.values().stream()
                .map(User::getEmail)
                .filter(Objects::nonNull)
                .anyMatch(email::equals);
    }

    @Override
    public boolean existsByEmailExcludingId(String email, Long excludedUserId) {
        return users.values().stream()
                .filter(user -> user.getId() != null)
                .filter(user -> !user.getId().equals(excludedUserId))
                .map(User::getEmail)
                .filter(Objects::nonNull)
                .anyMatch(email::equals);
    }
}