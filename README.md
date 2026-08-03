# Transaction Service

A Spring Boot REST service that exposes a secured transaction-lookup endpoint and a booking-lookup endpoint, backed by external integrations via WebClient.

---

## Table of Contents

- [Running the Service](#running-the-service)
- [Configuration](#configuration)
- [Authentication](#authentication)
- [Endpoints](#endpoints)
  - [GET /generate-token](#1-get-generate-token)
  - [GET /getTrx/{id}](#2-get-gettrxid)
  - [GET /booking/{id}](#3-get-bookingid)
- [Error Responses](#error-responses)
- [Architecture](#architecture)

---

## Running the Service

```bash
./gradlew bootRun
```

The service starts on **port 8080** by default.

---

## Configuration

All external URLs, secrets, and error messages are driven by `src/main/resources/application.yml`.

| Property | Default value | Description |
|---|---|---|
| `server.port` | `8080` | HTTP port |
| `jwt.secret` | `local-dev-secret-change-me-1234567890` | HMAC-SHA key used to sign/verify JWTs |
| `serviex.base-url` | `http://localhost:9090` | Base URL of the Serviex upstream service |
| `serviex.transactions-path` | `/getTrx` | Path appended to base URL for transaction calls |
| `error.mapping.code` | `SERVIEX_CONFLICT` | `code` field returned in Serviex 409 → 500 error body |
| `error.mapping.error` | `500` | `Error` field (int) returned in Serviex 409 → 500 error body |
| `booking.api.url` | `https://restful-booker.herokuapp.com/booking/{id}` | Full URI template for the external Booking API |
| `booking.error.message` | `ketoKet` | `message` field returned when the Booking API call fails |

---

## Authentication

`GET /getTrx/**` is **JWT-protected**. All other endpoints are public.

Requests to `/getTrx/**` must include a bearer token in the `Authorization` header:

```
Authorization: Bearer <jwt>
```

The token is validated against the HMAC-SHA key configured in `jwt.secret`. Requests with a missing, expired, or invalid token receive **HTTP 401 Unauthorized**.

---

## Endpoints

### 1. `GET /generate-token`

> **Dev/testing helper** — generates a short-lived JWT signed with the local `jwt.secret`.  
> Do **not** expose this endpoint in production.

**Auth required:** No

**Response:** `200 OK` — plain-text JWT string

```
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsb2...
```

**Token lifetime:** 1 hour from generation.

---

### 2. `GET /getTrx/{id}`

Retrieves transaction details from the upstream **Serviex** service.

**Auth required:** Yes — `Authorization: Bearer <jwt>`

**Path parameter:**

| Name | Type | Description |
|---|---|---|
| `id` | String | The transaction identifier (`idTransaction`) |

**Success response — `200 OK`:**

```json
{
  "amount": 250,
  "store": "MainStore",
  "currency": "USD",
  "users": [
    {
      "name": "Jane Doe",
      "address": "123 Main St"
    }
  ]
}
```

| Field | Type | Description |
|---|---|---|
| `amount` | int | Transaction amount |
| `store` | String | Store name |
| `currency` | String | ISO 4217 currency code |
| `users` | Array | List of users associated with the transaction |
| `users[].name` | String | User's full name |
| `users[].address` | String | User's address |

**Error responses:**

| Status | Condition | Body |
|---|---|---|
| `401 Unauthorized` | Missing or invalid JWT | — |
| `500 Internal Server Error` | Serviex returns HTTP 409 Conflict | See [Error Responses](#error-responses) → Serviex Conflict |

**Example — cURL:**

```bash
# 1. Get a token
TOKEN=$(curl -s http://localhost:8080/generate-token)

# 2. Call the endpoint
curl -H "Authorization: Bearer $TOKEN" \
     http://localhost:8080/getTrx/TXN-001
```

---

### 3. `GET /booking/{id}`

Retrieves booking details from the external **Restful-Booker** API.

**Auth required:** No

**Path parameter:**

| Name | Type | Description |
|---|---|---|
| `id` | Integer | The booking identifier |

**Success response — `200 OK`:**

```json
{
  "firstname": "Josh",
  "lastname": "Allen",
  "totalprice": 111,
  "depositpaid": true,
  "bookingdates": {
    "checkin": "2018-01-01",
    "checkout": "2019-01-01"
  },
  "additionalneeds": "super bowls"
}
```

| Field | Type | Description |
|---|---|---|
| `firstname` | String | Guest first name |
| `lastname` | String | Guest last name |
| `totalprice` | int | Total booking price |
| `depositpaid` | boolean | Whether the deposit has been paid |
| `bookingdates.checkin` | String | Check-in date (YYYY-MM-DD) |
| `bookingdates.checkout` | String | Check-out date (YYYY-MM-DD) |
| `additionalneeds` | String | Any additional requirements |

**Error responses:**

| Status | Condition | Body |
|---|---|---|
| `500 Internal Server Error` | External Booking API is unreachable or returns an error | See [Error Responses](#error-responses) → Booking API Error |

**Example — cURL:**

```bash
curl http://localhost:8080/booking/1
```

---

## Error Responses

### Serviex Conflict (`/getTrx/**`)

Returned when the upstream Serviex service responds with **HTTP 409 Conflict**.  
Values are sourced from `error.mapping.*` in `application.yml`.

```json
{
  "code": "SERVIEX_CONFLICT",
  "Error": 500
}
```

| Field | Type | Source property |
|---|---|---|
| `code` | String | `error.mapping.code` |
| `Error` | int | `error.mapping.error` |

---

### Booking API Error (`/booking/**`)

Returned when the external Booking API call throws any exception.  
The message value is sourced from `booking.error.message` in `application.yml`.

```json
{
  "message": "ketoKet"
}
```

| Field | Type | Source property |
|---|---|---|
| `message` | String | `booking.error.message` |

---

## Architecture

The service follows a strict three-tier layered architecture. Each layer depends only on the interface of the layer below it.

```
HTTP Request
    │
    ▼
┌──────────────────────────────┐
│        Controller Layer       │  BookingController, TransactionController
│  (handles HTTP, path params)  │
└──────────────┬───────────────┘
               │ uses interface
               ▼
┌──────────────────────────────┐
│         Service Layer         │  BookingServiceImpl, TransactionServiceImpl
│  (business/delegation logic)  │
└──────────────┬───────────────┘
               │ uses interface
               ▼
┌──────────────────────────────┐
│          Client Layer         │  BookingWebClientImpl, WebClientTransactionClient
│  (HTTP calls, @Value, errors) │
└──────────────┬───────────────┘
               │ WebClient
               ▼
        External APIs
  (Serviex, Restful-Booker)
```

| Layer | Interface | Implementation | Responsibility |
|---|---|---|---|
| Controller | — | `BookingController` | Route HTTP, extract path variable, return response |
| Controller | — | `TransactionController` | Route HTTP, JWT-protected, extract path variable |
| Service | `BookingService` | `BookingServiceImpl` | Delegate to client, no HTTP logic |
| Service | `TransactionService` | `TransactionServiceImpl` | Delegate to client, no HTTP logic |
| Client | `BookingClient` | `BookingWebClientImpl` | WebClient call, `@Value` injection, catch → `BookingApiException` |
| Client | `TransactionClient` | `WebClientTransactionClient` | WebClient call, `@Value` injection, surfaces 409 to global handler |

**Global exception handling** (`GlobalExceptionHandler`):

| Exception | HTTP Status | Response body |
|---|---|---|
| `WebClientResponseException.Conflict` (409 from Serviex) | `500` | `{"code": "...", "Error": ...}` |
| `BookingApiException` | `500` | `{"message": "..."}` |

