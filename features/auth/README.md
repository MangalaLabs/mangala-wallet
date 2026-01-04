# Authentication Module

This module provides a comprehensive authentication system that orchestrates passkey, biometric, and PIN authentication methods with automatic fallback strategies.

## Features

- Multi-factor authentication support
- Automatic fallback from Passkey → Biometric → PIN
- Secure session management with token refresh
- State management for UI integration
- Integration with existing PIN and Biometric modules

## Architecture

The module consists of several key components:

1. **AuthenticationFlowManager** - Orchestrates authentication methods
2. **SessionManager** - Manages secure token storage and refresh
3. **AuthRepository** - Handles backend authentication APIs
4. **AuthState** - Sealed class hierarchy for UI state management

## Setup

### Module Initialization

```kotlin
val authModule = authModule(baseUrl = "https://api.example.com")
```

### Required Dependencies

The auth module depends on:
- `:shared:passkey` - Passkey authentication
- `:core:biometry` - Biometric authentication  
- `:core:pin` - PIN authentication
- `:data:local` - Secure storage

## Usage

### Basic Authentication Flow

```kotlin
class LoginViewModel(
    private val authFlowManager: AuthenticationFlowManager
) : ViewModel() {
    
    fun login() {
        viewModelScope.launch {
            // Automatically tries passkey → biometric → PIN
            authFlowManager.authenticate()
            
            // Observe state changes
            authFlowManager.authState.collect { state ->
                when (state) {
                    is AuthState.Authenticated -> {
                        // Navigate to main screen
                    }
                    is AuthState.Error -> {
                        // Show error message
                    }
                    is AuthState.NotAuthenticated -> {
                        // Show PIN input
                    }
                }
            }
        }
    }
}
```

### Specific Authentication Methods

```kotlin
// Force passkey authentication
authFlowManager.authenticateWithPasskey()

// Force biometric authentication  
authFlowManager.authenticateWithBiometric()

// PIN authentication
authFlowManager.authenticateWithPin("1234")
```

### Passkey Registration

```kotlin
val success = authFlowManager.registerPasskey(
    userId = "user123",
    userName = "john.doe",
    userDisplayName = "John Doe"
)
```

### Session Management

```kotlin
val sessionManager: SessionManager = get()

// Check if session is valid
if (sessionManager.isSessionValid()) {
    // Continue with authenticated flow
}

// Refresh token if needed
authFlowManager.refreshTokenIfNeeded()

// Logout
authFlowManager.logout()
```

## Authentication States

The module provides a comprehensive state system:

```kotlin
sealed interface AuthState {
    object Initial
    object Loading
    
    sealed interface Authenticated {
        val userId: String
        val authMethod: AuthMethod
        
        data class WithPasskey(...)
        data class WithBiometric(...)
        data class WithPin(...)
    }
    
    sealed interface Error {
        val message: String
        val canRetry: Boolean
        
        data class PasskeyError(...)
        data class BiometricError(...)
        data class PinError(...)
        data class NetworkError(...)
    }
    
    object NotAuthenticated
}
```

## Integration with Voyager

```kotlin
class AuthScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { AuthScreenModel(get()) }
        val state by screenModel.state.collectAsState()
        
        when (state.authState) {
            is AuthState.Authenticated -> {
                // Navigate to main screen
                LocalNavigator.currentOrThrow.push(MainScreen())
            }
            is AuthState.Error -> {
                // Show error dialog
                ErrorDialog(
                    message = state.authState.message,
                    onRetry = { screenModel.authenticate() }
                )
            }
            is AuthState.NotAuthenticated -> {
                // Show PIN input
                PinInputScreen(
                    onPinEntered = { pin ->
                        screenModel.authenticateWithPin(pin)
                    }
                )
            }
        }
    }
}
```

## Security Features

1. **Secure Token Storage** - Uses platform-specific secure storage
2. **Automatic Token Refresh** - Refreshes tokens before expiry
3. **Session Validation** - Validates sessions with backend
4. **Multi-factor Support** - Supports multiple authentication methods
5. **Replay Attack Prevention** - Challenge-based authentication

## Error Handling

The module provides specific error types for different scenarios:

- `PasskeyError` - Passkey-specific errors with retry capability
- `BiometricError` - Biometric authentication failures
- `PinError` - PIN errors with attempt counting
- `NetworkError` - Network connectivity issues
- `UnknownError` - Unexpected errors

## Best Practices

1. Always check `isSupported()` before using passkeys
2. Implement proper error handling for each auth method
3. Provide clear user feedback during authentication
4. Use the automatic fallback for better UX
5. Regularly refresh tokens to maintain session

## Testing

```bash
# Run all tests
./gradlew :shared:auth:test

# Run specific test
./gradlew :shared:auth:test --tests "*SessionManagerTest"
```

## Configuration

Configure the module with your backend URL:

```kotlin
startKoin {
    modules(
        authModule(baseUrl = BuildConfig.API_BASE_URL),
        passkeyModule(baseUrl = BuildConfig.API_BASE_URL)
    )
}
```