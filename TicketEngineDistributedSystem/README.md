# 🎫 Flash-Sale Ticketing Engine

نظام حجز تذاكر متقدم يحاكي أنظمة مثل Ticketmaster، مبني على **Microservices Architecture** مع تقنيات enterprise-grade.

```

## 🚀 Features

### ✅ Microservices Architecture
- **Ticket Service**: معالجة الحجوزات والتذاكر
- **Notification Service**: إرسال الإشعارات والإيميلات (Port 8082)

### ✅ Database Sharding
- توزيع البيانات على 2 Shards
- كل Shard له Primary + Replica للـ High Availability
- Routing Strategy بناءً على Event ID

### ✅ Distributed Locking
- Redis-based locking لمنع الـ Race Conditions
- ضمان عدم حجز نفس الكرسي مرتين

### ✅ Event-Driven Architecture
- Kafka للتواصل بين الـ Services
- Asynchronous notifications
- Scalable message processing

### ✅ Load Balancing
- Nginx كـ Reverse Proxy
- توزيع الطلبات على instances متعددة

### ✅ Email Notifications
- إيميلات HTML احترافية
- تأكيد الحجز تلقائياً
- SMTP integration

## 📁 Project Structure

```
├── ticket-service/          # خدمة التذاكر الرئيسية
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/flashsale/ticketing/
│   │       │       ├── controller/    # REST Controllers
│   │       │       ├── service/       # Business Logic
│   │       │       ├── repository/    # Data Access
│   │       │       ├── model/         # Entities
│   │       │       └── config/        # Configuration
│   │       └── resources/
│   │           └── application.properties
│   └── pom.xml
│
├── notification-service/    # خدمة الإشعارات
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/flashsale/notification/
│   │       │       ├── kafka/         # Kafka Consumers
│   │       │       ├── service/       # Email Service
│   │       │       └── model/         # Event Models
│   │       └── resources/
│   │           └── application.properties
│   ├── Dockerfile
│   ├── README.md
│   └── pom.xml
│
└── infra/                   # Infrastructure
    ├── docker-compose.yml   # All services
    ├── nginx.conf           # Load balancer config
    └── loadtesting.js       # Performance tests
```

## 🛠️ Tech Stack

| Component | Technology |
|-----------|-----------|
| **Backend** | Java 17, Spring Boot 3.3.0 |
| **Database** | PostgreSQL (Sharded) |
| **Caching** | Redis |
| **Message Queue** | Apache Kafka |
| **Load Balancer** | Nginx |
| **Email** | JavaMail (SMTP) |
| **Containerization** | Docker, Docker Compose |

## 🚀 Quick Start

### Prerequisites

- Docker & Docker Compose
- Java 17+
- Maven 3.9+

### 1️⃣ Start Infrastructure

```bash
cd infra
docker-compose up -d
```

هيشغل:
- ✅ PostgreSQL (2 Shards × 2 instances)
- ✅ Redis
- ✅ Kafka + Zookeeper
- ✅ Nginx
- ✅ Notification Service

### 2️⃣ Configure Email (Important!)

قبل تشغيل الـ Notification Service، حدث الإعدادات في:

**Option A: Environment Variables (Recommended)**

```bash
# في docker-compose.yml
environment:
  - SPRING_MAIL_USERNAME=your-email@gmail.com
  - SPRING_MAIL_PASSWORD=your-app-password
  - NOTIFICATION_EMAIL_FROM=your-email@gmail.com
```

**Option B: Application Properties**

عدل `notification-service/src/main/resources/application.properties`:

```properties
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
notification.email.from=your-email@gmail.com
```

📖 **للحصول على Gmail App Password**: راجع [notification-service/README.md](notification-service/README.md)

### 3️⃣ Build & Run Ticket Service

```bash
cd ticket-service
mvn clean package
mvn spring-boot:run
```

الخدمة ستعمل على: `http://localhost:8080`

### 4️⃣ Test the System

```bash
# حجز تذكرة
curl -X POST http://localhost:8080/api/tickets \
  -H "Content-Type: application/json" \
  -d '{
    "eventId": 101,
    "seatNumber": "A15",
    "userId": "user@example.com",
    "status": "CONFIRMED"
  }'
```

**Expected Flow:**
1. ✅ Ticket Service يحفظ التذكرة في الـ Database
2. ✅ Redis Lock يمنع الحجز المزدوج
3. ✅ Kafka يرسل رسالة للـ Notification Service
4. ✅ Notification Service يبعت إيميل تأكيد
5. ✅ المستخدم يستلم إيميل HTML احترافي

## 📊 API Endpoints

### Ticket Service

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/tickets` | حجز تذكرة جديدة |

**Request Body:**
```json
{
  "eventId": 101,
  "seatNumber": "A15",
  "userId": "user@example.com",
  "status": "CONFIRMED"
}
```

**Response:**
```json
{
  "id": 1,
  "eventId": 101,
  "seatNumber": "A15",
  "userId": "user@example.com",
  "status": "CONFIRMED"
}
```

## 🔍 Monitoring & Logs

### View Logs

```bash
# Ticket Service logs
cd ticket-service
mvn spring-boot:run

# Notification Service logs
docker logs -f notification-service

# Kafka logs
docker logs -f kafka

# Database logs
docker logs -f db-shard1-primary
```

### Check Kafka Topics

```bash
# List topics
docker exec -it kafka kafka-topics --list --bootstrap-server localhost:9092

# Consume messages
docker exec -it kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic ticket-sold-topic \
  --from-beginning
```

## 🧪 Load Testing

```bash
cd infra
node loadtesting.js
```

## 🏗️ Key Design Patterns

### 1. Database Sharding
```java
// Routing based on Event ID
String targetShard = (eventId % 2 == 0) ? "SHARD_2" : "SHARD_1";
DbContext.setDbType(targetShard);
```

### 2. Distributed Locking
```java
String lockKey = "lock:event:" + eventId + ":seat:" + seatNumber;
boolean isLocked = redisLockService.acquireLock(lockKey, 10);
```

### 3. Event-Driven Communication
```java
// Producer (Ticket Service)
kafkaTemplate.send("ticket-sold-topic", jsonMessage);

// Consumer (Notification Service)
@KafkaListener(topics = "ticket-sold-topic")
public void handleTicketSoldEvent(String message) { ... }
```

## 📈 Scalability

- **Horizontal Scaling**: أضف instances من الـ Services
- **Database Sharding**: وزع البيانات على shards أكتر
- **Kafka Partitioning**: زود الـ throughput
- **Redis Clustering**: للـ High Availability

## 🔐 Security Considerations

- ⚠️ استخدم HTTPS في Production
- ⚠️ لا تحفظ الـ credentials في الكود
- ⚠️ استخدم Secrets Management (Vault, AWS Secrets Manager)
- ⚠️ فعّل Authentication & Authorization

## 🐛 Troubleshooting

### Database Connection Issues
```bash
# Check if databases are running
docker ps | grep postgres

# Test connection
docker exec -it db-shard1-primary psql -U admin -d ticket_db_shard1
```

### Kafka Not Working
```bash
# Restart Kafka
docker-compose restart kafka zookeeper

# Check Kafka logs
docker logs kafka
```

### Email Not Sending
- تأكد من صحة الـ SMTP credentials
- استخدم App Password بدلاً من كلمة المرور العادية
- راجع [notification-service/README.md](notification-service/README.md)

## 📚 Documentation

- [Notification Service README](notification-service/README.md)
- [Implementation Plan](../brain/implementation_plan.md)

## 🎯 Future Enhancements

- [ ] User Authentication (JWT)
- [ ] Event Service (إدارة الأحداث)
- [ ] Payment Integration
- [ ] Admin Dashboard
- [ ] Monitoring (Prometheus + Grafana)
- [ ] API Documentation (Swagger)
- [ ] Circuit Breaker Pattern
- [ ] Rate Limiting

## 👨‍💻 Development

```bash
# Build all services
mvn clean package

# Run tests
mvn test

# Format code
mvn spotless:apply
```

## 📝 License

This is a learning project for demonstrating enterprise-grade microservices architecture.

---

Made with ❤️ for learning Microservices, Kafka, Redis, and Distributed Systems
