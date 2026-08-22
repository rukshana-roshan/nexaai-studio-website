# TravelMate – Smart Tourist Day-Visit Planner
**University of Moratuwa | ITE2953 – Project**  
*Prepared by: Rukshana Roshan (Registration No: E2410132) &bull; Academic Year 2026*

---

## 📖 Overview
**TravelMate** is a responsive web-based tourist information and day-visit planning platform designed to centralize information about tourist attractions within ~25 km of **Atulugama, Kalutara District, Western Province, Sri Lanka**. 

The system solves the problem of dispersed travel information by allowing tourists to explore attractions, search and filter by interest, view interactive map locations, and automatically generate a personalized **One-Day Itinerary** with travel estimations and time-budget validation.

---

## 🌟 Key Features & SRS Traceability

| Feature Code | System Capability | Description |
| :--- | :--- | :--- |
| **FR-001 - FR-010** | **Tourist Attraction Management** | Complete catalog of 10+ attractions around Atulugama with images, categories, distances, and visiting durations. |
| **FR-011 - FR-016** | **Search & Category Filtering** | Live keyword search and filtering across 7 distinct categories with empty-state handling. |
| **FR-017 - FR-025** | **Attraction Details** | Modal & detailed cards displaying descriptions, best visiting times, entrance fees, and Google Maps direction links. |
| **FR-026 - FR-034** | **Smart One-Day Itinerary Planner** | Time-budget configuration, travel time estimation, nearest-neighbor sequence optimization, step-by-step day timeline, and time-overrun warnings. |
| **FR-035 - FR-038** | **Interactive Map & Navigation** | Leaflet & Google Maps integration featuring a 25 km radius boundary circle around Atulugama and custom category pins. |
| **FR-039 - FR-046** | **Administrator Portal (CRUD)** | Secure interface for viewing, adding, modifying, and deleting tourist attraction records with input validation and confirmation dialogs. |
| **FR-047 - FR-052** | **Responsive Web UI** | Modern Bootstrap 5 layout optimized across desktop, tablet, and mobile browsers. |
| **NFR-006 - NFR-009** | **Safety & Disclaimers** | Prominent disclaimers regarding approximate travel conditions and safety guidance. |

---

## 🏛️ System Architecture & Technology Stack

```
   ┌────────────────────────────────────────────────────────┐
   │            Responsive Frontend (Client Tier)           │
   │      HTML5 / CSS3 / JavaScript (ES6+) / Bootstrap 5    │
   │      Leaflet Map & Google Maps Direction Integration   │
   └───────────────────────────┬────────────────────────────┘
                               │ HTTP / REST APIs (JSON)
                               ▼
   ┌────────────────────────────────────────────────────────┐
   │         Spring Boot Backend (Application Tier)         │
   │  Controllers • Services • Itinerary Optimizer • JPA    │
   └───────────────────────────┬────────────────────────────┘
                               │ JDBC / Hibernate
                               ▼
   ┌────────────────────────────────────────────────────────┐
   │               Database (Persistence Tier)              │
   │          MySQL 8.0 / In-Memory H2 (Zero Config)        │
   └────────────────────────────────────────────────────────┘
```

- **Frontend**: HTML5, CSS3, ES6 JavaScript, Bootstrap 5.3, Bootstrap Icons, Leaflet.js
- **Backend**: Java 17+ / Spring Boot 3.3 (Spring Web, Spring Data JPA, Spring Validation)
- **Database**: MySQL (Production profile `application-mysql.properties`) / H2 In-Memory (Default zero-config profile)
- **Testing**: JUnit 5, Mockito, Spring Boot Test / MockMvc

---

## 🚀 How to Run the Application

### Option 1: Direct Run (H2 In-Memory, No setup needed)
1. Open terminal inside the project directory:
   ```bash
   cd C:\Users\pc\.gemini\antigravity\scratch\travelmate
   ```
2. Build and run using Maven:
   ```bash
   mvn spring-boot:run
   ```
3. Open your browser:
   - **Tourist Portal & Planner**: [http://localhost:8080](http://localhost:8080)
   - **Administrator Portal**: [http://localhost:8080/admin.html](http://localhost:8080/admin.html)
   - **H2 Database Console**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console) *(JDBC URL: `jdbc:h2:mem:travelmatedb`)*

---

### Option 2: Running with MySQL (Production SRS Profile)
1. Start MySQL database (or run `docker compose up -d`).
2. Run Spring Boot with the MySQL profile:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=mysql
   ```

---

### Option 3: Immediate Frontend Preview
You can also open `src/main/resources/static/index.html` directly in any modern web browser. The built-in client-side engine and local dataset will seamlessly handle searching, filtering, mapping, and itinerary generation even before the backend is booted!

---

## 🔌 REST API Endpoints

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/attractions` | List all attractions (supports `?category=...&query=...`) |
| `GET` | `/api/attractions/{id}` | Get attraction by ID |
| `GET` | `/api/attractions/categories` | Get all available attraction categories |
| `POST` | `/api/attractions` | Create a new attraction record (with validation) |
| `PUT` | `/api/attractions/{id}` | Update existing attraction record |
| `DELETE` | `/api/attractions/{id}` | Delete an attraction record |
| `POST` | `/api/attractions/reset` | Restore initial 10 Atulugama tourist attractions |
| `POST` | `/api/itinerary/plan` | Calculate travel sequence, schedule timeline, and time budget |

---

## 📍 Included Seed Tourist Attractions (Within 25km of Atulugama)

1. **Pearl Bay, Bandaragama** – Recreation (~5.0 km, 3.0h)
2. **Sri Lanka Karting Circuit, Bandaragama** – Adventure (~4.0 km, 2.0h)
3. **Bolgoda Lake** – Nature / Scenic (~6.5 km, 2.0h)
4. **Wadduwa Beach** – Beach (~14.0 km, 2.0h)
5. **Pothupitiya Beach** – Beach (~15.0 km, 1.5h)
6. **Kalutara Bodhiya** – Religious / Cultural (~19.0 km, 1.5h)
7. **Gangatilaka Viharaya** – Religious / Cultural (~19.2 km, 1.5h)
8. **Richmond Castle** – Historical (~20.5 km, 2.0h)
9. **Calido Beach** – Beach / Nature (~21.0 km, 1.5h)
10. **Thudugala Ella** – Nature / Waterfall (~22.5 km, 2.5h)
