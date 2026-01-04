# Backend Integration Guide

## Overview

This guide explains how to integrate the Mangala Wallet auth and passkey modules with the backend authentication service running on localhost:8089.

## Prerequisites

1. Backend service running on `http://localhost:8089`
2. Postman collection imported from `features/auth/docs/Auth-Gate-API.postman_collection.json`
3. Environment variables configured from `features/auth/docs/Auth-Gate-Environment.postman_environment.json`

## Architecture

```
┌─────────────────────┐     ┌──────────────────────┐     ┌─────────────────┐
│   Wallet App (KMP)  │────▶│   Auth Backend API   │────▶│  Hanko/Keycloak │
└─────────────────────┘     └──────────────────────┘     └─────────────────┘
         │                           │                             │
         ▼                           ▼                             ▼
   ┌─────────────┐           ┌─────────────┐             ┌─────────────┐
   │ PasskeyRepo │           │  /auth/*    │             │    WebAuthn │
   │  AuthRepo   │           │  endpoints  │             │     FIDO2   │
   └─────────────┘           └─────────────┘             └─────────────┘
```

## Key Changes for Integration

### 1. PasskeyRepository Implementation

Updated to use actual backend endpoints:
- Registration: `POST /auth/register` → `POST /auth/register/credential`
- Authentication: `POST /auth/login/initialize` → `POST /auth/login/complete`

### 2. Session Management

Implemented 30-day rolling sessions:
- Active users get automatic session extension
- Maximum session duration: 180 days
- Session extends by 30 days on each app activity

### 3. Backend Response Handling

- Proper DTO mapping for backend responses
- JWT token extraction and validation
- Error handling for network and server errors

## Usage Example

```kotlin
// Initialize modules with backend URL
val baseUrl = "http://localhost:8089/api/v1"

startKoin {
    modules(
        authModule(baseUrl),
        passkeyModule(baseUrl),
        // ... other modules
    )
}

// Use AuthDemoScreen to test integration
Navigator(AuthDemoScreen())
```

## Testing the Integration

### 1. Unit Tests
Run existing tests:
```bash
./gradlew :features:auth:test
```

### 2. Integration Tests
Run with backend:
```bash
# Start backend first
# Then run:
./gradlew :features:auth:test --tests "*BackendIntegrationTest*"
```

### 3. Manual Testing with Postman

1. **Register a new user:**
   - Run "1. Start Registration" 
   - Use the sessionId for "2. Complete Registration"

2. **Login with passkey:**
   - Run "1. Initialize Login"
   - Use the challenge for "2. Complete Login"

3. **Verify JWT:**
   - Use the access_token from login
   - Run "Decode Hanko JWT"

## Key Implementation Details

### PasskeyRepositoryImpl
- Manages session ID between registration steps
- Converts backend DTOs to domain models
- Handles Base64 encoding/decoding for WebAuthn data

### AuthRepositoryImpl
- JWT validation via `/auth/decode-hanko-jwt`
- Token refresh simulation (backend endpoint pending)
- Session validation

### SessionManager
- Secure storage of tokens using SecureStorageWrapper
- Rolling session extension logic
- Activity tracking for session management

### AuthenticationFlowManager
- Orchestrates multi-factor authentication flow
- Fallback logic: Passkey → Biometric → PIN
- Session persistence and restoration

## Security Considerations

1. **HTTPS in Production**: Change baseUrl to use HTTPS
2. **Certificate Pinning**: Implement for production
3. **Token Storage**: Uses platform-specific secure storage
4. **Session Expiry**: Automatic cleanup of expired sessions

## Troubleshooting

### Backend Connection Issues
- Verify backend is running: `curl http://localhost:8089/api/v1/actuator/health`
- Check network permissions in Android manifest
- For iOS, ensure local network access is allowed

### WebAuthn Issues
- Mock credentials are used in tests
- Real devices need actual WebAuthn implementation
- Platform-specific PasskeyManager implementations required

### Session Issues
- Clear app data to reset sessions
- Check SecureStorage implementation for platform
- Verify token expiry calculations

## Next Steps

1. Implement real WebAuthn on each platform
2. Add production backend endpoints
3. Implement proper error recovery flows
4. Add analytics for auth events
5. Implement account recovery flows