# Account Service API Documentation

## Base URL
```
http://localhost:8080/v1/account
```

## Authentication
All endpoints require JWT authentication. Include the token in the Authorization header:
```
Authorization: Bearer <jwt-token>
```

## Endpoints

### 1. Create Account
Creates a new user account.

**Endpoint:** `POST /create`

**Authorization:** Support User, WWW Service, Company Service

**Request Body:**
```json
{
  "name": "string (required)",
  "email": "string (optional, must be unique)",
  "phoneNumber": "string (optional, must be unique, E.164 format)"
}
```

**Response:**
```json
{
  "account": {
    "id": "string",
    "name": "string",
    "email": "string",
    "phoneNumber": "string",
    "memberSince": "ISO-8601 timestamp",
    "confirmedAndActive": "boolean",
    "support": "boolean",
    "photoUrl": "string"
  }
}
```

**Status Codes:**
- 200: Account created successfully
- 400: Invalid request or validation error
- 409: Email or phone number already exists
- 401: Unauthorized

### 2. Get Account
Retrieves an account by user ID.

**Endpoint:** `GET /get`

**Authorization:** Support User, WWW Service, Account Service, Company Service, Whoami Service, Bot Service, Authenticated User

**Query Parameters:**
- `userId` (required): The user ID to retrieve

**Response:**
```json
{
  "account": {
    "id": "string",
    "name": "string",
    "email": "string",
    "phoneNumber": "string",
    "memberSince": "ISO-8601 timestamp",
    "confirmedAndActive": "boolean",
    "support": "boolean",
    "photoUrl": "string"
  }
}
```

**Status Codes:**
- 200: Account retrieved successfully
- 404: Account not found
- 401: Unauthorized

### 3. Get Account by Phone Number
Retrieves an account by phone number.

**Endpoint:** `GET /get_account_by_phonenumber`

**Authorization:** Support User, WWW Service, Company Service

**Query Parameters:**
- `phoneNumber` (required): The phone number to search for (E.164 format)

**Response:** Same as Get Account

**Status Codes:**
- 200: Account retrieved successfully
- 404: Account not found
- 401: Unauthorized

### 4. Update Account
Updates an existing account.

**Endpoint:** `POST /update`

**Authorization:** Support User, WWW Service, Company Service, Authenticated User

**Request Body:**
```json
{
  "id": "string (required)",
  "name": "string (optional)",
  "email": "string (optional, must be unique)",
  "phoneNumber": "string (optional, must be unique, E.164 format)",
  "confirmedAndActive": "boolean (optional)",
  "support": "boolean (optional)",
  "photoUrl": "string (optional)"
}
```

**Response:** Same as Get Account

**Status Codes:**
- 200: Account updated successfully
- 404: Account not found
- 409: Email or phone number already exists
- 401: Unauthorized

### 5. List Accounts
Lists accounts with pagination.

**Endpoint:** `GET /list`

**Authorization:** Support User

**Query Parameters:**
- `offset` (required): Pagination offset (0-based)
- `limit` (required): Maximum number of accounts to return (min: 0)

**Response:**
```json
{
  "accountList": {
    "accounts": [
      {
        "id": "string",
        "name": "string",
        "email": "string",
        "phoneNumber": "string",
        "memberSince": "ISO-8601 timestamp",
        "confirmedAndActive": "boolean",
        "support": "boolean",
        "photoUrl": "string"
      }
    ],
    "limit": "integer",
    "offset": "integer"
  }
}
```

**Status Codes:**
- 200: Accounts retrieved successfully
- 400: Invalid pagination parameters
- 401: Unauthorized

### 6. Update Password
Updates a user's password.

**Endpoint:** `POST /update_password`

**Authorization:** Support User, WWW Service, Authenticated User

**Request Body:**
```json
{
  "userId": "string (required)",
  "password": "string (required, min: 6 characters)"
}
```

**Response:**
```json
{
  "message": "password updated"
}
```

**Status Codes:**
- 200: Password updated successfully
- 404: Account not found
- 401: Unauthorized

### 7. Verify Password
Verifies a user's password.

**Endpoint:** `POST /verify_password`

**Authorization:** Support User, WWW Service

**Request Body:**
```json
{
  "email": "string (required)",
  "password": "string (required)"
}
```

**Response:**
```json
{
  "message": "password verified"
}
```

**Status Codes:**
- 200: Password verified successfully
- 401: Invalid email or password
- 404: Account not found

### 8. Get or Create Account
Retrieves an existing account or creates a new one if it doesn't exist.

**Endpoint:** `POST /get_or_create`

**Authorization:** Support User, WWW Service, Company Service

**Request Body:**
```json
{
  "email": "string (optional)",
  "phoneNumber": "string (optional, E.164 format)",
  "name": "string (optional)"
}
```

**Response:** Same as Get Account

**Status Codes:**
- 200: Account retrieved or created successfully
- 400: Invalid request
- 401: Unauthorized

### 9. Track Event
Tracks a user event for analytics.

**Endpoint:** `POST /track_event`

**Authorization:** Account Service, Authenticated User

**Request Body:**
```json
{
  "userId": "string (required)",
  "event": "string (required)"
}
```

**Response:**
```json
{
  "message": "event tracked"
}
```

**Status Codes:**
- 200: Event tracked successfully
- 401: Unauthorized

### 10. Sync User
Synchronizes user data with external systems.

**Endpoint:** `POST /sync_user`

**Authorization:** Account Service, Authenticated User

**Request Body:**
```json
{
  "userId": "string (required)"
}
```

**Response:**
```json
{
  "message": "user synced"
}
```

**Status Codes:**
- 200: User synced successfully
- 401: Unauthorized

### 11. Request Password Reset
Initiates a password reset process.

**Endpoint:** `POST /request_password_reset`

**Authorization:** Support User, WWW Service

**Request Body:**
```json
{
  "email": "string (required)"
}
```

**Response:**
```json
{
  "message": "password reset requested"
}
```

**Status Codes:**
- 200: Password reset requested successfully
- 404: Account not found
- 401: Unauthorized

### 12. Request Email Change
Requests an email change for a user.

**Endpoint:** `POST /request_email_change`

**Authorization:** Support User, WWW Service, Authenticated User

**Request Body:**
```json
{
  "userId": "string (required)",
  "email": "string (required, must be unique)"
}
```

**Response:**
```json
{
  "message": "email change requested"
}
```

**Status Codes:**
- 200: Email change requested successfully
- 404: Account not found
- 409: Email already exists
- 401: Unauthorized

### 13. Change Email
Confirms and changes a user's email.

**Endpoint:** `POST /change_email`

**Authorization:** Support User, WWW Service

**Request Body:**
```json
{
  "userId": "string (required)",
  "email": "string (required)"
}
```

**Response:**
```json
{
  "message": "email changed"
}
```

**Status Codes:**
- 200: Email changed successfully
- 404: Account not found
- 401: Unauthorized

## Error Responses

All error responses follow this format:

```json
{
  "code": "integer error code",
  "message": "string error message",
  "details": "optional additional details"
}
```

### Common Error Codes
- 1000: Validation error
- 1001: Authentication required
- 1002: Authorization failed
- 1003: Resource not found
- 1004: Resource already exists
- 1005: Internal server error

## Rate Limiting

- 100 requests per minute per IP address
- 1000 requests per hour per user
- Rate limit headers included in responses:
  - `X-RateLimit-Limit`: Maximum requests allowed
  - `X-RateLimit-Remaining`: Remaining requests
  - `X-RateLimit-Reset`: Time when limit resets (Unix timestamp)

## Versioning

API version is included in the URL path (`/v1/`). Breaking changes will result in a new version.

## Data Validation

- Email addresses: RFC 5322 compliant
- Phone numbers: E.164 format (e.g., +1234567890)
- Passwords: Minimum 6 characters
- Names: Maximum 100 characters

## Pagination

List endpoints support offset-based pagination:
- `offset`: Starting position (0-based)
- `limit`: Maximum items per page (1-100)

Example: `GET /list?offset=20&limit=10` returns items 21-30.

## Sorting

List endpoints return accounts sorted by `memberSince` in descending order (newest first).

## Webhooks

The service emits webhooks for important events:
- `account.created`
- `account.updated`
- `password.changed`
- `email.changed`

Webhook payload includes the full account object and event metadata.

## Testing

Use the following test credentials:

```json
{
  "email": "test@example.com",
  "password": "test123",
  "userId": "test-user-id"
}
```

## Support

For API issues or questions:
1. Check the error response for details
2. Verify authentication and authorization
3. Ensure request format matches documentation
4. Contact support with the request ID from response headers