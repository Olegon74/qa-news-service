# QA News Service: инструкция по запуску и описание проекта

Этот документ описывает, как запускать и останавливать backend и frontend в Docker, а также объясняет назначение файлов, классов и основных методов проекта.

## 1. Запуск и остановка в Docker

Все команды ниже выполняются из корня проекта:

```bash
cd qa-news-service
```

### 1.1. Запустить весь проект

Команда:

```bash
docker compose up --build
```

Что произойдёт:

- Docker соберёт образ backend из папки `backend/`.
- Docker соберёт образ frontend из папки `frontend/`.
- Запустится контейнер PostgreSQL.
- Backend дождётся, пока PostgreSQL станет healthy.
- Frontend запустится через nginx.

После запуска доступны:

```text
Frontend:   http://localhost:3000
Backend:    http://localhost:8080
Swagger UI: http://localhost:8080/swagger-ui/index.html
OpenAPI:    http://localhost:8080/v3/api-docs
PostgreSQL: localhost:5432
```

### 1.2. Запустить весь проект в фоне

Команда:

```bash
docker compose up --build -d
```

Флаг `-d` означает фоновый режим: контейнеры запускаются в фоне, терминал освобождается.

### 1.3. Посмотреть запущенные контейнеры

Команда:

```bash
docker compose ps
```

Ожидаемые сервисы:

```text
postgres
backend
frontend
```

### 1.4. Смотреть логи всех сервисов

Команда:

```bash
docker compose logs -f
```

Флаг `-f` показывает новые строки логов в реальном времени.

### 1.5. Смотреть логи только backend

Команда:

```bash
docker compose logs -f backend
```

Используй эту команду, если нужно проверить старт Spring Boot, подключение к PostgreSQL, ошибки security или ошибки API.

### 1.6. Смотреть логи только frontend

Команда:

```bash
docker compose logs -f frontend
```

Используй эту команду, если нужно проверить запуск nginx и отдачу frontend-статики.

### 1.7. Смотреть логи только PostgreSQL

Команда:

```bash
docker compose logs -f postgres
```

Используй эту команду, если нужно проверить старт базы данных, проверку готовности или ошибки подключения.

### 1.8. Остановить все контейнеры

Команда:

```bash
docker compose stop
```

Что делает:

- останавливает контейнеры
- не удаляет контейнеры
- не удаляет PostgreSQL volume
- данные в базе сохраняются

### 1.9. Запустить остановленные контейнеры снова

Команда:

```bash
docker compose start
```

Команда запускает уже созданные контейнеры без пересборки образов.

### 1.10. Остановить и удалить контейнеры

Команда:

```bash
docker compose down
```

Что делает:

- останавливает контейнеры
- удаляет контейнеры
- удаляет compose network
- не удаляет PostgreSQL volume
- данные в базе сохраняются

### 1.11. Полностью остановить проект и удалить данные PostgreSQL

Команда:

```bash
docker compose down -v
```

Что делает:

- останавливает контейнеры
- удаляет контейнеры
- удаляет compose network
- удаляет volume `postgres_data`
- удаляет все данные PostgreSQL

После следующего запуска база будет создана заново, а initial users снова появятся через `DataInitializer`.

### 1.12. Пересобрать только backend

Команда:

```bash
docker compose build backend
docker compose up -d backend
```

Используй после изменений в Java-коде, `pom.xml` или `application.yml`.

### 1.13. Пересобрать только frontend

Команда:

```bash
docker compose build frontend
docker compose up -d frontend
```

Используй после изменений в React/TypeScript/CSS, `package.json`, `vite.config.ts`, `nginx.conf` или `Dockerfile` frontend.

### 1.14. Перезапустить только backend

Команда:

```bash
docker compose restart backend
```

Подходит, если образ не менялся, но нужно перезапустить приложение.

### 1.15. Перезапустить только frontend

Команда:

```bash
docker compose restart frontend
```

Подходит, если образ не менялся, но нужно перезапустить контейнер nginx.

### 1.16. Проверить Docker Compose файл

Команда:

```bash
docker compose config
```

Команда валидирует `docker-compose.yml` и показывает итоговую конфигурацию.

## 2. Как устроен Docker Compose

Файл: `docker-compose.yml`

Сервисы:

- `postgres` - база данных PostgreSQL 16.
- `backend` - Spring Boot API на Java 21.
- `frontend` - React-приложение, собранное в статические файлы и запущенное через nginx.

### 2.1. postgres

Основные настройки:

```text
container_name: qa-news-postgres
image: postgres:16
ports: 5432:5432
database: qa_news_db
username: qa_news_user
password: qa_news_password
volume: postgres_data
```

Проверка готовности:

```text
pg_isready -U qa_news_user -d qa_news_db
```

Он нужен, чтобы backend стартовал только после готовности PostgreSQL.

### 2.2. backend

Основные настройки:

```text
container_name: qa-news-backend
build context: ./backend
ports: 8080:8080
depends_on: postgres service_healthy
```

В Docker backend подключается к базе по hostname `postgres`:

```text
jdbc:postgresql://postgres:5432/qa_news_db
```

### 2.3. frontend

Основные настройки:

```text
container_name: qa-news-frontend
build context: ./frontend
ports: 3000:80
depends_on: backend
```

Внутри контейнера frontend работает на nginx port `80`, а на хосте доступен на port `3000`.

## 3. Основная структура проекта

```text
qa-news-service/
+-- backend/
+-- frontend/
+-- docker-compose.yml
+-- README.md
+-- CODEX_MEMORY.md
+-- PROJECT_GUIDE_RU.md
`-- .gitignore
```

## 4. Описание файлов в корне проекта

### `.gitignore`

Файл с правилами игнорирования для Git.

Нужен, чтобы в репозиторий не попадали:

- `target/`
- `node_modules/`
- `dist/`
- `.env`
- IDE-файлы
- logs
- build artifacts

### `README.md`

Основное описание проекта на русском языке.

Содержит:

- описание проекта
- tech stack
- endpoints
- auth
- roles
- business rules
- инструкции запуска
- Swagger URL
- frontend URL
- future testing plan

### `CODEX_MEMORY.md`

Файл памяти для следующих запусков Codex в этом проекте.

Нужен, чтобы быстро восстановить контекст:

- цель проекта
- текущий scope
- архитектура
- важные правила
- ограничения
- будущие этапы

### `PROJECT_GUIDE_RU.md`

Этот файл.

Нужен как подробная техническая справка по запуску, остановке, структуре, классам, методам и файлам проекта.

### `docker-compose.yml`

Файл запуска инфраструктуры и приложений через Docker Compose.

Поднимает:

- PostgreSQL
- backend
- frontend

## 5. Backend: структура и назначение

Backend находится в папке:

```text
backend/
```

Корневой Java-пакет:

```text
com.example.qanewsservice
```

### `backend/pom.xml`

Maven-конфигурация backend.

Определяет:

- Spring Boot parent
- Java version 21
- зависимости
- Spring Boot Maven plugin

Основные зависимости:

- `spring-boot-starter-web` - REST API.
- `spring-boot-starter-data-jpa` - работа с PostgreSQL через JPA.
- `spring-boot-starter-validation` - Bean Validation.
- `spring-boot-starter-security` - security layer.
- `postgresql` - JDBC driver.
- `jjwt-api`, `jjwt-impl`, `jjwt-jackson` - генерация и проверка JWT.
- `springdoc-openapi-starter-webmvc-ui` - Swagger UI и OpenAPI.
- `lombok` - генерация getters, setters, builders, constructors.

### `backend/Dockerfile`

Dockerfile backend.

Состоит из двух стадий:

- стадия сборки на `maven:3.9.9-eclipse-temurin-21`
- стадия запуска на `eclipse-temurin:21-jre`

Что делает:

- копирует `pom.xml`
- скачивает зависимости
- копирует `src`
- собирает jar через Maven
- запускает jar командой `java -jar app.jar`

### `backend/.dockerignore`

Исключает из Docker build context:

- `target`
- `.mvn`
- IDE-файлы

Нужен, чтобы Docker build был быстрее и чище.

### `backend/src/main/resources/application.yml`

Основной конфигурационный файл Spring Boot.

Содержит:

- `server.port=8080`
- имя приложения
- настройки datasource
- настройки JPA/Hibernate
- путь Swagger UI
- JWT secret
- JWT expiration

Datasource использует env fallback:

```yaml
url: ${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5432/qa_news_db}
```

Это позволяет запускать backend и локально, и внутри Docker без отдельных profile-файлов.

## 6. Backend: главный класс приложения

### `QaNewsServiceApplication`

Файл:

```text
backend/src/main/java/com/example/qanewsservice/QaNewsServiceApplication.java
```

Назначение:

- стартовая точка Spring Boot приложения
- запускает embedded web server
- включает сканирование компонентов от пакета `com.example.qanewsservice`

Метод:

```java
public static void main(String[] args)
```

Что делает:

- вызывает `SpringApplication.run(...)`
- поднимает Spring ApplicationContext
- запускает backend на port `8080`

## 7. Backend: модель новостей

### `News`

Файл:

```text
backend/src/main/java/com/example/qanewsservice/model/News.java
```

Назначение:

- JPA entity для таблицы `news`
- хранит данные новости
- содержит ограничения валидации и настройки колонок

Основные поля:

- `id` - primary key.
- `title` - заголовок, обязательный, максимум 150 символов.
- `previewText` - краткий текст, обязательный, максимум 300 символов.
- `fullText` - полный текст, обязательный, максимум 5000 символов.
- `author` - автор, обязательный, максимум 100 символов.
- `status` - статус новости.
- `createdAt` - дата создания.
- `updatedAt` - дата последнего обновления.
- `publishedAt` - дата публикации.
- `archivedAt` - дата архивации.

Метод:

```java
void prePersist()
```

Что делает:

- вызывается JPA перед первым сохранением entity
- выставляет `createdAt`
- выставляет `updatedAt`
- если `status == null`, устанавливает `DRAFT`

Метод:

```java
void preUpdate()
```

Что делает:

- вызывается JPA перед обновлением entity
- обновляет `updatedAt`

### `NewsStatus`

Файл:

```text
backend/src/main/java/com/example/qanewsservice/model/NewsStatus.java
```

Назначение:

- enum со статусами новости

Значения:

- `DRAFT` - черновик.
- `PUBLISHED` - опубликована.
- `ARCHIVED` - в архиве.

## 8. Backend: модель пользователей

### `User`

Файл:

```text
backend/src/main/java/com/example/qanewsservice/auth/model/User.java
```

Назначение:

- JPA entity для таблицы `users`
- хранит пользователей, которые могут логиниться в систему

Основные поля:

- `id` - primary key.
- `username` - уникальное имя пользователя.
- `password` - BCrypt hash пароля.
- `role` - роль пользователя.
- `enabled` - включён ли пользователь.
- `createdAt` - дата создания.
- `updatedAt` - дата последнего обновления.

Метод:

```java
void prePersist()
```

Что делает:

- вызывается JPA перед первым сохранением user
- выставляет `createdAt`
- выставляет `updatedAt`
- выставляет `enabled = true`

Метод:

```java
void preUpdate()
```

Что делает:

- вызывается JPA перед обновлением user
- обновляет `updatedAt`

### `UserRole`

Файл:

```text
backend/src/main/java/com/example/qanewsservice/auth/model/UserRole.java
```

Назначение:

- enum ролей пользователя

Значения:

- `ADMIN` - полный доступ.
- `VIEWER` - доступ только на чтение.

## 9. Backend: DTO

DTO нужны, чтобы не отдавать JPA-сущности напрямую через API и явно контролировать формат входящих запросов и исходящих ответов.

### `CreateNewsRequest`

Файл:

```text
backend/src/main/java/com/example/qanewsservice/dto/request/CreateNewsRequest.java
```

Назначение:

- тело запроса для `POST /api/news`
- используется при создании новости

Поля:

- `title`
- `previewText`
- `fullText`
- `author`

Валидация:

- все поля обязательные через `@NotBlank`
- максимальные длины задаются через `@Size`

### `UpdateNewsRequest`

Файл:

```text
backend/src/main/java/com/example/qanewsservice/dto/request/UpdateNewsRequest.java
```

Назначение:

- тело запроса для `PUT /api/news/{id}`
- используется при редактировании новости

Поля и валидация такие же, как у `CreateNewsRequest`.

### `NewsResponse`

Файл:

```text
backend/src/main/java/com/example/qanewsservice/dto/response/NewsResponse.java
```

Назначение:

- DTO ответа для endpoints новостей
- описывает, что backend возвращает клиенту после операций с новостями

Поля:

- `id`
- `title`
- `previewText`
- `fullText`
- `author`
- `status`
- `createdAt`
- `updatedAt`
- `publishedAt`
- `archivedAt`

### `ErrorResponse`

Файл:

```text
backend/src/main/java/com/example/qanewsservice/dto/response/ErrorResponse.java
```

Назначение:

- единый формат ошибки API

Поля:

- `timestamp` - время ошибки.
- `status` - HTTP status code.
- `error` - название HTTP ошибки.
- `message` - человекочитаемое описание.
- `path` - endpoint, на котором произошла ошибка.
- `validationErrors` - ошибки валидации по полям.

### `LoginRequest`

Файл:

```text
backend/src/main/java/com/example/qanewsservice/auth/dto/LoginRequest.java
```

Назначение:

- тело запроса для `POST /api/auth/login`

Поля:

- `username`
- `password`

Валидация:

- оба поля обязательные через `@NotBlank`

### `LoginResponse`

Файл:

```text
backend/src/main/java/com/example/qanewsservice/auth/dto/LoginResponse.java
```

Назначение:

- DTO ответа при успешном входе

Поля:

- `token` - JWT token.
- `tokenType` - тип token, сейчас `Bearer`.
- `username` - имя пользователя.
- `role` - роль пользователя.

## 10. Backend: репозитории

Репозитории нужны для доступа к базе данных через Spring Data JPA.

### `NewsRepository`

Файл:

```text
backend/src/main/java/com/example/qanewsservice/repository/NewsRepository.java
```

Наследуется от:

```java
JpaRepository<News, Long>
```

За счёт `JpaRepository` доступны готовые методы:

- `save`
- `findById`
- `findAll`
- `delete`
- `existsById`
- другие стандартные JPA-операции

Метод:

```java
List<News> findByStatus(NewsStatus status)
```

Что делает:

- возвращает новости с конкретным статусом
- используется для `GET /api/news?status=DRAFT`

### `UserRepository`

Файл:

```text
backend/src/main/java/com/example/qanewsservice/auth/repository/UserRepository.java
```

Наследуется от:

```java
JpaRepository<User, Long>
```

Метод:

```java
Optional<User> findByUsername(String username)
```

Что делает:

- ищет пользователя по username
- используется при login и при проверке JWT

Метод:

```java
boolean existsByUsername(String username)
```

Что делает:

- проверяет, существует ли пользователь
- используется в `DataInitializer`, чтобы не создавать дубль initial user

## 11. Backend: маппер

### `NewsMapper`

Файл:

```text
backend/src/main/java/com/example/qanewsservice/mapper/NewsMapper.java
```

Назначение:

- преобразует DTO входящего запроса в JPA-сущность
- преобразует JPA-сущность в DTO исходящего ответа
- отделяет API-модель от JPA-модели

Метод:

```java
News toEntity(CreateNewsRequest request)
```

Что делает:

- создаёт `News` из `CreateNewsRequest`
- переносит `title`, `previewText`, `fullText`, `author`
- устанавливает статус `DRAFT`

Метод:

```java
NewsResponse toResponse(News news)
```

Что делает:

- создаёт `NewsResponse` из `News`
- переносит все публичные поля новости для ответа API

## 12. Backend: сервисный слой

### `NewsService`

Файл:

```text
backend/src/main/java/com/example/qanewsservice/service/NewsService.java
```

Назначение:

- интерфейс бизнес-логики новостей
- задаёт контракт для controller
- упрощает будущую замену реализации и тестирование

Методы:

```java
NewsResponse createNews(CreateNewsRequest request)
```

Создаёт новость в статусе `DRAFT`.

```java
NewsResponse getNewsById(Long id)
```

Возвращает одну новость по id.

```java
List<NewsResponse> getAllNews(NewsStatus status)
```

Возвращает список всех новостей или список по статусу.

```java
NewsResponse updateNews(Long id, UpdateNewsRequest request)
```

Обновляет новость, если она не `ARCHIVED`.

```java
NewsResponse publishNews(Long id)
```

Публикует новость, если она в статусе `DRAFT`.

```java
NewsResponse archiveNews(Long id)
```

Архивирует новость, если она в статусе `PUBLISHED`.

```java
void deleteNews(Long id)
```

Удаляет новость, если она в статусе `DRAFT` или `ARCHIVED`.

### `NewsServiceImpl`

Файл:

```text
backend/src/main/java/com/example/qanewsservice/service/impl/NewsServiceImpl.java
```

Назначение:

- основная реализация бизнес-логики новостей
- проверяет статусные переходы
- работает с `NewsRepository`
- возвращает наружу `NewsResponse`

Метод:

```java
NewsResponse createNews(CreateNewsRequest request)
```

Что делает:

- преобразует входящий запрос в `News` через `NewsMapper`
- сохраняет entity через `newsRepository.save`
- возвращает `NewsResponse`

Метод:

```java
NewsResponse getNewsById(Long id)
```

Что делает:

- ищет новость через private method `findNews`
- если новости нет, будет `NewsNotFoundException`
- возвращает DTO

Метод:

```java
List<NewsResponse> getAllNews(NewsStatus status)
```

Что делает:

- если `status == null`, возвращает все новости через `findAll`
- если `status` передан, возвращает только новости с этим статусом через `findByStatus`
- преобразует каждую entity в DTO

Метод:

```java
NewsResponse updateNews(Long id, UpdateNewsRequest request)
```

Что делает:

- ищет новость
- запрещает обновление, если статус `ARCHIVED`
- обновляет `title`, `previewText`, `fullText`, `author`
- не меняет `status`
- не очищает `publishedAt`
- не меняет `archivedAt`
- сохраняет изменения

Метод:

```java
NewsResponse publishNews(Long id)
```

Что делает:

- ищет новость
- разрешает публикацию только из `DRAFT`
- меняет статус на `PUBLISHED`
- заполняет `publishedAt`
- сохраняет изменения

Метод:

```java
NewsResponse archiveNews(Long id)
```

Что делает:

- ищет новость
- разрешает архивацию только из `PUBLISHED`
- меняет статус на `ARCHIVED`
- заполняет `archivedAt`
- сохраняет изменения

Метод:

```java
void deleteNews(Long id)
```

Что делает:

- ищет новость
- запрещает удаление, если статус `PUBLISHED`
- удаляет `DRAFT` или `ARCHIVED` новость

Private method:

```java
News findNews(Long id)
```

Что делает:

- ищет новость по id через repository
- если не найдена, бросает `NewsNotFoundException`

## 13. Backend: сервисный слой аутентификации

### `AuthService`

Файл:

```text
backend/src/main/java/com/example/qanewsservice/auth/service/AuthService.java
```

Назначение:

- реализует login logic
- проверяет username/password
- проверяет, что пользователь включён
- генерирует JWT через `JwtService`

Метод:

```java
LoginResponse login(LoginRequest request)
```

Что делает:

1. Ищет пользователя по username.
2. Если пользователь не найден, бросает `BadCredentialsException`.
3. Если `enabled = false`, бросает `BadCredentialsException`.
4. Проверяет password через `PasswordEncoder.matches`.
5. Если пароль неверный, бросает `BadCredentialsException`.
6. Если всё корректно, генерирует JWT token.
7. Возвращает `LoginResponse`.

### `JwtService`

Файл:

```text
backend/src/main/java/com/example/qanewsservice/auth/service/JwtService.java
```

Назначение:

- создаёт JWT token
- извлекает claims
- проверяет срок действия token
- проверяет соответствие token и user

Метод:

```java
String generateToken(User user)
```

Что делает:

- создаёт JWT
- пишет username в subject
- добавляет claim `role`
- добавляет `issuedAt`
- добавляет `expiration`
- подписывает token secret key

Метод:

```java
String extractUsername(String token)
```

Что делает:

- читает subject из JWT
- возвращает username

Метод:

```java
boolean isTokenValid(String token, User user)
```

Что делает:

- извлекает username из token
- сравнивает username с user из базы
- проверяет, что token не истёк

Метод:

```java
<T> T extractClaim(String token, Function<Claims, T> claimsResolver)
```

Что делает:

- парсит JWT
- проверяет подпись
- возвращает нужный claim через переданную функцию

Private method:

```java
boolean isTokenExpired(String token)
```

Что делает:

- читает expiration claim
- сравнивает expiration с текущим временем

Private method:

```java
Key getSigningKey()
```

Что делает:

- пытается прочитать secret как Base64
- если secret не Base64, использует bytes строки
- создаёт HMAC signing key

## 14. Backend: контроллеры

### `AuthController`

Файл:

```text
backend/src/main/java/com/example/qanewsservice/auth/controller/AuthController.java
```

Назначение:

- REST-контроллер для endpoint входа

Endpoint:

```text
POST /api/auth/login
```

Метод:

```java
ResponseEntity<LoginResponse> login(LoginRequest request)
```

Что делает:

- принимает username/password
- запускает Bean Validation через `@Valid`
- вызывает `authService.login`
- возвращает `200 OK` и JWT token

### `NewsController`

Файл:

```text
backend/src/main/java/com/example/qanewsservice/controller/NewsController.java
```

Назначение:

- REST controller для `/api/news`
- описывает endpoints новостей
- содержит Swagger annotations
- помечен `@SecurityRequirement(name = "bearerAuth")`

Метод:

```java
ResponseEntity<NewsResponse> createNews(CreateNewsRequest request)
```

Endpoint:

```text
POST /api/news
```

Что делает:

- валидирует тело запроса
- создаёт новость
- возвращает `201 Created`

Метод:

```java
ResponseEntity<NewsResponse> getNewsById(Long id)
```

Endpoint:

```text
GET /api/news/{id}
```

Что делает:

- возвращает новость по id
- если новости нет, service бросает `NewsNotFoundException`

Метод:

```java
ResponseEntity<List<NewsResponse>> getAllNews(String status)
```

Endpoint:

```text
GET /api/news
GET /api/news?status=DRAFT
```

Что делает:

- принимает optional query param `status`
- парсит status через `parseStatus`
- возвращает весь список или фильтрованный список

Метод:

```java
ResponseEntity<NewsResponse> updateNews(Long id, UpdateNewsRequest request)
```

Endpoint:

```text
PUT /api/news/{id}
```

Что делает:

- валидирует тело запроса
- обновляет новость
- возвращает `200 OK`

Метод:

```java
ResponseEntity<NewsResponse> publishNews(Long id)
```

Endpoint:

```text
POST /api/news/{id}/publish
```

Что делает:

- публикует новость в статусе `DRAFT`
- возвращает обновлённую новость

Метод:

```java
ResponseEntity<NewsResponse> archiveNews(Long id)
```

Endpoint:

```text
POST /api/news/{id}/archive
```

Что делает:

- архивирует новость в статусе `PUBLISHED`
- возвращает обновлённую новость

Метод:

```java
ResponseEntity<Void> deleteNews(Long id)
```

Endpoint:

```text
DELETE /api/news/{id}
```

Что делает:

- удаляет новость
- возвращает `204 No Content`

Private method:

```java
NewsStatus parseStatus(String status)
```

Что делает:

- если status пустой, возвращает `null`
- приводит status к upper case
- преобразует строку в `NewsStatus`
- если status некорректный, бросает `IllegalArgumentException`

## 15. Backend: исключения и обработка ошибок

### `NewsNotFoundException`

Файл:

```text
backend/src/main/java/com/example/qanewsservice/exception/NewsNotFoundException.java
```

Назначение:

- exception для случая, когда новость не найдена

Constructor:

```java
NewsNotFoundException(Long id)
```

Что делает:

- формирует сообщение `News not found with id: {id}`

### `InvalidNewsStatusException`

Файл:

```text
backend/src/main/java/com/example/qanewsservice/exception/InvalidNewsStatusException.java
```

Назначение:

- exception для некорректных бизнес-переходов статуса
- например, попытка архивировать `DRAFT` или удалить `PUBLISHED`

Constructor:

```java
InvalidNewsStatusException(String message)
```

Что делает:

- сохраняет переданное сообщение ошибки

### `GlobalExceptionHandler`

Файл:

```text
backend/src/main/java/com/example/qanewsservice/exception/GlobalExceptionHandler.java
```

Назначение:

- централизованно обрабатывает исключения
- возвращает ошибки в формате `ErrorResponse`

Метод:

```java
handleValidation(MethodArgumentNotValidException exception, HttpServletRequest request)
```

Что делает:

- обрабатывает ошибки `@Valid`
- собирает `validationErrors`
- возвращает `400 Bad Request`

Метод:

```java
handleNewsNotFound(NewsNotFoundException exception, HttpServletRequest request)
```

Что делает:

- возвращает `404 Not Found`

Метод:

```java
handleInvalidNewsStatus(InvalidNewsStatusException exception, HttpServletRequest request)
```

Что делает:

- возвращает `409 Conflict`

Метод:

```java
handleBadCredentials(BadCredentialsException exception, HttpServletRequest request)
```

Что делает:

- возвращает `401 Unauthorized`
- скрывает точную причину ошибки login
- message всегда `Invalid username or password`

Метод:

```java
handleAccessDenied(AccessDeniedException exception, HttpServletRequest request)
```

Что делает:

- возвращает `403 Forbidden`

Метод:

```java
handleIllegalArgument(IllegalArgumentException exception, HttpServletRequest request)
```

Что делает:

- возвращает `400 Bad Request`
- используется, например, для неправильного `status` query param

Метод:

```java
handleException(Exception exception, HttpServletRequest request)
```

Что делает:

- fallback для неожиданных ошибок
- возвращает `500 Internal Server Error`

Private method:

```java
buildResponse(HttpStatus status, String message, String path, Map<String, String> validationErrors)
```

Что делает:

- собирает объект `ErrorResponse`
- выставляет timestamp, status, error, message, path и validationErrors
- возвращает `ResponseEntity`

## 16. Backend: конфигурация

### `SecurityConfig`

Файл:

```text
backend/src/main/java/com/example/qanewsservice/config/SecurityConfig.java
```

Назначение:

- настраивает Spring Security
- отключает CSRF для API без серверных сессий
- включает CORS
- настраивает политику сессий `STATELESS`
- подключает `JwtAuthenticationFilter`
- задаёт правила доступа к endpoints
- задаёт JSON-ответы для 401 и 403
- создаёт `PasswordEncoder`

Метод:

```java
SecurityFilterChain securityFilterChain(HttpSecurity http)
```

Что делает:

- разрешает login и Swagger без token
- разрешает GET `/api/news/**` для `ADMIN` и `VIEWER`
- разрешает остальные `/api/news/**` только для `ADMIN`
- требует авторизацию для остальных запросов
- добавляет JWT filter перед `UsernamePasswordAuthenticationFilter`

Метод:

```java
PasswordEncoder passwordEncoder()
```

Что делает:

- создаёт `BCryptPasswordEncoder`
- используется для хеширования и проверки паролей

Private method:

```java
writeError(HttpServletResponse response, String path, HttpStatus status, String error, String message)
```

Что делает:

- вручную пишет JSON-ответ с ошибкой для ошибок безопасности
- используется authentication entry point и access denied handler

### `JwtAuthenticationFilter`

Файл:

```text
backend/src/main/java/com/example/qanewsservice/config/JwtAuthenticationFilter.java
```

Назначение:

- фильтр, который проверяет JWT в header `Authorization`
- если token валиден, создаёт Spring Security authentication

Метод:

```java
doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
```

Что делает:

1. Читает header `Authorization`.
2. Если header отсутствует или не начинается с `Bearer `, пропускает запрос дальше.
3. Извлекает token.
4. Извлекает username из token.
5. Ищет пользователя в PostgreSQL.
6. Проверяет, что пользователь включён.
7. Проверяет token через `JwtService`.
8. Создаёт `UsernamePasswordAuthenticationToken`.
9. Добавляет authority `ROLE_ADMIN` или `ROLE_VIEWER`.
10. Сохраняет authentication в `SecurityContextHolder`.
11. Если token некорректный, очищает security context.

### `CorsConfig`

Файл:

```text
backend/src/main/java/com/example/qanewsservice/config/CorsConfig.java
```

Назначение:

- разрешает frontend обращаться к backend из browser

Метод:

```java
CorsConfigurationSource corsConfigurationSource()
```

Что делает:

- разрешает origin `http://localhost:3000`
- разрешает методы `GET`, `POST`, `PUT`, `DELETE`, `OPTIONS`
- разрешает headers `Authorization`, `Content-Type`, `Accept`
- регистрирует CORS config для всех paths

### `OpenApiConfig`

Файл:

```text
backend/src/main/java/com/example/qanewsservice/config/OpenApiConfig.java
```

Назначение:

- настраивает OpenAPI metadata
- добавляет Bearer JWT security scheme
- обеспечивает кнопку `Authorize` в Swagger UI

Метод:

```java
OpenAPI openAPI()
```

Что делает:

- задаёт title, description, version
- регистрирует security scheme `bearerAuth`

### `DataInitializer`

Файл:

```text
backend/src/main/java/com/example/qanewsservice/auth/config/DataInitializer.java
```

Назначение:

- создаёт initial users при старте приложения
- нужен, чтобы проект был готов к ручной проверке и будущим автотестам

Метод:

```java
CommandLineRunner createInitialUsers()
```

Что делает:

- при старте приложения создаёт `admin` и `viewer`, если их ещё нет

Private method:

```java
createUserIfMissing(String username, String password, UserRole role)
```

Что делает:

- проверяет существование username
- если user уже есть, ничего не делает
- если user отсутствует, хеширует password через BCrypt
- сохраняет user в PostgreSQL

## 17. Frontend: структура и назначение

Frontend находится в папке:

```text
frontend/
```

Используется:

- React
- TypeScript
- Vite
- Axios
- plain CSS

### `frontend/package.json`

Описание npm-проекта.

Содержит:

- имя frontend-пакета
- зависимости
- npm-скрипты

Скрипты:

```text
npm run dev
```

Запускает Vite dev server на port `3000`.

```text
npm run build
```

Проверяет TypeScript через `tsc --noEmit` и собирает production build через Vite.

```text
npm run preview
```

Запускает preview production build на port `3000`.

### `frontend/package-lock.json`

Фиксирует точные версии npm-зависимостей.

Нужен для воспроизводимой установки зависимостей через:

```bash
npm install
```

### `frontend/Dockerfile`

Dockerfile frontend.

Состоит из двух стадий:

- стадия сборки на `node:22-alpine`
- стадия запуска на `nginx:1.27-alpine`

Что делает:

- устанавливает npm-зависимости
- собирает Vite build
- копирует `dist` в nginx
- отдаёт frontend как статические файлы

### `frontend/.dockerignore`

Исключает из Docker build context:

- `node_modules`
- `dist`
- `.env.local`
- logs
- IDE-файлы

### `frontend/.env.example`

Пример env-файла для локального запуска frontend.

Содержит:

```text
VITE_API_BASE_URL=http://localhost:8080
```

### `frontend/nginx.conf`

Конфигурация nginx для контейнера frontend.

Что делает:

- слушает port `80`
- отдаёт файлы из `/usr/share/nginx/html`
- для всех frontend-маршрутов возвращает `index.html`

Это полезно для SPA-приложений, даже если сейчас роутинг не используется.

### `frontend/index.html`

HTML-точка входа Vite-приложения.

Содержит:

- `<div id="root"></div>`
- подключение `/src/main.tsx`
- title `QA News Service`

### `frontend/tsconfig.json`

Конфигурация TypeScript.

Определяет правила компиляции TypeScript и JSX для React/Vite.

### `frontend/vite.config.ts`

Конфигурация Vite.

Что делает:

- подключает React plugin
- настраивает dev server на `0.0.0.0:3000`
- настраивает preview server на `0.0.0.0:3000`

### `frontend/src/vite-env.d.ts`

Type declarations для Vite.

Нужен, чтобы TypeScript понимал `import.meta.env`.

## 18. Frontend: точка входа и основное приложение

### `frontend/src/main.tsx`

Точка входа React-приложения.

Что делает:

- импортирует React и ReactDOM
- импортирует `App`
- импортирует `index.css`
- монтирует приложение в DOM element `root`
- оборачивает приложение в `React.StrictMode`

### `frontend/src/App.tsx`

Главный компонент frontend-приложения.

Назначение:

- хранит глобальное состояние страницы
- управляет login/logout
- загружает список новостей
- управляет выбранной новостью
- управляет режимом редактирования
- показывает success/error messages
- применяет role-based UI

Основные состояния:

- `authenticated` - залогинен ли пользователь.
- `username` - текущий username.
- `role` - текущая роль.
- `news` - список новостей.
- `selectedNews` - новость для детального просмотра.
- `editingNews` - новость, которая редактируется.
- `statusFilter` - текущий фильтр.
- `success` - сообщение об успешном действии.
- `error` - ошибка API или validation.
- `authError` - ошибка login/auth.

Метод:

```typescript
handleLogin(nextUsername, nextRole)
```

Что делает:

- обновляет username и role
- переводит приложение в состояние авторизованного пользователя
- очищает auth errors

Метод:

```typescript
handleLogout()
```

Что делает:

- очищает auth data из localStorage
- сбрасывает состояние приложения
- возвращает пользователя на экран login

Метод:

```typescript
loadNews(filter)
```

Что делает:

- вызывает `getAllNews`
- если фильтр `ALL`, отправляет запрос без status
- иначе отправляет запрос с конкретным status
- обновляет список новостей

Метод:

```typescript
handleCreate(request)
```

Что делает:

- вызывает `createNews`
- показывает success message
- обновляет список

Метод:

```typescript
handleUpdate(id, request)
```

Что делает:

- вызывает `updateNews`
- выходит из режима редактирования
- показывает success message
- обновляет список

Метод:

```typescript
handleView(id)
```

Что делает:

- вызывает `getNewsById`
- сохраняет результат в `selectedNews`
- показывает блок деталей новости

Метод:

```typescript
handlePublish(id)
```

Что делает:

- вызывает `publishNews`
- обновляет selected news
- показывает success message
- обновляет список

Метод:

```typescript
handleArchive(id)
```

Что делает:

- вызывает `archiveNews`
- обновляет selected news
- показывает success message
- обновляет список

Метод:

```typescript
handleDelete(id)
```

Что делает:

- вызывает `deleteNews`
- очищает selected/editing news, если удалили текущую новость
- показывает success message
- обновляет список

Метод:

```typescript
runApiAction(action)
```

Что делает:

- единая обёртка для API-действий
- очищает предыдущую ошибку
- ловит ошибки
- при `401` очищает auth и возвращает на login
- при `403` показывает `Error: Forbidden`
- validation errors показывает в UI

Функция:

```typescript
getApiErrorMessage(apiError)
```

Что делает:

- преобразует Axios error в понятный текст
- отдельно обрабатывает `401`, `403` и validation errors

## 19. Frontend: слой API

### `frontend/src/api/authApi.ts`

Назначение:

- содержит функцию login
- отправляет запрос на endpoint аутентификации backend

Функция:

```typescript
login(username, password)
```

Что делает:

- отправляет `POST /api/auth/login`
- передаёт username и password
- возвращает `LoginResponse`

### `frontend/src/api/newsApi.ts`

Назначение:

- содержит функции для работы с endpoints новостей
- автоматически добавляет JWT token в headers

Axios client:

```typescript
newsClient
```

Что делает:

- использует `VITE_API_BASE_URL`
- через interceptor добавляет `Authorization: Bearer <token>`

Функция:

```typescript
createNews(request)
```

Что делает:

- отправляет `POST /api/news`
- создаёт новость

Функция:

```typescript
getNewsById(id)
```

Что делает:

- отправляет `GET /api/news/{id}`
- возвращает одну новость

Функция:

```typescript
getAllNews(status?)
```

Что делает:

- отправляет `GET /api/news`
- если status передан, добавляет query param `status`
- возвращает список новостей

Функция:

```typescript
updateNews(id, request)
```

Что делает:

- отправляет `PUT /api/news/{id}`
- обновляет новость

Функция:

```typescript
publishNews(id)
```

Что делает:

- отправляет `POST /api/news/{id}/publish`
- публикует новость в статусе `DRAFT`

Функция:

```typescript
archiveNews(id)
```

Что делает:

- отправляет `POST /api/news/{id}/archive`
- архивирует новость в статусе `PUBLISHED`

Функция:

```typescript
deleteNews(id)
```

Что делает:

- отправляет `DELETE /api/news/{id}`
- удаляет новость

## 20. Frontend: хранение данных аутентификации

### `frontend/src/auth/authStorage.ts`

Назначение:

- хранит auth data в browser `localStorage`
- используется слоем API frontend и компонентом `App`

Ключи:

```text
qa_news_token
qa_news_username
qa_news_role
```

Функция:

```typescript
saveAuthData(token, username, role)
```

Что делает:

- сохраняет JWT token
- сохраняет username
- сохраняет role

Функция:

```typescript
getToken()
```

Что делает:

- возвращает token из localStorage
- используется axios interceptor

Функция:

```typescript
getUsername()
```

Что делает:

- возвращает username из localStorage

Функция:

```typescript
getRole()
```

Что делает:

- возвращает role из localStorage

Функция:

```typescript
clearAuthData()
```

Что делает:

- удаляет token, username и role из localStorage

Функция:

```typescript
isAuthenticated()
```

Что делает:

- возвращает `true`, если token есть в localStorage

## 21. Frontend: компоненты

### `LoginForm`

Файл:

```text
frontend/src/components/LoginForm.tsx
```

Назначение:

- форма входа в приложение
- принимает username/password
- показывает auth errors

Входные параметры компонента:

- `onLogin` - функция обратного вызова при успешном входе.
- `externalError` - ошибка, пришедшая снаружи, например после `401`.

Метод:

```typescript
handleSubmit(event)
```

Что делает:

- отменяет стандартную отправку формы
- вызывает `login`
- сохраняет auth data
- вызывает `onLogin`
- при `401` показывает `Error: Unauthorized`
- при другой ошибке показывает текст ошибки

Ключевые test ids:

- `login-form`
- `username-input`
- `password-input`
- `login-button`
- `auth-error-message`

### `NewsForm`

Файл:

```text
frontend/src/components/NewsForm.tsx
```

Назначение:

- форма создания и редактирования новости
- используется только для ADMIN

Входные параметры компонента:

- `editingNews` - текущая редактируемая новость или `null`.
- `onCreate` - функция обратного вызова для создания.
- `onUpdate` - функция обратного вызова для обновления.
- `onCancelEdit` - функция обратного вызова для отмены редактирования.

Метод:

```typescript
handleSubmit(event)
```

Что делает:

- отменяет стандартную отправку формы
- запускает клиентскую валидацию
- если есть ошибка, показывает её
- если `editingNews` есть, вызывает `onUpdate`
- если `editingNews` нет, вызывает `onCreate`
- очищает форму после успешного действия

Функция:

```typescript
validate(form)
```

Что делает:

- проверяет обязательность `title`
- проверяет обязательность `previewText`
- проверяет обязательность `fullText`
- проверяет обязательность `author`

Ключевые test ids:

- `news-form`
- `title-input`
- `preview-text-input`
- `full-text-input`
- `author-input`
- `create-news-button`
- `update-news-button`
- `cancel-edit-button`

### `NewsList`

Файл:

```text
frontend/src/components/NewsList.tsx
```

Назначение:

- отображает список новостей
- для каждой новости рендерит `NewsCard`
- показывает `No news found.`, если список пустой

Входные параметры компонента:

- `news` - список новостей.
- `role` - роль текущего пользователя.
- `onView`
- `onEdit`
- `onPublish`
- `onArchive`
- `onDelete`

Ключевой test id:

- `news-list`

### `NewsCard`

Файл:

```text
frontend/src/components/NewsCard.tsx
```

Назначение:

- карточка одной новости в списке
- показывает метаданные новости
- показывает кнопки в зависимости от role и status

Внутренняя логика:

- `isAdmin` - true только для role `ADMIN`.
- `canEdit` - ADMIN и status `DRAFT` или `PUBLISHED`.
- `canPublish` - ADMIN и status `DRAFT`.
- `canArchive` - ADMIN и status `PUBLISHED`.
- `canDelete` - ADMIN и status `DRAFT` или `ARCHIVED`.

Функция:

```typescript
formatDate(value)
```

Что делает:

- если дата есть, форматирует через `toLocaleString`
- если даты нет, возвращает `-`

Ключевые dynamic test ids:

- `news-card-{id}`
- `news-title-{id}`
- `news-status-{id}`
- `view-news-button-{id}`
- `edit-news-button-{id}`
- `publish-news-button-{id}`
- `archive-news-button-{id}`
- `delete-news-button-{id}`

### `NewsDetails`

Файл:

```text
frontend/src/components/NewsDetails.tsx
```

Назначение:

- показывает полную информацию по выбранной новости
- если новость не выбрана, ничего не рендерит

Функция:

```typescript
formatDate(value)
```

Что делает:

- форматирует дату или возвращает `-`

Ключевой test id:

- `selected-news-details`

### `StatusFilter`

Файл:

```text
frontend/src/components/StatusFilter.tsx
```

Назначение:

- dropdown для фильтрации новостей по статусу

Варианты выбора:

- `ALL`
- `DRAFT`
- `PUBLISHED`
- `ARCHIVED`

Входные параметры компонента:

- `value` - текущий фильтр.
- `onChange` - функция обратного вызова при изменении фильтра.

Ключевой test id:

- `status-filter`

## 22. Frontend: TypeScript-типы

### `frontend/src/types/auth.ts`

Назначение:

- описывает auth-типы frontend

Тип:

```typescript
UserRole = 'ADMIN' | 'VIEWER'
```

Интерфейс:

```typescript
LoginResponse
```

Поля:

- `token`
- `tokenType`
- `username`
- `role`

### `frontend/src/types/news.ts`

Назначение:

- описывает news-типы frontend

Тип:

```typescript
NewsStatus = 'DRAFT' | 'PUBLISHED' | 'ARCHIVED'
```

Тип:

```typescript
NewsStatusFilter = 'ALL' | NewsStatus
```

Интерфейс:

```typescript
NewsRequest
```

Поля:

- `title`
- `previewText`
- `fullText`
- `author`

Интерфейс:

```typescript
NewsResponse
```

Поля:

- `id`
- `title`
- `previewText`
- `fullText`
- `author`
- `status`
- `createdAt`
- `updatedAt`
- `publishedAt`
- `archivedAt`

Интерфейс:

```typescript
ErrorResponse
```

Поля:

- `timestamp`
- `status`
- `error`
- `message`
- `path`
- `validationErrors`

## 23. Frontend: стили

### `frontend/src/index.css`

Назначение:

- общий CSS приложения
- стилизует login page
- стилизует header
- стилизует формы
- стилизует карточки новостей
- стилизует status badges
- стилизует success/error/info messages
- задаёт responsive поведение для маленьких экранов

Ключевые визуальные элементы:

- `.app-shell` - основной layout.
- `.app-header` - верхняя панель.
- `.panel` - блок формы.
- `.news-form` - форма новости.
- `.news-list` - список новостей.
- `.news-card` - карточка новости.
- `.details` - блок деталей.
- `.message.success` - зелёное сообщение успеха.
- `.message.error` - красное сообщение ошибки.
- `.message.info` - информационное сообщение.

## 24. Проверка после запуска

### 24.1. Проверить frontend

Открыть:

```text
http://localhost:3000
```

Ожидаемо:

- отображается login form
- можно войти под `admin/admin123`
- можно войти под `viewer/viewer123`

### 24.2. Проверить Swagger

Открыть:

```text
http://localhost:8080/swagger-ui/index.html
```

Ожидаемо:

- открывается Swagger UI
- есть кнопка `Authorize`
- endpoint `/api/auth/login` доступен без token
- endpoints `/api/news/**` требуют Bearer token

### 24.3. Проверить login через curl

Команда:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

Ожидаемо:

- статус ответа `200`
- в теле ответа есть `token`
- `role` равен `ADMIN`

### 24.4. Проверить список новостей без token

Команда:

```bash
curl -i http://localhost:8080/api/news
```

Ожидаемо:

```text
401 Unauthorized
```

### 24.5. Проверить PostgreSQL users

Подключиться к базе можно любым PostgreSQL client со следующими параметрами:

```text
Host: localhost
Port: 5432
Database: qa_news_db
Username: qa_news_user
Password: qa_news_password
```

SQL:

```sql
SELECT id, username, role, enabled, created_at, updated_at
FROM users;
```

Ожидаемо:

- есть user `admin` с role `ADMIN`
- есть user `viewer` с role `VIEWER`
- password hash не выводится этим запросом

## 25. Частые проблемы

### `src refspec main does not match any`

Причина:

- локальная ветка называется не `main`
- или ещё нет ни одного commit

Решение:

```bash
git commit -m "Initial qa-news-service implementation"
git branch -M main
git push -u origin main
```

### Backend не подключается к PostgreSQL

Проверить:

```bash
docker compose ps
docker compose logs -f postgres
docker compose logs -f backend
```

В Docker backend должен использовать:

```text
jdbc:postgresql://postgres:5432/qa_news_db
```

Локально backend должен использовать:

```text
jdbc:postgresql://localhost:5432/qa_news_db
```

### Frontend показывает ошибку сети

Проверить:

- backend запущен на `http://localhost:8080`
- frontend собран с `VITE_API_BASE_URL=http://localhost:8080`
- CORS разрешает origin `http://localhost:3000`

### Login возвращает 401

Проверить:

- username/password введены правильно
- initial users создались в PostgreSQL
- пользователь `enabled = true`

Тестовые пользователи:

```text
admin / admin123
viewer / viewer123
```
