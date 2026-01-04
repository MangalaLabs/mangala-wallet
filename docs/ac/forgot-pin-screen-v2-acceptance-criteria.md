# Forgot PIN Screen V2 - Acceptance Criteria

## Overview
This document defines the acceptance criteria for the Forgot PIN screen in the Mangala Wallet application. This screen provides recovery options when users cannot remember their PIN, offering two paths: restore with recovery phrase or reset the wallet entirely. The screen emphasizes security while providing clear guidance for account recovery.

## Entry Points

### 1. Screen Access

#### From Unlock Screen
- **Given** I am on the unlock PIN screen
- **When** I tap "Forgot PIN?" link
- **Then** I should:
  - Navigate to Forgot PIN screen
  - See recovery options
  - Have ability to go back
  - Maintain app state

#### Visibility Rules
- **Given** Different unlock contexts
- **When** Forgot PIN link visibility determined
- **Then** It should be:
  - Visible for: OPEN_APP, CHANGE_PIN, ENABLE_BIOMETRY
  - Hidden for: Security-sensitive operations (transactions, phrase access)
  - Context-appropriate display
  - Security-first approach

## Platform-Specific Content

### 2. Android Platform

#### Android Messaging
- **Given** I am on Android device
- **When** Viewing the screen
- **Then** I should see:
  - Title: "Forgot your PIN? 🤔"
  - Description: "No worries! You've got options. Your wallet is safe, and we'll help you get back in."
  - Restore icon: "🔑"
  - Reset icon: "🔄"
  - Casual, reassuring tone with emojis

#### Android Option Cards
- **Given** Android platform
- **When** Viewing options
- **Then** I should see:
  - Restore: "Restore with recovery phrase" / "Have your 12/24 words? Perfect! Restore your wallet in seconds."
  - Reset: "Start fresh" / "Reset everything and create a new wallet. Your current wallet will be erased."
  - Warning: "⚠️ Resetting will permanently delete your current wallet. Make sure you have your recovery phrase!"

### 3. iOS Platform

#### iOS Messaging
- **Given** I am on iOS device
- **When** Viewing the screen
- **Then** I should see:
  - Title: "FORGOT PIN?" (from localized strings)
  - Description: Long explanation about no PIN recovery (from MR.strings)
  - Restore icon: "🔐"
  - Reset icon: "⚠️"
  - Professional tone with minimal emojis

#### iOS Option Cards
- **Given** iOS platform
- **When** Viewing options
- **Then** I should see:
  - Restore: "Restore Wallet" / "Use your recovery phrase to restore access to your wallet"
  - Reset: "Reset Wallet" / "Clear all data and start with a new wallet"
  - Warning: "Important: Resetting will permanently remove your wallet. Ensure you have backed up your recovery phrase."

### 4. Desktop Platform

#### Desktop Messaging
- **Given** I am on Desktop
- **When** Viewing the screen
- **Then** I should see:
  - Title: "PIN Recovery Options"
  - Description: "Choose how you'd like to proceed with accessing your wallet."
  - Restore icon: "🔓"
  - Reset icon: "⚡"
  - Technical, professional tone

#### Desktop Option Cards
- **Given** Desktop platform
- **When** Viewing options
- **Then** I should see:
  - Restore: "Restore from Backup" / "Enter your recovery phrase to restore wallet access"
  - Reset: "Factory Reset" / "Delete all data and create a new wallet"
  - Warning: "Warning: Factory reset is irreversible. Backup your recovery phrase before proceeding."

## Visual Design

### 5. Screen Layout

#### Overall Structure
- **Given** The screen is displayed
- **When** I view the layout
- **Then** I should see:
  - OnboardingGradientBackground
  - Safe area insets respected
  - Back navigation button (top left)
  - Title and description section
  - Two option cards
  - Warning message at bottom

#### Navigation Bar
- **Given** The top navigation
- **When** Displayed
- **Then** I should see:
  - Back arrow in 40dp circular touch area
  - White arrow icon
  - 16dp padding
  - Returns to unlock screen on tap

### 6. Option Cards Design

#### Restore Wallet Card
- **Given** The restore option
- **When** Displayed
- **Then** I should see:
  - Blue gradient background (0xFF3B90FF to 0xFFC27DFF at 10% opacity)
  - Icon on left (32sp)
  - Title in white (18sp)
  - Description in light gray
  - 16dp rounded corners
  - Full width with 24dp horizontal margin

#### Reset Wallet Card
- **Given** The reset option
- **When** Displayed
- **Then** I should see:
  - Red gradient background (0xFFEF4444 to 0xFFDC2626 at 10% opacity)
  - Destructive styling
  - Icon on left (32sp)
  - Title in red (0xFFEF4444)
  - Clear danger indication
  - Same dimensions as restore card

#### Card Interaction
- **Given** Option cards
- **When** I tap
- **Then** I should:
  - See tap feedback
  - Navigate to respective screen
  - Maintain smooth transition
  - No double-tap issues

### 7. Animations

#### Content Entry Animation
- **Given** Screen loads
- **When** Content appears
- **Then** I should see:
  - 100ms initial delay
  - Fade in (600ms duration, 300ms delay)
  - Slide up from 25% position
  - Staggered animation for warning (450ms delay)
  - Smooth, professional feel

#### Exit Animation
- **Given** Navigating away
- **When** Screen exits
- **Then** I should see:
  - Fade out (400ms)
  - Slide down motion
  - Clean transition
  - No visual glitches

## Recovery Options

### 8. Restore Wallet Flow

#### Restore Navigation
- **Given** I tap restore option
- **When** Processing
- **Then** I should:
  - Navigate to RestoreWalletScreen
  - Enter recovery phrase there
  - Complete wallet restoration
  - Set new PIN after restore

#### Restore Requirements
- **Given** Restore process
- **When** Initiated
- **Then** User needs:
  - 12 or 24-word recovery phrase
  - Correct word order
  - Valid BIP39 phrase
  - Network connection for sync

### 9. Reset Wallet Flow

#### Reset Navigation
- **Given** I tap reset option
- **When** Processing
- **Then** I should:
  - Navigate to ResetWalletScreen
  - See final confirmation
  - Understand consequences
  - Proceed with caution

#### Reset Consequences
- **Given** Reset initiated
- **When** Confirmed
- **Then** The system will:
  - Delete all wallet data
  - Clear all accounts
  - Remove transaction history
  - Reset to fresh state
  - Cannot be undone

## Security Considerations

### 10. Data Protection

#### Before Reset Confirmation
- **Given** User attempts reset
- **When** On reset screen
- **Then** Should show:
  - Final warning message
  - Consequences clearly stated
  - No ambiguity about data loss
  - Confirmation required

#### Security Verification *[Enhancement]*
- **Given** High-risk action (reset)
- **When** User proceeds
- **Then** Could require:
  - Email verification
  - SMS confirmation
  - Security questions
  - Time delay (24 hours)
  - Multi-factor authentication

## Enhanced Recovery Options *[Enhancements]*

### 11. Backup Detection

#### Automatic Backup Check
- **Given** User forgot PIN
- **When** On recovery screen
- **Then** System could:
  - Check for cloud backups
  - Detect local backups
  - Show backup status
  - Guide to backup location
  - Simplify restore process

#### Backup Reminder
- **Given** No backup detected
- **When** User attempts reset
- **Then** Should:
  - Warn prominently
  - Suggest checking again
  - Provide backup locations to check
  - Offer to delay reset
  - Educational content about backups

### 12. Alternative Recovery Methods

#### Social Recovery *[Enhancement]*
- **Given** Social recovery enabled
- **When** PIN forgotten
- **Then** Could offer:
  - Guardian-based recovery
  - Multi-sig recovery
  - Trusted contacts system
  - Threshold signatures
  - Time-locked recovery

#### Security Questions *[Enhancement]*
- **Given** Questions were set up
- **When** Recovering access
- **Then** Could:
  - Answer security questions
  - Reset PIN without full reset
  - Maintain wallet data
  - Graduated security levels
  - Limited attempts

#### Recovery Email/SMS *[Enhancement]*
- **Given** Contact method verified
- **When** Recovery needed
- **Then** Could:
  - Send recovery link
  - Time-limited access
  - One-time PIN reset
  - Audit trail maintained
  - Geographic verification

### 13. Support Integration *[Enhancement]*

#### Help Center Access
- **Given** User needs help
- **When** On forgot PIN screen
- **Then** Could provide:
  - Direct support chat
  - FAQ section
  - Video tutorials
  - Step-by-step guides
  - Common issues resolution

#### Customer Support Escalation
- **Given** Self-service insufficient
- **When** User needs human help
- **Then** Could offer:
  - Support ticket creation
  - Live chat option
  - Callback request
  - Priority support (premium)
  - Identity verification process

## Advanced Features *[Enhancements]*

### 14. Smart Recovery

#### Recovery Phrase Assistant
- **Given** Partial phrase remembered
- **When** Attempting restore
- **Then** Could provide:
  - Word suggestions
  - Checksum validation
  - Common typo detection
  - Order hints (first/last words)
  - Partial recovery options

#### Biometric Recovery *[Enhancement]*
- **Given** Biometrics were enabled
- **When** PIN forgotten
- **Then** Could allow:
  - Biometric-only access
  - PIN reset via biometric
  - Limited access mode
  - Time-restricted access
  - Security downgrade warnings

### 15. Audit and Monitoring

#### Recovery Attempts Logging
- **Given** Recovery attempted
- **When** Any option used
- **Then** Should log:
  - Timestamp
  - Method attempted
  - Success/failure
  - Device information
  - Location (if permitted)

#### Suspicious Activity Detection
- **Given** Multiple recovery attempts
- **When** Pattern detected
- **Then** Could:
  - Alert user via email
  - Temporary lockdown
  - Require additional verification
  - Notify emergency contacts
  - Freeze high-value operations

### 16. Educational Content

#### Recovery Best Practices
- **Given** User on recovery screen
- **When** Viewing options
- **Then** Could show:
  - Why PIN can't be recovered
  - Importance of backups
  - Recovery phrase security
  - Prevention tips
  - Next steps guidance

#### Interactive Tutorial *[Enhancement]*
- **Given** First-time recovery
- **When** User unsure
- **Then** Could provide:
  - Guided walkthrough
  - Practice recovery (testnet)
  - Common mistakes to avoid
  - Security tips
  - Confidence building

## Warning and Confirmations

### 17. Warning Messages

#### Bottom Warning Display
- **Given** Warning section
- **When** Displayed
- **Then** I should see:
  - Centered text
  - Muted gray color (#8B95A7)
  - 12sp font size
  - Clear consequence statement
  - Adequate padding (24dp)

#### Warning Content Requirements
- **Given** Warning message
- **When** Written
- **Then** Must:
  - State permanence of reset
  - Mention recovery phrase importance
  - Be unambiguous
  - Use appropriate severity
  - Platform-appropriate tone

### 18. Confirmation Dialogs *[Enhancement]*

#### Reset Confirmation
- **Given** User selects reset
- **When** Before navigation
- **Then** Could show:
  - Modal confirmation dialog
  - Type "DELETE" to confirm
  - Checkbox acknowledgments
  - Final warning
  - Cancel option prominent

#### Restore Validation
- **Given** User selects restore
- **When** Before navigation
- **Then** Could verify:
  - User has phrase ready
  - Understands process
  - Network connectivity
  - Sufficient battery (mobile)
  - Time availability

## Accessibility

### 19. Screen Reader Support

#### Content Announcements
- **Given** Screen reader active
- **When** Navigating
- **Then** Should announce:
  - "Forgot PIN recovery options"
  - Each option clearly
  - Warning importance
  - Interactive elements
  - Navigation changes

#### Focus Management
- **Given** Accessibility enabled
- **When** Screen loads
- **Then** Should:
  - Focus on title first
  - Logical tab order
  - Clear option grouping
  - Warning emphasized
  - Back button accessible

### 20. Visual Accessibility

#### High Contrast Mode
- **Given** High contrast enabled
- **When** Viewing screen
- **Then** Should provide:
  - Enhanced card borders
  - Clear text contrast
  - Distinct option separation
  - Visible warning
  - Icon alternatives

#### Color Blind Considerations
- **Given** Color blindness
- **When** Viewing options
- **Then** Should:
  - Not rely on color alone
  - Use icons effectively
  - Text clearly indicates danger
  - Patterns for distinction
  - Shape differences

## Testing Scenarios

### 21. Happy Path - Restore

1. Tap "Forgot PIN?" on unlock screen
2. View forgot PIN screen
3. Read platform-specific messaging
4. Tap restore wallet option
5. Navigate to restore screen
6. Complete restoration
7. Set new PIN

### 22. Happy Path - Reset

1. Access forgot PIN screen
2. Read warning message
3. Tap reset wallet option
4. Navigate to reset screen
5. Confirm reset action
6. Complete wallet setup
7. Fresh start achieved

### 23. Edge Cases

#### Navigation Edge Cases
- Back during animation: Handle gracefully
- Double-tap options: Prevent duplicate navigation
- Quick back-forward: Maintain state
- Rotation during load: Preserve UI (mobile)
- Memory pressure: Handle appropriately

#### Recovery Edge Cases
- No network for restore: Clear error message
- Partial phrase entry: Helpful guidance
- Reset interruption: Safe state
- Multiple recovery attempts: Rate limiting
- Concurrent access: Prevent conflicts

## Performance Requirements

### 24. Response Times

- **Screen load**: < 100ms
- **Animation start**: < 100ms after trigger
- **Card tap response**: < 50ms
- **Navigation**: < 200ms
- **Back action**: Immediate

### 25. Resource Usage

- **Memory footprint**: < 25MB
- **CPU during animations**: < 20%
- **Battery impact**: Minimal
- **Network**: None required for display
- **Storage**: No persistence needed

## Security Requirements

### 26. Data Handling

- **No sensitive data displayed**
- **No PIN hints or clues**
- **No recovery phrase on screen**
- **Clear navigation history**
- **Secure screen transitions**

### 27. Recovery Security

- **Phrase validation required**
- **No phrase storage**
- **Secure navigation params**
- **Audit trail for attempts**
- **Rate limiting consideration**

## Acceptance Criteria Summary

### Must Have (Current Implementation)
✅ Two recovery options (restore/reset)
✅ Platform-specific messaging and tone
✅ Gradient option cards with icons
✅ Clear warning about data loss
✅ Smooth animations (fade/slide)
✅ Back navigation to unlock screen
✅ Navigation to respective recovery screens
✅ Visual distinction (blue vs red)
✅ Responsive card interactions

### Should Have (Reasonable Enhancements)
⭐ Backup detection before reset
⭐ Additional confirmation dialogs
⭐ Security verification for reset
⭐ Recovery attempt logging
⭐ Help/support integration
⭐ Educational content
⭐ Email/SMS recovery option
⭐ Security questions fallback
⭐ Partial phrase recovery help
⭐ Network connectivity check

### Nice to Have (Future Enhancements)
💡 Social recovery system
💡 Guardian-based recovery
💡 Biometric PIN reset
💡 AI-powered phrase assistance
💡 Video tutorial integration
💡 Live chat support
💡 Automated backup detection
💡 Cloud backup integration
💡 Time-locked recovery
💡 Progressive security levels