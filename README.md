<img width="2816" height="1536" alt="System Design" src="https://github.com/user-attachments/assets/073b09ac-0a98-47bc-bd35-be210f7046fc" />

🚀Flash-Sale Ticketing Engine Simulation

A high-concurrency distributed system simulation designed to handle race conditions and massive traffic surges during flash sales. This project implements core concepts from "Designing Data-Intensive Applications", focusing on Scalability, Consistency, and Reliability.

(Please replace path/to/your/architecture-diagram.png with the actual path of the image in your repo)

📖 Overview

Building a standard CRUD application is simple, but handling 50+ users trying to book the exact same seat in the exact same millisecond is a true engineering challenge.

This project simulates a ticketing engine where traffic is distributed across multiple application nodes, data is sharded across multiple databases, and consistency is guaranteed using distributed locks—all fully containerized.

🏗️ Key Architectural Patterns

1. Concurrency Control (Distributed Locking)

To prevent the Double-Booking Problem, I implemented a distributed lock using Redis.

Mechanism: Uses SETNX (Set if Not Exists) to ensure atomic transactions.

Safety: Implemented a TTL (Time-To-Live) of 10 seconds to prevent "Zombie Locks" if a service crashes.

Result: Only one user succeeds in booking the seat; others receive a "Seat Busy" exception immediately.

2. Database Sharding (Horizontal Scaling)

To overcome the bottleneck of a single database instance, data is partitioned across two PostgreSQL shards based on the Event ID.

Routing Logic: Implemented AbstractRoutingDataSource with ThreadLocal context to dynamically route queries to the correct physical shard (Shard 1 for odd IDs, Shard 2 for even IDs).

3. Read/Write Splitting (Replication)

To enhance throughput, Master-Slave Replication is enabled for each shard.

Write Operations: Routed to the Primary database.

Read Operations: Routed to the Replica database.

Tech Detail: Utilized LazyConnectionDataSourceProxy to defer connection acquisition, ensuring the routing logic executes before the transaction binds to a specific database connection.

4. Load Balancing

Traffic is distributed using Nginx acting as a reverse proxy.

Strategy: Configured with least_conn algorithm to route traffic to the least busy application instance, ensuring optimal resource utilization under heavy load.

5. Event-Driven Architecture

The system is decoupled using Apache Kafka.

Booking Flow: The booking is persisted synchronously for consistency.

Notification Flow: An event is published to Kafka, and a separate Notification Microservice consumes it to send confirmation emails via Gmail SMTP asynchronously.

🛠️ Tech Stack

Language: Java 17

Framework: Spring Boot 3

Databases: PostgreSQL (4 instances: 2 Primaries, 2 Replicas)

Caching/Locking: Redis

Messaging: Apache Kafka & Zookeeper

Load Balancer: Nginx

Containerization: Docker & Docker Compose

Testing: K6 (for Stress/Load Testing)

🚀 Getting Started

Prerequisites

Docker & Docker Compose

Java 17 (JDK)

Maven

1. Start the Infrastructure

Run the following command to spin up Redis, Kafka, Zookeeper, Nginx, and the Databases:

cd infra
docker-compose -f docker-compose.yml up -d


2. Run the Services

You need to run two instances of the Ticket Service (for load balancing) and one instance of the Notification Service.

Terminal 1 (Notification Service - Port 8082):

cd notification-service
mvn spring-boot:run


Terminal 2 (Ticket Service Node 1 - Port 8080):

cd ticket-service
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=8080"


Terminal 3 (Ticket Service Node 2 - Port 8081):

cd ticket-service
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=8081"


🧪 Testing & Verification

Load Testing with K6

A load-test.js script is included to simulate 50 concurrent users attacking the system.

Install K6.

Run the script:

k6 run loadtesting.js


What to Observe in Logs?

Check the logs of the Ticket Service nodes. You will see the system handling the load dynamically:

Sharding: >>> 🔀 Switching to [SHARD_2] for Event ID: [102]

Locking: >>> 🔒 Lock Acquired: lock:event:102:seat:A1

Contention: >>> ⛔ Lock Contention: Seat is busy.

Replication (On Read): >>> 📖 Reading from: SHARD_2_REPLICA

📂 Project Structure

├── infra/                  # Docker Compose & Nginx Config
├── ticket-service/         # Core Booking Logic (Producer)
│   ├── config/             # Datasource Routing & ThreadLocal
│   ├── service/            # Redis Locking & Transaction Management
│   └── controller/         # REST Endpoints
└── notification-service/   # Email Logic (Consumer)
    └── kafka/              # Kafka 
