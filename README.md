# QA News Service

Учебный fullstack pet-project для GitHub-портфолио QA Automation / SDET.

Проект представляет собой простой сервис новостей, на котором в следующих этапах удобно писать автотесты:

- API-тесты
- UI-тесты
- contract-тесты
- WireMock-тесты
- Kafka-тесты
- Testcontainers-тесты

В репозитории есть backend, frontend, API/UI автотесты и Kafka integration tests.

## Allure Reports в GitHub Pages

Отчёты публикует workflow [Test suites and Allure Pages](.github/workflows/allure-pages.yml):

- [Главная страница отчётов](https://Olegon74.github.io/qa-news-service/)
- [API tests](https://Olegon74.github.io/qa-news-service/api/)
- [UI tests](https://Olegon74.github.io/qa-news-service/ui/)
- [Kafka tests](https://Olegon74.github.io/qa-news-service/kafka/)

Workflow запускает три независимые jobs на `main` и для pull requests. После тестов `allure-report` собирает доступные результаты и публикует HTML только с `main`; для pull requests доступны только artifacts. В Actions → **Test suites and Allure Pages** → **Run workflow** можно выбрать `all`, `api`, `ui` или `kafka`. При выборе одной группы отчёты остальных групп сохраняются. Отчёты появятся по ссылкам после первого успешного запуска публикации.

Перед первым запуском в GitHub откройте **Settings → Pages → Build and deployment → Source** и выберите **GitHub Actions**. В **Settings → Actions → General → Workflow permissions** разрешите **Read and write permissions**, чтобы workflow мог сохранять историю в ветке `allure-pages-state`. Для job `github-pages` не задавайте правило, которое блокирует публикацию с `main`. Убедитесь, что GitHub Pages разрешён для репозитория.

Каждая test job загружает `allure-results` и Maven XML reports даже при падении тестов. API запросы/ответы (с маскировкой токенов и паролей), UI скриншоты и Kafka JSON-события хранятся как Allure attachments. Для Allure 2 предыдущая история API, UI и Kafka хранится отдельно в служебной ветке `allure-pages-state`; при следующей генерации она добавляется к результатам соответствующей группы. Если тесты упали, их job остаётся красной, но доступные результаты всё равно публикуются. Если тесты не создали ни одного Allure result, прежний отчёт сохраняется.

Локальные команды для тех же Maven-профилей:

```bash
cd backend
mvn -B test -Pparallel-api -Dtag=API -Denv=dev
mvn -B test -Pparallel-ui -Dtag=UI -Denv=dev
mvn -B verify -Pkafka-it
```

API/UI команды требуют запущенные сервисы Docker Compose и параметры подключения, приведённые в workflow. Kafka integration tests поднимают Kafka и PostgreSQL через Testcontainers; для них нужен только Docker daemon.

## Технологический стек

Backend:

- Java 21
- Spring Boot 3
- Maven
- Spring Web
- Spring Data JPA
- Hibernate
- PostgreSQL
- Jakarta Validation
- Lombok
- Spring Security
- JWT
- BCryptPasswordEncoder
- Springdoc OpenAPI / Swagger UI

Frontend:

- React
- Vite
- TypeScript
- Axios
- CSS без UI-библиотек

Инфраструктура:

- Docker Compose
- PostgreSQL 16
- Selenoid / Selenoid UI для remote UI-тестов
- GitHub Actions ручной запуск UI-тестов по выбранному браузеру, версии и разрешению

## Архитектура проекта

```text
qa-news-service/
+-- backend/
+-- frontend/
+-- docker-compose.yml
+-- README.md
+-- CODEX_MEMORY.md
`-- .gitignore
```

Корневой пакет backend:

```text
com.example.qanewsservice
```

Основные слои backend:

- `controller` - REST endpoints
- `service` - бизнес-логика
- `repository` - Spring Data JPA repositories
- `model` - JPA entities и enums
- `dto` - request/response DTO
- `mapper` - преобразование entity в DTO
- `exception` - обработка ошибок API
- `auth` - login, users, JWT
- `config` - security, CORS и OpenAPI

Основные модули frontend:

- `api` - запросы к backend через Axios
- `auth` - работа с `localStorage`
- `components` - UI-компоненты
- `types` - TypeScript-типы

## Backend endpoints

Base URL:

```text
http://localhost:8080
```

News API:

```text
POST   /api/news
GET    /api/news/{id}
GET    /api/news
PUT    /api/news/{id}
POST   /api/news/{id}/publish
POST   /api/news/{id}/archive
DELETE /api/news/{id}
```

Authentication API:

```text
POST /api/auth/login
```

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

## Аутентификация

Пользователи хранятся в PostgreSQL в таблице `users`.

Начальные тестовые пользователи создаются автоматически при старте приложения, если их ещё нет в базе данных.

Пароли хранятся только в виде BCrypt-хешей. Plain text passwords в базе данных не сохраняются.

Для авторизации API используется JWT token.

Тестовые пользователи:

```text
admin / admin123 / ADMIN
viewer / viewer123 / VIEWER
```

Authorization header:

```text
Authorization: Bearer <token>
```

SQL для просмотра пользователей без вывода password hash:

```sql
SELECT id, username, role, enabled, created_at, updated_at
FROM users;
```

## Роли и права доступа

`ADMIN` имеет полный доступ к новостям:

- создание
- просмотр списка
- просмотр одной новости
- редактирование
- публикация
- архивация
- удаление

`VIEWER` имеет доступ только на чтение:

- просмотр списка новостей
- просмотр одной новости

Разрешённые действия:

```text
ADMIN:
POST   /api/news
GET    /api/news
GET    /api/news/{id}
PUT    /api/news/{id}
POST   /api/news/{id}/publish
POST   /api/news/{id}/archive
DELETE /api/news/{id}

VIEWER:
GET    /api/news
GET    /api/news/{id}
```

Если JWT отсутствует или некорректен, API возвращает:

```text
401 Unauthorized
```

Если JWT валиден, но роли недостаточно, API возвращает:

```text
403 Forbidden
```

## Бизнес-правила новостей

Статусы новости:

```text
DRAFT
PUBLISHED
ARCHIVED
```

Правила:

- новая новость всегда создаётся в статусе `DRAFT`
- опубликовать можно только новость в статусе `DRAFT`
- после публикации статус становится `PUBLISHED`
- при публикации заполняется `publishedAt`
- архивировать можно только новость в статусе `PUBLISHED`
- после архивации статус становится `ARCHIVED`
- при архивации заполняется `archivedAt`
- редактировать можно новости в статусах `DRAFT` и `PUBLISHED`
- новость в статусе `ARCHIVED` редактировать нельзя
- удалить можно новости в статусах `DRAFT` и `ARCHIVED`
- новость в статусе `PUBLISHED` удалить нельзя
- некорректный переход статуса возвращает `409 Conflict`
- получение несуществующей новости возвращает `404 Not Found`
- невалидный request body возвращает `400 Bad Request`
- некорректный query parameter `status` возвращает `400 Bad Request`

## Формат ошибок

Ошибки backend возвращаются в едином формате:

```json
{
  "timestamp": "2026-06-26T12:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/news",
  "validationErrors": {
    "title": "must not be blank"
  }
}
```

Поле `validationErrors` используется для ошибок валидации request body.

## Запуск через Docker Compose

Из корня проекта:

```bash
docker compose up --build
```

Будут подняты сервисы:

```text
PostgreSQL: localhost:5432
Backend:    http://localhost:8080
Frontend:   http://localhost:3000
```

Остановить сервисы:

```bash
docker compose down
```

Остановить сервисы и удалить PostgreSQL volume:

```bash
docker compose down -v
```

## Локальный запуск backend

Сначала запустить PostgreSQL:

```bash
docker compose up postgres
```

Затем запустить backend:

```bash
cd backend
mvn spring-boot:run
```

Локальные настройки подключения к PostgreSQL:

```text
Database: qa_news_db
Username: qa_news_user
Password: qa_news_password
Host:     localhost
Port:     5432
```

Backend будет доступен по адресу:

```text
http://localhost:8080
```

## Локальный запуск frontend

При необходимости создать `.env` на основе примера:

```bash
cd frontend
cp .env.example .env
```

Установить зависимости и запустить Vite:

```bash
npm install
npm run dev
```

Frontend будет доступен по адресу:

```text
http://localhost:3000
```

## PostgreSQL

Параметры подключения:

```text
Host:     localhost
Port:     5432
Database: qa_news_db
Username: qa_news_user
Password: qa_news_password
```

Ожидаемые таблицы:

```text
users
news
```

Таблицы создаются и обновляются Hibernate автоматически:

```yaml
spring.jpa.hibernate.ddl-auto: update
```

Flyway и Liquibase пока не используются.

## Swagger и OpenAPI

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

В Swagger настроена Bearer Authentication схема:

```text
bearerAuth
type: http
scheme: bearer
bearerFormat: JWT
```

Endpoint `/api/auth/login` не требует token.

Endpoints `/api/news/**` требуют JWT token.

## Frontend

Frontend реализован как простое одностраничное приложение без роутинга.

Основные возможности:

- login под `ADMIN`
- login под `VIEWER`
- logout
- сохранение token, username и role в `localStorage`
- автоматическая отправка `Authorization: Bearer <token>` во все news API requests
- создание новости под `ADMIN`
- просмотр списка новостей под `ADMIN` и `VIEWER`
- фильтрация по статусу
- просмотр деталей новости
- редактирование `DRAFT` и `PUBLISHED` новостей под `ADMIN`
- публикация `DRAFT` новости под `ADMIN`
- архивация `PUBLISHED` новости под `ADMIN`
- удаление `DRAFT` или `ARCHIVED` новости под `ADMIN`
- отображение validation errors
- отображение `Forbidden` при `403`
- очистка auth data и возврат на login при `401`

Role-based UI:

- `ADMIN` видит create, edit, publish, archive, delete и view actions
- `VIEWER` видит только view action
- `VIEWER` не видит форму создания и редактирования
- edit button виден только для `DRAFT` и `PUBLISHED`
- archived news нельзя редактировать

## Стабильные data-testid для UI-тестов

Auth:

```text
login-form
username-input
password-input
login-button
logout-button
current-user
current-role
auth-error-message
viewer-readonly-message
```

News form:

```text
news-form
title-input
preview-text-input
full-text-input
author-input
create-news-button
update-news-button
cancel-edit-button
```

Filter/list/details:

```text
status-filter
news-list
selected-news-details
success-message
error-message
```

Dynamic news elements:

```text
news-card-{id}
news-title-{id}
news-status-{id}
view-news-button-{id}
edit-news-button-{id}
publish-news-button-{id}
archive-news-button-{id}
delete-news-button-{id}
```

Где `{id}` - id новости.

## Примеры API-запросов

Login as ADMIN:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

Login as VIEWER:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"viewer","password":"viewer123"}'
```

Create news with token:

```bash
curl -X POST http://localhost:8080/api/news \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "title": "Test news",
    "previewText": "Short preview",
    "fullText": "Full text",
    "author": "Oleg"
  }'
```

Get news list with token:

```bash
curl -X GET http://localhost:8080/api/news \
  -H "Authorization: Bearer <token>"
```

Get news list by status:

```bash
curl -X GET "http://localhost:8080/api/news?status=DRAFT" \
  -H "Authorization: Bearer <token>"
```

Publish news:

```bash
curl -X POST http://localhost:8080/api/news/1/publish \
  -H "Authorization: Bearer <token>"
```

Archive news:

```bash
curl -X POST http://localhost:8080/api/news/1/archive \
  -H "Authorization: Bearer <token>"
```

Delete news:

```bash
curl -X DELETE http://localhost:8080/api/news/1 \
  -H "Authorization: Bearer <token>"
```

## Проверка сборки

Backend:

```bash
cd backend
mvn -DskipTests package
```

Frontend:

```bash
cd frontend
npm run build
```

Docker Compose config:

```bash
docker compose config
```

## UI-тесты через Selenoid

Локальный запуск поднимет приложение и Selenoid-совместимый hub, затем выполнит UI-тесты в Chrome по умолчанию:

```powershell
.\scripts\run-ui-selenoid.ps1
```

Браузер и одну из двух предыдущих версий можно выбрать параметрами:

```powershell
.\scripts\run-ui-selenoid.ps1 -Browser firefox -BrowserVersion before_previous
.\scripts\run-ui-selenoid.ps1 -Browser opera -Tag SMOKE
```

Selenoid UI будет доступен на:

```text
http://localhost:8081
```

Ручной запуск в GitHub Actions находится в workflow `Manual UI tests in Selenoid`.
Доступны браузеры `chrome`, `firefox`, `opera`, `edge`; версии `previous`, `before_previous`; разрешения `1920x1080`, `1366x768`, `1280x720`. Safari требует macOS и `safaridriver`.
Подробности: [docs/selenoid-github-actions-guide.md](docs/selenoid-github-actions-guide.md).
