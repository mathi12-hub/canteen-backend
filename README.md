# College Canteen Ordering System — Backend

Spring Boot REST API for a college canteen ordering system: menu browsing, order
placement, and live order-status tracking (`PENDING → PREPARING → READY → COMPLETED`).

**Stack:** Java 17, Spring Boot 3.3, Spring Data JPA (Hibernate), MySQL, Maven.

---

## 1. Project structure

```
canteen-backend/
├── pom.xml
└── src/main/java/com/canteen/canteenbackend/
    ├── CanteenBackendApplication.java
    ├── model/          Order, OrderItem, MenuItem, OrderStatus (JPA entities)
    ├── repository/     Spring Data JPA repositories
    ├── service/        MenuService, OrderService (business logic)
    ├── controller/      MenuController, OrderController, StaffController (REST)
    ├── dto/            Request/response objects
    ├── exception/       Custom exceptions + global @RestControllerAdvice
    └── config/          CORS config
└── src/main/resources/
    ├── application.properties
    └── data.sql          sample menu data, seeded on startup
```

## 2. Database design

- **menu_items** — sellable items (name, description, price, category, available, image_url)
- **orders** — one order per customer transaction (customer_name, customer_phone, status, total_amount, timestamps)
- **order_items** — join table modelling the **many-to-many relationship** between orders and menu items,
  with `quantity` and `price_at_order` captured per line (so later menu price changes don't rewrite order history)

```
menu_items (1) ──< order_items >── (1) orders
```

Tables are auto-created/updated by Hibernate (`spring.jpa.hibernate.ddl-auto=update`) — no manual schema
scripts needed for a fresh dev database.

## 3. Setup

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8.x running locally (or accessible remotely)

### 1. Create the database (optional — the app can auto-create it)
```sql
CREATE DATABASE canteen_db;
```

### 2. Configure connection
Either edit `src/main/resources/application.properties` directly, or set environment variables:
```bash
export DB_URL="jdbc:mysql://localhost:3306/canteen_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC"
export DB_USERNAME=root
export DB_PASSWORD=yourpassword
```

### 3. Run
```bash
cd canteen-backend
mvn spring-boot:run
```
The API starts on **http://localhost:8080**. Sample menu items are seeded automatically on first run.

### 4. Build a jar
```bash
mvn clean package
java -jar target/canteen-backend-1.0.0.jar
```

---

## 4. REST API reference

All endpoints return JSON. Base URL: `http://localhost:8080`

### Menu — `/api/menu`

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/menu` | List all **available** menu items |
| GET | `/api/menu?category=Snacks` | Filter by category |
| GET | `/api/menu?search=dosa` | Search by name |
| GET | `/api/menu?all=true` | List every item incl. unavailable (staff view) |
| GET | `/api/menu/{id}` | Get one item |
| POST | `/api/menu` | Create item |
| PUT | `/api/menu/{id}` | Update item |
| PATCH | `/api/menu/{id}/availability` | Toggle availability — body `{"available": false}` |
| DELETE | `/api/menu/{id}` | Delete item |

**Create/update body:**
```json
{
  "name": "Masala Dosa",
  "description": "Crispy rice crepe with spiced potato filling",
  "price": 60.00,
  "category": "Meals",
  "available": true,
  "imageUrl": null
}
```

### Orders — `/api/orders` (customer-facing)

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/orders` | Place a new order |
| GET | `/api/orders/{id}` | Get order + live status (poll this to track pending → preparing → ready) |
| GET | `/api/orders/customer/{phone}` | Order history for a customer |
| PATCH | `/api/orders/{id}/cancel` | Cancel an order |

**Place order body:**
```json
{
  "customerName": "Aditi Sharma",
  "customerPhone": "9876543210",
  "notes": "Less spicy please",
  "items": [
    { "menuItemId": 1, "quantity": 2 },
    { "menuItemId": 8, "quantity": 1 }
  ]
}
```

**Order response:**
```json
{
  "id": 15,
  "customerName": "Aditi Sharma",
  "customerPhone": "9876543210",
  "status": "PENDING",
  "totalAmount": 135.00,
  "notes": "Less spicy please",
  "createdAt": "2026-07-03T10:15:30",
  "updatedAt": "2026-07-03T10:15:30",
  "items": [
    { "menuItemId": 1, "name": "Masala Dosa", "quantity": 2, "priceAtOrder": 60.00, "lineTotal": 120.00 },
    { "menuItemId": 8, "name": "Masala Chai", "quantity": 1, "priceAtOrder": 15.00, "lineTotal": 15.00 }
  ]
}
```

### Staff — `/api/staff/orders` (kitchen/fulfilment)

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/staff/orders` | All orders, newest first |
| GET | `/api/staff/orders?status=PENDING` | Orders filtered by status, oldest first (FIFO queue for the kitchen) |
| PUT | `/api/staff/orders/{id}/status` | Advance order status — body `{"status": "PREPARING"}` |

Valid status transitions are enforced server-side:
```
PENDING → PREPARING → READY → COMPLETED
   ↓            ↓         ↓
CANCELLED   CANCELLED  CANCELLED
```

---

## 5. Notes for the frontend (HTML/CSS/JS)

- Enable CORS is already configured for all origins on `/api/**` (see `CorsConfig`) so a plain
  `fetch()`-based frontend served from a different port (e.g. Live Server on `:5500`) works out of the box.
- For "live" status tracking without websockets, have the frontend poll `GET /api/orders/{id}` every
  few seconds after an order is placed and update the UI based on the `status` field.
- Validation errors return `400` with a `fieldErrors` map; not-found resources return `404`; invalid
  status transitions or unavailable items return `400` — all in a consistent JSON error shape via
  `GlobalExceptionHandler`.

## 6. Production hardening (not included, by design, to keep this focused)

- Add Spring Security with role-based auth (STUDENT vs STAFF) on `/api/staff/**`
- Switch `ddl-auto` to `validate` and manage schema via Flyway/Liquibase migrations
- Add pagination to list endpoints
- Add WebSocket/SSE push for real-time status updates instead of polling
