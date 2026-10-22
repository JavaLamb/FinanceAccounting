# Finance Accounting API

REST API для управления личными финансами: учёт счетов, категорий, доходов и расходов, а также переводы между счетами.

Проект разработан на Java с использованием Spring Boot. Основной фокус — бизнес-логика финансовых операций, разграничение доступа к данным, транзакционность и автоматизированное тестирование.

## Features

- **Authentication & Authorization** — регистрация, аутентификация и управление пользовательской сессией с помощью Spring Security.
- **Secure Password Storage** — хеширование паролей с использованием BCrypt.
- **Account Management** — создание, просмотр и деактивация счетов с ограничением количества активных счетов.
- **Transaction Management** — создание доходов и расходов, переводы между счетами, изменение и удаление транзакций.
- **Category Management** — создание и просмотр пользовательских категорий.
- **Access Control** — проверка владения ресурсами и прав пользователя на выполнение операций.
- **Input Validation** — валидация входных данных с Jakarta Bean Validation.
- **Centralized Error Handling** — единый формат ошибок и централизованная обработка исключений.
- **Database Migrations** — управление схемой PostgreSQL с помощью Liquibase.
- **Automated Testing** — unit-тесты сервисного слоя и MVC slice-тесты контроллеров.

## Tech Stack

| Technology | Purpose |
|---|---|
| Java 25 | Основной язык |
| Spring Boot 4.1 | Конфигурация приложения |
| Spring Web | REST API |
| Spring Security | Аутентификация и авторизация |
| Spring Data JPA / Hibernate | ORM и доступ к данным |
| PostgreSQL | Реляционная база данных |
| Liquibase | Миграции базы данных |
| Jakarta Bean Validation | Валидация запросов |
| Maven | Сборка проекта |
| JUnit 5 / Mockito | Unit-тестирование |
| Spring MVC Test | Тестирование HTTP-слоя |
| Lombok | Сокращение шаблонного кода |

## Architecture

Приложение разделено на несколько слоёв, каждый из которых отвечает за свою область ответственности.

- `controllers` — обработка HTTP-запросов и формирование HTTP-ответов.
- `dto` — объекты запросов и ответов API.
- `service` — бизнес-логика и управление транзакциями.
- `repositories` — взаимодействие с базой данных через Spring Data JPA.
- `entities` — сущности предметной области и их связи.
- `converters` — преобразование сущностей в DTO ответов.
- `security` — конфигурация Spring Security и загрузка пользовательских данных.
- `exceptions` — бизнес-исключения и централизованная обработка ошибок.

Бизнес-операции вынесены в сервисный слой, а HTTP-контракты отделены от JPA-сущностей с помощью DTO.

Для операций изменения данных используется `@Transactional`. Деактивация счёта реализована через soft delete: запись сохраняется в базе данных, а история связанных транзакций остаётся доступной на уровне хранения.

## API

Все бизнес-эндпоинты требуют аутентификации.

### Authentication

| Method | Endpoint | Description |
|---|---|---|
| POST | `/registration` | Регистрация пользователя |
| POST | `/login` | Аутентификация |
| POST | `/logout` | Завершение сессии |

### Accounts

| Method | Endpoint | Description |
|---|---|---|
| GET | `/accounts` | Получить активные счета пользователя |
| POST | `/accounts` | Создать счёт |
| GET | `/accounts/{id}` | Получить счёт по ID |
| DELETE | `/accounts/{id}` | Деактивировать счёт |

Поддерживаемые типы счетов: `DEBIT`, `CREDIT`, `SAVINGS`.

Для одного пользователя предусмотрено не более пяти активных счетов.

Пример создания счёта:

```http
POST /accounts
Content-Type: application/json
```

```json
{
  "name": "Everyday account",
  "accountType": "DEBIT"
}
```

Успешное создание возвращает `201 Created` и заголовок `Location` со ссылкой на созданный ресурс.

### Categories

| Method | Endpoint | Description |
|---|---|---|
| GET | `/categories` | Получить категории пользователя |
| POST | `/categories` | Создать категорию |

Количество категорий ограничено пятью на пользователя.

### Transactions

| Method | Endpoint | Description |
|---|---|---|
| GET | `/transactions/accounts/{accountId}` | Получить транзакции счёта |
| POST | `/transactions/accounts/{accountId}` | Создать доход или расход |
| POST | `/transactions/accounts/{accountId}/transfer` | Перевести деньги на другой счёт |
| GET | `/transactions/{transactionId}` | Получить транзакцию |
| PATCH | `/transactions/{transactionId}` | Изменить сумму транзакции |
| DELETE | `/transactions/{transactionId}` | Удалить транзакцию |

Пример создания расхода:

```http
POST /transactions/accounts/1
Content-Type: application/json
```

```json
{
  "transactionType": "EXPENSE",
  "categoryId": 1,
  "amount": 24.90
}
```

Поддерживаемые типы обычных операций: `INCOME` и `EXPENSE`.

Пример перевода между счетами:

```http
POST /transactions/accounts/1/transfer
Content-Type: application/json
```

```json
{
  "toAccountId": 2,
  "categoryId": 1,
  "amount": 50.00
}
```

Сервисный слой проверяет существование ресурсов, права доступа, корректность суммы и возможность выполнения финансовой операции.

## Security

Для защиты API используется Spring Security.

- Пароли хранятся в виде BCrypt-хешей.
- Аутентификация выполняется через `AuthenticationManager`.
- Пользовательские данные загружаются через `UserDetailsService`.
- Аутентифицированный пользователь представлен собственным `CustomUserDetails`.
- Контекст аутентификации сохраняется в HTTP-сессии.
- Доступ к бизнес-эндпоинтам разрешён пользователям с ролями `USER` или `ADMIN`.
- Сервисный слой проверяет принадлежность ресурсов пользователю.

Для последующих запросов после входа клиент должен передавать session cookie.

## Error Handling

Ошибки API обрабатываются централизованно через `@RestControllerAdvice`.

Ответ содержит информацию об ошибке, HTTP-статус, время возникновения, путь запроса и, при необходимости, подробности валидации.

Основные HTTP-статусы:

| Status | Meaning |
|---|---|
| `400 Bad Request` | Ошибка валидации или бизнес-ограничения |
| `401 Unauthorized` | Пользователь не аутентифицирован или неверны учётные данные |
| `403 Forbidden` | Недостаточно прав |
| `404 Not Found` | Ресурс не найден |
| `500 Internal Server Error` | Непредвиденная ошибка |

## Testing

Тестирование разделено по уровням.

**Service unit tests**
- Проверка бизнес-правил.
- Проверка позитивных и негативных сценариев.
- Проверка обработки некорректных данных и отсутствующих ресурсов.
- Изоляция сервисов от базы данных с помощью Mockito.

**Controller MVC slice tests**
- Проверка HTTP-статусов и содержимого JSON.
- Проверка формирования ответов контроллеров.
- Проверка поведения при ошибках, возникающих в сервисном слое.
- Использование Spring MVC Test и тестовых пользовательских данных.

Запуск тестов:

```bash
mvn test
```

## Getting Started

### Requirements

- JDK 25
- Maven
- PostgreSQL

### Database Configuration

Настройки подключения находятся в `src/main/resources/application.yaml`.

Перед запуском необходимо убедиться, что PostgreSQL работает и параметры подключения соответствуют локальной среде.

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/postgres
    username: postgres
    password: password
  liquibase:
    change-log: classpath:db.changelog-master.yaml
  jpa:
    hibernate:
      ddl-auto: validate
```

Указанные учётные данные приведены как текущий пример конфигурации проекта. Для реального использования их необходимо заменить и не хранить секреты в репозитории.

Liquibase применяет миграции при запуске. Hibernate использует `ddl-auto: validate` для проверки соответствия схемы базы данных JPA-сущностям.

### Run Application

Из корневой директории проекта:

```bash
./mvnw spring-boot:run
```

### Run Tests

```bash
./mvnw test
```

Проект использует **JaCoCo** для анализа покрытия кода тестами.

## Покрытие тестами (JaCoCo)

| Пакет | Покрытие (Instructions) | Покрытие (Branches) |
| :--- | :---: | :---: |
| **Total (Всего)** | **90%** | **89%** |
| `main.controllers` | 100% | n/a |
| `main.service` | 99% | 100% |
| `main.exceptions` | 91% | 75% |
| `main.entities` | 84% | 69% |
| `main.security` | 74% | n/a |
| `main.converters` | 14% | 0% |



## Possible Improvements/Work in progress

- Вынести конфигурацию подключения к базе данных в переменные окружения.
- Добавить Docker Compose для воспроизводимого локального окружения.
- Подключить OpenAPI/Swagger.
- Добавить интеграционные тесты с PostgreSQL и Testcontainers.
- Реализовать пагинацию истории транзакций.
- Добавить аудит финансовых операций.

---

**Project:** Finance Accounting API  
**Type:** Java Backend Pet Project
