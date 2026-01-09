@echo off
echo ========================================
echo Starting Banking System with Eureka
echo ========================================

echo.
echo Step 1: Starting Eureka Server (Service Discovery)...
start "Eureka Server" cmd /k "cd eureka-server && mvn spring-boot:run"
echo Waiting for Eureka to start (30 seconds)...
timeout /t 30 /nobreak >nul

echo.
echo Step 2: Starting Config Server (Configuration)...
start "Config Server" cmd /k "cd config-server && mvn spring-boot:run"
echo Waiting for Config Server (15 seconds)...
timeout /t 15 /nobreak >nul

echo.
echo Step 3: Starting All Microservices...

echo Starting Transaction Service...
start "Transaction Service" cmd /k "cd transaction-service && mvn spring-boot:run"
timeout /t 3 /nobreak >nul

echo Starting Notification Service...
start "Notification Service" cmd /k "cd notification-service && mvn spring-boot:run"
timeout /t 2 /nobreak >nul

echo Starting Wallet Service...
start "Wallet Service" cmd /k "cd wallet-service && mvn spring-boot:run"
timeout /t 2 /nobreak >nul

echo Starting Fraud Service...
start "Fraud Service" cmd /k "cd fraud-service && mvn spring-boot:run"
timeout /t 2 /nobreak >nul

echo Starting Auth Service...
start "Auth Service" cmd /k "cd auth-service && mvn spring-boot:run"
timeout /t 2 /nobreak >nul

echo Starting Account Service...
start "Account Service" cmd /k "cd account-service && mvn spring-boot:run"

echo.
echo ========================================
echo All services are starting...
echo.
echo Eureka Dashboard: http://localhost:8761
echo Config Server: http://localhost:8888
echo.
echo Wait 2-3 minutes for all services to register
echo ========================================
pause
