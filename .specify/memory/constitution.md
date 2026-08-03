# Test Project Constitution
<!-- Sync Impact Report
- Version change: 1.0.1 -> 1.1.0
- Modified principles: III. Servlet WebClient Integration
- Added sections: VI. Standard Java API Surface
- Removed sections: None
- Follow-up TODOs: None
-->

## Core Principles

### I. Java and Spring Boot Baseline
All services MUST be implemented in Java 21 using Spring Boot 2.5.x and Gradle.
Build files MUST use a standard, simple build.gradle file and MUST NOT use Kotlin
DSL (build.gradle.kts). New modules, dependencies, and build scripts MUST align
with this baseline unless a formal exception is approved through governance.

### II. Layered Architecture
All application code MUST follow this strict flow:
Controller -> Service Interface -> Service Implementation -> Client Interface ->
Client Implementation. Cross-layer shortcuts, direct repository access from
controllers, and bypassing the interface chain are prohibited.

### III. Servlet WebClient Integration
External HTTP calls MUST be made through Spring WebClient, but the application
runs strictly as a Servlet application (`spring.main.web-application-type:
servlet`) and not as a reactive Netty application. Blocking WebClient usage via
`.block()` is permitted and expected inside client implementations. Ad-hoc
RestTemplate usage is not permitted for new code.

### IV. Security by Default
Incoming API requests MUST require JWT authentication. Any endpoint that exposes
business functionality without a verified JWT is non-compliant. Authentication and
authorization rules MUST be enforced in the application layer and documented in
configuration.

### V. Configuration and Error Contract
Local configuration MUST be stored in application.yml, including external service
endpoints and custom error definitions. The application MUST provide a global
@ControllerAdvice that intercepts external 409 Conflict responses and remaps them
to HTTP 500 with a body containing a String field named "code" and an int field
named "Error".

### VI. Standard Java API Surface
Service and client interfaces MUST expose standard Java objects and collections
(e.g. `TransactionDto`, `List<String>`) rather than Reactor `Mono`/`Flux` in public
API signatures. Client implementations MUST unblock WebClient responses internally
before returning domain payloads so controllers remain synchronous.

## Architecture and Technology Standards
The project MUST keep the following conventions:
- Controllers only orchestrate request and response flow and delegate to service
  interfaces.
- Service interfaces define business contracts; service implementations contain
  business logic.
- Client interfaces abstract remote integration; client implementations encapsulate
  transport details and error translation.
- External service endpoints, timeouts, retry limits, and error mappings MUST be
  defined in application.yml where possible.
- Service and client methods MUST return standard Java objects and collections.
  Client implementations may unblock WebClient responses internally; controller
  signatures MUST avoid exposing `Mono` or `Flux`.

## Quality Gates
All changes MUST be reviewed for compliance with this constitution before merge.
Build and test evidence MUST be produced for each change, and any deviation from
required architecture or security rules MUST be documented and approved.

### VII. DTO Lombok Standard
Data transfer object classes (DTOs) MUST use Lombok annotations for boilerplate.
DTO classes MUST include `@Data`, `@NoArgsConstructor`, and `@AllArgsConstructor`.
Manual getters, setters, and constructors are prohibited for DTO types.

## Governance
This constitution supersedes informal practices for this project. Amendments
require a documented proposal, review by the maintainer, and a version bump.
Compliance reviews MUST verify the architecture layers, security controls,
configuration location, and error handling behavior before release.

**Version**: 1.0.1 | **Ratified**: 2026-07-31 | **Last Amended**: 2026-07-31
