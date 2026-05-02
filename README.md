# 📈 Stock Portfolio Monitoring App

A production-ready **Spring Boot Microservices** backend for monitoring stock portfolios — featuring real-time price tracking, gain/loss calculations, configurable alerts, and PDF/Excel reporting.

---

## 🏗️ Architecture

```
                        ┌─────────────────┐
                        │   API Gateway   │  :8080
                        │  (JWT Routing)  │
                        └────────┬────────┘
                                 │
          ┌──────────────────────┼──────────────────────┐
          │                      │                      │
   ┌──────▼──────┐      ┌────────▼───────┐    ┌────────▼───────┐
   │ User Service│      │Portfolio Service│    │Price Fetcher   │
   │   :8081     │      │    :8082        │    │   :8083        │
   └─────────────┘      └────────────────┘    └────────────────┘
          │                      │                      │
   ┌──────▼──────┐      ┌────────▼───────┐    ┌────────▼───────┐
   │Alert Service│      │Reporting Service│   │  Eureka Server │
   │   :8084     │      │    :8085        │   │    :8761       │
   └─────────────┘      └────────────────┘   └────────────────┘
                                                      │
                                             ┌────────▼───────┐
                                             │ Config Server  │
                                             │    :8888       │
                                             └────────────────┘
```

---

## 📦 Microservices

| Service | Port | Owner | Description |
|---|---|---|---|
| Config Server | 8888 | Balaji | Centralized configuration |
| Eureka Server | 8761 | Balaji | Service discovery |
| API Gateway | 8080 | Balaji | JWT routing & load balancing |
| User Service | 8081 | Balaji | Auth, JWT, role-based access |
| Portfolio Service | 8082 | Balaji | Portfolios & holdings management |
| Price Fetcher Service | 8083 | Nivrutti | Real-time prices, gain/loss calculator |
| Alert Service | 8084 | Nivrutti | Price & portfolio loss alerts |
| Reporting Service | 8085 | Balaji | PDF/Excel report generation |

---

## ⚙️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2.5, Spring Data JPA, Spring Security |
| Authentication | JWT (jjwt 0.11.5) |
| Database | MySQL |
| Service Discovery | Netflix Eureka |
| API Gateway | Spring Cloud Gateway |
| Inter-service Comm | OpenFeign |
| Scheduler | Spring Scheduler |
| REST Client | RestTemplate |
| JSON | Jackson |
| Reporting | Apache POI (Excel), iText (PDF) |
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

### Auth
```
POST /api/auth/register    - Register new user
POST /api/auth/login       - Login and get JWT token
```

### Portfolio
```
GET    /api/portfolios              - Get all portfolios
POST   /api/portfolios              - Create portfolio
GET    /api/portfolios/{id}         - Get portfolio by ID
PUT    /api/portfolios/{id}         - Update portfolio
DELETE /api/portfolios/{id}         - Delete portfolio
GET    /api/portfolios/{id}/holdings - Get holdings
```

### Holdings
```
POST   /api/holdings        - Add holding
PUT    /api/holdings/{id}   - Update holding
DELETE /api/holdings/{id}   - Delete holding
```

### Prices & Gain/Loss
```
GET  /api/prices/{symbol}              - Get price details
GET  /api/prices/{symbol}/current      - Get current price
POST /api/prices/{symbol}/refresh      - Refresh price
GET  /api/prices/gainloss/{symbol}?buyPrice=X&quantity=Y - Calculate gain/loss
```

### Alerts
```
GET    /api/alerts          - Get all alerts
POST   /api/alerts          - Create alert
PUT    /api/alerts/{id}     - Update alert
DELETE /api/alerts/{id}     - Delete alert
PATCH  /api/alerts/{id}/status?status=ACTIVE|DISABLED - Toggle status
```

### Reports
```
GET /api/reports/portfolio-summary          - Daily summary
GET /api/reports/export?type=pdf            - Export as PDF
GET /api/reports/export?type=excel          - Export as Excel
```

---

## 🧠 Business Logic

### Gain/Loss Formula
```
gain = (currentPrice - buyPrice) * quantity
percentage = (gain / (buyPrice * quantity)) * 100
```

### Alert Types
- **PRICE_THRESHOLD** — triggers when stock price goes ABOVE or BELOW a set value
- **PORTFOLIO_LOSS** — triggers when total portfolio loss exceeds a percentage

### Price Fetching
- Scheduled job runs every 5 minutes (configurable)
- Integrates with TwelveData API (mock mode available for development)
- Results cached in DB for fast access

---

## 🔒 Security

- Spring Security + JWT authentication
- BCrypt password hashing
- Role-based access: `USER` and `ADMIN`
- JWT validated at API Gateway before routing

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
