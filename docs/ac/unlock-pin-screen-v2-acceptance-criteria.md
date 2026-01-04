# Unlock PIN Screen V2 - Acceptance Criteria

## Overview
This document defines the acceptance criteria for the PIN unlock screen in the Mangala Wallet application. This screen provides secure authentication for various operations including app access, transaction verification, and sensitive feature enablement. The screen includes biometric authentication, rate limiting with lockout, and platform-specific messaging.

## Security Timeout Logic

### 1. Failed Attempt Handling

#### Attempt Counter
- **Given** I am on the unlock PIN screen
- **When** I enter an incorrect PIN
- **Then** The system should:
  - Increment incorrect attempts counter
  - Persist counter in secure storage
  - Show shake animation
  - Clear entered PIN
  - Allow immediate retry (up to 5 attempts)

#### Maximum Attempts Lockout
- **Given** I have entered incorrect PIN 5 times
- **When** The 5th incorrect attempt is made
- **Then** The system should:
  - Lock the keypad immediately
  - Display 5-minute countdown timer
  - Show error message: "Too many incorrect attempts. Please try again in X:XX"
  - Persist lock state in secure storage
  - Prevent any PIN entry during lockout

#### Countdown Timer Display
- **Given** The PIN entry is locked
- **When** Viewing the countdown
- **Then** I should see:
  - Red error text (#EF4444)
  - Time format: "M:SS" (e.g., "4:59")
  - Real-time countdown updates every second
  - Centered below PIN dots
  - Clear visibility of remaining time

#### Lockout Recovery
- **Given** The 5-minute lockout period has expired
- **When** The timer reaches 0:00
- **Then** The system should:
  - Re-enable the keypad
  - Hide error message
  - Reset attempt counter to 0
  - Allow PIN entry again
  - Update persisted state

## Unlock Cases

### 2. OPEN_APP (Case 1)

#### App Launch Authentication
- **Given** I launch the app
- **When** PIN is required
- **Then** I should:
  - See unlock screen immediately
  - Have "Forgot PIN?" option visible
  - Navigate to home on success
  - Have biometric option if enabled
  - No back button (can't exit without auth)

#### First Launch Without PIN
- **Given** No PIN is set up
- **When** Opening app
- **Then** The system should:
  - Skip unlock screen
  - Navigate directly to home
  - Prompt for PIN setup later

### 3. CHANGE_PIN (Case 2)

#### PIN Change Flow
- **Given** I want to change my PIN
- **When** Accessing from settings
- **Then** I should:
  - Verify current PIN first
  - See "Forgot PIN?" option
  - Navigate to setup new PIN on success
  - Return to settings on back

### 4. SHOW_WORDS_PHRASE (Case 3)

#### Recovery Phrase Access
- **Given** I want to view recovery phrase
- **When** Requesting access
- **Then** I should:
  - Authenticate with PIN
  - No "Forgot PIN?" option (security)
  - Navigate to phrase display on success
  - Return to previous screen on back

### 5. ADD_ACCOUNT (Case 4)

#### EVM Account Addition
- **Given** I want to add an EVM account
- **When** PIN verification required
- **Then** I should:
  - Authenticate before proceeding
  - No "Forgot PIN?" option
  - Navigate to EVM account creation on success
  - Cancel returns to previous screen

### 6. ADD_ACCOUNT_BITCOIN (Case 5)

#### Bitcoin Account Addition
- **Given** I want to add a Bitcoin account
- **When** PIN verification required
- **Then** I should:
  - Authenticate before proceeding
  - No "Forgot PIN?" option
  - Navigate to Bitcoin account creation on success
  - Cancel returns to previous screen

### 7. CONFIRM_DAPP (Case 6)

#### DApp Transaction Confirmation
- **Given** A DApp requests transaction signing
- **When** PIN verification required
- **Then** I should:
  - See unlock screen immediately
  - No "Forgot PIN?" option
  - Confirm transaction on success
  - Cancel rejects transaction
  - Show back button for cancellation
  - Callback invoked with result (true/false)

### 8. ENABLE_BIOMETRY (Case 7)

#### Biometric Setup
- **Given** I want to enable biometric authentication
- **When** Setting up biometrics
- **Then** I should:
  - Verify PIN first
  - See "Forgot PIN?" option
  - Enable biometric on success
  - Try biometric auth immediately
  - Return to settings on completion

### 9. VERIFY_SEND_TRANSACTION (Case 8)

#### Transaction Authorization
- **Given** I'm sending a transaction
- **When** Authorization required
- **Then** I should:
  - Authenticate with PIN
  - No "Forgot PIN?" option
  - Process transaction on success
  - Cancel aborts transaction
  - Show back button
  - Callback invoked with result (true/false)
  - Create PIN if not set (and continue)

### 10. BACKUP_ANTELOPE_ACCOUNT (Case 9)

#### Antelope Account Backup
- **Given** I need to backup Antelope account
- **When** PIN required
- **Then** I should:
  - Authenticate first
  - No "Forgot PIN?" option
  - Navigate to backup screen on success
  - Include account name in navigation

## Biometric Authentication

### 11. Biometric Priority

#### Auto-Biometric Prompt
- **Given** Biometrics are enabled
- **When** Unlock screen opens
- **Then** The system should:
  - Check device security status first
  - Prompt for biometric immediately
  - Show appropriate prompt text per platform
  - Fall back to PIN on failure
  - Skip if device not secure

#### Platform-Specific Prompts
- **Given** Different platforms
- **When** Biometric prompt shows
- **Then** I should see:
  - Android: Title only (no reason text)
  - iOS: Title + reason + button text
  - Desktop: No biometric support currently

### 12. Biometric States

#### Success Flow
- **Given** Biometric authentication succeeds
- **When** Verified
- **Then** The system should:
  - Navigate to appropriate destination
  - Skip PIN entry completely
  - Trigger success callbacks
  - Complete unlock flow

#### Failure Flow
- **Given** Biometric authentication fails
- **When** User cancels or fails
- **Then** The system should:
  - Show PIN entry interface
  - Enable manual PIN entry
  - Keep biometric button available
  - Allow retry via keypad button

### 13. Biometric Button

#### Manual Biometric Trigger
- **Given** Biometric is available and enabled
- **When** I tap the biometric button on keypad
- **Then** The system should:
  - Reset biometric state
  - Show authentication prompt
  - Handle success/failure appropriately
  - Maintain PIN as fallback

## Platform-Specific Content

### 14. Android Platform

#### Android Messaging
- **Given** I am on Android
- **When** Viewing unlock screen
- **Then** I should see:
  - Title: "Welcome back! 👋"
  - Description: "Enter your PIN to access your wallet. Your crypto is right where you left it."
  - Casual tone with emoji
  - Fingerprint/Face icon for biometric

### 15. iOS Platform

#### iOS Messaging
- **Given** I am on iOS
- **When** Viewing unlock screen
- **Then** I should see:
  - Title: "Enter Passcode"
  - Description: "Access your wallet with your secure passcode."
  - Professional tone, minimal emoji
  - Face ID/Touch ID icon

### 16. Desktop Platform

#### Desktop Messaging
- **Given** I am on Desktop
- **When** Viewing unlock screen
- **Then** I should see:
  - Title: "Unlock Wallet"
  - Description: "Enter your PIN to access your wallet."
  - Technical, straightforward tone
  - No biometric option (currently)

## Visual Design

### 17. Screen Layout

#### Layout Structure
- **Given** The unlock screen displays
- **When** I view it
- **Then** I should see:
  - Gradient background (OnboardingGradientBackground)
  - Safe area insets respected
  - Two-section layout (top content, bottom keypad)
  - Smooth fade and slide animations
  - Staggered animation delays (300ms, 450ms)

#### Navigation Bar
- **Given** Different unlock cases
- **When** Viewing navigation
- **Then** I should see:
  - No back button for OPEN_APP
  - Back button for transactional cases
  - Empty title (design choice)
  - Standard navigation height

### 18. PIN Entry Interface

#### PIN Dots Display
- **Given** I am entering PIN
- **When** Digits are entered
- **Then** I should see:
  - 6 circular indicators
  - Same visual as setup screen
  - White border when empty
  - White filled when entered
  - Shake animation on error

#### Entry Animations
- **Given** Screen interactions
- **When** States change
- **Then** I should see:
  - Fade in (600ms duration, 300ms delay)
  - Slide up from bottom (1/4 height)
  - Shake on incorrect PIN
  - Smooth transitions

### 19. Keypad Interface

#### Keypad Layout
- **Given** The keypad is displayed
- **When** I view it
- **Then** I should see:
  - Same layout as setup screen
  - 3x4 grid with 0 at bottom
  - Delete button (bottom right)
  - Biometric button (bottom left, if available)
  - 64dp horizontal padding

#### Keypad States
- **Given** Different states
- **When** Interacting
- **Then** The keypad should be:
  - Enabled: Normal interaction
  - Disabled: During lockout period
  - Visually disabled when locked
  - Re-enabled after timeout

### 20. Forgot PIN Link

#### Forgot PIN Visibility
- **Given** Different unlock cases
- **When** Viewing screen
- **Then** "Forgot PIN?" should be:
  - Visible for: OPEN_APP, CHANGE_PIN, ENABLE_BIOMETRY
  - Hidden for: Security-sensitive operations
  - Blue color (#3B90FF)
  - 16dp padding for touch target

#### Forgot PIN Action
- **Given** I tap "Forgot PIN?"
- **When** Navigating
- **Then** I should:
  - Navigate to ForgotPinScreen
  - Potentially lose wallet access
  - See recovery options
  - Understand consequences

## State Management

### 21. Persistence

#### State Persistence
- **Given** App lifecycle changes
- **When** State needs preservation
- **Then** The system should persist:
  - Incorrect attempt count
  - Lock state (LOCKED/UNLOCKING)
  - Lockout timer remaining
  - Current unlock case
  - All in secure storage

#### State Recovery
- **Given** App restarts during lockout
- **When** Returning to unlock screen
- **Then** The system should:
  - Restore attempt count
  - Continue lockout if active
  - Calculate remaining time
  - Maintain security state
  - Resume where left off

### 22. Navigation Management

#### Success Navigation
- **Given** PIN verified successfully
- **When** Navigating
- **Then** The system should:
  - Reset attempt counter
  - Navigate per unlock case
  - Invoke success callbacks
  - Clear sensitive data
  - Update navigation stack appropriately

#### Callback Handling
- **Given** Callbacks provided
- **When** Events occur
- **Then** The system should:
  - Call onUnlockSuccess on success
  - Call unlockPinCallback(true) on success
  - Call unlockPinCallback(false) on cancel
  - Handle disposal properly
  - Prevent double invocation

## Security Enhancements

### 23. Advanced Security *[Enhancements]*

#### Progressive Delay
- **Given** Multiple failed attempts
- **When** Before lockout
- **Then** Consider implementing:
  - 1 second delay after 2nd attempt
  - 2 seconds after 3rd attempt
  - 5 seconds after 4th attempt
  - Full lockout after 5th
  - Better user experience

#### Anomaly Detection *[Enhancement]*
- **Given** Suspicious activity
- **When** Patterns detected
- **Then** The system could:
  - Track unusual access times
  - Detect rapid attempts
  - Flag location changes
  - Require additional verification
  - Alert user of suspicious activity

### 24. Recovery Options *[Enhancements]*

#### Emergency Access Code
- **Given** User locked out
- **When** Needing emergency access
- **Then** Could provide:
  - One-time recovery codes
  - Email/SMS verification
  - Security questions
  - Time-delayed recovery
  - Account recovery flow

#### Adaptive Timeouts
- **Given** Repeated lockouts
- **When** Pattern emerges
- **Then** The system could:
  - Increase timeout duration
  - 5 min → 15 min → 1 hour → 24 hours
  - Require account recovery
  - Contact support threshold
  - Security alert to user

## Accessibility

### 25. Screen Reader Support

#### Content Announcements
- **Given** Screen reader active
- **When** Using the screen
- **Then** Users should hear:
  - "Enter PIN to unlock wallet"
  - Number of attempts remaining
  - Lockout timer updates
  - Clear error messages
  - Success confirmation

#### Interactive Elements
- **Given** Accessibility enabled
- **When** Navigating
- **Then** All elements should:
  - Have proper labels
  - Announce state changes
  - Provide hints
  - Support gestures
  - Clear navigation order

### 26. Visual Accessibility

#### High Contrast
- **Given** High contrast mode
- **When** Viewing screen
- **Then** Should provide:
  - Enhanced dot borders
  - Higher text contrast
  - Clear error visibility
  - Button boundaries
  - Timer readability

#### Motion Sensitivity
- **Given** Reduced motion enabled
- **When** Animations play
- **Then** Should:
  - Minimize shake animation
  - Reduce slide effects
  - Simple fade transitions
  - No distracting motion
  - Maintain functionality

## Testing Scenarios

### 27. Happy Path

#### Successful PIN Entry
1. Open unlock screen
2. Enter correct 6-digit PIN
3. See dots fill sequentially
4. Wait 200ms processing
5. Navigate to destination
6. Callbacks invoked
7. State cleared

#### Biometric Success
1. Screen opens
2. Biometric prompt appears
3. Authenticate successfully
4. Skip PIN entry
5. Navigate immediately
6. Complete flow

### 28. Security Testing

#### Lockout Flow
1. Enter wrong PIN once (attempt 1)
2. See shake animation
3. Enter wrong PIN 4 more times
4. See lockout message
5. Wait 5 minutes
6. Keypad re-enables
7. Enter correct PIN
8. Successfully unlock

#### Persistence Test
1. Enter 3 wrong attempts
2. Force close app
3. Reopen app
4. See 3 attempts used
5. Enter 2 more wrong
6. Get locked out
7. Verify timer continues

### 29. Edge Cases

#### Callback Disposal
1. Start transaction verification
2. Enter partial PIN
3. Navigate away/dispose
4. Verify callback(false) invoked
5. No memory leaks
6. Clean state

#### Rapid Input
- Enter 6 digits rapidly: Process correctly
- Spam delete button: Handle gracefully
- Quick back navigation: Cancel properly
- Biometric during PIN entry: Handle state
- Background during entry: Clear for security

## Performance Requirements

### 30. Response Times

- **Screen load**: < 100ms
- **Biometric prompt**: < 500ms
- **PIN validation**: < 200ms
- **Navigation**: < 300ms
- **Animation FPS**: 60 FPS
- **Timer updates**: Every 1000ms exactly

### 31. Resource Management

- **Memory usage**: < 30MB
- **CPU during idle**: < 5%
- **CPU during animation**: < 20%
- **Secure storage access**: Minimal
- **Background impact**: None
- **Battery drain**: Negligible

## Security Requirements

### 32. PIN Validation

- **Secure comparison**: Constant-time
- **No PIN logging**: Never log values
- **Memory clearing**: After validation
- **Secure storage**: Platform encryption
- **No plaintext**: Always encrypted

### 33. Lockout Security

- **Persisted securely**: Encrypted storage
- **Tamper-proof**: Can't bypass
- **Time validation**: Server time check *[Enhancement]*
- **Reset protection**: Admin only *[Enhancement]*
- **Audit logging**: Track attempts *[Enhancement]*

## Acceptance Criteria Summary

### Must Have (Current Implementation)
✅ 5-attempt limit with 5-minute lockout
✅ Persistent lockout state across app restarts
✅ 9 different unlock cases with specific flows
✅ Biometric authentication with PIN fallback
✅ Platform-specific messaging
✅ Forgot PIN option (context-aware)
✅ Shake animation on incorrect PIN
✅ Secure state persistence
✅ Callback support for async operations
✅ Countdown timer display

### Should Have (Reasonable Enhancements)
⭐ Progressive delays before lockout
⭐ Adaptive timeout increases
⭐ Emergency recovery codes
⭐ Anomaly detection
⭐ Improved accessibility
⭐ Server time validation
⭐ Audit logging
⭐ Push notification on lockout
⭐ Email alerts for suspicious activity
⭐ Remember device option

### Nice to Have (Future Enhancements)
💡 Multi-factor authentication
💡 Behavioral biometrics
💡 Location-based security
💡 Time-based access controls
💡 Delegated unlock (guardian)
💡 Hardware key support
💡 Voice authentication
💡 Pattern unlock alternative
💡 Panic PIN (duress code)
💡 Remote wipe capability