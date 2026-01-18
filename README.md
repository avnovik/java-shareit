# ShareIt
 
 ShareIt — учебный сервис для шеринга вещей.
  
 ## Возможности
 
 ### Реализовано
 - CRUD пользователей
 - CRUD вещей
 - Поиск доступных вещей по тексту
 
 ## Архитектура
 
 Слои приложения:
 - Controller: REST API
 - Service: бизнес-логика
 - Repository: слой хранения ( in-memory реализации)
 
 Обработка ошибок централизована через `@RestControllerAdvice`.
 
 ## REST API
 
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
 
 Для методов, работающих от имени пользователя, используется заголовок:
 - `X-Sharer-User-Id: <userId>`
 
 | Метод | Endpoint | Описание |
 | --- | --- | --- |
 | `POST` | `/items` | Создать вещь |
 | `PATCH` | `/items/{itemId}` | Обновить вещь (только владелец) |
 | `GET` | `/items/{itemId}` | Получить вещь по id |
 | `GET` | `/items` | Получить вещи владельца |
 | `GET` | `/items/search?text=...` | Поиск доступных вещей |
 
 ## Технологии
 
 - Java 21
 - Spring Boot
 - Maven
 - Lombok
 - Jakarta Validation
 - JUnit 5, Spring Boot Test, MockMvc
 
 ## Как запустить
 
 ### Требования
 - JDK 21
 - Maven 3.9+
 
 ### Сборка и тесты
 ```bash
 mvn test
 ```
 
 ### Запуск приложения
 ```bash
 mvn spring-boot:run
 ```
 
 ## Качество кода
 
 ### Checkstyle (если включён)
 ```bash
 mvn checkstyle:check
 ```
