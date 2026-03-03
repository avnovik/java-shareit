package ru.practicum.shareit.request.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import ru.practicum.shareit.request.model.ItemRequest;

/**
 * JPA-репозиторий для {@link ItemRequest}.
 */
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
	List<ItemRequest> findAllByRequestorIdOrderByCreatedDesc(Long requestorId);

	Page<ItemRequest> findAllByRequestorIdNot(Long requestorId, Pageable pageable);
}
