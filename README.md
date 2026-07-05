# 🛡️ Araksha — Smart Disaster Relief Backend API

This repository contains the **Spring Boot REST API** backend application for the **Araksha** disaster relief system. It manages authentication, user management, scheduling, live mapping telemetry, P2P mutual aid, notifications, and emergency request dispatch.

---

## 🏗️ Layered Architecture & Directory Structure

The application strictly follows a standard layered architecture:
```
Controller (REST API) ──> Service Layer ──> Repository (Spring Data JPA) ──> Entity (MySQL/H2)
```

### Backend Directory Layout
```
org.example.arakshasmartdisasterreliefbackend/
├── config/                 # JWT & Spring Security Config, CORS, Data Seeds
├── controller/             # REST Controllers (Triage, Map, Weather, Auth)
├── dto/                    # Data Transfer Objects (Requests & Responses)
├── entity/                 # 22 Domain Database Entities
├── enums/                  # Role & Severity constants
├── exception/              # Centralized global exception handler
├── repository/             # Spring Data JPA Repository Interfaces
├── service/                # Business logic Interfaces
│   └── impl/               # Service Implementations (auth, maps, tracking)
└── specification/          # Advanced criteria Specifications for filtering
```

---

## 🗄️ Database Mappings (22 Core Entities)

The persistence layer consists of the following JPA entities:

| Entity Name | Description / Mapped Fields |
| :--- | :--- |
| **User** | Central credentials account containing `email`, encrypted `password`, and security `Role` |
| **Citizen** | Extended profile containing address details, contact numbers, and reporting history |
| **Volunteer** | Admin volunteer entity tracking skills, coordinates, and dispatcher ratings |
| **VolunteerHub** | Volunteer dashboard entity tracking session status, location updates, and codes |
| **EmergencyRequest**| Main disaster report tracking coordinates, citizen details, and assigned volunteer |
| **Incident** | Disaster cataloging detailing severity, type, and geographic boundaries |
| **MutualAidItem** | Community P2P postings mapping mutual help items (`OFFER` or `NEED`) with phone details |
| **Shelter** | Refugee shelter tracking capacity, occupied beds, and distance calculations |
| **Inventory** | Supplies registry tracking quantity, low stock thresholds, and units |
| **Allocation** | Dispatch audit log detailing volunteer or supply allocations |
| **Need** | Base master listing of relief resources (first aid, blankets, water) |
| **EmergencyNeed** | Relational link entity tracking quantities of needs requested per incident |
| **ScheduledReport**| Configuration registry for auto-generating system stats reports |
| **SchedulerJob** | Configuration registry for interactive cron triggers |
| **SchedulerLog** | Historical logs recording executions of background tasks |
| **Notification** | Global alert center mapping notification titles and read/unread status |
| **Performance** | Performance records charting weekly volunteer ratings |
| **Task** | Active tasks mapping locations and code statuses |
| **CompletedTask** | Historic metrics registry tracking completed tasks per volunteer |
| **UploadedFile** | File records tracking stored document and profile photo URLs |
| **Settings** | Global system configuration parameters |
| **EmergencyRequestVolunteers** | Relational mapping for emergency task volunteer allocations |

---

## 🔒 Security & Session Configurations

### 1. HttpOnly Cookie Authentication
- **Secure Token Cookies**: The `/api/auth/login` endpoint issues JWT tokens inside secure, `HttpOnly` cookie payloads to defend against XSS attacks.
- **Session Lifespan**: Auth cookies are configured with a 7-day expiration policy.
- **Logouts**: Logging out calls POST `/api/auth/logout` which clears cookies instantly.

### 2. Cookie CSRF Protection
- Enforced utilizing Spring Security's `CookieCsrfTokenRepository.withHttpOnlyFalse()` to authorize state-changing request actions (POST, PUT, DELETE).
- CORS headers are configured via property configurations (`cors.allowed-origins`) to whitelist credentialed requests (`withCredentials: true`) from the frontend application (e.g. S3 hosting environment `http://araksha-forntend-storage.s3-website.eu-north-1.amazonaws.com`).

### 3. Layered Web Security Settings
```java
http
    .cors(Customizer.withDefaults())
    .csrf(csrf -> csrf
        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        .ignoringRequestMatchers("/api/auth/**", "/api/external/**")
    )
    .authorizeHttpRequests(auth -> auth
        .requestMatchers("/api/**").permitAll()
        .anyRequest().authenticated()
    )
    .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
```

---

## ⚙️ Advanced System Modules (Beyond CRUD)

- **Advanced Criteria Specifications**: Implementations of JPA Specifications for complex filters on volunteers and reports.
- **File Upload Service**: Direct uploads to local storage with validation for size/extension.
- **Spring Scheduler**: Configured background tasks using dynamic cron updates persisted in the database.
- **Maps Routing & Weather API**: Coordinates distance calculation using the Haversine equation, routes calculation, and OpenWeatherMap telemetry.
- **Certified PDF Export**: Generates government-certified PDFs via jsPDF integration.

---

## 🚀 Installation & Local Run

### Prerequisites
- JDK 21
- Maven 3.x

### Execution
Run the Spring Boot development server:
```bash
./mvnw.cmd spring-boot:run
```
The API server will listen on **http://localhost:8080**.
