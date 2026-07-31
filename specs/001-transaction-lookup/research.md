# Research: Transaction Lookup

## Decision

Use a single-module Spring Boot 2.5.x web service with Spring WebFlux and Spring Security, implemented with a controller-service-client structure and a global exception handler for upstream 409 conflicts.

## Rationale

This aligns directly with the constitution and keeps the implementation simple while supporting the required WebClient integration, JWT security, and configuration-driven error mapping.

## Alternatives considered

- Use RestTemplate for HTTP calls: rejected because the constitution requires WebClient.
- Use a monolithic controller-only design: rejected because the constitution requires strict layering.
- Use a separate authentication service: rejected because the requirement only needs a local JWT generator for testing.
