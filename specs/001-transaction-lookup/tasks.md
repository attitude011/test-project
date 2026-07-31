# Tasks: Transaction Lookup

**Input**: Design documents from `/specs/001-transaction-lookup/`

**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Establish the Gradle build and application configuration baseline.

- [X] T001 Initialize `build.gradle` with Spring Boot 2.5.x, Java 21, WebFlux, Spring Security, and JWT dependencies
- [X] T002 Create `src/main/resources/application.yml` with Serviex endpoint configuration, error mapping values, and a local `jwt.secret`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Build the core security and error-handling framework required by all stories.

- [X] T003 Create `src/main/java/com/example/transaction/dto/ErrorResponseDto.java` for global error payloads
- [X] T004 Implement `src/main/java/com/example/transaction/exception/GlobalExceptionHandler.java` to catch `WebClientResponseException.Conflict` and return HTTP 500 with `{code, Error}`
- [X] T005 Implement `src/main/java/com/example/transaction/config/SecurityConfig.java` to enforce JWT authentication on `/getTrx/**`
- [X] T006 Add `src/main/java/com/example/transaction/config/JwtTokenGenerator.java` to expose a local token generation helper endpoint for Postman testing

---

## Phase 3: User Story 1 - Retrieve transaction details (Priority: P1)

**Goal**: Implement the lookup flow from controller through service to the external Serviex client.

**Independent Test**: A valid JWT caller invokes `GET /getTrx/{id}` and receives the transaction response with amount, store, currency, and users.

- [X] T007 Create `src/main/java/com/example/transaction/dto/UserDto.java` for transaction user entries
- [X] T008 Create `src/main/java/com/example/transaction/dto/TransactionResponseDto.java` for the lookup response model
- [X] T009 Implement `src/main/java/com/example/transaction/client/TransactionClient.java` as the client layer interface
- [X] T010 Implement `src/main/java/com/example/transaction/client/impl/WebClientTransactionClient.java` to call Serviex using WebClient
- [X] T011 Implement `src/main/java/com/example/transaction/service/TransactionService.java` as the service layer interface
- [X] T012 Implement `src/main/java/com/example/transaction/service/impl/TransactionServiceImpl.java` to delegate lookup calls to `TransactionClient`
- [X] T013 Implement `src/main/java/com/example/transaction/controller/TransactionController.java` with `GET /getTrx/{id}` to invoke `TransactionService`

---

## Phase 4: User Story 2 - Handle upstream conflict mapping (Priority: P2)

**Goal**: Ensure Seriex 409 conflicts are remapped globally to HTTP 500 using the configured error payload.

**Independent Test**: A Serviex HTTP 409 response results in API HTTP 500 with JSON body containing configured `code` and `Error`.

- [X] T014 Verify `src/main/resources/application.yml` contains `error.mapping.code` and `error.mapping.error`
- [X] T015 Verify `src/main/java/com/example/transaction/exception/GlobalExceptionHandler.java` returns `ResponseEntity.status(500)` with `ErrorResponseDto`
- [X] T016 Confirm `src/main/java/com/example/transaction/client/impl/WebClientTransactionClient.java` surfaces 409 responses to the exception handler

---

## Phase 5: Polish & Cross-Cutting Concerns

**Purpose**: Clean up any mismatches, ensure package names are valid, and finalize the implementation.

- [X] T017 Refactor any reserved-keyword package names such as `client.interface` or `service.interface` to valid packages `client` and `service`
- [X] T018 Validate `build.gradle` and `application.yml` for consistency with the constitution requirements
- [X] T019 Confirm the service layer, controller, and client packages follow the layered architecture

---

## Dependencies & Execution Order

- Phase 1 must be completed first.
- Phase 2 must be completed before Phase 3 and Phase 4.
- Phase 3 and Phase 4 can proceed after Phase 2, with Phase 3 as the MVP priority.
- Phase 5 is cross-cutting cleanup after implementation.

## Parallel Opportunities

- `T001` and `T002` are independent setup tasks and can run in parallel
- `T003`, `T004`, `T005`, and `T006` can be worked on in parallel within foundational work
- `T007` through `T013` are sequential within User Story 1, but DTO creation can overlap with interface design
- `T014` through `T016` are focused on conflict mapping and can be verified after core flow is implemented
- `T017` through `T019` are polish tasks that can run in parallel with final validation
