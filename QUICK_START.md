# Banking System - Quick Start Guide

## 🚀 Quick Start (Test Karne Ke Liye)

### Step 1: Docker Start Karo
```bash
docker-compose up -d
```

### Step 2: Services Start Karo (Separate Terminals Mein)

**Terminal 1 - Transaction Service:**
```bash
cd transaction-service
mvn spring-boot:run
```

**Terminal 2 - Notification Service:**
```bash
cd notification-service
mvn spring-boot:run
```

**Terminal 3 - Wallet Service:**
```bash
cd wallet-service
mvn spring-boot:run
```

**Terminal 4 - Fraud Service:**
```bash
cd fraud-service
mvn spring-boot:run
```

**Terminal 5 - Auth Service:**
```bash
cd auth-service
mvn spring-boot:run
```

**Terminal 6 - Account Service:**
```bash
cd account-service
mvn spring-boot:run
```

---

## 🧪 Testing APIs

### 1️⃣ Auth Service (Port 8080)

**Signup:**
```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"john\",\"email\":\"john@example.com\",\"password\":\"password123\"}"
```

**Login:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"john\",\"password\":\"password123\"}"
```

**Response:**
```json
{
  "token": "eyJhbGc...",
  "type": "Bearer",
  "userId": 1,
  "username": "john",
  "email": "john@example.com",
  "role": "USER"
}
```

---

### 2️⃣ Account Service (Port 8081)

**Create Account:**
```bash
curl -X POST "http://localhost:8081/api/accounts/create" \
  -H "Content-Type: application/json" \
  -d "{\"userId\":1,\"accountType\":\"SAVINGS\",\"initialBalance\":5000}"
```

**Get User Accounts:**
```bash
curl http://localhost:8081/api/accounts/user/1
```

**Get Account by Number:**
```bash
curl http://localhost:8081/api/accounts/number/ACC1234567890
```

---

### 3️⃣ Transaction Service (Port 8082) ⭐

**Create Transaction:**
```bash
curl -X POST http://localhost:8082/api/transactions \
  -H "Content-Type: application/json" \
  -d "{\"fromAccount\":\"ACC001\",\"toAccount\":\"ACC002\",\"amount\":1000,\"description\":\"Test payment\"}"
```

**Get Transaction:**
```bash
curl http://localhost:8082/api/transactions/1
```

**Get Transaction History:**
```bash
curl http://localhost:8082/api/transactions/history/ACC001
```

---

### 4️⃣ Wallet Service (Port 8083)

**Create Wallet:**
```bash
curl -X POST "http://localhost:8083/api/wallets/create?accountNumber=ACC001&initialBalance=10000"
```

**Get Wallet:**
```bash
curl http://localhost:8083/api/wallets/ACC001
```

**Get Balance:**
```bash
curl http://localhost:8083/api/wallets/ACC001/balance
```

---

### 5️⃣ Fraud Service (Port 8084)

**Get Flagged Transactions:**
```bash
curl http://localhost:8084/api/fraud/flagged
```

**Get All Fraud Logs:**
```bash
curl http://localhost:8084/api/fraud/all
```

**Test High Amount (> 50,000):**
```bash
curl -X POST http://localhost:8082/api/transactions \
  -H "Content-Type: application/json" \
  -d "{\"fromAccount\":\"ACC001\",\"toAccount\":\"ACC002\",\"amount\":55000,\"description\":\"High amount test\"}"
```

---

### 6️⃣ Notification Service (Port 8085)

**Get All Notifications:**
```bash
curl http://localhost:8085/api/notifications/all
```

**Get Notifications by Account:**
```bash
curl http://localhost:8085/api/notifications/recipient/ACC001
```

---

## 🔄 Complete Flow Test

### Test Scenario: Money Transfer with Fraud Detection

```bash
# Step 1: Create Transaction (₹60,000 - will trigger fraud alert)
curl -X POST http://localhost:8082/api/transactions \
  -H "Content-Type: application/json" \
  -d "{\"fromAccount\":\"ACC001\",\"toAccount\":\"ACC002\",\"amount\":60000,\"description\":\"High value transfer\"}"

# Step 2: Check Wallet Updates
curl http://localhost:8083/api/wallets/ACC001

# Step 3: Check Fraud Detection
curl http://localhost:8084/api/fraud/flagged

# Step 4: Check Notifications
curl http://localhost:8085/api/notifications/all
```

**Expected Flow:**
1. ✅ Transaction created (PENDING)
2. 📤 Kafka: `TRANSACTION_INITIATED` event published
3. 💰 Wallet Service: Debit ACC001, Credit ACC002
4. 🚨 Fraud Service: Detects high amount, flags transaction
5. 📧 Notification Service: Sends fraud alert
6. ✅ Transaction updated to SUCCESS

---

## 📊 Check Kafka Topics

```bash
# List all topics
docker exec -it banking-kafka kafka-topics --list --bootstrap-server localhost:9092

# Read from TRANSACTION_INITIATED topic
docker exec -it banking-kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic TRANSACTION_INITIATED \
  --from-beginning
```

---

## 🐛 Troubleshooting

### Check Docker Containers:
```bash
docker ps
```

### Check Kafka Logs:
```bash
docker logs banking-kafka
```

### Check MongoDB:
```bash
docker exec -it banking-mongodb mongosh -u admin -p admin123
```

### Check PostgreSQL:
```bash
docker exec -it banking-postgres psql -U postgres -d transaction_db
```

### Check MySQL:
```bash
mysql -u root -pPrince@123 -h localhost
```

---

## 📱 Postman Collection Export Karne Ke Liye:

All APIs ko Postman mein import kar sakte ho:
1. Create new workspace
2. Import → Raw text
3. Copy-paste above curl commands

---

## ✅ Success Indicators:

1. **All services start without errors**
2. **Kafka topics created automatically**
3. **Database tables created (JPA DDL)**
4. **Transaction flows through all services**
5. **Fraud detection works for high amounts**
6. **Notifications logged in MongoDB**

Happy Testing! 🎉
