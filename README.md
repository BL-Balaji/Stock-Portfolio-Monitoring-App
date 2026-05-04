# 📈 Stock Portfolio Monitoring App

A production-ready **Spring Boot Microservices** backend for monitoring stock portfolios — featuring real-time price tracking, gain/loss calculations, configurable alerts, and PDF/Excel reporting.

---

## 🏗️ Architecture

```
Client ──► API Gateway (JWT Validation + Routing) :8080
                │
    ┌───────────┼───────────────────┐
    │           │                   │
User Service  Portfolio Service  Price Fetcher Service
  :8081          :8082               :8083
    │           │                   │
Alert Service  Reporting Service  Eureka Server
  :8084          :8085               :8761
                                     │
                              Config Server
                                  :8888
```

---

## 📦 Microservices

| Service | Port | Owner | Description |
|---|---|---|---|
| Config Server | 8888 | Balaji | Centralized configuration (native profile) |
| Eureka Server | 8761 | Balaji | Service discovery & registration |
| API Gateway | 8080 | Balaji | JWT routing, load balancing, CORS |
| User Service | 8081 | Balaji | Auth, JWT issuance, role-based access |
| Portfolio Service | 8082 | Balaji | Portfolios & holdings management, gain/loss |
| Price Fetcher Service | 8083 | Nivrutti | Real-time prices, TwelveData API, caching, gain/loss calculator |
| Alert Service | 8084 | Nivrutti | Price threshold & portfolio loss alerts, email notifications |
| Reporting Service | 8085 | Balaji | PDF/Excel report generation |

---

## ⚙️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2.5, Spring Data JPA, Spring Security |
| Authentication | JWT (jjwt 0.11.5) |
| Database | MySQL 8.0 (shared schema: `stock_portfolio_db`) |
| Service Discovery | Netflix Eureka (Spring Cloud 2023.0.1) |
| API Gateway | Spring Cloud Gateway |
| Inter-service Comm | OpenFeign with Circuit Breaker fallbacks |
| Scheduler | Spring `@Scheduled` (price refresh + alert evaluation) |
| REST Client | RestTemplate (external price API) |
| JSON | Jackson |
| Reporting | Apache POI 5.2.5 (Excel), iText 5.5.13.3 (PDF) |
| Build | Maven (multi-module) |

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8.0+

### 1. Database Setup
```sql
CREATE DATABASE stock_portfolio_db;
```

### 2. Start Services (in order)

```bash
# 1. Config Server
cd config-server && mvn spring-boot:run

# 2. Eureka Server
cd eureka-server && mvn spring-boot:run

# 3. API Gateway
cd api-gateway && mvn spring-boot:run

# 4. Business Services (any order)
cd user-service && mvn spring-boot:run
cd portfolio-service && mvn spring-boot:run
cd price-fetcher-service && mvn spring-boot:run
cd alert-service && mvn spring-boot:run
cd reporting-service && mvn spring-boot:run
```

### 3. Verify
- Eureka Dashboard: http://localhost:8761
- API Gateway: http://localhost:8080

---

## 🔐 API Endpoints

### Auth (User Service via Gateway)
```
POST /api/auth/register    - Register new user
POST /api/auth/login       - Login and get JWT token
```

### Portfolio
```
GET    /api/portfolios              - Get all portfolios for current user
POST   /api/portfolios              - Create portfolio
GET    /api/portfolios/{id}         - Get portfolio by ID
PUT    /api/portfolios/{id}         - Update portfolio
DELETE /api/portfolios/{id}         - Delete portfolio
GET    /api/portfolios/{id}/holdings - Get all holdings in portfolio
```

### Holdings
```
POST   /api/holdings              - Add holding to portfolio
PUT    /api/holdings/{id}         - Update holding
DELETE /api/holdings/{id}         - Delete holding
```

### Prices & Gain/Loss
```
GET  /api/prices/{symbol}                                    - Get price details
GET  /api/prices/{symbol}/current                            - Get current price
POST /api/prices/{symbol}/refresh                            - Force refresh price
GET  /api/prices/gainloss/{symbol}?buyPrice=X&quantity=Y     - Calculate gain/loss
GET  /api/prices/all                                         - Get all cached prices
```

### Alerts
```
GET    /api/alerts                        - Get all alerts for current user
POST   /api/alerts                        - Create alert
GET    /api/alerts/{id}                   - Get alert by ID
PUT    /api/alerts/{id}                   - Update alert
DELETE /api/alerts/{id}                   - Delete alert
PATCH  /api/alerts/{id}/status?status=ACTIVE|TRIGGERED|DISABLED - Toggle status
```

### Reports
```
GET /api/reports/portfolio-summary    - Portfolio summary with gain/loss
GET /api/reports/export?type=pdf      - Export as PDF
GET /api/reports/export?type=excel    - Export as Excel
```

---

## 🧠 Business Logic

### Gain/Loss Formula
```
investedValue  = buyPrice × quantity
currentValue   = currentPrice × quantity
gainLoss       = currentValue - investedValue
gainLoss%      = (gainLoss / investedValue) × 100
```

### Alert Types
- **PRICE_THRESHOLD** — triggers when stock price goes ABOVE or BELOW a set value
- **PORTFOLIO_LOSS** — triggers when total portfolio loss exceeds a percentage

### Price Fetching
- Scheduled job runs every 5 minutes (configurable via `price.fetch.interval`)
- Integrates with TwelveData API (mock mode available: `external.api.use-mock=true`)
- Results cached in DB for fast access

### Alert Evaluation
- Scheduled job runs every 5 minutes (configurable via `alert.evaluation.interval`)
- Fetches current prices via Feign call to price-fetcher-service
- Triggers notifications (DB log + optional email)

---

## 🔒 Security

- Spring Security + JWT authentication
- BCrypt password hashing
- Role-based access: `USER` and `ADMIN`
- JWT validated at API Gateway before routing to downstream services
- User info forwarded via `X-User-Id` and `X-User-Role` headers

---

## 🔗 Inter-Service Communication (OpenFeign)

| Consumer | Provider | Purpose |
|---|---|---|
| portfolio-service | price-fetcher-service | Fetch current prices for holdings |
| alert-service | price-fetcher-service | On-demand price check for threshold alerts |
| reporting-service | portfolio-service | Fetch holdings + summary for report generation |

All Feign clients have fallback implementations for resilience.

---

## 👥 Contributors

| Contributor | GitHub | Services |
|---|---|---|
| Balaji | [@BL-Balaji](https://github.com/BL-Balaji) | Infra (Config, Eureka, Gateway), User Service, Portfolio Service, Reporting Service |
| Nivrutti | [@BL-Nivrutti](https://github.com/BL-Nivrutti) | Price Fetcher Service, Alert Service, Gain/Loss Calculator |

---

## 🌿 Branch Strategy

```
main          ← stable, production-ready
  └── dev     ← integration branch
        ├── feature/infra-services
        ├── feature/user-service
        ├── feature/portfolio-service
        ├── feature/price-fetcher-service
        ├── feature/alert-service
        └── feature/reporting-service
```

---

## 📝 Commit Message Convention

```
[Name] : Message

Examples:
[Balaji]   : Add User Service APIs
[Nivrutti] : Implement price threshold alert evaluation
[Balaji]   : Fix gain/loss calculation in Portfolio Service
```
