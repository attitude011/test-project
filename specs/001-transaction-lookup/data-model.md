# Data Model: Transaction Lookup

## Entities

### TransactionResponse
- amount: integer
- store: string
- currency: string
- users: array of User

### User
- name: string
- address: string

### ErrorResponse
- code: string
- Error: integer

## Validation Rules

- Transaction ID must be supplied in the path parameter.
- JWT must be present and valid for access to the endpoint.
- Upstream 409 errors must be translated to HTTP 500 with the configured error payload.
