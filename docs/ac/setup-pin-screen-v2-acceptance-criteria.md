# Setup PIN Screen V2 - Acceptance Criteria

## Overview
This document defines the acceptance criteria for the PIN setup screen in the Mangala Wallet application. This screen is the first step in the two-step PIN creation flow, where users create a 6-digit PIN to secure their wallet access. The screen adapts its messaging and behavior based on the platform (Android, iOS, Desktop) and the context of PIN creation.

## Screen Context Types

### 1. PIN Setup Cases

#### CREATE_NEW_WALLET
- **Given** I am creating a new wallet
- **When** I reach the PIN setup step
- **Then** I should:
  - See PIN setup screen after wallet creation
  - Navigate to confirm PIN screen after entering 6 digits
  - Return to previous screen on back navigation
  - Have PIN stored securely after confirmation

#### RESTORE_WALLET
- **Given** I am restoring an existing wallet
- **When** I complete seed phrase import
- **Then** I should:
  - See PIN setup screen after successful import
  - Set a new PIN for this device
  - Navigate through confirmation flow
  - Access restored wallet after PIN setup

#### CREATE_NEW_PIN
- **Given** I am setting up PIN for the first time
- **When** I access PIN setup
- **Then** I should:
  - Create initial PIN for the app
  - Navigate to confirmation screen
  - Return to previous screen on cancel
  - Enable app security after completion

#### CHANGE_PIN
- **Given** I want to change my existing PIN
- **When** I access change PIN from settings
- **Then** I should:
  - Enter new PIN (different from current)
  - Confirm new PIN
  - Navigate to home screen on completion
  - Use new PIN for future access

#### CREATE_NEW_PIN_AND_BACKUP_ANTELOPE
- **Given** I need to setup PIN and backup Antelope account
- **When** I enter this flow
- **Then** I should:
  - Create PIN first
  - Navigate to Antelope backup after PIN confirmation
  - Complete both security steps sequentially
  - Access account after both steps complete

#### CREATE_NEW_PIN_AND_CONTINUE
- **Given** I skipped PIN setup but triggered action requiring PIN
- **When** The system prompts for PIN setup
- **Then** I should:
  - Set up PIN before continuing
  - Return to previous action after setup
  - Complete original intended action
  - Not lose context or data

#### CREATE_NEW_PIN_AND_CONTINUE_HOME_SCREEN
- **Given** I need to setup PIN and continue to home
- **When** I complete PIN setup
- **Then** I should:
  - Navigate directly to home screen
  - Replace entire navigation stack
  - Have PIN protection enabled
  - Start using the app immediately

## Platform-Specific Content

### 2. Android Platform

#### Title and Messaging
- **Given** I am on Android device
- **When** Viewing the setup screen
- **Then** I should see:
  - Title: "Secure your wallet 🔐"
  - Description: "Keep your crypto safer than your DMs 💬 PIN protects this app only."
  - Security indicator: "🛡️ Your PIN stays on this device only • 🔒 Encrypted with military-grade security"
  - Casual, emoji-rich messaging style

#### Android-Specific Considerations
- **Given** Android platform requirements
- **When** Using the screen
- **Then** The app should:
  - Support back button navigation
  - Handle app switching gracefully
  - Store PIN in encrypted SharedPreferences
  - Support fingerprint after PIN setup *[Enhancement]*

### 3. iOS Platform

#### Title and Messaging
- **Given** I am on iOS device
- **When** Viewing the setup screen
- **Then** I should see:
  - Title: "Create Your Passcode"
  - Description: "Quick access to your wallet with a 6-digit code that only you know. ✨"
  - Security indicator: "🛡️ Device-only storage • Bank-level encryption"
  - Professional tone with minimal emoji usage

#### iOS-Specific Considerations
- **Given** iOS platform requirements
- **When** Using the screen
- **Then** The app should:
  - Follow iOS HIG guidelines
  - Support swipe-back gesture
  - Store PIN in Keychain
  - Support Face ID/Touch ID after setup *[Enhancement]*

### 4. Desktop Platform

#### Title and Messaging
- **Given** I am on Desktop
- **When** Viewing the setup screen
- **Then** I should see:
  - Title: "Set Access PIN"
  - Description: "Create a 4-6 digit PIN for quick wallet access on this device."
  - Security indicator: "🔒 AES-256 encryption • Device-specific authentication • Cleared if browser data reset"
  - Technical, professional messaging

#### Desktop-Specific Considerations
- **Given** Desktop platform requirements
- **When** Using the screen
- **Then** The app should:
  - Support keyboard input *[Enhancement]*
  - Show browser-specific warnings
  - Handle window resizing
  - Warn about browser data clearing impact

## Visual Design

### 5. Screen Layout

#### Background and Container
- **Given** The screen is displayed
- **When** I view the layout
- **Then** I should see:
  - Onboarding gradient background
  - Safe area insets respected
  - Full-screen coverage
  - Smooth fade-in animations

#### Content Structure
- **Given** I am viewing the content
- **When** The screen loads
- **Then** I should see:
  - Top navigation bar with back button
  - Title and description section
  - PIN dots indicator (center)
  - Numeric keypad (bottom)
  - Security indicator text (footer)

### 6. Navigation Bar

#### Back Button
- **Given** I am on the PIN setup screen
- **When** I tap the back button
- **Then** I should:
  - Reset any entered PIN digits
  - Invoke onPinSetupCancel callback
  - Navigate based on PIN case:
    - CREATE_NEW_WALLET/RESTORE_WALLET: Pop to previous
    - CHANGE_PIN: Replace stack with home
    - Others: Context-specific navigation

#### Navigation Title
- **Given** The navigation bar is displayed
- **When** I view it
- **Then** I should see:
  - Empty title (design choice)
  - Centered layout
  - Standard height
  - Consistent with app navigation

## PIN Entry Interface

### 7. PIN Dots Display

#### Visual Representation
- **Given** I am entering my PIN
- **When** I view the dots
- **Then** I should see:
  - 6 circular indicators
  - 16dp size per dot
  - 16dp spacing between dots
  - White border (1dp) when empty
  - White filled dot (10dp) when entered

#### Entry Animation
- **Given** I enter a digit
- **When** The dot updates
- **Then** I should see:
  - Immediate visual feedback
  - Smooth fill animation
  - No delay in visual update
  - Clear distinction between filled/empty

### 8. Shake Animation

#### Error Feedback
- **Given** An error occurs (e.g., PIN mismatch)
- **When** Error animation triggers
- **Then** I should see:
  - Horizontal shake animation
  - Quick left-right movement
  - Visual attention grabber
  - Auto-reset after animation

#### Animation Timing
- **Given** Shake animation is triggered
- **When** It executes
- **Then** It should:
  - Complete within 500ms
  - Move 10dp left and right
  - Return to center position
  - Not affect other UI elements

## Numeric Keypad

### 9. Keypad Layout

#### Button Grid
- **Given** I view the keypad
- **When** The screen displays
- **Then** I should see:
  - 3x4 grid layout
  - Numbers 1-9, 0 at bottom center
  - Delete button (bottom right)
  - Biometric button (bottom left) *[Platform dependent]*
  - Equal spacing (32dp vertical)
  - Horizontal padding (64dp)

#### Button Design
- **Given** I view keypad buttons
- **When** They are displayed
- **Then** Each button should have:
  - Circular shape
  - 64dp diameter
  - Number centered
  - Clear touch target
  - Visual feedback on press

### 10. Keypad Interaction

#### Number Input
- **Given** The keypad is enabled
- **When** I tap a number
- **Then** I should:
  - See immediate visual feedback
  - Have digit added to PIN
  - See corresponding dot filled
  - Hear haptic feedback *[Enhancement]*
  - Not exceed 6 digits

#### Delete Function
- **Given** I have entered digits
- **When** I tap delete
- **Then** I should:
  - Remove last entered digit
  - See corresponding dot empty
  - Be able to delete all digits
  - Have no effect when PIN is empty

#### Keypad States
- **Given** Different interaction states
- **When** State changes occur
- **Then** The keypad should:
  - Enabled: Accept all inputs
  - Disabled: Show visually disabled, reject inputs
  - Processing: Disable during navigation
  - Error: Re-enable after shake animation

### 11. Biometric Integration *[Enhancement]*

#### Biometric Button
- **Given** Device supports biometrics
- **When** On PIN setup screen
- **Then** I should see:
  - Biometric icon (fingerprint/face)
  - Platform-appropriate icon
  - Disabled state (setup phase)
  - Option to enable after PIN creation

#### Future Biometric Setup
- **Given** PIN is successfully created
- **When** Completing setup
- **Then** I should:
  - See option to enable biometrics
  - Link biometric to PIN
  - Use either method for future access
  - Maintain PIN as fallback

## PIN Validation

### 12. Input Validation

#### Length Requirement
- **Given** I am entering a PIN
- **When** I input digits
- **Then** The system should:
  - Require exactly 6 digits
  - Auto-proceed at 6 digits
  - Not allow more than 6
  - Show all 6 dots always

#### Auto-Navigation
- **Given** I have entered 6 digits
- **When** The 6th digit is entered
- **Then** The system should:
  - Wait 200ms (PIN_DELAY_ENTERED)
  - Navigate to confirm screen
  - Pass entered PIN securely
  - Maintain screen state

### 13. Security Patterns *[Enhancement]*

#### Weak PIN Detection
- **Given** I enter a PIN
- **When** It matches weak patterns
- **Then** I should see:
  - Warning for sequential (123456)
  - Warning for repeated (111111)
  - Warning for common patterns (123321)
  - Option to continue or change
  - Educational message about security

#### PIN History Check *[Enhancement]*
- **Given** I am changing my PIN
- **When** I enter the new PIN
- **Then** The system should:
  - Check against recent PINs
  - Prevent reuse of last 3 PINs
  - Show appropriate message
  - Require different PIN

## Text Content

### 14. Typography

#### Title Styling
- **Given** The title is displayed
- **When** I view it
- **Then** It should have:
  - Font size: 20sp
  - Font weight: Medium
  - Color: White (#FFFFFF)
  - Line height: 28sp
  - Letter spacing: -0.2sp
  - Inter font family

#### Description Styling
- **Given** The description is displayed
- **When** I view it
- **Then** It should have:
  - Font size: 14sp
  - Font weight: Normal
  - Color: Light gray (#A5B4CB)
  - Line height: 19.6sp
  - Letter spacing: -0.14sp
  - Inter font family

#### Security Indicator Styling
- **Given** The security text is displayed
- **When** I view it
- **Then** It should have:
  - Font size: 12sp
  - Font weight: Normal
  - Color: Muted gray (#8B95A7)
  - Line height: 16.8sp
  - Letter spacing: -0.12sp
  - Bottom padding: 16dp

## State Management

### 15. Screen State

#### PIN Entry State
- **Given** I interact with the screen
- **When** State changes occur
- **Then** The system should track:
  - Current PIN entered (0-6 digits)
  - Keypad enabled/disabled state
  - Animation trigger state
  - Navigation flow state
  - Biometry click state

#### State Persistence
- **Given** Screen interruptions occur
- **When** I return to the screen
- **Then** The state should:
  - Clear PIN for security
  - Maintain navigation context
  - Reset to initial state
  - Preserve callbacks

### 16. Navigation Flow

#### Flow States
- **Given** Different navigation states
- **When** Transitions occur
- **Then** The system should handle:
  - ShowCurrentScreen: Display PIN setup
  - ShowConfirmPinScreen: Navigate to confirmation
  - ShowHomeScreen: Complete and go home
  - ShowBackLastScreen: Return to previous
  - Error states appropriately

#### Callback Management
- **Given** Callbacks are provided
- **When** Events occur
- **Then** The system should:
  - Call onPinSetupSuccess after confirmation
  - Call onPinSetupCancel on back/cancel
  - Maintain callback references
  - Handle null callbacks gracefully

## Accessibility

### 17. Screen Reader Support

#### Content Description
- **Given** Screen reader is enabled
- **When** Navigating the screen
- **Then** Users should hear:
  - "Create PIN screen"
  - Number of digits entered
  - Button numbers when focused
  - Security information
  - Clear navigation instructions

#### Interactive Elements
- **Given** Using assistive technology
- **When** Interacting with elements
- **Then** All elements should:
  - Have proper content descriptions
  - Announce state changes
  - Provide action hints
  - Support navigation gestures

### 18. Visual Accessibility

#### High Contrast Mode
- **Given** High contrast is enabled
- **When** Viewing the screen
- **Then** I should see:
  - Enhanced borders on PIN dots
  - Higher contrast text
  - Clear button boundaries
  - Maintained readability

#### Text Scaling
- **Given** Large text is enabled
- **When** Viewing the screen
- **Then** The layout should:
  - Scale text appropriately
  - Maintain layout integrity
  - Keep elements accessible
  - Prevent overlapping

## Advanced Features *[Enhancements]*

### 19. Keyboard Input Support

#### Desktop Keyboard Entry
- **Given** I am on desktop
- **When** The screen is focused
- **Then** I should be able to:
  - Type numbers on keyboard
  - Use backspace to delete
  - Use Enter to confirm (at 6 digits)
  - See visual feedback
  - Have keypad as fallback

#### Keyboard Navigation
- **Given** Keyboard navigation is enabled
- **When** Using Tab/Arrow keys
- **Then** I should:
  - Navigate through keypad buttons
  - Activate with Space/Enter
  - Access all functions
  - See focus indicators

### 20. PIN Strength Indicator

#### Visual Strength Meter
- **Given** I enter PIN digits
- **When** Pattern is analyzed
- **Then** I should see:
  - Strength indicator bar
  - Color coding (red/yellow/green)
  - Strength label (Weak/Fair/Strong)
  - Real-time updates
  - Suggestions for improvement

#### Strength Calculation
- **Given** PIN analysis is enabled
- **When** Evaluating strength
- **Then** The system should check:
  - Sequential patterns
  - Repeated digits
  - Common PINs database
  - Birthday patterns
  - Keyboard patterns

### 21. Alternative Input Methods

#### Pattern Input *[Enhancement]*
- **Given** Pattern option is enabled
- **When** Setting up security
- **Then** I should see:
  - Option to use pattern instead
  - 3x3 or 4x4 grid
  - Visual pattern preview
  - Complexity requirements
  - Switch between PIN/pattern

#### Passphrase Option *[Enhancement]*
- **Given** Advanced security is needed
- **When** Setting up access
- **Then** I should have:
  - Option for longer passphrase
  - Alphanumeric input
  - Minimum length requirements
  - Strength validation
  - Secure storage

### 22. Security Enhancements

#### Rate Limiting *[Enhancement]*
- **Given** Multiple failed attempts
- **When** Threshold is reached
- **Then** The system should:
  - Implement progressive delays
  - Show countdown timer
  - Increase delay exponentially
  - Max out at reasonable limit
  - Reset after successful entry

#### Anti-Screenshot Protection
- **Given** Security is paramount
- **When** On PIN entry screen
- **Then** The system should:
  - Prevent screenshots *[Platform dependent]*
  - Block screen recording
  - Hide in app switcher
  - Clear from memory quickly
  - Show warning if detected

## Testing Scenarios

### 23. Happy Path

#### New Wallet Creation
1. Start wallet creation flow
2. Reach PIN setup screen
3. View platform-specific messaging
4. Enter 6 digits smoothly
5. See all dots fill
6. Auto-navigate to confirmation
7. Successfully create PIN

#### PIN Change Flow
1. Access change PIN from settings
2. See PIN setup screen
3. Enter new 6-digit PIN
4. Navigate to confirmation
5. Confirm new PIN
6. Return to home screen
7. Use new PIN successfully

### 24. Error Scenarios

#### Navigation Interruption
1. Enter 3 digits
2. Press back button
3. See confirmation *[Enhancement]*
4. Confirm cancellation
5. PIN cleared
6. Navigate appropriately
7. No partial state saved

#### App Background/Foreground
1. Enter 4 digits
2. Switch to another app
3. Return to wallet app
4. See PIN cleared (security)
5. Start entry again
6. Complete successfully
7. No data loss

### 25. Edge Cases

- **Rapid digit entry**: Handle without missing inputs
- **Rapid delete taps**: Clear smoothly without over-deleting
- **Screen rotation**: Maintain state and layout *[Mobile]*
- **Memory pressure**: Handle gracefully
- **Slow device**: Maintain responsiveness
- **Multiple fast navigations**: Prevent navigation bugs
- **Callback disposal**: Clean up properly

## Performance Requirements

### 26. Responsiveness

- **Screen load time**: < 100ms
- **Digit input response**: < 50ms
- **Animation frame rate**: 60 FPS
- **Navigation delay**: 200ms (intentional)
- **Shake animation**: < 500ms

### 27. Resource Usage

- **Memory footprint**: < 20MB
- **CPU usage**: < 10% idle, < 30% active
- **Battery impact**: Minimal
- **Storage**: Encrypted PIN only
- **Network**: None required

## Security Requirements

### 28. PIN Storage

- **Encryption**: AES-256 or platform equivalent
- **Storage location**: Platform secure storage
- **Access control**: App-only access
- **Biometric binding**: Optional enhancement
- **No plaintext**: Never store unencrypted

### 29. Security Best Practices

- **No logging**: Never log PIN values
- **Memory clearing**: Clear after use
- **Secure navigation**: Pass PIN securely
- **Anti-tampering**: Detect modifications
- **Secure random**: For any generation

## Acceptance Criteria Summary

### Must Have (Current Implementation)
✅ Platform-specific messaging (Android/iOS/Desktop)
✅ 6-digit PIN requirement
✅ Visual PIN dots indicator
✅ Numeric keypad with delete
✅ Auto-navigation after 6 digits
✅ Multiple PIN setup contexts
✅ Shake animation for errors
✅ Back navigation with cleanup
✅ State management
✅ Gradient background design

### Should Have (Reasonable Enhancements)
⭐ Weak PIN detection and warnings
⭐ Keyboard input support (Desktop)
⭐ Biometric setup option after PIN
⭐ Navigation confirmation dialogs
⭐ Haptic feedback on input
⭐ PIN strength indicator
⭐ Rate limiting for attempts
⭐ Screenshot prevention
⭐ Better error messages
⭐ Accessibility improvements

### Nice to Have (Future Enhancements)
💡 Pattern/passphrase alternatives
💡 PIN history checking
💡 Advanced strength analysis
💡 Custom PIN lengths (4-8)
💡 Voice input support
💡 Multi-factor setup
💡 Emergency access codes
💡 Time-based PIN expiry
💡 Geolocation-based security
💡 Behavioral biometrics