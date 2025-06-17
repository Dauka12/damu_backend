# Public User Courses API Documentation

## Overview
Новый открытый API endpoint для получения информации о курсах пользователя по его ИИН без необходимости аутентификации.

## Endpoint

### GET /api/public/{iin}

Получает список всех курсов пользователя с информацией о прогрессе по ИИН.

#### Parameters
- `iin` (path parameter) - ИИН пользователя (12 цифр)

#### Response Format

**Success Response (200 OK):**
```json
{
  "iin": "123456789012",
  "fullName": "Иванов Иван Иванович",
  "courses": [
    {
      "courseId": 1,
      "courseName": "Основы финансового мониторинга",
      "progressPercentage": 75.5,
      "status": "process",
      "paymentType": "free",
      "isCompleted": false
    },
    {
      "courseId": 2,
      "courseName": "Противодействие отмыванию денег",
      "progressPercentage": 100.0,
      "status": "finished",
      "paymentType": "paid",
      "isCompleted": true
    }
  ],
  "completedCourses": [
    {
      "courseId": 2,
      "courseName": "Противодействие отмыванию денег",
      "progressPercentage": 100.0,
      "status": "finished",
      "paymentType": "paid",
      "isCompleted": true
    }
  ],
  "totalCourses": 2,
  "completedCoursesCount": 1
}
```

**Error Responses:**

- `400 Bad Request` - Некорректный формат ИИН
```json
"Некорректный ИИН. ИИН должен содержать 12 цифр."
```

- `404 Not Found` - Пользователь с таким ИИН не найден

#### Example Usage

```bash
# Получить курсы пользователя по ИИН
curl -X GET "http://localhost:8000/api/public/123456789012"

# С заголовками
curl -X GET "http://localhost:8000/api/public/123456789012" \
     -H "Accept: application/json"
```

## Data Fields Explanation

### UserCoursesInfoDTO
- `iin` - ИИН пользователя
- `fullName` - Полное имя пользователя (Фамилия Имя Отчество)
- `courses` - Массив всех курсов пользователя
- `completedCourses` - Массив только завершенных курсов (прогресс = 100%)
- `totalCourses` - Общее количество курсов
- `completedCoursesCount` - Количество завершенных курсов

### UserCourseProgressDTO
- `courseId` - ID курса
- `courseName` - Название курса
- `progressPercentage` - Процент прохождения (0-100)
- `status` - Статус курса (process, finished, request, etc.)
- `paymentType` - Тип оплаты (free, paid, etc.)
- `isCompleted` - Флаг завершения курса (true если прогресс >= 100%)

## Security
- Endpoint открытый (не требует аутентификации)
- Доступен для всех источников (CORS enabled)
- Валидация формата ИИН (должен содержать 12 цифр)

## Implementation Files
- Controller: `PublicUserController.java`
- Service: `UserCoursesService.java`
- DTOs: `UserCoursesInfoDTO.java`, `UserCourseProgressDTO.java`
- Repository: Enhanced `UserCourseRepository.java`
- Security: Updated `SecurityConfig.java`
