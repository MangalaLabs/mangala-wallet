# Biometry Screen - Acceptance Criteria

## Overview
This document defines the acceptance criteria for the Biometry setup screen in the Mangala Wallet application. This screen allows users to enable biometric authentication (Face ID, Touch ID, Fingerprint) as a convenient alternative to PIN entry. The screen appears after PIN setup and adapts its messaging and behavior based on the platform and available biometric hardware.

## Screen Context

### 1. Entry Points and Navigation Flows

#### From PIN Setup Flow
- **Given** I have just completed PIN setup
- **When** Navigating from PIN confirmation
- **Then** I should:
  - See biometry setup screen
  - Have option to enable or skip
  - Continue to appropriate next screen
  - Maintain navigation context

#### Navigation Cases Based on PIN Setup Context

##### CREATE_NEW_WALLET Flow
- **Given** I'm creating a new wallet
- **When** I complete biometry decision
- **Then** I should navigate to:
  - Step4CreatingAccountScreen (account creation)
  - AccountOperationType.CREATE
  - Replace entire navigation stack
  - Continue wallet creation process

##### RESTORE_WALLET Flow
- **Given** I'm restoring a wallet
- **When** I complete biometry decision
- **Then** I should navigate to:
  - CreateWalletScreen with IMPORT_WALLET case
  - Pass mnemonic list and wallet name
  - Replace navigation stack
  - Continue import process

##### CHANGE_PIN Flow
- **Given** I changed my PIN
- **When** I complete biometry decision
- **Then** I should:
  - Navigate to HomeScreen
  - Replace entire navigation stack
  - Apply new biometry setting
  - Return to normal app usage

##### CREATE_NEW_PIN Flow
- **Given** I'm setting up initial PIN
- **When** I complete biometry decision
- **Then** I should navigate to:
  - Step4CreatingAccountScreen
  - AccountOperationType.IMPORT
  - Continue account import
  - Replace navigation stack

##### CREATE_NEW_PIN_AND_BACKUP_ANTELOPE Flow
- **Given** I need to backup Antelope account
- **When** I complete biometry decision
- **Then** I should:
  - Navigate to BackupAntelopeAccountScreen
  - Pass account name and blockchain UID
  - Replace navigation stack
  - Continue backup process

##### CREATE_NEW_PIN_AND_CONTINUE Flow
- **Given** I was prompted to create PIN mid-flow
- **When** I complete biometry decision
- **Then** I should:
  - Invoke callback with enabled status
  - Pop back to previous screen
  - Continue interrupted flow
  - Maintain original context

##### CREATE_NEW_PIN_AND_CONTINUE_HOME_SCREEN Flow
- **Given** I need to go directly to home
- **When** I complete biometry decision
- **Then** I should:
  - Navigate to HomeScreen
  - Replace entire stack
  - Start using app immediately
  - Apply biometry preference

## Platform-Specific Requirements

### 2. Android Platform

#### Android Title and Messaging
- **Given** I am on Android device
- **When** Viewing the screen
- **Then** I should see:
  - Title: "Enable fingerprint unlock"
  - Description: "Access Mangala wallet with your biometry. Quick, easy and secure."
  - Enable button: "Enable Biometry Unlock"
  - Skip button: "Skip"

#### Android Biometric Types
- **Given** Android device capabilities
- **When** Biometric is available
- **Then** The system should support:
  - ANDROID_FINGERPRINT (most common)
  - ANDROID_FACE_ID (newer devices)
  - Generic fallback icon displayed
  - No reason text in auth prompt (Android pattern)

### 3. iOS Platform

#### iOS Title and Messaging
- **Given** I am on iOS device
- **When** Viewing the screen
- **Then** I should see:
  - Title: "Face ID or Touch ID unlock"
  - Description: "Access Mangala wallet with your biometry. Quick, easy and secure."
  - Enable button: "Enable Biometry Unlock"
  - Skip button: "Skip"

#### iOS Biometric Types
- **Given** iOS device capabilities
- **When** Biometric is available
- **Then** The system should support:
  - IOS_FACE_ID (iPhone X and later)
  - IOS_TOUCH_ID (older iPhones, some iPads)
  - Appropriate icon displayed
  - Reason text in auth prompt (iOS requirement)

### 4. Desktop Platform

#### Desktop Limitations
- **Given** I am on Desktop
- **When** Checking biometric support
- **Then** The system should:
  - Show DESKTOP_FINGERPRINT enum exists
  - Currently no implementation
  - Skip biometry screen *[Current behavior]*
  - Future support possible *[Enhancement]*

## Visual Design

### 5. Screen Layout

#### Background and Container
- **Given** The screen is displayed
- **When** I view the layout
- **Then** I should see:
  - OnboardingGradientBackground
  - Full screen coverage
  - Safe area insets respected
  - Two-section layout (content/buttons)

#### Content Structure
- **Given** I am viewing the content
- **When** The screen loads
- **Then** I should see:
  - Top navigation bar with back button
  - Title and description (24dp padding)
  - Centered biometric icon (96dp size)
  - Bottom button section
  - Proper spacing throughout

### 6. Navigation Bar

#### Back Button
- **Given** I am on biometry screen
- **When** I tap back button
- **Then** I should:
  - Invoke onCancel callback
  - Pop to previous screen
  - Not enable biometry
  - Return to PIN setup flow

#### Navigation Title
- **Given** The navigation bar displays
- **When** I view it
- **Then** I should see:
  - Empty title (design choice)
  - Consistent with PIN screens
  - Standard navigation height
  - Back button on left

### 7. Biometric Icon Display

#### Icon Selection
- **Given** Different biometric types
- **When** Icon is displayed
- **Then** I should see:
  - Face ID icon for all types currently
  - 96dp size
  - White tint color
  - Centered horizontally
  - *[Enhancement]* Type-specific icons

#### Icon Positioning
- **Given** The icon section
- **When** Layout renders
- **Then** I should see:
  - 56dp spacing above icon
  - Centered in available space
  - Proper aspect ratio
  - Clear visibility

## User Interaction

### 8. Enable Biometry Flow

#### Enable Button Action
- **Given** I tap "Enable Biometry Unlock"
- **When** Processing the request
- **Then** The system should:
  - Trigger authentication prompt immediately
  - Show platform-specific prompt
  - Handle success/failure states
  - Update biometry preference

#### Authentication Prompt - Android
- **Given** Android biometric prompt
- **When** Displayed
- **Then** I should see:
  - Title: "Open App"
  - No reason text (empty string)
  - Cancel button: "Cancel"
  - Native Android UI

#### Authentication Prompt - iOS
- **Given** iOS biometric prompt
- **When** Displayed
- **Then** I should see:
  - Title: "Open App"
  - Reason: "Open App"
  - Cancel button: "Cancel"
  - Native iOS UI

### 9. Skip Biometry Flow

#### Skip Button Action
- **Given** I tap "Skip"
- **When** Processing
- **Then** The system should:
  - Not enable biometry
  - Navigate to next screen
  - Maintain PIN-only authentication
  - Complete setup flow

#### Skip Implications
- **Given** Biometry is skipped
- **When** Using the app
- **Then** I should:
  - Always use PIN for authentication
  - Can enable later in settings
  - No biometric prompts on unlock
  - Full app functionality maintained

## Biometry States

### 10. State Management

#### NONE State
- **Given** Initial state
- **When** Screen loads
- **Then** The system should:
  - Show enable/skip options
  - No authentication in progress
  - Buttons enabled
  - Ready for user input

#### UNLOCKING State
- **Given** Authentication triggered
- **When** Biometric prompt active
- **Then** The system should:
  - Show native biometric UI
  - Await user authentication
  - Disable screen interactions
  - Process in background

#### SUCCESS State
- **Given** Biometric authenticated successfully
- **When** Verification complete
- **Then** The system should:
  - Enable biometric preference
  - Navigate to next screen
  - Invoke success callbacks
  - Store preference securely

#### FAIL State
- **Given** Biometric authentication failed
- **When** User cancels or fails
- **Then** The system should:
  - Invoke onBiometryCallback(false)
  - Pop to previous screen
  - Not enable biometry
  - Allow retry from settings later

## Callback Handling

### 11. Callback Management

#### onBiometryCallback
- **Given** Callback is provided
- **When** Biometry decision made
- **Then** The system should:
  - Call with true if enabled
  - Call with false if skipped/failed
  - Handle null gracefully
  - Invoke before navigation

#### onCancel Callback
- **Given** Cancel action triggered
- **When** Back button pressed
- **Then** The system should:
  - Invoke onCancel()
  - Pop navigation stack
  - Not enable biometry
  - Return to previous context

### 12. Navigation Callbacks

#### Success Navigation
- **Given** Biometry decision complete
- **When** Navigating away
- **Then** The system should:
  - Navigate based on PIN case
  - Pass required parameters
  - Maintain or replace stack as needed
  - Preserve user context

## Security Considerations

### 13. Biometric Security

#### Hardware Security
- **Given** Biometric hardware available
- **When** Enabling biometry
- **Then** The system should:
  - Use hardware-backed security
  - Never store biometric data
  - Link to secure keystore
  - Maintain PIN as fallback

#### Preference Storage
- **Given** Biometry preference set
- **When** Storing the setting
- **Then** The system should:
  - Store in secure preferences
  - Encrypt preference value
  - Sync with biometric hardware
  - Validate on each use

### 14. Fallback Mechanisms

#### Biometric Unavailable
- **Given** No biometric hardware
- **When** Checking availability
- **Then** The system should:
  - Skip biometry screen entirely
  - Continue with PIN only
  - Not show in settings
  - Handle gracefully

#### Biometric Disabled
- **Given** User disabled biometrics in OS
- **When** Attempting to use
- **Then** The system should:
  - Detect disabled state
  - Fall back to PIN
  - Show appropriate message
  - Guide to system settings *[Enhancement]*

## Advanced Features *[Enhancements]*

### 15. Multiple Biometric Options

#### Biometric Selection
- **Given** Multiple biometrics available
- **When** Setting up
- **Then** Could offer:
  - Choice between Face ID and Touch ID
  - Preference for primary method
  - Fallback order configuration
  - Method-specific settings

#### Adaptive Authentication
- **Given** Context-aware security
- **When** Different scenarios
- **Then** Could provide:
  - Quick unlock with Face ID
  - Transaction confirmation with fingerprint
  - High-security operations require both
  - Configurable security levels

### 16. Enhanced User Experience

#### Biometric Quality Check
- **Given** Poor biometric enrollment
- **When** Detecting issues
- **Then** Could:
  - Warn about weak enrollment
  - Suggest re-enrollment
  - Provide setup tips
  - Link to system settings

#### Tutorial Mode
- **Given** First-time setup
- **When** Enabling biometry
- **Then** Could show:
  - How biometry works
  - Security benefits
  - Privacy information
  - Best practices

### 17. Desktop Biometry Support *[Enhancement]*

#### Windows Hello Integration
- **Given** Windows with Hello
- **When** Desktop app runs
- **Then** Could support:
  - Fingerprint readers
  - Facial recognition
  - PIN as fallback
  - Seamless integration

#### macOS Touch ID Integration
- **Given** Mac with Touch ID
- **When** Desktop app runs
- **Then** Could support:
  - Touch Bar Touch ID
  - External Touch ID keyboard
  - Apple Watch unlock
  - Keychain integration

## Accessibility

### 18. Screen Reader Support

#### Content Description
- **Given** Screen reader active
- **When** Navigating screen
- **Then** Should announce:
  - "Biometry setup screen"
  - Button purposes clearly
  - Current biometric type
  - Security benefits

#### Interactive Elements
- **Given** Accessibility enabled
- **When** Using screen
- **Then** All elements should:
  - Have descriptive labels
  - Indicate button states
  - Provide action hints
  - Support keyboard navigation *[Desktop]*

### 19. Visual Accessibility

#### High Contrast Mode
- **Given** High contrast enabled
- **When** Viewing screen
- **Then** Should provide:
  - Clear button boundaries
  - Enhanced text contrast
  - Visible icon outlines
  - Distinct interactive elements

#### Large Text Support
- **Given** Large text enabled
- **When** Viewing screen
- **Then** Layout should:
  - Scale text appropriately
  - Maintain button sizes
  - Wrap text if needed
  - Preserve usability

## Testing Scenarios

### 20. Happy Path - Enable

1. Complete PIN setup
2. See biometry screen
3. View platform-specific title
4. Tap "Enable Biometry Unlock"
5. Authenticate successfully
6. Navigate to next screen
7. Biometry enabled for future

### 21. Happy Path - Skip

1. Complete PIN setup
2. See biometry screen
3. View enable option
4. Tap "Skip"
5. Navigate to next screen
6. PIN-only authentication
7. Can enable later

### 22. Edge Cases

#### Rapid Button Taps
- Multiple enable taps: Handle once only
- Enable then skip quickly: Process first only
- Back during auth: Cancel properly
- Navigation during prompt: Handle gracefully

#### Authentication Interruptions
1. Start biometric authentication
2. Receive phone call (mobile)
3. Return to app
4. Should resume or restart gracefully
5. No stuck states
6. Clear error handling

### 23. Platform-Specific Testing

#### Android Testing
- Test with fingerprint scanner
- Test with face unlock
- Test with no biometric hardware
- Test with disabled biometrics
- Verify no reason text in prompt

#### iOS Testing
- Test with Face ID
- Test with Touch ID  
- Test on devices without biometric
- Test with disabled biometrics
- Verify reason text appears

## Performance Requirements

### 24. Response Times

- **Screen load**: < 100ms
- **Button tap response**: < 50ms
- **Biometric prompt**: < 200ms
- **Navigation**: < 300ms
- **Preference save**: < 100ms

### 25. Resource Usage

- **Memory footprint**: < 20MB
- **CPU usage**: < 10%
- **Battery impact**: Minimal
- **Storage**: Preference only
- **Network**: None required

## Security Requirements

### 26. Biometric Binding

- **Hardware binding**: Required
- **Secure enclave**: iOS requirement
- **Keystore**: Android requirement
- **No biometric storage**: Policy
- **Encryption**: Platform-level

### 27. Privacy Protection

- **No biometric data collected**
- **No analytics on biometric type**
- **Local processing only**
- **User control maintained**
- **Clear data on app uninstall**

## Acceptance Criteria Summary

### Must Have (Current Implementation)
✅ Platform-specific titles (Android/iOS)
✅ Enable and Skip options
✅ Native biometric prompts
✅ Navigation based on PIN setup context
✅ State management (NONE/UNLOCKING/SUCCESS/FAIL)
✅ Callback support for external flows
✅ Secure preference storage
✅ Fallback to PIN always available
✅ Face ID/Touch ID/Fingerprint support

### Should Have (Reasonable Enhancements)
⭐ Type-specific icons (fingerprint vs face)
⭐ Desktop biometric support
⭐ Biometric quality warnings
⭐ Tutorial/explanation mode
⭐ Re-enrollment guidance
⭐ System settings deep links
⭐ Multiple biometric method support
⭐ Better error messages
⭐ Accessibility improvements
⭐ Context-aware authentication

### Nice to Have (Future Enhancements)
💡 Adaptive security levels
💡 Biometric-specific settings
💡 Windows Hello integration
💡 macOS Touch ID support
💡 Apple Watch unlock
💡 Behavioral biometrics
💡 Risk-based authentication
💡 Passwordless future
💡 WebAuthn integration
💡 Hardware key support