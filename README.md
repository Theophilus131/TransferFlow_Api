# TransactFlow API

A secure REST API for user authentication and money transfer simulation built with Spring Boot, MongoDB, and JWT.

##  Features

-   User registration and authentication with JWT
-  Secure money transfer between users
-  Wallet management with initial balance
-  Transaction history tracking
-  Overdraft prevention
-  Double spending prevention
-  Rate limiting (10 req/min for transactions, 5 req/min for auth)
-  postman documentation


## Prerequisites

- Java 24
- Maven 3.8+
- MongoDB 5.0+
- Postman (for testing)

### 2. Install MongoDB
**MacOS:**
```bash
brew tap mongodb/brew
brew install mongodb-community
brew services start mongodb-community
```

**Using Maven Wrapper (if available):**
```bash
./mvnw clean install
./mvnw spring-boot:run
```

**Build JAR and run:**
```bash
mvn clean package
java -jar target/TransactFlow-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8080`

## API Documentation
post man 
## 🔑 API Endpoints

### Authentication Endpoints (No Auth Required)

#### 1. Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe"
}
```

**Response (201 Created):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "user@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "message": "User registered successfully"
}
```

#### 2. Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "user@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "message": "Login successful"
}
```

---

### Transaction Endpoints (Auth Required)

**All transaction endpoints require JWT token in header:**
```
Authorization: Bearer <your_jwt_token>
```

#### 3. Transfer Money
```http
POST /api/transactions/transfer
Authorization: Bearer <token>
Content-Type: application/json

{
  "receiverEmail": "receiver@example.com",
  "amount": 100.50,
  "description": "Payment for services"
}
```

**Response (200 OK):**
```json
{
  "transactionId": "507f1f77bcf86cd799439011",
  "referenceNumber": "TXN1A2B3C4D5E6F7",
  "senderEmail": "user@example.com",
  "receiverEmail": "receiver@example.com",
  "amount": 100.50,
  "description": "Payment for services",
  "status": "SUCCESS",
  "newBalance": 9899.50,
  "timestamp": "2025-11-17T21:45:30",
  "message": "Transfer successful"
}
```

#### 4. Check Balance
```http
GET /api/transactions/balance
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
{
  "email": "user@example.com",
  "balance": 9899.50,
  "currency": "USD"
}
```

#### 5. Get Transaction History
```http
GET /api/transactions/history
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
[
  {
    "transactionId": "507f1f77bcf86cd799439011",
    "referenceNumber": "TXN1A2B3C4D5E6F7",
    "type": "SENT",
    "otherParty": "receiver@example.com",
    "amount": 100.50,
    "description": "Payment for services",
    "status": "SUCCESS",
    "balanceBefore": 10000.00,
    "balanceAfter": 9899.50,
    "timestamp": "2025-11-17T21:45:30"
  }
]
```

#### 6. Get Sent Transactions
```http
GET /api/transactions/sent
Authorization: Bearer <token>
```

#### 7. Get Received Transactions
```http
GET /api/transactions/received
Authorization: Bearer <token>
```

##  Security Features

### JWT Authentication
- Tokens expire after 24 hours (configurable)
- HS256 algorithm with secret key
- Stateless authentication

### Rate Limiting
- **Auth endpoints:** 5 requests per minute
- **Transaction endpoints:** 10 requests per minute
- Per-user for authenticated, per-IP for unauthenticated

### Transaction Security
- Overdraft prevention
- Double spending prevention
- Email uniqueness validation
- Password encryption with BCrypt
- Transaction audit trail

## Testing with Postman

### 1. Import Environment Variables
Create a Postman environment with:
- `base_url`: `http://localhost:8080`
- `token`: (will be set automatically)

### 2. Test Flow

**Step 1:** Register User A
```
POST {{base_url}}/api/auth/register
```
Save the token from response.

**Step 2:** Register User B
```
POST {{base_url}}/api/auth/register
```

**Step 3:** Check Balance (User A)
```
GET {{base_url}}/api/transactions/balance
Authorization: Bearer {{token}}
```

**Step 4:** Transfer Money (A → B)
```
POST {{base_url}}/api/transactions/transfer
Authorization: Bearer {{token}}
Body: {
  "receiverEmail": "userb@example.com",
  "amount": 50.00,
  "description": "Test transfer"
}
```

**Step 5:** Check Transaction History
```
GET {{base_url}}/api/transactions/history
Authorization: Bearer {{token}}
```

## Error Responses

### 400 Bad Request
```json
{
  "timestamp": "2025-11-17T21:45:30",
  "status": 400,
  "error": "Bad Request",
  "message": "Insufficient balance. Available: 100.00, Required: 500.00"
}
```

### 401 Unauthorized
```json
{
  "timestamp": "2025-11-17T21:45:30",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid email or password"
}
```

### 429 Too Many Requests
```json
{
  "timestamp": "2025-11-17T21:45:30",
  "status": 429,
  "error": "Too Many Requests",
  "message": "Rate limit exceeded. Please try again in 45 seconds",
  "retryAfter": "45 seconds"
}
```

##  Architecture
```
src/main/java/com/TransactFlow/
├── controller/          # REST endpoints
├── service/            # Business logic
├── repository/         # Database access
├── dto/               # Data Transfer Objects
├── mapper/            # Entity ↔ DTO conversion
├── model/             # MongoDB entities
├── security/          # JWT & authentication
├── config/            # App configuration
└── exception/         # Custom exceptions
```

##  Configuration

### Development Profile
```bash
SPRING_PROFILE=dev
```
- Debug logging enabled
- Local MongoDB
- Relaxed security for testing

### Production Profile
```bash
SPRING_PROFILE=prod
```
- Warn/Error logging only
- Environment variables required
- Enhanced security

## 🌍 Environment Variables

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `MONGODB_URI` | MongoDB connection string | `mongodb://localhost:27017/transactflow` | Yes |
| `MONGODB_DATABASE` | Database name | `transactflow` | Yes |
| `JWT_SECRET` | JWT signing secret (256+ bits) | - | Yes |
| `JWT_EXPIRATION` | Token expiration (milliseconds) | `86400000` (24h) | No |
| `SERVER_PORT` | Application port | `8080` | No |
| `SPRING_PROFILE` | Active profile (dev/prod) | `dev` | No |
| `INITIAL_BALANCE` | Initial wallet balance | `10000.00` | No |
| `MIN_TRANSACTION_AMOUNT` | Minimum transfer amount | `0.01` | No |
| `MAX_TRANSACTION_AMOUNT` | Maximum transfer amount | `100000.00` | No |

## 📦 Dependencies

- **Spring Boot 3.2+** - Framework
- **Spring Security** - Authentication & Authorization
- **Spring Data MongoDB** - Database access
- **JWT (jjwt 0.12.5)** - Token generation/validation
- **Lombok** - Reduce boilerplate code
- **Bucket4j** - Rate limiting
- **SpringDoc OpenAPI** - API documentation

##  Author

Your Theophilus Umar - theophilusumar27@gmail.com
