# Feature Specification: Transaction Lookup

**Feature Branch**: `001-transaction-lookup`

**Created**: 2026-07-31

**Status**: Draft

**Input**: User description: "/speckit.specify Create a REST service specification for transaction lookup with the following requirements:
1. Endpoint: GET /getTrx/{id} where {id} is the transaction ID (idTransaction).
2. Security: The endpoint must be secured with JWT authentication.
3. Behavior: When invoked, the controller calls the service, which uses the client (via WebClient) to call an external service (\"Serviex\") passing the idTransaction.
4. Expected Response Model:
   - amount: int
   - store: String
   - currency: String
   - users: List of user objects, where each object has:
       - name: String
       - address: String
5. Error Handling Requirement: If the external \"Serviex\" returns an HTTP 409 error, catch it and map it globally via @ControllerAdvice to an HTTP 500 response with a JSON body structured as:
   - code: String
   - Error: int (loaded from local application.yml configuration).
6. Configuration: All endpoints for \"Serviex\" and custom error mappings must be configurable in application.yml."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Retrieve transaction details for a known transaction (Priority: P1)
A client with a valid JWT can request a transaction by its identifier and receive the transaction details for that record.

**Why this priority**: This is the primary business flow and the core value of the service.

**Independent Test**: A caller can authenticate successfully, invoke the endpoint with a valid transaction ID, and receive a structured transaction response.

**Acceptance Scenarios**:

1. **Given** a client presents a valid JWT and a known transaction ID, **When** the client calls GET /getTrx/{id}, **Then** the system returns the transaction details including amount, store, currency, and users.
2. **Given** a client presents an invalid or missing JWT, **When** the client calls GET /getTrx/{id}, **Then** the request is rejected before any transaction lookup is performed.

---

### User Story 2 - Handle upstream conflict from the external service (Priority: P2)
A client receives a clear error response when the external Serviex service rejects the lookup request with a conflict response.

**Why this priority**: This protects the API contract and ensures unexpected upstream failures are surfaced consistently.

**Independent Test**: An upstream 409 response from Serviex results in a standardized 500 response for the caller.

**Acceptance Scenarios**:

1. **Given** the external Serviex service returns HTTP 409 for a transaction lookup, **When** the request is processed, **Then** the API returns HTTP 500 with a JSON body containing code and Error values from configuration.

---

### Edge Cases

- If the transaction ID is missing or malformed, the request is rejected with an appropriate validation error before integration is attempted.
- If the external service is unavailable or returns an unexpected error, the API returns a consistent failure response to the caller.
- If the configuration for the external endpoint or custom error body is missing or invalid, the system uses the configured default values defined in application.yml.

## Requirements *(mandatory)*

### Coding Standards

- **CS-001**: All DTO classes MUST use Lombok annotations (`@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`). Manual getters, setters, and constructors are prohibited in DTO classes.

### Functional Requirements

- **FR-001**: The system MUST expose a GET /getTrx/{id} endpoint where {id} is the transaction ID identified as idTransaction.
- **FR-002**: The endpoint MUST require a valid JWT for access.
- **FR-003**: The system MUST process the request through a controller-service-client flow to retrieve transaction details from an external Serviex integration.
- **FR-004**: The system MUST return transaction data with the fields amount, store, currency, and users.
- **FR-005**: Each user entry in the response MUST include the fields name and address.
- **FR-006**: The system MUST support configuration of the Serviex endpoint details in application.yml.
- **FR-007**: The system MUST support configuration of custom error mapping values in application.yml.
- **FR-008**: If the external Serviex service returns HTTP 409, the system MUST translate that condition into a global HTTP 500 response with a JSON body containing a String field named code and an int field named Error.
- **FR-009**: The HTTP 500 error payload MUST use the configured values from application.yml for the code and Error fields.

### Key Entities

- **Transaction**: A business record identified by idTransaction and containing amount, store, currency, and associated users.
- **User**: A person associated with a transaction, represented by name and address.
- **External Service Configuration**: Configuration values that define the Serviex endpoint and the error response payload used for conflict handling.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Authenticated clients can retrieve transaction details for a valid transaction ID in under 5 seconds under normal operating conditions.
- **SC-002**: At least 95% of valid authenticated requests return the expected response structure with amount, store, currency, and users.
- **SC-003**: 100% of external Serviex 409 responses are surfaced to clients as HTTP 500 responses with the configured code and Error values.
- **SC-004**: Configuration changes for Serviex endpoints and error mappings can be applied without changing application code.

## Assumptions

- A valid JWT is issued by the existing authentication infrastructure.
- The external Serviex service is reachable and returns transaction data in a format compatible with the specified response contract.
- The feature is limited to read-only transaction lookup and does not introduce creation, update, or deletion workflows.
