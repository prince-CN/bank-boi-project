@echo off
echo ========================================
echo Testing Banking System APIs
echo ========================================

echo.
echo Waiting for services to start...
timeout /t 10 /nobreak >nul

echo.
echo 1. Testing Auth Service - Signup
curl -X POST http://localhost:8080/api/auth/signup ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"testuser\",\"email\":\"test@example.com\",\"password\":\"password123\"}"

echo.
echo.
echo 2. Testing Auth Service - Login
curl -X POST http://localhost:8080/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"testuser\",\"password\":\"password123\"}"

echo.
echo.
echo 3. Testing Account Service - Create Account
curl -X POST "http://localhost:8081/api/accounts/create" ^
  -H "Content-Type: application/json" ^
  -d "{\"userId\":1,\"accountType\":\"SAVINGS\",\"initialBalance\":10000}"

echo.
echo.
echo 4. Testing Transaction Service - Create Transaction
curl -X POST http://localhost:8082/api/transactions ^
  -H "Content-Type: application/json" ^
  -d "{\"fromAccount\":\"ACC001\",\"toAccount\":\"ACC002\",\"amount\":1000,\"description\":\"Test payment\"}"

echo.
echo.
echo 5. Testing Wallet Service - Get Wallet
timeout /t 2 /nobreak >nul
curl http://localhost:8083/api/wallets/ACC001

echo.
echo.
echo 6. Testing Fraud Service - Get All Fraud Logs
curl http://localhost:8084/api/fraud/all

echo.
echo.
echo 7. Testing Notification Service - Get All Notifications
curl http://localhost:8085/api/notifications/all

echo.
echo.
echo 8. Testing High Amount Transaction (Fraud Detection)
curl -X POST http://localhost:8082/api/transactions ^
  -H "Content-Type: application/json" ^
  -d "{\"fromAccount\":\"ACC001\",\"toAccount\":\"ACC002\",\"amount\":55000,\"description\":\"High amount test\"}"

echo.
echo.
echo ========================================
echo Testing Complete!
echo ========================================
pause
