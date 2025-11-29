# FINAL CORRECTED PROMPT - Flash-Sale Ticketing Engine Architecture

## 🎯 CRITICAL: Use this EXACT prompt with Gemini Pro + Imagen

---

Create a professional system architecture diagram for a Flash-Sale Ticketing Engine with the following EXACT specifications:

## 📐 LAYOUT (Top to Bottom, 7 Layers)

### **LAYER 1: USERS (Top)**
- 5 user avatar icons in a horizontal row
- Below them: Blue box with text "10,000+ Concurrent Users"
- **ARROW 1**: 5 blue arrows going DOWN from users box
- Label on arrows: "① HTTP Requests"

---

### **LAYER 2: LOAD BALANCER**
- Single gray box: "Nginx Load Balancer"
- Port badge: "80"
- Icon: Network/distribution icon
- **ARROW 2**: From Load Balancer, draw 2 arrows going DOWN
  - One arrow to Ticket Service #1
  - One arrow to Ticket Service #2
- Label: "② Round-Robin Distribution"

---

### **LAYER 3: MICROSERVICES (3 boxes in a row)**

**LEFT BOX:**
- Green box: "Ticket Service #1"
- Subtitle: "(Instance 1)"
- Port: "8080"
- Icon: Shopping cart/ticket

**MIDDLE BOX:**
- Green box: "Ticket Service #2"
- Subtitle: "(Instance 2)"
- Port: "8081"
- Icon: Shopping cart/ticket

**RIGHT BOX:**
- Blue box: "Notification Service"
- Subtitle: "(Email Service)"
- Port: "8082"
- Icon: Bell/notification

---

### **LAYER 4: REDIS LOCK (CRITICAL - Make it PROMINENT)**

**POSITION:** Centered, BETWEEN Layer 3 and Layer 6 (NOT between Layer 3 and Layer 5)

- **LARGE RED BOX** (1.5x bigger than other components)
- Text: "Redis Distributed Lock"
- Port: "6379"
- Large lock icon 🔒
- Red glow effect around the box
- Label above: "⚠️ CRITICAL: Concurrency Control"
- Subtitle: "Prevents Race Conditions & Double-Booking"

**CRITICAL ARROWS FOR REDIS:**

**STEP 3 - ACQUIRE LOCK (Going TO Redis):**
- **Arrow A**: RED DASHED arrow from Ticket Service #1 → Redis
  - Label: "③ Acquire Lock (SETNX)"
  - Position: From bottom of Ticket Service #1 to top-left of Redis
  
- **Arrow B**: RED DASHED arrow from Ticket Service #2 → Redis
  - Label: "③ Acquire Lock (SETNX)"
  - Position: From bottom of Ticket Service #2 to top-right of Redis

**STEP 4 - LOCK RESPONSE (Coming FROM Redis):**
- **Arrow C**: GREEN SOLID arrow from Redis → Ticket Service #1
  - Label: "④ Lock Granted/Denied"
  - Position: From left side of Redis back to Ticket Service #1
  
- **Arrow D**: GREEN SOLID arrow from Redis → Ticket Service #2
  - Label: "④ Lock Granted/Denied"
  - Position: From right side of Redis back to Ticket Service #2

---

### **LAYER 5: KAFKA (Message Queue)**

**POSITION:** To the RIGHT of Redis (same horizontal level)

- Orange box: "Apache Kafka"
- Port: "9095"
- Icon: Stream/flow
- Label: "Event Streaming Platform"

**ARROWS FOR KAFKA:**

**STEP 6 - PUBLISH TO KAFKA:**
- **Arrow E**: ORANGE arrow from Ticket Service #1 → Kafka
  - Label: "⑥ Publish Event (JSON)"
  - Position: From right side of Ticket Service #1 to Kafka
  
- **Arrow F**: ORANGE arrow from Ticket Service #2 → Kafka
  - Label: "⑥ Publish Event (JSON)"
  - Position: From right side of Ticket Service #2 to Kafka

**STEP 7 - CONSUME FROM KAFKA:**
- **Arrow G**: BLUE arrow from Kafka → Notification Service
  - Label: "⑦ Consume Event"
  - Position: From Kafka to Notification Service

---

### **LAYER 6: DATABASES (2 Shard Groups)**

**POSITION:** Below Redis

**LEFT GROUP - Shard 1:**
- Purple box containing 2 sub-boxes:
  - "PostgreSQL Primary" - Port: 5433
  - "PostgreSQL Replica" - Port: 5434
- Curved arrow between them labeled "Master-Slave Replication"
- Group label: "Shard 1"

**RIGHT GROUP - Shard 2:**
- Purple box containing 2 sub-boxes:
  - "PostgreSQL Primary" - Port: 5435
  - "PostgreSQL Replica" - Port: 5436
- Curved arrow between them labeled "Master-Slave Replication"
- Group label: "Shard 2"

**CRITICAL ARROWS TO DATABASES:**

**STEP 5 - WRITE TO DATABASE (ONLY IF LOCK ACQUIRED):**
- **Arrow H**: PURPLE arrow from Ticket Service #1 → Shard 1 Primary
  - Label: "⑤ INSERT Ticket (if lock acquired)"
  - Position: From bottom of Ticket Service #1 to Shard 1 Primary
  - **IMPORTANT**: This arrow should go AROUND Redis (not through it)
  
- **Arrow I**: PURPLE arrow from Ticket Service #2 → Shard 2 Primary
  - Label: "⑤ INSERT Ticket (if lock acquired)"
  - Position: From bottom of Ticket Service #2 to Shard 2 Primary
  - **IMPORTANT**: This arrow should go AROUND Redis (not through it)

---

### **LAYER 7: EXTERNAL SERVICES**

**POSITION:** Bottom right

- Cyan box: "External Services"
- Icon: Email envelope
- **Arrow J**: CYAN arrow from Notification Service → External Services
  - Label: "⑧ Send Confirmation Email"

---

## 🎨 VISUAL SPECIFICATIONS

### **Colors (Exact Hex Codes):**
- Users box: #60A5FA (Light Blue)
- Load Balancer: #6B7280 (Gray)
- Ticket Services: #10B981 (Green)
- Notification Service: #3B82F6 (Blue)
- **Redis: #EF4444 (Bright Red) + RED GLOW EFFECT**
- Kafka: #F59E0B (Orange)
- Databases: #8B5CF6 (Purple)
- External Services: #06B6D4 (Cyan)
- Background: #FFFFFF (White)
- Text: #1F2937 (Dark Gray)

### **Arrow Specifications:**
- Users → LB: Blue solid, 3px
- LB → Services: Gray solid, 2px (split into 2)
- **Services → Redis: RED DASHED, 3px, with arrow heads**
- **Redis → Services: GREEN SOLID, 3px, with arrow heads**
- Services → Databases: Purple solid, 2px, **curved to avoid Redis**
- Services → Kafka: Orange solid, 2px
- Kafka → Notification: Blue solid, 2px
- Notification → Email: Cyan solid, 2px
- DB Replication: Purple dashed, 1px, curved

### **Component Sizes:**
- Standard components: 200px wide × 100px tall
- **Redis: 300px wide × 150px tall (LARGER)**
- Users box: 250px wide × 60px tall
- Database groups: 180px wide × 120px tall each

### **Typography:**
- Component names: 18px, bold, white text
- Port numbers: 14px, regular, white text
- Arrow labels: 12px, regular, dark gray
- Annotations: 10px, italic, gray

### **Spacing:**
- Vertical spacing between layers: 50px
- Horizontal spacing between components: 30px
- Padding inside components: 15px

---

## 📊 LEGEND (Bottom Left Corner)

Create a small box with:
- 🎯 Microservice (Green square)
- 🔒 Distributed Lock (Red square with glow)
- 📨 Message Queue (Orange square)
- 💾 Database (Purple square)
- ⚖️ Load Balancer (Gray square)
- 📧 External Service (Cyan square)

---

## 🔢 FLOW SEQUENCE SUMMARY

Display these numbered steps clearly:

1. **Users** send HTTP requests
2. **Load Balancer** distributes to Ticket Services
3. **Ticket Services** request lock from **Redis**
4. **Redis** grants or denies lock
5. **Ticket Services** write to **Databases** (only if locked)
6. **Ticket Services** publish events to **Kafka**
7. **Kafka** delivers events to **Notification Service**
8. **Notification Service** sends emails via **External Services**

---

## ⚠️ CRITICAL REQUIREMENTS

1. **Redis MUST be positioned BETWEEN Ticket Services and Databases**
2. **BOTH Ticket Services MUST have arrows TO and FROM Redis**
3. **Database arrows MUST curve AROUND Redis (not go through it)**
4. **Redis MUST be the LARGEST and most PROMINENT component**
5. **Redis MUST have a RED GLOW effect**
6. **All arrows MUST be clearly visible with labels**
7. **Arrow colors MUST match the specifications exactly**
8. **Numbered sequence (①-⑧) MUST be visible on arrows**

---

## 🎯 FINAL CHECKLIST

Before generating, verify:
- [ ] Redis is CENTERED and LARGE
- [ ] Redis has RED color with GLOW
- [ ] 2 RED arrows go FROM services TO Redis
- [ ] 2 GREEN arrows go FROM Redis TO services
- [ ] Database arrows CURVE around Redis
- [ ] All 10 arrows (A-J) are present
- [ ] All components have correct ports
- [ ] Legend is included
- [ ] Flow numbers (①-⑧) are visible

---

## 📝 EXAMPLE ARROW PATHS

```
Ticket Service #1:
  ↓ (gray) from Load Balancer
  ↓ (red dashed) to Redis [③]
  ← (green solid) from Redis [④]
  ↓ (purple, curved) to Shard 1 [⑤]
  → (orange) to Kafka [⑥]

Ticket Service #2:
  ↓ (gray) from Load Balancer
  ↓ (red dashed) to Redis [③]
  ← (green solid) from Redis [④]
  ↓ (purple, curved) to Shard 2 [⑤]
  → (orange) to Kafka [⑥]

Notification Service:
  ← (blue) from Kafka [⑦]
  ↓ (cyan) to External Services [⑧]
```

---

## 🚀 GENERATE THE IMAGE

Use this prompt EXACTLY as written. The result should be a professional, LinkedIn-worthy architecture diagram that clearly shows:
- The critical role of Redis in preventing race conditions
- The complete request flow with numbered steps
- Clean, modern design suitable for technical presentations

**Image specs:**
- Size: 1920×1080 pixels (landscape)
- Format: PNG
- Resolution: 300 DPI
- Style: Modern, flat design with subtle shadows

---

**This is the FINAL, CORRECT prompt. Generate the image now! 🎨**
