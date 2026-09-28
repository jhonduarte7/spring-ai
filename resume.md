# Project Summary & Architecture Guide: `spring-ai`

Welcome to the **`spring-ai`** project documentation. This document serves as a comprehensive briefing and reference for any developer, technical lead, or autonomous agent reviewing or contributing to this codebase.

---

## 1. Executive Summary

- **Project Name**: `spring-ai`
- **Group ID**: `com.venefast.springboot`
- **Artifact ID**: `spring-ai`
- **Base Package**: `com.venefast.springboot.springai.app`
- **Spring Boot Version**: **4.1.0**
- **Java Version**: **Java 25 (LTS)**
- **Spring AI Version**: **2.0.1** (BOM-managed via `spring-ai-bom`)
- **Active AI Model**: **Ollama `qwen3:1.7b`** running on `http://localhost:11434`
- **Application Port**: `8090`
- **In-Memory Database**: H2 Database (`jdbc:h2:mem:spring_ai_db`) with Web Console enabled at `/h2-console`
- **Build System**: Apache Maven with Maven Wrapper (`mvnw`, `mvnw.cmd`)

---

## 2. Core Capabilities & AI Features

The application integrates Spring Boot 4.1 with Spring AI 2.0.1, communicating with a local Ollama instance running the `qwen3:1.7b` model:

1. **Lightweight & Direct Text Generation (Stateless GET)**:
   - Quick GET endpoints that accept query parameters, invoke the Spring AI `ChatClient` inside the service layer, and return raw plain-text completions with minimal overhead.
2. **System Instruction & Multilingual Personas**:
   - Demonstration of Spring AI's fluent `.system(...)` and `.user(...)` prompt chaining (e.g. enforcing single-line French answers, persona conditioning, and personalized greetings).
3. **Interaction Persistence & Auditing (Stateful POST)**:
   - Full persistence workflow: validates user input, prompts the model, saves the question, answer, model name, and audit timestamps (`createdAt`, `updatedAt`) into an H2 database via Spring Data JPA.
4. **History & Entity Lookup**:
   - Retrieve the 10 most recent persistent AI interactions or inspect a specific interaction by its unique database ID.
5. **Centralized Exception Handling**:
   - Uniform REST API error structures for missing resources (`404 Not Found`), validation failures (`400 Bad Request`), and uncaught exceptions (`500 Internal Server Error`).

---

## 3. Layered Architecture & Separation of Concerns

The project strictly follows the enterprise layered architecture standards defined in the project's workspace skills:

```
src/main/java/com/venefast/springboot/springai/app/
├── SpringAiApplication.java                     # Main Spring Boot application bootstrap
├── config/
│   └── AiConfig.java                            # Spring AI ChatClient Bean definition
├── controllers/
│   └── AiInteractionController.java             # REST controller exposing endpoints under /api/v1/ai
├── exceptions/
│   ├── ResourceNotFoundException.java           # Domain-specific runtime exception
│   └── GlobalExceptionHandler.java              # Centralized @RestControllerAdvice error handler
├── mappers/
│   └── AiInteractionMapper.java                 # Dedicated bidirectional Entity <-> DTO conversion (@Component)
├── models/
│   ├── dtos/
│   │   ├── AiPromptRequestDto.java              # Validation record for POST request payloads
│   │   └── AiInteractionDto.java                # Immutable record contract returned to clients
│   └── entities/
│       └── AiInteraction.java                   # JPA entity representing the ai_interactions table
├── repositories/
│   └── AiInteractionRepository.java             # Spring Data JPA interface extending JpaRepository
└── services/
    ├── AiInteractionService.java                # Business service interface
    └── impl/
        └── AiInteractionServiceImpl.java        # Service implementation orchestrating ChatClient & JPA transactions
```

### Layer Details:

| Layer | Component | Responsibility |
| :--- | :--- | :--- |
| **Presentation** | [`AiInteractionController`](file:///D:/IA/SPRING-BOOT/WORKSPACE/spring-ai/src/main/java/com/venefast/springboot/springai/app/controllers/AiInteractionController.java) | Exposes REST endpoints, validates inputs, and delegates immediately to the service layer. Does not inject `ChatClient` directly. |
| **Service (Contract)** | [`AiInteractionService`](file:///D:/IA/SPRING-BOOT/WORKSPACE/spring-ai/src/main/java/com/venefast/springboot/springai/app/services/AiInteractionService.java) | Defines business contracts using DTO records and primitive strings. |
| **Service (Implementation)** | [`AiInteractionServiceImpl`](file:///D:/IA/SPRING-BOOT/WORKSPACE/spring-ai/src/main/java/com/venefast/springboot/springai/app/services/impl/AiInteractionServiceImpl.java) | Manages `ChatClient` prompt configuration, executes AI calls, and manages transaction boundaries (`@Transactional`). |
| **Data Access** | [`AiInteractionRepository`](file:///D:/IA/SPRING-BOOT/WORKSPACE/spring-ai/src/main/java/com/venefast/springboot/springai/app/repositories/AiInteractionRepository.java) | Spring Data JPA repository with custom queries (`findTop10ByOrderByCreatedAtDesc`). |
| **Domain Entities** | [`AiInteraction`](file:///D:/IA/SPRING-BOOT/WORKSPACE/spring-ai/src/main/java/com/venefast/springboot/springai/app/models/entities/AiInteraction.java) | JPA Entity representing table `ai_interactions` with automated `@PrePersist` and `@PreUpdate` timestamp auditing. |
| **DTO Records** | [`AiPromptRequestDto`](file:///D:/IA/SPRING-BOOT/WORKSPACE/spring-ai/src/main/java/com/venefast/springboot/springai/app/models/dtos/AiPromptRequestDto.java), [`AiInteractionDto`](file:///D:/IA/SPRING-BOOT/WORKSPACE/spring-ai/src/main/java/com/venefast/springboot/springai/app/models/dtos/AiInteractionDto.java) | Immutable Java `record` types enforcing API contracts and concealing internal persistence fields. |
| **Mappers** | [`AiInteractionMapper`](file:///D:/IA/SPRING-BOOT/WORKSPACE/spring-ai/src/main/java/com/venefast/springboot/springai/app/mappers/AiInteractionMapper.java) | Spring `@Component` decoupling entities from presentation DTOs with `toDto` and `toEntity`. |
| **Configuration** | [`AiConfig`](file:///D:/IA/SPRING-BOOT/WORKSPACE/spring-ai/src/main/java/com/venefast/springboot/springai/app/config/AiConfig.java) | Produces the `ChatClient` bean using Spring AI's auto-configured `ChatClient.Builder`. |
| **Error Handling** | [`GlobalExceptionHandler`](file:///D:/IA/SPRING-BOOT/WORKSPACE/spring-ai/src/main/java/com/venefast/springboot/springai/app/exceptions/GlobalExceptionHandler.java) | Translates runtime errors and validation failures into structured JSON responses. |

---

## 4. Technology Stack & Key Dependencies

| Technology / Library | Version | Role |
| :--- | :--- | :--- |
| **Java** | 25 (LTS) | Modern language runtime |
| **Spring Boot** | 4.1.0 | Application framework |
| **Spring AI BOM** | 2.0.1 | Official Spring abstraction for GenAI & LLMs |
| **Spring AI Ollama Starter** | 2.0.1 | Ollama chat model client autoconfiguration |
| **Spring Boot WebMvc** | 4.1.0 | RESTful HTTP request dispatching |
| **Spring Boot Validation** | 4.1.0 | Hibernate Validator / Bean validation annotations |
| **Spring Boot Data JPA** | 4.1.0 | Hibernate ORM 7.x persistence engine |
| **H2 Database** | Runtime | Rapid in-memory relational SQL engine |
| **Spring Boot Actuator** | 4.1.0 | Operational health checks and metrics |
| **JUnit 5 & Mockito** | Test Scope | Unit testing and slice integration testing |

---

## 5. Configuration Highlights (`application.properties`)

File: [`src/main/resources/application.properties`](file:///D:/IA/SPRING-BOOT/WORKSPACE/spring-ai/src/main/resources/application.properties)

```properties
# Application Configuration
spring.application.name=spring-ai
server.port=8090

# Spring AI Ollama Configuration
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.model=qwen3:1.7b
spring.ai.ollama.chat.options.model=qwen3:1.7b

# H2 Database & Console Configuration
spring.datasource.url=jdbc:h2:mem:spring_ai_db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JPA / Hibernate Configuration
spring.jpa.open-in-view=false
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Actuator Configuration
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always
```

---

## 6. Endpoints Reference & Examples

All AI-related endpoints are grouped under `/api/v1/ai`.

### 6.1 Direct GET Endpoints (Stateless, Plain String Response)

#### 1. Ask a Question: `GET /api/v1/ai/generate`
- **Query Parameters**: `message` (Optional. Default: `"is it worth to lear spring AI?"`)
- **Behavior**: Calls the service layer which invokes `ChatClient`.
- **Browser**:
  ```
  http://localhost:8090/api/v1/ai/generate?message=What is Spring Boot?
  ```
- **cURL**:
  ```bash
  curl -G "http://localhost:8090/api/v1/ai/generate" --data-urlencode "message=Explain Java records in one sentence"
  ```

#### 2. Personalized Name Greeting: `GET /api/v1/ai/greet` (Alias: `/api/v1/ai/name`)
- **Query Parameters**: `name` (Optional. Default: `"Developer Jhon"`)
- **Behavior**: Generates a friendly greeting and brief inspirational message for `name`.
- **cURL**:
  ```bash
  curl -G "http://localhost:8090/api/v1/ai/greet" --data-urlencode "name=Alice"
  ```

#### 3. French Greeting with Last Name: `GET /api/v1/ai/last-name` (Alias: `/api/v1/ai/lastname`)
- **Query Parameters**: `lastName` or `lastname` (Optional. Default: `"Doe"`)
- **Behavior**: Injects a system instruction (`"Always answer in French, in one line."`) and prompts the model with `"Hello world " + lastName`.
- **cURL**:
  ```bash
  curl -G "http://localhost:8090/api/v1/ai/last-name" --data-urlencode "lastName=Duarte"
  ```
- **Sample Output**:
  ```text
  Bonjour le monde Duarte, j'espère que vous passez une excellente journée.
  ```

---

### 6.2 Persistent Endpoints (Stateful, JSON DTO Response)

#### 4. Generate & Save Interaction: `POST /api/v1/ai/generate`
- **Headers**: `Content-Type: application/json`
- **Payload**:
  ```json
  {
    "prompt": "What are the advantages of Spring AI?",
    "model": "qwen3:1.7b"
  }
  ```
- **cURL**:
  ```bash
  curl -X POST http://localhost:8090/api/v1/ai/generate \
    -H "Content-Type: application/json" \
    -d "{\"prompt\": \"What are the advantages of Spring AI?\"}"
  ```
- **Response** (`201 Created` with `Location: /api/v1/ai/{id}`):
  ```json
  {
    "id": 1,
    "prompt": "What are the advantages of Spring AI?",
    "response": "Spring AI provides portable abstractions across multiple AI providers...",
    "model": "qwen3:1.7b"
  }
  ```

#### 5. Retrieve Recent Interaction History: `GET /api/v1/ai/history`
- Returns an array containing up to the 10 most recent persisted interactions.
- **cURL**:
  ```bash
  curl http://localhost:8090/api/v1/ai/history
  ```

#### 6. Retrieve Interaction by ID: `GET /api/v1/ai/{id}`
- Returns a specific interaction record by database ID, or `404 Not Found` if missing.
- **cURL**:
  ```bash
  curl http://localhost:8090/api/v1/ai/1
  ```

---

### 6.3 Management & Database Endpoints

- **Health Check**:
  ```
  http://localhost:8090/actuator/health
  ```
- **H2 Database Console**:
  ```
  http://localhost:8090/h2-console
  ```
  - **JDBC URL**: `jdbc:h2:mem:spring_ai_db`
  - **User**: `sa`
  - **Password**: *(leave blank)*

---

## 7. Prerequisites & Local Execution Guide

### 7.1 Prerequisites
1. **Java 25 SDK**: Verify by running `java -version`.
2. **Ollama**: Must be running locally on port 11434 with the target model:
   ```bash
   ollama run qwen3:1.7b
   ```

### 7.2 Run the Application
- **Windows (Command Prompt / PowerShell)**:
  ```powershell
  .\mvnw.cmd -DskipTests spring-boot:run
  ```
- **Linux / macOS**:
  ```bash
  ./mvnw -DskipTests spring-boot:run
  ```

### 7.3 Run Automated Tests
```powershell
.\mvnw.cmd test
```

---

## 8. Automated Test Suite Overview

The project includes 10 automated test cases guaranteeing robustness and regression protection:

1. **[`AiInteractionControllerTest`](file:///D:/IA/SPRING-BOOT/WORKSPACE/spring-ai/src/test/java/com/venefast/springboot/springai/app/controllers/AiInteractionControllerTest.java)** (6 tests):
   - Validates that `GET /generate` properly passes custom `message` parameters and defaults to fallback messages.
   - Validates that `GET /greet` properly delegates `name` parameters and uses defaults.
   - Validates that `GET /last-name` properly routes `lastName` queries to the service layer.
2. **[`AiInteractionMapperTest`](file:///D:/IA/SPRING-BOOT/WORKSPACE/spring-ai/src/test/java/com/venefast/springboot/springai/app/mappers/AiInteractionMapperTest.java)** (3 tests):
   - Verifies bidirectional conversions between `AiInteraction` entity and `AiInteractionDto` record.
   - Tests null-safety handling in mapping methods.
3. **[`SpringAiApplicationTests`](file:///D:/IA/SPRING-BOOT/WORKSPACE/spring-ai/src/test/java/com/venefast/springboot/springai/app/SpringAiApplicationTests.java)** (1 test):
   - Verifies complete Spring Boot application context bootstrapping, Hikari connection pool startup, H2 table generation, and Ollama autoconfiguration.

---

## 9. Governance & Rules Summary

All contributors and autonomous agents must respect the guidelines codified in [**`AGENTS.md`**](file:///D:/IA/SPRING-BOOT/WORKSPACE/spring-ai/AGENTS.md):
- **Code Language**: English only (identifiers, methods, classes, comments).
- **Communication Language**: English only (explanations, responses, planning reports).
- **Architecture**: Strict layered architecture without leaking JPA entities to controllers or clients.
- **DTO Strategy**: Implemented as Java `record`s. Sensitive fields and audit timestamps excluded.
- **Config Format**: `application.properties` exclusively (no YAML).
