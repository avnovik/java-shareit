package ru.practicum.shareit.item.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import ru.practicum.shareit.item.model.Comment;

/**
 * JPA-репозиторий для {@link Comment}.
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {
	List<Comment> findAllByItemId(Long itemId, Sort sort);
}
