package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.model.User;

/**
 * JPA-репозиторий для {@link User}.
 * Spring Data JPA создаёт реализацию автоматически: доступны CRUD-операции и
 * запросы из имени методов.
 */
public interface UserRepository extends JpaRepository<User, Long> {
	boolean existsByEmail(String email);

	boolean existsByEmailAndIdNot(String email, Long id);
}