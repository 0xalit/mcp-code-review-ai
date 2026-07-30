# MCP Code Review AI (Microservices)

An automated AI code review platform built with **Spring Boot 3**, **Spring Cloud**, **Spring AI**, and the **Model Context Protocol (MCP)**. This system utilizes a local **Ollama** LLM engine to perform deep code inspection, identify bugs, flag security risks, and provide actionable refactoring advice within a distributed microservices architecture.

---

##  Core Features

- **Automated AI Code Inspection:** Evaluates code snippets or full source files using local LLM models (e.g., Llama 3) via Spring AI abstractions.
- **Model Context Protocol (MCP) Integration:** Extends LLM capabilities by allowing AI workers to call structured MCP tools (`code-tools-service`) for enhanced context parsing.
- **Structured Output & Severity Ratings:** Returns strictly typed JSON responses (`ReviewResponse`, `Issue`) containing issue descriptions, code snippets, fix suggestions, and severity classifications (`HIGH`, `MEDIUM`, `LOW`).
- **Fail-Safe Fallbacks:** Handles downstream outages gracefully by returning structured fallback diagnostics without crashing caller workflows.

---

##  Microservices Architecture Breakdown

The platform is designed as a set of decoupled, specialized microservices managed under a parent Maven multi-module structure:

```
                          ┌───────────────────────┐
                          │   Client / HTTP API   │
                          └───────────┬───────────┘
                                      │
                                      ▼
                          ┌───────────────────────┐
                          │    gateway-service    │  (Port 8080)
                          │   (API Gateway Edge)  │
                          └───────────┬───────────┘
                                      │
                   ┌──────────────────┴──────────────────┐
                   │                                     │
                   ▼                                     ▼
        ┌─────────────────────┐               ┌─────────────────────┐
        │  ai-review-service  │ (Port 8081)   │  code-tools-service │ (Port 8082)
        │ (AI Worker & Prompts│               │     (MCP Tools)     │
        └──────────┬──────────┘               └─────────────────────┘
                   │
                   ▼
        ┌─────────────────────┐
        │     Ollama LLM      │ (Port 11434)
        └─────────────────────┘
```

| Service Module | Port | Description |
| :--- | :--- | :--- |
| **`gateway-service`** | `8080` | Public API Gateway. Handles request validation, edge routing, and Resilience4j circuit breaking. |
| **`ai-review-service`** | `8081` | Core AI Engine. Formats review prompts, connects to Ollama via Spring AI, and parses structured output. |
| **`code-tools-service`** | `8082` | MCP Tool Provider. Exposes tool definitions and repository inspection utilities to the AI worker. |
| **`config-service`** | `8888` | Spring Cloud Config Server. Centralizes YAML configurations for all services via `config-repo`. |
| **`eureka-server`** | `8761` | Netflix Eureka Service Discovery Server for dynamic registration and load balancing. |
| **`common-models`** | *N/A* | Shared domain DTO library (`ReviewRequest`, `ReviewResponse`, `Issue`) used across services. |
| **`zipkin`** | `9411` | Centralized distributed tracing server for visualization of cross-service spans. |

---

##  Architectural Capabilities & Microservice Patterns

### 1. Service Discovery (Eureka)
Services register with the **Eureka Discovery Server** (`eureka-server` at `http://localhost:8761`). Services discover each other dynamically using service IDs (e.g., `ai-review-service`), eliminating hardcoded hostnames and enabling seamless scaling.

### 2. Centralized Configuration
Environment settings, LLM models, and resilience thresholds are centralized in `config-repo` and served by **Spring Cloud Config Server** (`config-service` at `http://localhost:8888`). Microservices fetch their configuration dynamically upon startup.

### 3. API Gateway & Validation
The **`gateway-service`** acts as a single point of entry (`http://localhost:8080`). It enforces payload guardrails (such as payload size limits), validates request formatting, and routes traffic cleanly to internal microservices.

### 4. Inter-Service Communication via RestClient
Microservices communicate synchronously using Spring 6’s fluent **`RestClient`** equipped with Spring Cloud's `@LoadBalanced` annotation. Inter-service calls use logical service names registered in Eureka with automatic HTTP trace header propagation.

### 5. Resilience (Circuit Breakers, Retries & Fallbacks)
Powered by **Resilience4j** (`resilience4j-spring-boot3`):
- **Circuit Breaker (`@CircuitBreaker`):** Monitors failure rates and short-circuits calls to failing downstream services, executing custom fallback methods (`fallbackReview`).
- **Retry (`@Retry`):** Automatically retries transient network anomalies on connectivity failures while ignoring validation errors.
- **Bulkhead Isolation:** Limits concurrent requests to prevent cascading system saturation.

### 6. Distributed Tracing
Integrated with **Micrometer Tracing (Brave)** and **Zipkin** (`http://localhost:9411`). Every incoming request is stamped with a unique `traceId` and `spanId` propagated across service boundaries in HTTP headers and log statements, enabling end-to-end visibility into latency and bottlenecks.

---

##  Tech Stack

- **Core & Runtime:** Java 21, Spring Boot `3.2.5`
- **Microservices Framework:** Spring Cloud `2023.0.6` (Config Server, Eureka Server, LoadBalancer)
- **AI & MCP:** Spring AI `1.0.0-M7`, Model Context Protocol (MCP), Ollama (`llama3`)
- **Resilience & Observability:** Resilience4j, Micrometer Tracing (Brave), Zipkin Reporter, Spring Boot Actuator
- **Build & Infrastructure:** Maven, Docker, Docker Compose

---

##  Prerequisites

Ensure you have the following installed:

1. **Java 21 or higher**
2. **Maven 3.8+**
3. **Docker & Docker Compose**
4. **Ollama**: Download from [ollama.ai](https://ollama.ai/) and pull the default model:
   ```bash
   ollama run llama3
   ```

---

##  Getting Started

### Option 1: Run with Docker Compose (Recommended)

To start the entire microservices stack alongside Zipkin:

```bash
docker-compose up --build
```

### Option 2: Run Locally via Batch Script

On Windows environments, you can start all services in sequence using the provided batch script:

```cmd
run-all.bat
```

Or start the individual modules using Maven in order:
1. `config-service`
2. `eureka-server`
3. `code-tools-service`
4. `ai-review-service`
5. `gateway-service`

---

##  API Reference & Usage

### Submit Code for AI Review

**Endpoint:** `POST http://localhost:8080/api/v1/review`

**Sample Request (`curl`):**
```bash
curl -X POST http://localhost:8080/api/v1/review \
  -H "Content-Type: application/json" \
  -d '{
    "code": "public class Calculator { public int divide(int a, int b) { return a / b; } }"
  }'
```

**Sample JSON Response:**
```json
{
  "status": "COMPLETED",
  "issuesCount": 1,
  "issues": [
    {
      "severity": "HIGH",
      "description": "Potential ArithmeticException: Division by zero is not handled when parameter 'b' is 0."
    }
  ],
  "suggestions": [
    "Add a validation check for 'b == 0' before performing division."
  ],
  "summary": "The code works for normal inputs but lacks input validation for division by zero."
}
```

---

##  Dashboard & Observability Links

Once the system is running, you can access the operational dashboards:

- **Eureka Service Registry:** [http://localhost:8761](http://localhost:8761)
- **Config Server (Gateway Profile):** [http://localhost:8888/gateway-service/default](http://localhost:8888/gateway-service/default)
- **Zipkin Tracing UI:** [http://localhost:9411](http://localhost:9411)
- **Gateway Health Check:** [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)
