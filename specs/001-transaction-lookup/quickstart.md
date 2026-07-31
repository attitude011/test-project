# Quickstart: Transaction Lookup

## Prerequisites

- Java 21
- Gradle
- A local or mock Serviex service listening on http://localhost:9090

## Setup

1. Run `gradle bootRun` from the repository root.
2. Use the local token generator at `/generate-token` to obtain a test JWT.
3. Send a request to `/getTrx/123` with the JWT in the Authorization header.

## Validation

- Expected success: HTTP 200 with a JSON body containing amount, store, currency, and users.
- Expected conflict mapping: if the upstream Serviex service returns HTTP 409, the API returns HTTP 500 with a body containing code and Error from application.yml.
