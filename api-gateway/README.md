# 🌐 API Gateway - Banking Application

## Overview
Spring Cloud Gateway serving as the single entry point for all microservices in the banking application.

## Port
**9090** - API Gateway listens on this port

## Features

### ✅ Service Discovery
- Integrated with Eureka Server
- Automatic service discovery and load balancing
- Dynamic routing based on service availability

### ✅ Routing Configuration
All routes use the `/api/<service>/**` pattern:

| Route | Target Service | Port |
|-------|---------------|------|
| `/api/auth/**` | auth-service | 8080 |
| `/api/accounts/**` | account-service | 8081 |
| `/api/transactions/**` | transaction-service | 8082 |
| `/api/wallets/**` | wallet-service | 8083 |
| `/api/fraud/**` | fraud-service | 8084 |
| `/api/notifications/**` | notification-service | 8085 |

### ✅ CORS Support
Pre-configured CORS for frontend applications:
- React (port 3000)
- Vite (port 5173)
- Angular (port 4200)

### ✅ JWT Authentication
- Optional JWT validation filter
- Skips authentication for `/login` and `/register` endpoints
- Adds user context headers for downstream services

### ✅ Health Monitoring
- Spring Boot Actuator endpoints
- Gateway-specific metrics
- Health checks for all routes

## Usage Examples

### Authentication
```bash
# Register new user
POST http://localhost:9090/api/auth/register

# Login
POST http://localhost:9090/api/auth/login
```

### Account Operations
```bash
# Create account
POST http://localhost:9090/api/accounts

# Get account
GET http://localhost:9090/api/accounts/{accountNumber}
```

### Transaction Operations
```bash
# Initiate transaction
POST http://localhost:9090/api/transactions

# Get transaction status
GET http://localhost:9090/api/transactions/{transactionId}/status
```

### Wallet Operations
```bash
# Get wallet balance
GET http://localhost:9090/api/wallets/{accountNumber}

# Update wallet
POST http://localhost:9090/api/wallets/update
```

## Configuration

### application.yml
Key configurations:
- **Server Port**: 9090
- **Eureka Server**: http://localhost:8761/eureka/
- **JWT Secret**: Shared with auth-service
- **CORS Origins**: Configure allowed frontend URLs

### Route Pattern
```yaml
- id: service-name
  uri: lb://service-name  # Load balanced
  predicates:
    - Path=/api/service/**
  filters:
    - RewritePath=/api/service/(?<segment>.*), /${segment}
```

## Starting the Gateway

```bash
cd api-gateway
mvn spring-boot:run
```

**Access Gateway:**
- Main: http://localhost:9090
- Health: http://localhost:9090/actuator/health
- Routes: http://localhost:9090/actuator/gateway/routes

## Prerequisites

1. **Eureka Server** must be running on port 8761
2. **Target services** should be registered with Eureka
3. **Java 21** and **Maven** installed

## Security Notes

### JWT Filter
The JWT authentication filter is configured but optional. To enable:
1. Uncomment the filter in route configurations
2. Ensure JWT secret matches auth-service

### Public Endpoints
These endpoints bypass JWT validation:
- `/api/auth/login`
- `/api/auth/register`
- `/actuator/**`

## Monitoring

### Gateway Actuator Endpoints
```bash
# All routes
GET http://localhost:9090/actuator/gateway/routes

# Refresh routes
POST http://localhost:9090/actuator/gateway/refresh

# Gateway metrics
GET http://localhost:9090/actuator/metrics
```

## Testing

### Check Gateway Health
```bash
curl http://localhost:9090/actuator/health
```

### Test Service Routing
```bash
# Should route to auth-service
curl http://localhost:9090/api/auth/login -X POST \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"test123"}'
```

## Architecture

```
Frontend (React/Vite)
         ↓
API Gateway (Port 9090)
         ↓
    Eureka Server
         ↓
┌────────┴────────┐
↓                 ↓
Services      Load Balancing
```

## Troubleshooting

### Gateway Not Starting
- Ensure Eureka Server is running
- Check port 9090 is not in use
- Verify Maven dependencies are resolved

### Routes Not Working
- Check service registration in Eureka Dashboard
- Verify service names match route URIs
- Check gateway logs for routing errors

### CORS Issues
- Verify frontend origin in CORS configuration
- Check browser console for CORS errors
- Ensure credentials are enabled if needed

## Future Enhancements

- [ ] Rate limiting per service
- [ ] Circuit breaker integration
- [ ] Request/Response logging
- [ ] API versioning support
- [ ] Distributed tracing with Sleuth/Zipkin
