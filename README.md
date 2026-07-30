# MCP Code Review AI (Microservices Architecture)

An automated, AI-driven code review microservices platform built with **Spring Boot 3**, **Spring Cloud**, **Spring AI**, **Docker**, **Docker Compose**, and the **Model Context Protocol (MCP)**. This system leverages a local **Ollama** instance to analyze code, identify bugs, measure complexity, and suggest improvements.

## Microservices Architecture

- **`config-service`** (Port `8888`): Centralized Spring Cloud Config Server reading configuration from `config-repo`.
- **`eureka-server`** (Port `8761`): Service Discovery Server for dynamic registration and routing.
- **`code-tools-service`** (Port `8082`): Model Context Protocol (MCP) server exposing code analysis tools.
- **`ai-review-service`** (Port `8081`): AI review orchestrator connecting to Ollama and invoking MCP tools.
- **`gateway-service`** (Port `8080`): Public entry point with Resilience4j Circuit Breaker, Retry, and Routing.
- **`zipkin`** (Port `9411`): Distributed tracing server for end-to-end telemetry.

---

## Running with Docker Compose (Recommended)

### 1. Prerequisites
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) (with Docker Compose v2+)
- [Ollama](https://ollama.ai/) running on your host machine (with model pulled, e.g. `ollama run mistral`)

### 2. Launch All Microservices
Run the following command in the root project directory to build containers and start all services in dependency order:

```bash
docker compose up --build -d
```

### 3. Service Health & Dashboards
Once started, monitor services and health checks:

- **Gateway API**: `http://localhost:8080`
- **Eureka Dashboard**: `http://localhost:8761`
- **Zipkin Tracing UI**: `http://localhost:9411`
- **Config Server**: `http://localhost:8888/gateway-service/default`

To check container statuses:
```bash
docker compose ps
```

To view logs:
```bash
docker compose logs -f
```

To stop all services:
```bash
docker compose down
```

---

## Running Locally (Without Docker)

You can also run all services locally using the automated startup script:

1. Start Zipkin and all Spring Boot microservices in order:
   ```cmd
   run-all.bat
   ```

---

## Submitting Code Review Requests

Send a `POST` request to the Gateway:

```bash
curl -X POST http://localhost:8080/api/v1/reviews \
  -H "Content-Type: application/json" \
  -d '{
    "code": "public class Calculator { public int add(int a, int b) { return a + b; } }",
    "language": "java"
  }'
```

