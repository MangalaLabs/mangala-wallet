# Core Security Module

This module provides shared security infrastructure for the Mangala Wallet application.

## Overview

The `:core:security` module contains common security components that can be reused across different features:

- **Security Models**: Core types like `SecureAction`, `SecurityLevel`, and `SecurityCheckResult`
- **Policy Providers**: Interfaces and base implementations for security policy management
- **Configuration**: Security configuration providers for customizable security rules
- **Authentication**: Authentication manager interface for handling various auth methods

## Usage

### 1. Define Secure Actions

Each feature should define its own secure actions by extending `SecureAction`:

```kotlin
sealed class MyFeatureSecureAction : SecureAction() {
    object CreateItem : MyFeatureSecureAction() {
        override val actionId = "myfeature.create_item"
    }
}
```

### 2. Implement Security Policy Provider

Create a policy provider for your feature:

```kotlin
class MyFeatureSecurityPolicyProvider : BaseSecurityPolicyProvider<MyFeatureSecureAction>() {
    override fun getSecurityLevel(action: MyFeatureSecureAction): SecurityLevel {
        return when (action) {
            is MyFeatureSecureAction.CreateItem -> SecurityLevel.RequirePin
        }
    }
}
```

### 3. Check Security Requirements

Use the policy provider to check security requirements:

```kotlin
val securityCheck = policyProvider.checkSecurity(action, context)
when (securityCheck) {
    is SecurityCheckResult.Allowed -> {
        // Execute action directly
    }
    is SecurityCheckResult.AuthenticationRequired -> {
        // Show authentication UI
    }
    is SecurityCheckResult.Denied -> {
        // Show error message
    }
}
```

## Security Levels

- **None**: No authentication required
- **RequirePin**: PIN authentication required
- **RequireBiometryOrPin**: Biometric authentication with PIN fallback
- **Require2FA**: Two-factor authentication required

## Configuration

The module supports configurable security policies through `SecurityConfigProvider`. The default implementation provides:

- Amount-based thresholds for transaction security
- Feature toggles for security features
- Extensible configuration system

## Testing

The module includes comprehensive tests for all components. Run tests with:

```bash
./gradlew :core:security:test
```