# Optimized Prompt for Flash-Sale Ticketing Architecture Diagram

## Use this prompt with Gemini Pro + Imagen (Nana Banana)

---

Create a stunning, professional system architecture diagram for a Flash-Sale Ticketing Engine with CORRECT arrow flow:

**CRITICAL: ARROW FLOW MUST BE EXACT**

**VISUAL STYLE:**
- Ultra-modern, clean design with white background
- Flat design with subtle shadows
- Professional color palette
- Rounded rectangles (16px radius)
- Clear visual hierarchy top to bottom
- Sans-serif typography (Inter/Roboto style)
- Smooth arrows with clear direction
- Modern icons for each component

**LAYOUT - VERTICAL FLOW (Top to Bottom):**

**LAYER 1 - USERS:**
- 4 user icons in a row
- Label: "10,000+ Concurrent Users"
- Color: Light blue (#60A5FA)
- **ARROWS DOWN** labeled "① HTTP Requests"

**LAYER 2 - LOAD BALANCER:**
- Box: "Nginx Load Balancer"
- Port: "80"
- Color: Gray (#6B7280)
- Icon: Network/distribution
- **ARROWS SPLIT DOWN** to BOTH Ticket Services
- Label: "② Round-Robin Distribution"

**LAYER 3 - MICROSERVICES (3 boxes horizontal):**

LEFT: "Ticket Service #1"
- Port: "8080"
- Color: Green (#10B981)
- Icon: Ticket

MIDDLE: "Ticket Service #2"
- Port: "8081"
- Color: Green (#10B981)
- Icon: Ticket

RIGHT: "Notification Service"
- Port: "8082"
- Color: Blue (#3B82F6)
- Icon: Bell/Email

**LAYER 4 - REDIS LOCK (CRITICAL - CENTERED):**
- Large box: "Redis Distributed Lock"
- Port: "6379"
- Color: BRIGHT RED (#EF4444) with glow
- Large lock icon 🔒
- Size: 1.5x larger than other components
- Label: "⚠️ CRITICAL: Concurrency Control"

**CRITICAL ARROWS FOR REDIS:**

**FROM Ticket Services TO Redis:**
- Arrow from Ticket Service #1 → Redis (RED DASHED)
  - Label: "③ Acquire Lock (SETNX)"
- Arrow from Ticket Service #2 → Redis (RED DASHED)
  - Label: "③ Acquire Lock (SETNX)"

**FROM Redis BACK TO Ticket Services:**
- Arrow from Redis → Ticket Service #1 (GREEN SOLID)
  - Label: "④ Lock Granted/Denied"
- Arrow from Redis → Ticket Service #2 (GREEN SOLID)
  - Label: "④ Lock Granted/Denied"

**LAYER 5 - KAFKA (CENTERED):**
- Box: "Apache Kafka"
- Port: "9095"
- Color: Orange (#F59E0B)
- Icon: Stream

**ARROWS FOR KAFKA:**
- Arrow from Ticket Service #1 → Kafka (ORANGE)
  - Label: "⑥ Publish Event (JSON)"
- Arrow from Ticket Service #2 → Kafka (ORANGE)
  - Label: "⑥ Publish Event (JSON)"
- Arrow from Kafka → Notification Service (BLUE)
  - Label: "⑦ Consume Event"

**LAYER 6 - DATABASES (2 groups side by side):**

LEFT GROUP - "Shard 1":
- "PostgreSQL Primary" (Port 5433) - Purple (#8B5CF6)
- "PostgreSQL Replica" (Port 5434) - Purple (#8B5CF6)
- Curved arrow between them: "Replication"

RIGHT GROUP - "Shard 2":
- "PostgreSQL Primary" (Port 5435) - Purple (#7C3AED)
- "PostgreSQL Replica" (Port 5436) - Purple (#7C3AED)
- Curved arrow between them: "Replication"

**CRITICAL ARROWS TO DATABASES:**
- Arrow from Ticket Service #1 → Shard 1 Primary (PURPLE)
  - Label: "⑤ INSERT Ticket (if lock acquired)"
- Arrow from Ticket Service #2 → Shard 2 Primary (PURPLE)
  - Label: "⑤ INSERT Ticket (if lock acquired)"

**LAYER 7 - EXTERNAL SERVICES:**
- Email icon (envelope)
- Arrow from Notification Service → Email (CYAN)
  - Label: "⑧ Send Confirmation Email"

**COMPLETE FLOW SEQUENCE (NUMBERED):**
1. Users → Load Balancer
2. Load Balancer → Ticket Services (split)
3. **BOTH Ticket Services → Redis (Acquire Lock)**
4. **Redis → BOTH Ticket Services (Lock Response)**
5. Ticket Services → Databases (if locked)
6. Ticket Services → Kafka
7. Kafka → Notification Service
8. Notification Service → Email

**ARROW SPECIFICATIONS:**
- Users to LB: Blue solid, thick (3px)
- LB to Services: Gray solid, medium (2px), split into 2
- **Services to Redis: RED DASHED, thick (3px) - BOTH services**
- **Redis to Services: GREEN SOLID, thick (3px) - BOTH services**
- Services to Databases: Purple solid, medium (2px)
- Services to Kafka: Orange solid, medium (2px)
- Kafka to Notification: Blue solid, medium (2px)
- Notification to Email: Cyan solid, medium (2px)
- Database Replication: Purple curved, dashed (1px)

**LEGEND (Bottom Left):**
- 🎯 Microservice (Green)
- 🔒 Distributed Lock (Red)
- 📨 Message Queue (Orange)
- 💾 Database (Purple)
- ⚖️ Load Balancer (Gray)
- 📧 External Service (Cyan)

**ANNOTATIONS:**
- Near Redis: "Critical Path - Zero Double-Booking"
- Near Kafka: "Async Event Processing"
- Near Databases: "Horizontal Partitioning"
- Near Load Balancer: "High Availability"

**TECHNICAL SPECS:**
- Size: 1920x1080 landscape
- Resolution: 300 DPI
- Format: PNG
- Spacing: 40px between layers
- Component sizes: 
  - Standard: 200x100px
  - Redis: 300x150px (LARGER)
- Fonts:
  - Component names: 18px bold
  - Ports: 14px
  - Labels: 12px
  - Annotations: 10px

**COLOR PALETTE:**
- Users: #60A5FA
- Load Balancer: #6B7280
- Ticket Services: #10B981
- Notification: #3B82F6
- Redis: #EF4444 (WITH GLOW)
- Kafka: #F59E0B
- Databases: #8B5CF6
- Email: #06B6D4
- Background: #FFFFFF
- Text: #1F2937

**SPECIAL EMPHASIS:**
- Redis is the LARGEST component
- Redis has RED GLOW effect
- Arrows to/from Redis are THICKEST (3px)
- Redis has large lock icon 🔒
- Warning icon ⚠️ near Redis

**STYLE:**
- Similar to AWS/GCP architecture diagrams
- Clean, modern, professional
- Suitable for LinkedIn/tech presentations

---

## SIMPLIFIED VERSION (if above is too complex):

Create a professional system architecture diagram:

**Components (top to bottom):**
1. Users → Nginx LB (port 80)
2. Two Ticket Services (8080, 8081) + Notification (8082)
3. **Redis Lock (RED, LARGE, 6379)** - centered
4. Kafka (orange, 9095)
5. Two DB Shards (purple): Shard1 (5433/5434), Shard2 (5435/5436)

**CRITICAL ARROW FLOW:**
- Users → LB → **BOTH** Ticket Services
- **BOTH Ticket Services → Redis** (red dashed, "Acquire Lock")
- **Redis → BOTH Ticket Services** (green solid, "Lock Response")
- Ticket Service #1 → Shard 1 (purple, "INSERT if locked")
- Ticket Service #2 → Shard 2 (purple, "INSERT if locked")
- **BOTH Ticket Services → Kafka** (orange)
- Kafka → Notification Service (blue)
- Notification → Email (cyan)

**Style:**
- Modern, clean, white background
- Rounded rectangles with shadows
- Numbered flow (1-8)
- Redis is LARGE and RED with glow
- ALL arrows must be clearly visible
- Include legend and port numbers

---

## KEY FIXES FROM PREVIOUS VERSION:

1. ✅ **BOTH Ticket Services** now have arrows TO Redis
2. ✅ **Redis has return arrows** TO both Ticket Services
3. ✅ Arrow from Ticket Service #2 goes to **Redis FIRST**, not directly to database
4. ✅ All arrows are numbered in sequence (1-8)
5. ✅ Arrow colors match the flow (red for locking, green for response)
6. ✅ Notification Service has clear arrow to External Email

---

**This prompt ensures the CORRECT flow showing Redis as the critical gatekeeper! 🔒**

---

## Alternative Prompt (Simplified Version)

If the above is too detailed, use this concise version:

---

Create a professional system architecture diagram for a Flash-Sale Ticketing Engine:

**Components (Top to Bottom):**
1. Users (blue) → Nginx Load Balancer (gray, port 80)
2. Two Ticket Services (green, ports 8080/8081) + Notification Service (blue, port 8082)
3. **Redis Lock (RED, PROMINENT, port 6379)** with lock icon - positioned between services and database
4. Apache Kafka (orange, port 9095)
5. Two PostgreSQL Shards (purple): Shard1 (5433/5434), Shard2 (5435/5436) with replication

**Flow:**
- Users → LB → Ticket Services
- Ticket Services → **Redis (Acquire Lock)** → Ticket Services (Lock Response)
- Ticket Services → Databases (if locked)
- Ticket Services → Kafka → Notification Service → Email

**Style:**
- Modern, clean, white background
- Rounded rectangles with shadows
- Numbered arrows showing flow (1-8)
- Make Redis LARGE and RED with glow effect
- Include legend and port numbers
- Professional color scheme

---

## Tips for Best Results:

1. **Use the detailed prompt first** - it gives more control
2. **If output is too complex**, use the simplified version
3. **Iterate**: Generate 2-3 versions and pick the best
4. **Adjust colors** if needed in post-processing
5. **Add text manually** if AI misses some labels

## Expected Output:
A stunning, LinkedIn-worthy architecture diagram that clearly shows:
- The critical role of Redis in preventing race conditions
- The complete request flow with numbered steps
- Professional appearance suitable for tech presentations
- Clear visual hierarchy and component relationships

---

**Good luck! This should generate an amazing diagram! 🚀**
