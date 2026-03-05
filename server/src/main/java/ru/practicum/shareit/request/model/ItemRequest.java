package ru.practicum.shareit.request.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.model.User;

/**
 * Модель запроса вещи.
 * Используется, когда нужной вещи нет и пользователь оставляет запрос.
 */
@Getter
@Setter
@Entity
@Table(name = "item_requests")
public class ItemRequest {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 1024)
	private String description;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "requestor_id", nullable = false)
	private User requestor;

	@Column(nullable = false)
	private LocalDateTime created;
}
