# FreshCart

Grocery-delivery backend in the style of Picnic.

Customers browse products, book a delivery slot and place an order. FreshCart makes sure a slot is never overbooked and stock never goes wrong, even when many people order at the same moment. Customers can cancel until the van leaves.

> Learning project, built week by week. The parts marked *planned* are not built yet.

## What it does

1. A customer registers and logs in, and gets a signed JWT token.
2. The customer picks products and a delivery slot and places an order.
3. The slot row is locked while existing orders are counted, so two orders can never take the last place at the same time.
4. Stock is lowered for every item. If another order changed the same product first, the order fails instead of selling stock that isn't there.
5. The whole order is one transaction: if any item fails, nothing is saved.
6. The customer can see and cancel only their own orders, and only before the slot starts. Cancelled stock goes back.

| Role | What they can do |
|---|---|
| Customer | Browse products, place, view and cancel own orders |
| Admin | Everything a customer can, plus add products |

## Architecture

```
customer ──HTTPS + JWT──► order-service (Spring Boot)
                            ├─ security: JWT login, roles CUSTOMER / ADMIN
                            ├─ REST API: products, slots, orders
                            ├─ rules: slot lock (pessimistic), stock @Version (optimistic), @Transactional
                            └─ PostgreSQL, schema by Flyway

planned:
  order-service ──Kafka "order-placed" / "order-cancelled"──► warehouse-service: picking lists
  order-service ──RabbitMQ + dead-letter queue──► notification-service: "on the way", "delivered"
  Spring AI + pgvector: replacement for out-of-stock items, behind a circuit breaker
  React page · Python weekly report · Prometheus + Grafana · Kubernetes
```

## Tech stack

| Area | Technology |
|---|---|
| Language and framework | Java 21, Spring Boot 4 |
| Data | PostgreSQL, Hibernate, Flyway migrations |
| Security | Spring Security, JWT (HS256), BCrypt |
| Concurrency | Pessimistic lock on slots, optimistic locking on stock, transactions |
| Testing | API test script (8 cases incl. concurrency); JUnit 5, Mockito, Testcontainers *planned* |
| Delivery | Docker Compose; GitHub Actions *planned* |
| Messaging | Kafka, RabbitMQ *planned* |

## Services

| Service | Responsibility | Status |
|---|---|---|
| order-service | Users, login, products, delivery slots, orders, stock | Working |
| warehouse-service | Picking lists from Kafka events, low-stock alerts | Planned |
| notification-service | Order status messages through RabbitMQ, retries, dead-letter queue | Planned |

## Run it

Requirements: Docker and Java 21.

```bash
docker compose up -d
```

Set the JWT key as an environment variable (any random Base64 value of at least 32 bytes):

```bash
export JWT_SECRET=$(openssl rand -base64 32)
```

Start the app from `order-service`:

```bash
./mvnw spring-boot:run
```

Load test data and run the 8 API checks. This empties the database first:

```bash
bash api-test.sh
```

Seeded users: `admin@freshcart.test`, `customer1@freshcart.test`, `customer2@freshcart.test`. The password is in `api-test.sh`.

| What | Where |
|---|---|
| API | http://localhost:8080 |
| PostgreSQL | localhost:5432, database `freshcart` |

## API

| Method | Path | Who |
|---|---|---|
| POST | `/auth/register` | Anyone |
| POST | `/auth/login` | Anyone, returns a token |
| GET | `/product`, `/product/{id}` | Logged in |
| POST | `/product` | Admin |
| POST | `/order` | Logged in |
| GET | `/order/list`, `/order/{id}` | Owner |
| POST | `/order/{id}/cancel` | Owner, before the slot starts |

Example:

```bash
curl -X POST http://localhost:8080/order \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"slot": 1, "orderItems": [{"quantity": 2, "product": 1}]}'
```

## Project structure

```
freshcart/
├── order-service/           Spring Boot: users, login, products, slots, orders
│   └── src/main/resources/
│       ├── db/migration/    Flyway migrations
│       └── data.sql         Test data
├── api-test.sh              8 API checks incl. 10 concurrent orders
└── docker-compose.yml       PostgreSQL
```

## Key design decisions

- **Pessimistic lock on the slot.** Capacity depends on counting other rows, and the slot row itself never changes when an order is added, so `@Version` on the slot would not notice. Locking the slot first makes orders for the same slot wait in line.
- **Optimistic lock on the product.** Every order changes the stock value on the row itself, so `@Version` catches two orders from different slots buying the last item, without making unrelated orders wait.
- **One transaction per order.** If the second item is out of stock, the first item's stock is not lowered.
- **`>=` for capacity.** The count runs before the new order is saved, so a slot with max 5 and 5 orders is full.
- **Delivered orders still take a place.** A slot is one van trip; only cancelled orders free a place.
- **User from the token, never from the request body.** Ownership is checked in the database query, so other people's orders return 404.
- **Flyway owns the schema.** Hibernate only validates (`ddl-auto=validate`).
- **The JWT key is an environment variable**, never in git.
