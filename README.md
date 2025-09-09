📌 Overview

This project implements an API Rate Limiting System using Spring Boot and MongoDB.
It enforces fair usage of APIs by restricting requests based on configurable rules and provides monitoring & analytics endpoints.

⚡ Features Implemented

Sliding Window (Per-Minute Limits): Restricts requests per API key within the last minute.

Fixed Window (Daily Quotas): Enforces daily request caps per API key.

Burst Handling: Allows brief request spikes above normal limits.

Endpoint-Specific Limits: Different rate limits for different API endpoints.

IP-Based Blocking: Temporarily blocks abusive IPs.

Monitoring & Analytics APIs:

/api/health → System stats (requests/min, uptime, denied requests, etc.)

/api/violations → Recent violations with reason and timestamp

🛠 Tech Stack

Java 17

Spring Boot (REST APIs, validation)

MongoDB (API keys, request logs, violations)

Maven (build tool)

Docker (optional for MongoDB)

📂 Project Structure
src/main/java/com/rate_limiter
│
├── controller         # REST controllers (RateLimit, Analytics, API Keys)
├── service            # Business logic (rate limiting, analytics)
├── model              # MongoDB entities (ApiKey, RequestLog, DTOs)
├── repository         # Mongo repositories
└── RateLimiterApplication.java  # Entry point

🚀 Getting Started
✅ Prerequisites

Java 17+

Maven

MongoDB (local or Docker)

▶️ Run MongoDB with Docker
docker run --name mongo -d -p 27017:27017 mongo

▶️ Build & Run Backend
mvn clean install
mvn spring-boot:run


The backend will start at:

http://localhost:8080

📡 API Endpoints
🔑 API Key Management

POST /api/keys → Create API Key

GET /api/keys → List all API Keys

⚖️ Rate Limiting

POST /api/check-limit?endpoint=/api/test&ip=192.168.1.1
Header: X-API-KEY: <your-api-key>
Response:

{
  "allowed": true,
  "remaining": 998,
  "limit": 1000
}

📊 Monitoring & Analytics

GET /api/health → Live system stats

GET /api/violations?limit=10&appName=MyApp → Recent violations

🧪 Testing with cURL
curl --location --request POST 'http://localhost:8080/api/check-limit?endpoint=%2Fapi%2Ftest&ip=192.168.1.1' \
--header 'X-API-KEY: <your-api-key>'

🚀 Future Enhancements

Redis integration for faster request counting

JWT-based authentication for API key management

Advanced alerting for violations
