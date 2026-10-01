# CampusConnect — Tier 1

Clean full-stack prototype for a college management and placement portal.

## Current implementation

### Student
- JWT login
- Dashboard with live API data
- Profile editing
- Attendance records
- Internal marks
- Assignments with prototype submission flow
- Submission history
- Placement drives and eligibility check
- Placement application tracking
- College notices

### Faculty
- Dashboard
- Mark attendance
- Record internal marks
- Publish assignments

### Admin
- Dashboard
- Create companies
- Create placement drives
- Review placement applications

## Stack
- Frontend: React + Vite
- Backend: Java 21 + Spring Boot 3.5
- Security: Spring Security + JWT
- ORM: Spring Data JPA / Hibernate
- Database: MySQL 8.4
- API docs: Springdoc/OpenAPI

## Run

### 1. Start MySQL

```bash
docker compose up -d
```

### 2. Start backend

From `backend`:

```powershell
mvn spring-boot:run
```

or, if Maven is available on Windows:

```powershell
mvn spring-boot:run
```

### 3. Start frontend

From `frontend`:

```powershell
npm install
npm run dev
```

Open `http://localhost:5173`.

Backend: `http://localhost:8080`
Swagger: `http://localhost:8080/swagger-ui.html`

## Demo accounts

```text
Student
student@campusconnect.local
Student@123

Faculty
faculty@campusconnect.local
Faculty@123

Admin
admin@campusconnect.local
Admin@123
```

These are development credentials only.

## Visual direction

The UI uses the approved warm off-white, deep green and orange visual direction with soft modular cards. The reference image is treated as visual inspiration only; CampusConnect uses its own layout and content.

## Verification note

The source has been updated as a complete Tier 1 implementation pass. Dependency installation/build could not be completed in the build environment because the package download timed out, so runtime verification should be performed locally after `npm install` and Maven dependencies are available.
