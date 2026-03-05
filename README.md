# ShareIt

ShareIt — учебный сервис для шеринга вещей: пользователи добавляют вещи, ищут доступные, бронируют на даты и оставляют комментарии после аренды.

## Возможности

### Реализовано
- CRUD пользователей
- CRUD вещей
- Поиск доступных вещей по тексту
- Бронирования вещей:
	- создание бронирования
	- подтверждение/отклонение владельцем
	- просмотр бронирования
	- списки бронирований (для арендатора и владельца) с фильтрами по состоянию
- Комментарии к вещам (после завершённой аренды)

## Архитектура

Проект состоит из двух модулей:
- `gateway` — входная точка (REST API), выполняет валидацию входящих запросов и проксирует их в `server`.
- `server` — бизнес-логика и работа с базой данных (Spring Data JPA).

Порты по умолчанию:
- `gateway`: `8080`
- `server`: `9090`

Слои приложения (внутри `server`):
- Controller: REST API
- Service: бизнес-логика
- Repository: Spring Data JPA (PostgreSQL / H2 для тестов)

Обработка ошибок централизована через `@RestControllerAdvice`.

## REST API

Все запросы выполняются в `gateway` и проксируются в `server`.

Для методов, работающих от имени пользователя, используется заголовок:
- `X-Sharer-User-Id: <userId>`

Ниже перечислены основные endpoints текущей версии.

### Пользователи: `/users`

| Метод | Endpoint | Описание |
| --- | --- | --- |
| `POST` | `/users` | Создать пользователя |
| `PATCH` | `/users/{userId}` | Обновить пользователя (частично) |
| `GET` | `/users/{userId}` | Получить пользователя по id |
| `GET` | `/users` | Получить список пользователей |
| `DELETE` | `/users/{userId}` | Удалить пользователя |

### Вещи: `/items`

| Метод | Endpoint | Описание |
| --- | --- | --- |
| `POST` | `/items` | Создать вещь |
| `PATCH` | `/items/{itemId}` | Обновить вещь (только владелец) |
| `GET` | `/items/{itemId}` | Получить вещь по id (с комментариями; для владельца — с last/next booking) |
| `GET` | `/items` | Получить вещи владельца (с last/next booking и комментариями) |
| `GET` | `/items/search?text=...` | Поиск доступных вещей |
| `POST` | `/items/{itemId}/comment` | Добавить комментарий к вещи |

### Бронирования: `/bookings`

| Метод | Endpoint | Описание |
| --- | --- | --- |
| `POST` | `/bookings` | Создать бронирование |
| `PATCH` | `/bookings/{bookingId}?approved={true\|false}` | Подтвердить/отклонить бронирование (только владелец) |
| `GET` | `/bookings/{bookingId}` | Получить бронирование (владелец или автор) |
| `GET` | `/bookings?state={state}` | Список бронирований текущего пользователя (арендатора) |
| `GET` | `/bookings/owner?state={state}` | Список бронирований для вещей текущего пользователя (владельца) |

`state` — фильтр по состоянию бронирования:
- `ALL` (по умолчанию) — все бронирования
- `CURRENT` — текущие (start <= now <= end)
- `PAST` — завершённые (end < now)
- `FUTURE` — будущие (start > now)
- `WAITING` — ожидают подтверждения владельцем
- `REJECTED` — отклонены владельцем

### Запросы вещей: `/requests`

| Метод | Endpoint | Описание |
| --- | --- | --- |
| `POST` | `/requests` | Создать запрос вещи |
| `GET` | `/requests` | Получить список запросов текущего пользователя |
| `GET` | `/requests/all?from={from}&size={size}` | Получить список запросов других пользователей (пагинация) |
| `GET` | `/requests/{requestId}` | Получить запрос по id |

## Технологии

- Java 21
- Spring Boot
- Spring Data JPA (Hibernate)
- PostgreSQL (основная БД)
- H2 (тестовая БД)
- Maven
- Lombok
- Jakarta Validation (валидация входящих запросов на стороне `gateway`)
- JUnit 5, Spring Boot Test, MockMvc

## Как запустить

### Требования
- JDK 21
- Maven 3.9+
- PostgreSQL 14+

### Подготовка PostgreSQL

По умолчанию `server` ожидает настройки из `server/src/main/resources/application.properties`:
- URL: `jdbc:postgresql://localhost:5432/shareit`
- user: `shareit`
- password: `shareit`

База данных поднимается через Docker Compose (см. `docker-compose.yml`).

Запуск PostgreSQL:

```bash
docker compose up -d
```

Проверка, что PostgreSQL поднялся:

```bash
docker compose ps
```

Ожидаемое состояние сервиса `db`:
- статус `running`
- healthcheck `healthy`

Посмотреть логи:

```bash
docker logs -f shareit-db
```

Остановка PostgreSQL:

```bash
docker compose down
```

Таблицы создаются автоматически при старте приложения из `server/src/main/resources/schema.sql`.

### Сборка и тесты

Тесты используют H2 (настройки в `server/src/main/resources/application-test.properties`).

```bash
mvn test
```

H2-консоль включена в тестовом профиле:
- если запустить приложение с профилем `test`, будет доступна по пути `/h2-console`

Примечание: при `mvn test` используется H2, но веб-сервер не поднимается, поэтому H2 Console в браузере недоступна.

Пример запуска приложения с профилем `test` (для доступа к H2 Console):

```bash
mvn -pl server spring-boot:run -Dspring-boot.run.profiles=test
```

### Запуск приложения

```bash
mvn -pl server spring-boot:run
```

Запуск `gateway`:

`gateway` проксирует запросы в `server` по адресу из `gateway/src/main/resources/application.properties`:
- `shareit-server.url=http://localhost:9090`

```bash
mvn -pl gateway spring-boot:run
```

## Качество кода

### Покрытие тестами (JaCoCo)

В проекте настроена проверка покрытия тестами (JaCoCo). В CI/CD пайплайне проверка выполняется для модуля `server`.

Проверка тестов и порогов покрытия:

```bash
mvn -pl server test jacoco:check
```

Сгенерировать HTML-отчёт:

```bash
mvn -pl server test jacoco:report
```

Отчёт будет доступен в файле:
- `server/target/site/jacoco/index.html`

### Checkstyle (если включён)

```bash
mvn checkstyle:check
```
