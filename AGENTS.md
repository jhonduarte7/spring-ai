# AGENTS.md - Workspace Standards & Agent Guidelines

This document provides architectural standards, conventions, and skill orchestration guidelines for all autonomous agents and developers working on this project.

---

## 1. Project Overview & Technology Stack

- **Project Name**: `spring-ai`
- **Group ID**: `com.venefast.springboot`
- **Base Package**: `com.venefast.springboot.springai.app`
- **Java Version**: **Java 25** (LTS)
- **Spring Boot Version**: **4.1.0**
- **Spring AI Version**: **2.0.1** (BOM managed)
- **AI Model Provider**: **Ollama** (`spring-ai-starter-model-ollama`)
- **Default AI Model**: `qwen3:1.7b` (running on `http://localhost:11434`)
- **Server Port**: `8090`
- **Database**: H2 In-Memory Database (`jdbc:h2:mem:spring_ai_db`) with H2 Console enabled
- **Configuration Format**: `application.properties` (never YAML)

---

## 2. Workspace Rules & Governance

1. **Code Language**: All source code, class names, method names, comments, commit messages, and package identifiers must be in **English**.
2. **Communication Language**: All user interactions, explanations, responses, and planning reports must be written in **English**.
3. **Java Version**: Always target **Java 25**.
4. **Layered Architecture**: Strict separation of concerns across layers.

---

## 3. Layered Architecture Specifications

```
src/main/java/com/venefast/springboot/springai/app/
├── SpringAiApplication.java
├── config/
│   └── AiConfig.java                       # Spring AI & ChatClient Bean definitions
├── controllers/
│   └── AiInteractionController.java        # REST API endpoints (@RestController)
├── exceptions/
│   ├── ResourceNotFoundException.java      # Domain exceptions
│   └── GlobalExceptionHandler.java         # Centralized REST error handling
├── mappers/
│   └── AiInteractionMapper.java            # Dedicated Entity <-> DTO conversion (@Component)
├── models/
│   ├── dtos/
│   │   ├── AiPromptRequestDto.java         # Input request validation record
│   │   └── AiInteractionDto.java           # Immutable record contract for clients
│   └── entities/
│       └── AiInteraction.java              # JPA Entity (@Entity) for database persistence
├── repositories/
│   └── AiInteractionRepository.java        # Spring Data JPA Repository
└── services/
    ├── AiInteractionService.java           # Business contract (DTO & simple query signatures)
    └── impl/
        └── AiInteractionServiceImpl.java   # Business logic, ChatClient & transaction orchestration
```

### Layer Responsibilities

- **Controllers**: Thin endpoints returning `String` or `ResponseEntity<DTO>`. Strictly validate incoming payload using `@Valid`. Never expose JPA entities.
- **Services**: Contain business logic, ChatClient interaction, and transactional boundaries (`@Transactional`). Communicate externally with DTOs/primitives and delegate persistence conversions to the `Mapper`.
- **Mappers**: `@Component` classes providing bidirectional transformations (`toDto`, `toEntity`).
- **Repositories**: Standard Spring Data `JpaRepository<Entity, ID>` interfaces.
- **Entities**: JPA persistence models containing database annotations and audit fields (`createdAt`, `updatedAt`).
- **DTOs**: Java `record` types omitting sensitive and internal audit fields.

---

## 4. Active Skills

| Skill | Path | Focus |
| :--- | :--- | :--- |
| **`spring-boot-best-practices-workspaces`** | `.agents/skills/spring-boot-best-practices-workspaces/SKILL.md` | Workspace governance, English user communication, Java 25 standards. |
| **`spring-boot-best-practices`** | `.agents/skills/spring-boot-best-practices/SKILL.md` | Layered architecture implementation, DTOs as records, mappers, Spring Initializr setup. |
| **`skill-creator`** | `.agents/skills/skill-creator/SKILL.md` | Authoring, evaluating, and maintaining agent skills. |

---

## 5. Local Execution Commands

### Run Application
- **Windows**:
  ```cmd
  mvnw.cmd -DskipTests spring-boot:run
  ```
- **Linux / macOS**:
  ```bash
  ./mvnw -DskipTests spring-boot:run
  ```

### Run Tests
```cmd
mvnw.cmd test
```

### Endpoints Reference
- **GET** `/api/v1/ai/generate?message={question}`: Send question directly to the service layer using ChatClient (also aliased at `/api/v1/ai/ask`).
- **GET** `/api/v1/ai/greet?name={name}`: Send name parameter to service layer to generate a personalized message via ChatClient (also aliased at `/api/v1/ai/name`).
- **GET** `/api/v1/ai/last-name?lastName={lastName}`: Send lastName parameter; service applies system instruction in French (one line) and greets with "Hello world" + lastName (also aliased at `/api/v1/ai/lastname`).
- **POST** `/api/v1/ai/generate`: Send prompt to Ollama via JSON body and persist interaction in H2 database.
- **GET** `/api/v1/ai/history`: Retrieve the 10 most recent persistent interactions.
- **GET** `/api/v1/ai/{id}`: Retrieve a specific interaction by ID.
- **GET** `/actuator/health`: Application health check.
- **H2 Console**: `http://localhost:8090/h2-console` (JDBC URL: `jdbc:h2:mem:spring_ai_db`, User: `sa`, Password: empty).
