# java-explore-with-me-plus
Выбрана и реализована следующая функциональность: comments — комментарии к событиям

## Описание:

В приложение добавлена функциональность для зарегистрированных пользователей оставлять комментарии к событиям, запрос на
посещение которых был одобрен. У любого пользователя есть возможность просматривать комментарии, которые были оставлены 
к событию, а также комментарии пользователей, одобренные для публикации администратором. Все комментарии публикуются после 
проверки администратором. Администратор может как принять, так и отклонить публикацию комментария. Также у администратора
существует возможность просмотра всех комментариев с использованием различных фильтров.


### API спецификация

> ### Public

***

```
GET /events/{eventId}/comments
```

- Получение всех комментариев (со статусом APPROVED) к событию.

**Response**

`Code: 200`

```json
[
    {
        "id": 1,
        "text": "Aut consequatur reic",
        "eventId": 1,
        "authorId": 2,
        "createdDate": "2025-05-24 10:57:35",
        "updatedDate": "2025-05-24 10:57:35",
        "publishedDate": "2025-05-24 10:57:35",
        "status": "APPROVED"
    }
]
```

***

```
GET /events/{eventId}/comments/{commentId}
```

- Получение комментария по определенному идентификатору (со статусом APPROVED) к событию.

**Response**

`Code: 200`

```json
{
  "id": 3,
  "text": "Consequatur beatae i",
  "eventId": 2,
  "authorId": 5,
  "createdDate": "2025-05-24 10:58:38",
  "updatedDate": "2025-05-24 10:58:38",
  "publishedDate": "2025-05-24 10:58:38",
  "status": "APPROVED"
}
```

***

> ### Private

```
POST /users/{userId}/events/{eventId}/comments
```

- Создание комментария к событию. Оставить комментарий может лишь тот,
  у кого есть одобренная заявка на участие.

**Response**

`Code: 201`

```json
{
    "id": 4,
    "text": "Новый комментарий",
    "eventId": 3,
    "authorId": 7,
    "createdDate": "2025-05-24 11:00:57",
    "status": "PENDING"
}
```

***

```
GET /users/{userId}/comments
```

- Получение комментариев (со статусом APPROVED) конкретного пользователя.

**Response**

`Code: 200`

```json
[
  {
    "id": 5,
    "text": "Quas blanditiis sit ",
    "eventId": 4,
    "authorId": 9,
    "createdDate": "2025-05-24 11:02:20",
    "updatedDate": "2025-05-24 11:02:20",
    "publishedDate": "2025-05-24 11:02:20",
    "status": "APPROVED"
  }
]
```

***

> ### Admin

```
GET /admin/comments
```

Просмотр администатором комментариев.

- по списку id клмментариев
- по вхождению текста
- по списку id событий
- по списку id авторов
- по статусу опубликован или нет комментарий
- по дате создания
- по дате публикации
- доступна пагинация

**Response**

`Code: 200`

```json
[
  {
    "id": 5,
    "text": "Quas blanditiis sit ",
    "eventId": 4,
    "authorId": 9,
    "createdDate": "2025-05-24 11:02:20",
    "updatedDate": "2025-05-24 11:02:20",
    "publishedDate": "2025-05-24 11:02:20",
    "status": "APPROVED"
  }
]
```

***

```
GET /admin/comments/{commentId}
```

- Просмотр администратором информации по конкретному комментарию

**Response**

`Code: 200`

```json
{
  "id": 5,
  "text": "Quas blanditiis sit ",
  "eventId": 4,
  "authorId": 9,
  "createdDate": "2025-05-24 11:02:20",
  "updatedDate": "2025-05-24 11:02:20",
  "publishedDate": "2025-05-24 11:02:20",
  "status": "APPROVED"
}
```

***

```
PATCH /admin/comments/{commentId}
```

- Подтверждение администратором или отклонение модерации комментария

**Response**

`Code: 200`

```json
{
  "id": 5,
  "text": "Quas blanditiis sit ",
  "eventId": 4,
  "authorId": 9,
  "createdDate": "2025-05-24 11:02:20",
  "updatedDate": "2025-05-24 11:02:20",
  "publishedDate": "2025-05-24 11:02:20",
  "status": "APPROVED"
}
```

***

```
DELETE /admin/comments/{commentId}
```

- Удаление администратором комментария

**Response**

`Code: 204`

***