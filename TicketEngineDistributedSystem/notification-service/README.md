# Notification Service 📧

Microservice مسؤول عن استقبال أحداث Kafka وإرسال إشعارات البريد الإلكتروني للمستخدمين.

## 🎯 الوظائف الأساسية

- **Kafka Consumer**: الاستماع لـ topic: `ticket-sold-topic`
- **Email Service**: إرسال إيميلات تأكيد الحجز بتصميم HTML احترافي
- **Event Processing**: معالجة رسائل JSON والنصوص البسيطة

## 🔧 المتطلبات

### Email Configuration

قبل تشغيل الخدمة، يجب تحديث الإعدادات التالية في `application.properties`:

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=YOUR_EMAIL@gmail.com
spring.mail.password=YOUR_APP_PASSWORD
notification.email.from=YOUR_EMAIL@gmail.com
```

### للحصول على App Password من Gmail:

1. اذهب إلى [Google Account Security](https://myaccount.google.com/security)
2. فعّل **2-Step Verification**
3. اذهب إلى **App Passwords**
4. اختر **Mail** و **Other (Custom name)**
5. انسخ الـ 16-digit password

## 🚀 التشغيل

### Local Development

```bash
# Build the project
mvn clean package

# Run the service
mvn spring-boot:run
```

الخدمة ستعمل على: `http://localhost:8082`

### Docker

```bash
# Build Docker image
docker build -t notification-service .

# Run container
docker run -p 8081:8081 \
  -e SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:29092 \
  -e SPRING_MAIL_USERNAME=your-email@gmail.com \
  -e SPRING_MAIL_PASSWORD=your-app-password \
  notification-service
```

### Docker Compose

```bash
cd infra
docker-compose up notification-service
```

## 📋 Message Format

الخدمة تدعم نوعين من الرسائل:

### JSON Format (Recommended)

```json
{
  "ticketId": 123,
  "eventId": 456,
  "seatNumber": "A15",
  "userId": "user@example.com",
  "status": "CONFIRMED",
  "timestamp": "2025-11-28T03:20:00"
}
```

### Plain Text Format

```
Ticket Booked! ID: 123, User: user@example.com
```

## 📧 Email Template

الإيميل يحتوي على:
- ✅ تصميم HTML احترافي
- 🎨 Gradient header
- 📊 معلومات التذكرة كاملة
- 🎫 Ticket ID, Event ID, Seat Number
- 📅 Booking timestamp

## 🔍 Logging

الخدمة توفر logs تفصيلية:
- 🔔 استقبال رسائل Kafka
- ✅ نجاح إرسال الإيميل
- ❌ أخطاء المعالجة

## 🏗️ Architecture

```
Kafka (ticket-sold-topic)
    ↓
TicketEventConsumer
    ↓
EmailService
    ↓
JavaMailSender (SMTP)
    ↓
User's Email Inbox
```

## 📦 Dependencies

- Spring Boot 3.3.0
- Spring Kafka
- Spring Boot Mail (JavaMail)
- Lombok
- Jackson (JSON processing)

## 🔐 Security Notes

- ⚠️ لا تضع الـ email credentials في الكود
- ✅ استخدم environment variables
- ✅ استخدم App Passwords بدلاً من كلمة المرور الأساسية
- ✅ فعّل TLS/STARTTLS للأمان

## 🧪 Testing

```bash
# Run tests
mvn test

# Send test message to Kafka
kafka-console-producer --broker-list localhost:9095 --topic ticket-sold-topic
```

ثم أدخل رسالة JSON للاختبار.

## 📝 Environment Variables

| Variable | Description | Example |
|----------|-------------|---------|
| `SPRING_KAFKA_BOOTSTRAP_SERVERS` | Kafka server address | `localhost:9095` |
| `SPRING_MAIL_HOST` | SMTP server | `smtp.gmail.com` |
| `SPRING_MAIL_PORT` | SMTP port | `587` |
| `SPRING_MAIL_USERNAME` | Email username | `your-email@gmail.com` |
| `SPRING_MAIL_PASSWORD` | Email password/app password | `your-app-password` |
| `NOTIFICATION_EMAIL_FROM` | From email address | `your-email@gmail.com` |

## 🐛 Troubleshooting

### Email not sending?
- تأكد من صحة الـ SMTP credentials
- تأكد من تفعيل "Less secure app access" أو استخدام App Password
- تحقق من الـ logs للأخطاء

### Kafka connection failed?
- تأكد من أن Kafka يعمل: `docker ps | grep kafka`
- تحقق من الـ bootstrap servers configuration

### Messages not received?
- تأكد من أن الـ topic موجود: `kafka-topics --list --bootstrap-server localhost:9095`
- تحقق من الـ consumer group ID

---

Made with ❤️ for Flash-Sale Ticketing System
