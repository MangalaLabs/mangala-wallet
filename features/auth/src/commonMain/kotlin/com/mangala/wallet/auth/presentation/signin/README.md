# Sign-In Screen

A modern sign-in screen for the Mangala Wallet app that uses passkey authentication with the AI Assistant UI/UX design.

## Overview

The Sign-In Screen provides a beautiful, user-friendly interface for passkey-based authentication. It leverages the existing auth module functionality while presenting the AI Assistant-themed UI.

## Features

- **Passkey Authentication**: Secure, passwordless sign-in using device biometrics
- **Beautiful UI**: Robot emoji animation, gradient button, and smooth transitions
- **Error Handling**: User-friendly error messages with retry options
- **Help Dialog**: "What is a passkey?" explanation for users
- **Session Management**: Creates 30-day sessions after successful authentication

## Usage

```kotlin
// Set as initial screen in App.kt
val initialScreen = SignInScreen()

// Or navigate from anywhere
navigator.push(SignInScreen())
```

## Flow

1. **Check Session**: On launch, checks for existing valid session
2. **Show UI**: If no session, shows sign-in UI with passkey button
3. **Authenticate**: Uses `AuthenticationFlowManager` for passkey auth
4. **Create Session**: Creates 30-day session on success
5. **Navigate**: Redirects to `ConversationUiScreen` (AI Assistant)

## Components

### SignInScreen
- Main screen with Compose UI
- Handles navigation and state observation
- Shows different UI based on auth state

### SignInScreenModel
- Extends `AuthScreenModel`
- Manages authentication state
- Reuses `AuthenticationFlowManager` and `SessionManager`
- Handles error mapping and help dialog

## States

- **Idle**: Initial state, shows sign-in UI
- **Loading**: Shows during authentication with appropriate messages
- **Error**: Shows error dialog with retry option
- **Authenticated**: Shows success animation before navigation
- **RequiresBiometric/RequiresPin**: Handled by auth flow manager

## UI Elements

1. **Robot Emoji**: Animated scaling effect
2. **Gradient Button**: Purple gradient with shadow
3. **Loading Spinner**: Shows during authentication
4. **Success Checkmark**: Animated success state
5. **Error Dialog**: Clear error messages with actions

## Integration

The screen integrates with:
- `AuthenticationFlowManager`: For passkey authentication
- `SessionManager`: For session creation and validation
- `ConversationUiScreen`: Navigation destination after sign-in

## Customization

To customize the screen:
1. Modify UI colors in the gradient brush
2. Change animations in `SignInContent`
3. Update text in the UI components
4. Adjust navigation destination in `LaunchedEffect`