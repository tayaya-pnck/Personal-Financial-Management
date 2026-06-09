# 💰 FinanceTracker

> yeah, another finance app — but this one's built to show i actually know what i'm doing with Java Spring Boot.

a personal finance tracking web app built from scratch. manage accounts, transactions, budgets, savings goals, and get actual useful reports. dark mode included because my eyes hurt.

---

## **what's inside**

### **backend** — Java 21 + Spring Boot 3.4
```
POST /api/auth/register     → sign up
POST /api/auth/login        → sign in
POST /api/auth/refresh      → refresh expired tokens

GET/POST    /api/accounts       → list / create accounts
PUT/DELETE  /api/accounts/:id   → update / soft-delete

GET/POST    /api/transactions         → list (paginated) / create
DELETE      /api/transactions/:id     → delete (reverses balance)
GET         /api/transactions/by-account/:id

GET/POST    /api/categories          → tree or flat list / create
PUT/DELETE  /api/categories/:id

GET/POST    /api/budgets             → filtered by month
PUT/DELETE  /api/budgets/:id

GET/POST    /api/goals               → savings goals
PUT/DELETE  /api/goals/:id
PATCH       /api/goals/:id/progress  → update current amount

GET  /api/reports/dashboard            → net worth, income vs expense, recent tx
GET  /api/reports/spending-by-category → pie chart data
GET  /api/reports/monthly-summary      → bar chart data (last N months)
```

### **frontend** — React + TypeScript + Tailwind v4 + Recharts
```
/login      → sign in
/register   → sign up
/           → dashboard (net worth cards, charts, recent tx)
/accounts   → CRUD accounts (checking, savings, credit card, etc)
/transactions → paginated list, create with account+category
/categories → hierarchical tree (Food → Dining Out → Pizza)
/budgets   → monthly budget vs actuals with progress bars
/goals     → savings goals with progress rings
```

---

## **tech stack — why each thing**

| thing | why |
|---|---|
| **Java 21** | latest LTS, virtual threads ready, pattern matching, records |
| **Spring Boot 3.4** | current-gen, security + data + web all in one |
| **Spring Security + JWT** | stateless auth, access + refresh tokens, BCrypt passwords |
| **JPA + Hibernate** | ORM with proper lazy loading, batch inserts, UUID PKs |
| **H2 (default) / PostgreSQL (docker)** | H2 for zero-config local dev, Postgres for prod |
| **Flyway** | database migrations you can actually version-control |
| **MapStruct** | removes the DTO-mapping boilerplate at compile time |
| **Testcontainers** | integration tests with real Postgres, not mocks |
| **SpringDoc OpenAPI** | `/swagger-ui.html` — interactive API docs |
| **React + Vite** | fast dev server, TS out of the box |
| **TanStack Query** | caching, auto-refetch, optimistic updates |
| **Recharts** | responsive charts that don't suck |
| **Tailwind v4** | utility-first CSS, dark theme, no bloat |
| **Lucid React** | clean icon set, tree-shakeable |

---

## **architecture stuff that matters**

### **project structure**
```
backend/
└── src/main/java/com/finance/tracker/
    ├── config/         # Security, CORS, OpenAPI setup
    ├── auth/           # JWT service, filter, login/register
    ├── user/           # User entity (implements UserDetails)
    ├── account/        # Account module (entity → repo → service → controller)
    ├── transaction/    # Transaction with balance auto-update
    ├── category/       # Self-referencing tree (parent/child)
    ├── budget/         # Monthly budgets with vs-actual logic
    ├── goal/           # Savings goals with progress tracking
    ├── report/         # Aggregation queries for charts
    ├── common/         # BaseEntity, error handling, pagination
    └── infrastructure/ # Scheduling, audit, etc.
```

each feature module is self-contained — its entities, DTOs, mapper, service, and controller live together. no cross-contamination. if you delete `budget/`, the rest still compiles.

### **security flow**
1. user registers → gets access token (24h) + refresh token (7d)
2. every API call includes `Authorization: Bearer <token>`
3. `JwtAuthenticationFilter` validates token on every request
4. if token expired → client calls `/auth/refresh` → gets new pair
5. passwords hashed with BCrypt (strength 10)
6. CORS locked to frontend origins only

### **data integrity**
- creating a transaction automatically updates the account balance (in a `@Transactional` block)
- deleting a transaction reverses the balance change atomically
- soft-delete for accounts (data doesn't disappear, just gets `archived`)
- unique constraint on budgets: one budget per (user, category, month)
- UUID primary keys — no sequential IDs leaking info

### **database profiles**
| profile | database | flyway | use case |
|---|---|---|---|
| default (none) | H2 in-memory | off | local dev, zero setup |
| `docker` | PostgreSQL | on | docker compose |
| `test` | Testcontainers | on | integration tests |

---

## **how to run this thing**

### **the easy way (recommended)**
```bash
# terminal 1 — backend
cd backend
mvn spring-boot:run
# → http://localhost:9090

# terminal 2 — frontend
cd frontend
npm run dev
# → http://localhost:3000
```
open `http://localhost:3000`, register an account, start tracking.

### **the docker way**
```bash
# requires Docker Desktop with WSL2 integration enabled
docker compose up --build
# → backend at http://localhost:9090
# → frontend at http://localhost:3000
```

### **run specific profile**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=docker
```

---

## **api docs**

once the backend is running:
- swagger UI: http://localhost:9090/swagger-ui.html
- openAPI JSON: http://localhost:9090/api-docs

there's also an H2 console at http://localhost:9090/h2-console (JDBC URL: `jdbc:h2:mem:finance_tracker`) — useful for debugging, don't expose in prod.

---

## **what i'd add next (if this were real)**

- [ ] **CSV/OFX import** — upload bank statements, auto-categorize
- [ ] **recurring transactions** — scheduled jobs that auto-create monthly bills
- [ ] **email notifications** — "you blew your dining budget" alerts
- [ ] **multi-currency** — exchange rates, cross-currency conversions
- [ ] **investment tracking** — portfolios, cost basis, unrealized gains
- [ ] **data export** — download transactions as CSV/PDF
- [ ] **CI/CD** — GitHub Actions → build → test → deploy
- [ ] **rate limiting** — because bots shouldn't bruteforce auth

---

## **what this proves in an interview**

1. **i can build a full-stack app from zero** — not just copy tutorials
2. **i understand security** — JWT, BCrypt, CORS, validation, stateless auth
3. **i write clean code** — modules, interfaces, mappers, no god classes
4. **i think about data** — migrations, constraints, integrity, indexes
5. **i can design APIs** — RESTful, paginated, documented, consistent errors
6. **i know the ecosystem** — Spring, JPA, security, testing, docker

---

## **the boring stuff**

built with Java 21, Spring Boot 3.4.4, React 19, Vite, and way too much coffee.

---

*questions, feedback, job offers? you know where to find me.*
