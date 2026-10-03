# CampusConnect — Tier 2

Clean full-stack prototype for a college management and placement portal.

## Current implementation

### Student
- JWT login
- Dashboard with live API data
- Profile editing
- Attendance records
- Internal marks
- Assignments with file submission and history
- Placement drives, eligibility and application tracking
- Academic performance analytics
- Timetable and subject details
- College notices

### Faculty
- Dashboard
- Mark attendance
- Record internal marks
- Publish assignments
- Review and grade student submissions

### Admin
- Dashboard
- User account management
- Create companies
- Create placement drives
- Review and update placement applications

## Stack
- Frontend: React + Vite
- Backend: Java 21 + Spring Boot 3.5
- Security: Spring Security + JWT
- ORM: Spring Data JPA / Hibernate
- Database: MySQL
- API docs: Springdoc/OpenAPI

## Run

### 1. Start MySQL

```bash
docker compose up -d
```

### 2. Configure demo account passwords

Set these environment variables before starting a fresh database:

```text
DEMO_ADMIN_PASSWORD
DEMO_FACULTY_PASSWORD
DEMO_STUDENT_PASSWORD
```

The deployed Railway environment uses these variables for demo account credentials. Do not commit passwords to the repository.

### 3. Start backend

From `backend`:

```powershell
mvn spring-boot:run
```

### 4. Start frontend

From `frontend`:

```powershell
npm install
npm run dev
```

Open `http://localhost:5173`.

Backend: `http://localhost:8080`

Swagger: `http://localhost:8080/swagger-ui.html`

## Demo accounts

The application uses these demo email addresses:

```text
Student
student@campusconnect.local

Faculty
faculty@campusconnect.local

Admin
admin@campusconnect.local
```

Passwords are intentionally kept out of source control and should be supplied through environment variables.

## Visual direction

The UI uses the approved warm off-white, deep green and orange visual direction with soft modular cards. The reference image is treated as visual inspiration only; CampusConnect uses its own layout and content.

## Verification note

The production deployment is hosted with Vercel for the frontend and Railway for the backend/database.
