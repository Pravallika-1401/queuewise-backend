# QueueWise — Backend

Spring Boot REST API for QueueWise Smart Queue Management System.

## Tech Stack
- Java 17 + Spring Boot 3.2
- Spring Security + JWT Authentication
- Spring Data JPA + Hibernate
- MySQL Database
- OpenAI API (AI wait time estimation)

## Setup
```bash
# application.properties lo DB password set cheyyi
./mvnw spring-boot:run
```

## API Endpoints
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/auth/register | Register |
| POST | /api/auth/login | Login, get JWT |
| GET | /api/queues | All queues |
| POST | /api/queues/join/{id} | Get token |
| PUT | /api/admin/queues/{id}/next | Call next |

## Deployment
Deployed on **Render** — connected to Railway MySQL database.
