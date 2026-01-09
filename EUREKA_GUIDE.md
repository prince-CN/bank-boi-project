# 🌐 Eureka Service Discovery & Config Server Guide

## 🎯 Architecture Overview

```
Eureka Server (8761) ← All Services Register Here
   ↓
Config Server (8888) ← Centralized Configuration
   ↓
All Microservices ← Fetch Config & Register
```

## 📦 Services Added:

### 1️⃣ Eureka Server (Port 8761)
**Service Discovery Dashboard**

- URL: http://localhost:8761
- All services register here
- Service health monitoring
- Load balancing support

### 2️⃣ Config Server (Port 8888)
**Centralized Configuration Management**

- URL: http://localhost:8888
- Stores all service configurations
- Dynamic configuration updates
- Version control ready

---

## 🚀 Starting Services (Updated Order)

### Step 1: Start Eureka Server FIRST
```bash
cd eureka-server
mvn spring-boot:run
```
**Wait for:** Dashboard at http://localhost:8761

### Step 2: Start Config Server
```bash
cd config-server
mvn spring-boot:run
```

### Step 3: Start All Other Services
```bash
# They will auto-register with Eureka
cd transaction-service && mvn spring-boot:run
cd notification-service && mvn spring-boot:run
cd wallet-service && mvn spring-boot:run
cd fraud-service && mvn spring-boot:run
cd auth-service && mvn spring-boot:run
cd account-service && mvn spring-boot:run
```

---

## 🔍 How to Use Eureka Dashboard

### Access Dashboard:
```
http://localhost:8761
```

### What You'll See:
- **Instances currently registered**: All running services
- **Service Name**: transaction-service, wallet-service, etc.
- **Status**: UP (green) = healthy
- **Instance Info**: IP, port, health URL

---

## 📊 Service Discovery Benefits

### 1️⃣ Dynamic Service Discovery
Services find each other automatically - no hardcoded URLs!

**Before:**
```java
String walletServiceUrl = "http://localhost:8083";
```

**After (with Eureka):**
```java
@Autowired
private DiscoveryClient discoveryClient;

List<ServiceInstance> instances = 
    discoveryClient.getInstances("wallet-service");
```

### 2️⃣ Load Balancing
Multiple instances of same service? Eureka handles load balancing!

### 3️⃣ Health Monitoring
Eureka automatically removes unhealthy services

### 4️⃣ Resilience
If one instance fails, traffic routes to healthy instances

---

## 🎨 Eureka UI Features

| Feature | Description |
|---------|-------------|
| **Instance Status** | Green = UP, Red = DOWN |
| **Renewal Interval** | Heartbeat frequency (10s) |
| **Lease Duration** | Max time without heartbeat (30s) |
| **Replicas** | For multi-node Eureka setup |

---

## 🧪 Testing Service Discovery

### 1. Check All Registered Services:
```bash
curl http://localhost:8761/eureka/apps
```

### 2. Check Specific Service:
```bash
curl http://localhost:8761/eureka/apps/TRANSACTION-SERVICE
```

### 3. Service Health Check:
```bash
curl http://localhost:8082/actuator/health
```

---

## 📝 Configuration Hierarchy

```
Config Server
├── application.yml (Common for all)
├── transaction-service.yml
├── wallet-service.yml
├── fraud-service.yml
├── notification-service.yml
├── auth-service.yml
└── account-service.yml
```

### Get Configuration:
```bash
# Get transaction-service config
curl http://localhost:8888/transaction-service/default
```

---

## 🔧 Advanced Features

### 1️⃣ Service-to-Service Communication

**Using RestTemplate with Load Balancing:**
```java
@LoadBalanced
@Bean
public RestTemplate restTemplate() {
    return new RestTemplate();
}

// Usage
String response = restTemplate.getForObject(
    "http://wallet-service/api/wallets/ACC001", 
    String.class
);
```

### 2️⃣ Feign Client (Declarative REST)
```java
@FeignClient(name = "wallet-service")
public interface WalletClient {
    @GetMapping("/api/wallets/{accountNumber}")
    Wallet getWallet(@PathVariable String accountNumber);
}
```

### 3️⃣ Circuit Breaker with Resilience4j
```java
@CircuitBreaker(name = "walletService", fallbackMethod = "fallbackWallet")
public Wallet getWallet(String accountNumber) {
    return walletClient.getWallet(accountNumber);
}
```

---

## 🐛 Troubleshooting

### Service Not Showing in Eureka?

**Check:**
1. Eureka Server is running (http://localhost:8761)
2. Service has `@EnableDiscoveryClient` or `@EnableEurekaClient`
3. `eureka.client.service-url.defaultZone` is correct
4. No firewall blocking port 8761

### Service Shows as DOWN?

**Check:**
1. Health endpoint accessible: `/actuator/health`
2. Service actually running
3. Network connectivity

### Config Not Loading?

**Check:**
1. Config Server running (port 8888)
2. `spring.application.name` matches config file name
3. `spring.cloud.config.uri` pointing to correct URL

---

## 🎯 Production Best Practices

### 1️⃣ Multiple Eureka Instances
Run 2-3 Eureka servers for high availability

### 2️⃣ Secure Eureka Dashboard
Add Spring Security to Eureka Server

### 3️⃣ Git-based Config
Store configurations in Git repository:
```yaml
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/your-repo/configs
```

### 4️⃣ Encrypt Sensitive Data
```bash
# Encrypt password
curl http://localhost:8888/encrypt -d mysecretpassword
```

---

## 📊 Monitoring & Metrics

All services expose actuator endpoints:

```bash
# Health
curl http://localhost:8082/actuator/health

# Metrics
curl http://localhost:8082/actuator/metrics

# Environment
curl http://localhost:8082/actuator/env
```

---

## ✅ Success Checklist

- [ ] Eureka Server dashboard accessible
- [ ] All 6 services registered in Eureka
- [ ] All services showing as UP (green)
- [ ] Config Server returning configurations
- [ ] Health endpoints responding
- [ ] Service discovery working between services

---

## 🚀 Next Steps

1. **API Gateway**: Add Spring Cloud Gateway
2. **Distributed Tracing**: Add Sleuth + Zipkin
3. **Centralized Logging**: Add ELK Stack
4. **Security**: Add OAuth2 with Keycloak
5. **Rate Limiting**: Add resilience4j rate limiter

Happy Discovering! 🎉
