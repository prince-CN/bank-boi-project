# Frontend API Gateway Integration

## ✅ Changes Made

### API Configuration Updated
All API calls now route through the **API Gateway** on port **9090** instead of calling services directly.

### Before
```javascript
// Multiple service URLs
const authUrl = 'http://localhost:8080';
const accountUrl = 'http://localhost:8081';
const transactionUrl = 'http://localhost:8082';
const walletUrl = 'http://localhost:8083';
const fraudUrl = 'http://localhost:8084';
const notificationUrl = 'http://localhost:8085';
```

### After
```javascript
// Single API Gateway URL
const API_GATEWAY_URL = 'http://localhost:9090/api';
```

---

## Updated Routes

| Service | Old URL | New URL |
|---------|---------|---------|
| **Auth** | `http://localhost:8080/login` | `http://localhost:9090/api/auth/login` |
| **Accounts** | `http://localhost:8081/api/accounts` | `http://localhost:9090/api/accounts` |
| **Transactions** | `http://localhost:8082/api/transactions` | `http://localhost:9090/api/transactions` |
| **Wallets** | `http://localhost:8083/api/wallets` | `http://localhost:9090/api/wallets` |
| **Fraud** | `http://localhost:8084/api/fraud` | `http://localhost:9090/api/fraud` |
| **Notifications** | `http://localhost:8085/api/notifications` | `http://localhost:9090/api/notifications` |

---

## Benefits

✅ **Single Entry Point** - Only one URL to manage  
✅ **Automatic CORS** - Gateway handles CORS for all services  
✅ **Load Balancing** - Gateway distributes load automatically  
✅ **Simplified Config** - No need to track multiple service ports  
✅ **JWT Validation** - Centralized authentication at gateway  

---

## Testing Frontend

### 1. Start the Services
```bash
# Start in order:
1. Eureka Server (8761)
2. API Gateway (9090)
3. Backend Services (8080-8085)
4. Frontend (5173 or 3000)
```

### 2. Test Login
```javascript
// Frontend will call:
POST http://localhost:9090/api/auth/login

// Gateway routes to:
auth-service (port 8080)
```

### 3. Test Transactions
```javascript
// Frontend will call:
POST http://localhost:9090/api/transactions

// Gateway routes to:
transaction-service (port 8082)
```

---

## Configuration

### API Gateway URL
Update `.env` or configuration file if needed:
```env
VITE_API_GATEWAY_URL=http://localhost:9090/api
```

### For Production
```env
VITE_API_GATEWAY_URL=https://api.yourdomain.com/api
```

---

## File Updated
✅ [`src/services/api.js`](file:///d:/cloud nexus project/banking-aap-prince/frontend/src/services/api.js)

All API calls now use the API Gateway! 🚀
