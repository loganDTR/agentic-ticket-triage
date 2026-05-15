# Agentic Ticket Triage (Spring Boot + LangGraph4j)

Small educational project that shows how to orchestrate a **stateful, multi-node ticket triage workflow** in Spring Boot using LangGraph-style execution.

The API receives a ticket text and returns:
- `classification` (`category`)
- `confidence`
- `route`
- `answer`
- `error`
- `executionId`
- `executionTrace`

---

## 1) Project overview

`agentic-ticket-triage` exposes one REST endpoint:
- `POST /api/v1/tickets/triage`

Input (`TriageRequest`):
- `text` (ticket message)

Output (`TriageResponse`):
- `executionId`: unique id generated per graph run
- `originalText`: original ticket text
- `category`: `TECHNICAL | BILLING | GENERAL | ESCALATION`
- `confidence`: numeric confidence score (0-100)
- `route`: selected downstream route (`billingSupport`, `technicalSupport`, `humanEscalation`)
- `answer`: final user-facing answer
- `error`: non-empty error message when a node fails, otherwise empty string
- `executionTrace`: ordered list of executed nodes

The goal is to make graph orchestration explicit and easy to inspect.

---

## 2) Architecture

### Layers in this codebase

- **API layer** (`controller/`)
  - HTTP request/response mapping and validation.
  - Main class: `TicketTriageController`.

- **Application service** (`application/`)
  - Orchestration entrypoint for the use case.
  - Main class: `TicketTriageService`.

- **Graph orchestration layer** (`graph/TicketTriageGraph`)
  - Defines nodes, edges, conditional routing, and execution start/end.

- **Graph nodes** (`graph/*Node.java`)
  - Each node is a unit of work implementing `NodeAction<TicketTriageState>`.

- **Model/DTOs** (`model/`)
  - API records and domain enums for structured data flow.

- **Tools** (`tool/`)
  - Tool-calling functions exposed to LLM agents (example: `InvoiceTool`).

- **Configuration** (`config/`, `application.yaml`)
  - OpenAI/LangChain model properties, SpringDoc paths, H2/JPA settings.

### Framework responsibilities

- **Spring Boot**
  - HTTP API, request validation, dependency injection, configuration binding.

- **LangChain4j**
  - OpenAI integration (`OpenAiChatModel`), AI Services interfaces, structured output mapping, tool calling (`@Tool`).

- **LangGraph4j**
  - Stateful graph orchestration with explicit nodes, edges, conditional transitions, and shared execution state.

---

## 3) Workflow diagram

```text
START
  -> classifyTicket
  -> decideRoute
  -> conditional edge
      -> billingAnswer
      -> technicalAnswer
      -> humanEscalationAnswer
  -> END
```

---

## 4) Main concepts explained

- **`TicketTriageState` as shared graph state**
  - Central state object passed across nodes.
  - Holds channels like `text`, `category`, `confidence`, `route`, `answer`, `executionTrace`, `executionId`.

- **`NodeAction` as a graph node contract**
  - Every node implements `NodeAction<TicketTriageState>` and returns partial state updates (`Map<String, Object>`).

- **`node_async` for async node adapter**
  - Graph nodes are registered with `node_async(...)` in `TicketTriageGraph`.
  - This adapts node actions to async graph execution APIs.

- **Conditional edges and `routeToAnswerNode`**
  - After `decideRoute`, graph uses a conditional edge resolver (`routeToAnswerNode`) to select the next answer node.

- **`executionId`**
  - Created at graph start (`UUID.randomUUID().toString()`) to correlate one full run.

- **`executionTrace`**
  - Node names appended in execution order (`state.traceWith("nodeName")`) for observability/debug.

- **Confidence-based escalation**
  - `DecideRouteNode` escalates to human when confidence is below threshold (`MIN_CONFIDENCE = 60`).

- **`InvoiceTool` and tool calling**
  - `BillingAnswerNode` builds an AI service (`BillingAgent`) with tool access.
  - LLM can call `InvoiceTool.getInvoiceStatus(...)` when invoice id is present.

---

## 5) Important classes

- `TicketTriageController`
  - REST controller for `POST /api/v1/tickets/triage`.

- `TicketTriageService`
  - Application service: calls graph and maps final state to `TriageResponse`.

- `TicketTriageGraph`
  - Builds and compiles the state graph (nodes, edges, conditional routing, invocation).

- `TicketTriageState`
  - Shared graph state schema/channels and typed getters.

- `LlmClassifyTicketNode`
  - Uses `TicketClassifierAgent` to classify ticket and set `category` + `confidence`.

- `DecideRouteNode`
  - Applies confidence threshold and category-based route selection.

- `BillingAnswerNode`
  - Uses `BillingAgent` with `InvoiceTool` for billing answers and tool calls.

- `TechnicalAnswerNode`
  - Returns static technical support routing message.

- `HumanEscalationAnswerNode`
  - Returns fallback escalation message.

- `InvoiceTool`
  - Demo in-memory invoice status lookup exposed as LangChain tool.

- `ClassificationResult`
  - Structured classifier output (`category`, `confidence`).

- `TriageRequest`
  - Request DTO with `text` (validated with `@NotBlank`).

- `TriageResponse`
  - Response DTO containing classification, route, answer, and execution metadata.

- `OpenAIChatModelProperties`
  - Binds `langchain4j.open-ai.chat-model.*` properties.

- `AiModelConfig`
  - Creates `OpenAiChatModel` bean from bound properties.

- `GraphNodeConfig`
  - Spring bean factory for graph nodes requiring injected dependencies.

---

## 6) Configuration

Main config file: `src/main/resources/application.yaml`

```yaml
spring:
  application:
    name: agentic-ticket-triage
  datasource:
    url: jdbc:h2:mem:ticket-triage-db;DB_CLOSE_DELAY=-1
    driver-class-name: org.h2.Driver
    username: sa
    password: sa

  h2:
    console:
      enabled: true
      path: /h2-console

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

langchain4j:
  open-ai:
    chat-model:
      api-key: ${OPENAI_API_KEY}
      model-name: ${OPENAI_CHAT_MODEL:gpt-4o-mini}
      base-url: ${OPENAI_BASE_URL:https://api.openai.com/v1}
      temperature: ${OPENAI_TEMPERATURE:0.2}

springdoc:
  swagger-ui:
    path: /swagger-ui.html
  api-docs:
    path: /v3/api-docs
```

Key properties:
- `spring.application.name`
- `langchain4j.open-ai.chat-model.api-key`
- `langchain4j.open-ai.chat-model.model-name`
- `langchain4j.open-ai.chat-model.base-url`
- `langchain4j.open-ai.chat-model.temperature`
- `springdoc.swagger-ui.path`
- `springdoc.api-docs.path`
- `spring.h2.console.path`

Required environment variable:
- `OPENAI_API_KEY` (must be set before startup)

---

## 7) How to run

### Prerequisites

- JDK 25
- Maven Wrapper (`mvnw.cmd` is already in repo)
- Valid OpenAI API key

### Start the app (PowerShell)

```powershell
Set-Location "D:\dev\LangChain4j\agentic-ticket-triage"
$env:OPENAI_API_KEY="<your-openai-api-key>"
.\mvnw.cmd spring-boot:run
```

### OpenAPI, Swagger UI and H2 Console

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- H2 Console: `http://localhost:8080/h2-console`

### Call the triage endpoint

```powershell
curl.exe -X POST "http://localhost:8080/api/v1/tickets/triage" `
  -H "Content-Type: application/json" `
  -d "{\"text\":\"Invoice 123411 is still unpaid, can you check?\"}"
```

Example response:

```json
{
  "executionId": "ad4f5db4-71f0-4d8d-84f0-6a6f4e6b351b",
  "originalText": "Invoice 123411 is still unpaid, can you check?",
  "category": "BILLING",
  "confidence": 87,
  "route": "billingSupport",
  "answer": "La fattura 123411 risulta OVERDUE. Vuoi che ti spieghi i prossimi passi?",
  "error": "",
  "executionTrace": [
    "classifyTicket",
    "decideRoute",
    "billingAnswer"
  ]
}
```

---

## 8) Current limitations

- Audit persistence is available, but currently on in-memory H2 (data is lost on restart).
- Stored audit is write-only for now (no dedicated read/query API yet).
- `InvoiceTool` is a fake in-memory demo tool.
- Error handling is still basic.
- No checkpoint/resume support yet.

---

## 9) Suggested next steps

- Improve error handling and API-level error contracts.
- Move audit persistence from H2 in-memory to a durable database profile (e.g. PostgreSQL/MySQL).
- Add audit query endpoints (list/detail/filter by `executionId`, category, route, date).
- Add metrics per node (latency, token usage, failures).
- Add a final reviewer/quality node before returning response.
- Add more tools (CRM lookup, order status, SLA policy checks).
- Add checkpoint/resume support for long-running or human-in-the-loop flows.

