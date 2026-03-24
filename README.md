# Job Portal API

A clean and simple Job Portal REST API built with Java and Spring Boot.

This repository implements a backend service for a job portal with support for recruiters posting jobs, applicants viewing and applying for jobs, and user authentication (recruiter/employee). It uses Spring Data JPA for persistence, Spring Security + JWT for authentication, and Spring Mail for notification support. OpenAPI (Swagger) is included for API exploration.

---

## Table of contents

- [Features](#features)
- [Tech stack](#tech-stack)
- [Requirements](#requirements)
- [Configuration (environment variables)](#configuration-environment-variables)
- [Run locally](#run-locally)
- [Build artifact](#build-artifact)
- [Run tests](#run-tests)
- [API endpoints (overview & examples)](#api-endpoints-overview--examples)
- [Swagger / OpenAPI UI](#swagger--openapi-ui)
- [Database notes](#database-notes)
- [Logging & multipart limits](#logging--multipart-limits)
- [Contributing](#contributing)
- [License](#license)

---

## Features

- User registration and login (roles: recruiter, employee)
- Recruiter endpoints: post job, list posted jobs (paginated), close job, list applicants, download resume, change application status
- Applicant endpoints: view available jobs (paginated), apply for job, upload resume (multipart)
- JWT-based authentication and request filtering
- Email configuration prepared (SMTP)
- OpenAPI/Swagger UI for interactive API exploration

## Tech stack

- Java 21
- Spring Boot 4.x
- Spring Web MVC
- Spring Data JPA
- Spring Security + JWT (jjwt)
- Spring Mail
- H2 (runtime for tests) and MySQL (production/runtime)
- springdoc-openapi (Swagger UI)
- Lombok (compile-time)
- Maven (wrapper included)

## Requirements

- JDK 21
- Maven (you can use the included Maven wrapper)
- MySQL server or a compatible JDBC URL (unless you run with an in-memory DB for tests)

## Configuration (environment variables)

This project reads sensitive configuration from environment variables. The following variables are expected (names come from `src/main/resources/application.properties`):

- `JDBC_DATABASE_URL` - JDBC URL for the database (e.g. `jdbc:mysql://host:port/dbname`)
- `USERNAME` - database username
- `PASSWORD` - database password
- `JWT_SECRET` - secret key used to sign JWT tokens
- `EMAIL_ID` - SMTP username (for sending emails)
- `EMAIL_PASSWORD` - SMTP password / app password

Important: Do NOT commit your real secrets into the repo. The repository may contain a `src/secrets.env` on your local machine for convenience; avoid committing such files. Instead use a secure secret management approach in production (environment variables, CI/CD secrets, or vaults).

Example (Linux/macOS):

```bash
export JDBC_DATABASE_URL="jdbc:mysql://localhost:3306/jobportal"
export USERNAME="dbuser"
export PASSWORD="dbpassword"
export JWT_SECRET="a-very-secret-key"
export EMAIL_ID="you@example.com"
export EMAIL_PASSWORD="emailpassword"
```

Windows PowerShell example:

```powershell
$env:JDBC_DATABASE_URL = 'jdbc:mysql://localhost:3306/jobportal'
$env:USERNAME = 'dbuser'
$env:PASSWORD = 'dbpassword'
$env:JWT_SECRET = 'a-very-secret-key'
$env:EMAIL_ID = 'you@example.com'
$env:EMAIL_PASSWORD = 'emailpassword'
```

## Run locally

You can run the application using the included Maven wrapper.

Windows (PowerShell):

```powershell
.\mvnw.cmd spring-boot:run
```

Linux / macOS:

```bash
./mvnw spring-boot:run
```

By default the app runs on port 8080. If you need to change it, set `server.port` in `application.properties` or pass `-Dserver.port=...` to the JVM.

## Build artifact

Create an executable JAR:

Windows:

```powershell
.\mvnw.cmd -DskipTests package
```

Linux/macOS:

```bash
./mvnw -DskipTests package
```

Run the JAR:

```bash
java -jar target/job-portal-api-0.0.1-SNAPSHOT.jar
```

## Run tests

Run unit/integration tests with Maven:

Windows:

```powershell
.\mvnw.cmd test
```

Linux/macOS:

```bash
./mvnw test
```

## API endpoints (overview & examples)

Base host: `http://localhost:8080`

Notes: The API is organized around three controllers: `UserController` (`/auth`), `RecruiterController` (`/recruiter`) and `ApplicantController` (root paths like `/jobs`). Below are the main endpoints discovered in the codebase.

1) Authentication and user management (`/auth`)

- POST `/auth/recruit/register` - Register as a recruiter
  - Body: JSON (see DTOs in `src/main/java/com/dev/jobportal/model/dto`)

- POST `/auth/employee/register` - Register as an employee
  - Body: JSON

- POST `/auth/login` - Login and receive JWT
  - Body: JSON with `email` and `password`

Example (login):

```bash
curl -X POST "http://localhost:8080/auth/login" -H "Content-Type: application/json" -d '{"email":"user@example.com","password":"passw0rd"}'
```

2) Applicant endpoints

- GET `/jobs?pageNumber={n}&size={s}` - List available jobs (paginated)

- POST `/apply?jobId={id}` - Apply for a job (must be authenticated)

- POST `/upload-resume` (multipart) - Upload resume file (form part name `file`)

Example (upload resume):

```bash
curl -X POST "http://localhost:8080/upload-resume" -H "Authorization: Bearer <JWT>" -F "file=@/path/to/resume.pdf"
```

3) Recruiter endpoints (`/recruiter`)

- POST `/recruiter/post-job` - Post a new job (JSON body)

- GET `/recruiter/posted-jobs?pageNumber={n}&size={s}` - Get posts by the authenticated recruiter (paginated)

- GET `/recruiter/posted-jobs/{id}` - Get a posted job by id

- PUT `/recruiter/close-job/{id}` - Close hiring for a job

- GET `/recruiter/allApplicants?id={jobId}&jobTitle={title}&pageNumber={n}&size={s}` - Get applicants for a job (paginated)

- GET `/recruiter/{applicationId}/resume` - Download resume for an application (returns binary `byte[]`)

- PUT `/recruiter/application/{id}/status` - Update application status. The controller expects a request part `status` and path variable `id`.

Example (change application status):

```bash
curl -X PUT "http://localhost:8080/recruiter/application/123/status" -H "Authorization: Bearer <JWT>" -F "status=SHORTLISTED"
```

Authentication: Most recruiter/applicant actions will require a valid JWT in the `Authorization: Bearer <token>` header.

## Swagger / OpenAPI UI

The project includes `springdoc-openapi-starter-webmvc-ui`. Once the app is running, open the API docs UI at:

- http://localhost:8080/swagger-ui.html

or

- http://localhost:8080/swagger-ui/index.html

Depending on your springdoc version the redirect path may differ but the UI should be available at one of the above.

## Database notes

- `spring.jpa.hibernate.ddl-auto` is set to `validate` in `application.properties`. That means the database schema must already exist and match the JPA mappings. For development you can change this to `update` or `create-drop` if you understand the implications.

- The project includes `mysql-connector-j` as the runtime DB driver and `h2` for tests.

## Logging & multipart limits

- `spring.jpa.show-sql=true` is enabled to show SQL statements in logs during development.
- File upload limits are set in `application.properties` (1MB max file size and request size). Adjust `spring.servlet.multipart.*` if you need larger files.

## Contributing

- Feel free to open issues or PRs. Keep changes small and focused. Add unit tests for any new logic.

- If you want me to expand the README with example DTO schemas, sample Postman collection, or CI/CD instructions, tell me which you'd prefer and I will add them.

## License

This repo does not contain a license file. Add a `LICENSE` if you plan to publish this publicly.


---

If you'd like, I can also:
- Add a minimal Postman collection or example OpenAPI export.
- Add a `docker-compose.yml` to bring up a local MySQL instance for development.
- Provide example request/response DTOs by extracting them from the `model/dto` package.

Tell me which of these you'd like next.
