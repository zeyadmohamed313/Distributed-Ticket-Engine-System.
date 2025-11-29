# ⚙️ Email Configuration Guide

## 📧 Gmail Setup (Recommended)

### Step 1: Enable 2-Step Verification

1. اذهب إلى [Google Account Security](https://myaccount.google.com/security)
2. في قسم "Signing in to Google"، اختر **2-Step Verification**
3. اتبع الخطوات لتفعيله

### Step 2: Generate App Password

1. بعد تفعيل 2-Step Verification، ارجع لـ [Security Settings](https://myaccount.google.com/security)
2. اختر **App passwords** (في قسم "Signing in to Google")
3. اختر:
   - **App**: Mail
   - **Device**: Other (Custom name) - اكتب "Flash-Sale Ticketing"
4. اضغط **Generate**
5. انسخ الـ 16-digit password (مثال: `abcd efgh ijkl mnop`)

### Step 3: Update Configuration

#### Option A: Docker Compose (Recommended)

عدل `infra/docker-compose.yml`:

```yaml
notification-service:
  environment:
    - SPRING_MAIL_USERNAME=your-email@gmail.com
    - SPRING_MAIL_PASSWORD=abcdefghijklmnop  # بدون مسافات
    - NOTIFICATION_EMAIL_FROM=your-email@gmail.com
```

#### Option B: Application Properties

عدل `notification-service/src/main/resources/application.properties`:

```properties
spring.mail.username=your-email@gmail.com
spring.mail.password=abcdefghijklmnop
notification.email.from=your-email@gmail.com
```

#### Option C: Environment Variables (Production)

```bash
export SPRING_MAIL_USERNAME=your-email@gmail.com
export SPRING_MAIL_PASSWORD=abcdefghijklmnop
export NOTIFICATION_EMAIL_FROM=your-email@gmail.com
```

---

## 📧 Other Email Providers

### Outlook/Hotmail

```properties
spring.mail.host=smtp-mail.outlook.com
spring.mail.port=587
spring.mail.username=your-email@outlook.com
spring.mail.password=your-password
```

### Yahoo Mail

```properties
spring.mail.host=smtp.mail.yahoo.com
spring.mail.port=587
spring.mail.username=your-email@yahoo.com
spring.mail.password=your-app-password
```

### Custom SMTP Server

```properties
spring.mail.host=smtp.your-domain.com
spring.mail.port=587
spring.mail.username=your-username
spring.mail.password=your-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

---

## 🧪 Testing Email Configuration

### Test 1: Send Test Email via Kafka

```bash
# Start Kafka console producer
docker exec -it kafka kafka-console-producer \
  --broker-list localhost:9092 \
  --topic ticket-sold-topic

# Paste this JSON and press Enter
{"ticketId":999,"eventId":101,"seatNumber":"TEST-A1","userId":"your-email@gmail.com","status":"CONFIRMED","timestamp":"2025-11-28T03:00:00"}
```

### Test 2: Check Notification Service Logs

```bash
docker logs -f notification-service
```

يجب أن تشوف:
```
🔔 Kafka Message Received!
📩 Raw Message: {...}
✅ Parsed Event: Ticket ID=999, Event ID=101, Seat=TEST-A1
📧 Sending email to: your-email@gmail.com
✅ Email sent successfully to: your-email@gmail.com
```

### Test 3: Check Your Inbox

- تحقق من الـ Inbox
- لو مش موجود، تحقق من **Spam/Junk**

---

## 🐛 Common Issues

### ❌ "Authentication failed"

**السبب**: كلمة مرور خاطئة أو 2-Step Verification مش مفعل

**الحل**:
1. تأكد من تفعيل 2-Step Verification
2. استخدم App Password مش كلمة المرور العادية
3. تأكد إنك نسخت الـ password بدون مسافات

### ❌ "Could not connect to SMTP host"

**السبب**: الـ port أو الـ host غلط

**الحل**:
```properties
# Gmail
spring.mail.host=smtp.gmail.com
spring.mail.port=587

# تأكد من تفعيل TLS
spring.mail.properties.mail.smtp.starttls.enable=true
```

### ❌ "Mail server connection failed"

**السبب**: Firewall أو Network issue

**الحل**:
1. تأكد من الاتصال بالإنترنت
2. تحقق من الـ Firewall settings
3. جرب port 465 بدلاً من 587:
```properties
spring.mail.port=465
spring.mail.properties.mail.smtp.ssl.enable=true
```

### ❌ Email goes to Spam

**الحل**:
1. أضف الإيميل للـ Contacts
2. استخدم domain email بدلاً من Gmail في Production
3. استخدم SPF/DKIM records

---

## 🔐 Security Best Practices

### ✅ DO:
- استخدم App Passwords
- استخدم Environment Variables
- استخدم Secrets Management في Production
- فعّل TLS/STARTTLS

### ❌ DON'T:
- تحط الـ password في الكود
- تشارك الـ credentials على Git
- تستخدم كلمة المرور الأساسية
- تعطل الـ TLS

---

## 📝 Configuration Checklist

- [ ] Gmail 2-Step Verification مفعل
- [ ] App Password متولد
- [ ] `SPRING_MAIL_USERNAME` محدث
- [ ] `SPRING_MAIL_PASSWORD` محدث (بدون مسافات)
- [ ] `NOTIFICATION_EMAIL_FROM` محدث
- [ ] الـ SMTP host و port صحيحين
- [ ] TLS/STARTTLS مفعل
- [ ] اختبرت إرسال إيميل
- [ ] الإيميل وصل (تحقق من Spam)

---

## 🆘 Need Help?

إذا واجهت مشاكل:

1. تحقق من الـ logs: `docker logs notification-service`
2. تأكد من الـ Kafka يشتغل: `docker ps | grep kafka`
3. جرب إرسال test message
4. راجع [notification-service/README.md](../notification-service/README.md)

---

Made with ❤️ for Flash-Sale Ticketing System
