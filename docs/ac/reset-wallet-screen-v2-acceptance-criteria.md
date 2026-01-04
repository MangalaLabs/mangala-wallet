# Reset Wallet Screen V2 - Acceptance Criteria

## Overview
This document defines the acceptance criteria for the Reset Wallet screen in the Mangala Wallet application. This is a critical destructive operation screen that permanently deletes all wallet data. The screen implements multiple safeguards to prevent accidental data loss while providing a clear path for intentional wallet reset.

## Critical Implementation Note

### 1. Current Implementation Status

#### TODO: Reset Logic Missing
- **Given** The current implementation
- **When** Reviewing the code
- **Then** Note that:
  - Line 60: "TODO: Implement actual wallet reset logic here"
  - Currently only navigates back on confirmation
  - Actual data deletion not implemented
  - **CRITICAL**: Must implement before production

#### Required Reset Operations *[To Implement]*
- **Given** User confirms reset
- **When** Processing reset
- **Then** System must:
  - Clear all wallet data
  - Delete all accounts
  - Remove transaction history
  - Clear secure storage
  - Reset PIN
  - Clear biometric settings
  - Navigate to onboarding

## Platform-Specific Content

### 2. Android Platform

#### Android Messaging
- **Given** I am on Android device
- **When** Viewing the screen
- **Then** I should see:
  - Title: From localized strings ("Reset wallet")
  - Subtitle: "⚠️ This is permanent!"
  - Description: Localized warning message
  - Button: "Reset My Wallet 🔄"
  - Emoji-enhanced warnings

#### Android Warning Cards
- **Given** Android platform
- **When** Viewing warnings
- **Then** I should see:
  - 💰 "All your accounts and balances will be lost forever"
  - 🔑 "Your recovery phrase is the ONLY way to restore"
  - ♻️ "This action cannot be undone or reversed"

### 3. iOS Platform

#### iOS Messaging
- **Given** I am on iOS device
- **When** Viewing the screen
- **Then** I should see:
  - Title: Localized "Reset wallet"
  - Subtitle: Localized warning about losing assets
  - Description: Localized reset warning
  - Button: "Reset Wallet"
  - Professional tone

#### iOS Warning Cards
- **Given** iOS platform
- **When** Viewing warnings
- **Then** I should see:
  - 💰 "All wallet data will be permanently erased"
  - 🔑 "Ensure you have saved your recovery phrase"
  - ♻️ "You will need to set up a new wallet"

### 4. Desktop Platform

#### Desktop Messaging
- **Given** I am on Desktop
- **When** Viewing the screen
- **Then** I should see:
  - Title: "Factory Reset"
  - Subtitle: "Permanent Data Deletion"
  - Description: Technical warning message
  - Button: "Proceed with Reset"
  - Technical terminology

#### Desktop Warning Cards
- **Given** Desktop platform
- **When** Viewing warnings
- **Then** I should see:
  - 💰 "Complete removal of all wallet data"
  - 🔑 "Recovery phrase required for restoration"
  - ♻️ "Irreversible operation"

## Visual Design

### 5. Screen Layout

#### Overall Structure
- **Given** The screen displays
- **When** I view the layout
- **Then** I should see:
  - OnboardingGradientBackground
  - Safe area insets respected
  - Back navigation (top left)
  - Title section with red subtitle
  - Three warning cards
  - Confirmation checkbox
  - Red reset button
  - Two-level confirmation flow

#### Color Scheme
- **Given** Destructive action
- **When** Viewing elements
- **Then** Colors should be:
  - Subtitle: Red (#EF4444) for danger
  - Warning cards: Red gradient background
  - Button: Red (#EF4444) when enabled
  - Checkbox: Blue when checked (#3B90FF)
  - Consistent danger indication

### 6. Warning Cards Design

#### Card Appearance
- **Given** Warning cards
- **When** Displayed
- **Then** Each should have:
  - Red gradient background (10% opacity)
  - Start: #EF4444, End: #DC2626
  - 12dp rounded corners
  - 16dp padding
  - Icon (24sp emoji) on left
  - Warning text on right
  - 16dp spacing between cards

#### Warning Content
- **Given** Three warnings
- **When** User reads
- **Then** Should communicate:
  - Data loss permanence
  - Recovery phrase importance
  - Action irreversibility
  - Clear consequences
  - No ambiguity

### 7. Confirmation Controls

#### Checkbox Requirement
- **Given** Confirmation checkbox
- **When** Displayed
- **Then** Should:
  - Be unchecked initially
  - Required before button enables
  - Dark background (#1E293B at 50%)
  - Clickable entire row
  - Display confirmation text
  - 12dp rounded corners

#### Checkbox Text
- **Given** Checkbox label
- **When** Displayed
- **Then** Shows:
  - "I have read and am sure about the wallet reset"
  - Light gray color (#A5B4CB)
  - 14sp font size
  - Wraps properly on small screens

### 8. Reset Button States

#### Disabled State
- **Given** Checkbox unchecked
- **When** Viewing button
- **Then** Should show:
  - 50% opacity
  - Not clickable
  - Grayed appearance
  - Clear disabled state
  - Smooth transition to enabled

#### Enabled State
- **Given** Checkbox checked
- **When** Button enables
- **Then** Should show:
  - Full opacity (animated)
  - Red background (#EF4444)
  - Clickable
  - 300ms fade animation
  - Clear call-to-action

### 9. Confirmation Dialog

#### Dialog Appearance
- **Given** Reset button clicked
- **When** Dialog shows
- **Then** Should display:
  - Dark background (#1E293B)
  - 16dp rounded corners
  - Title: "Final Confirmation"
  - Warning text
  - Two action buttons

#### Dialog Actions
- **Given** Confirmation dialog
- **When** User interacts
- **Then** Options are:
  - "Reset Wallet" in red (#EF4444)
  - "Cancel" in gray (#64748B)
  - Dismiss on backdrop tap
  - Clear button labels
  - Appropriate button order

## Animations

### 10. Content Entry Animations

#### Staggered Animation
- **Given** Screen loads
- **When** Content appears
- **Then** Should animate:
  - 100ms initial delay
  - Warning cards: 600ms fade, 300ms delay
  - Bottom section: 600ms fade, 450ms delay
  - Slide up from below
  - Smooth, professional feel

#### Button Animation
- **Given** Checkbox interaction
- **When** State changes
- **Then** Should:
  - Animate opacity (300ms)
  - Smooth transition
  - No jarring changes
  - Visual feedback
  - Professional feel

## Security Safeguards

### 11. Multi-Level Confirmation

#### Current Implementation
- **Given** Reset process
- **When** User attempts
- **Then** Requires:
  - Level 1: Reading warnings
  - Level 2: Checkbox confirmation
  - Level 3: Dialog confirmation
  - Three deliberate actions
  - No accidental reset possible

#### Enhanced Confirmation *[Enhancement]*
- **Given** Additional security
- **When** Implementing
- **Then** Could add:
  - Type "DELETE" confirmation
  - PIN verification
  - Biometric confirmation
  - Time delay (countdown)
  - Email verification

### 12. Backup Verification *[Enhancement]*

#### Pre-Reset Backup Check
- **Given** Reset initiated
- **When** Before allowing
- **Then** Could:
  - Check for existing backups
  - Prompt to create backup
  - Export wallet data
  - Save recovery phrase
  - Verify phrase written down

#### Backup Confirmation Flow
- **Given** No backup detected
- **When** User proceeds
- **Then** Could:
  - Show extra warning
  - Require additional confirmation
  - Offer backup creation
  - Delay reset 24 hours
  - Contact support option

## Data Handling

### 13. Reset Operations *[To Implement]*

#### Data Deletion Scope
- **Given** Reset confirmed
- **When** Processing
- **Then** Must delete:
  - All wallet accounts
  - Transaction history
  - Address book
  - Settings preferences
  - PIN/biometric data
  - Cached data
  - Token balances

#### Secure Deletion
- **Given** Sensitive data
- **When** Deleting
- **Then** Should:
  - Overwrite memory
  - Clear secure storage
  - Invalidate sessions
  - Clear clipboard
  - Remove from backups

### 14. Post-Reset State

#### Clean State
- **Given** Reset complete
- **When** App restarts
- **Then** Should show:
  - Fresh onboarding
  - No residual data
  - Clean preferences
  - Default settings
  - Welcome experience

#### Recovery Prevention
- **Given** Data deleted
- **When** User tries recovery
- **Then** Should be:
  - Truly irreversible
  - No hidden backups
  - No recovery mode
  - Complete deletion
  - As advertised

## Enhanced Features *[Enhancements]*

### 15. Audit Trail

#### Reset Logging
- **Given** Reset performed
- **When** Logging
- **Then** Could record:
  - Timestamp
  - Device ID
  - Reason (if provided)
  - Wallet ID (anonymized)
  - Platform/version

#### Analytics Tracking
- **Given** Reset event
- **When** Tracking
- **Then** Could measure:
  - Reset frequency
  - Cancel rate
  - Time to decision
  - Platform distribution
  - User journey

### 16. Recovery Period *[Enhancement]*

#### Grace Period
- **Given** Accidental reset
- **When** Within timeframe
- **Then** Could offer:
  - 24-hour recovery window
  - Encrypted backup retention
  - Email recovery link
  - Support intervention
  - One-time restoration

#### Recovery Requirements
- **Given** Recovery requested
- **When** Within grace period
- **Then** Would require:
  - Identity verification
  - Original device
  - Email confirmation
  - Support approval
  - Security questions

### 17. Export Options *[Enhancement]*

#### Pre-Reset Export
- **Given** Reset pending
- **When** User wants backup
- **Then** Could export:
  - Transaction history CSV
  - Account addresses
  - Token list
  - Settings backup
  - QR codes

#### Export Security
- **Given** Data export
- **When** Creating
- **Then** Should:
  - Encrypt exports
  - Password protect
  - Time-limited links
  - Secure delivery
  - Audit trail

### 18. Support Integration *[Enhancement]*

#### Support Contact
- **Given** User uncertain
- **When** On reset screen
- **Then** Could offer:
  - Live chat
  - Support ticket
  - FAQ links
  - Video guide
  - Callback request

#### Assisted Reset
- **Given** Support case
- **When** Reset needed
- **Then** Could provide:
  - Screen sharing
  - Guided process
  - Verification help
  - Recovery assistance
  - Follow-up check

## Accessibility

### 19. Screen Reader Support

#### Content Announcements
- **Given** Screen reader active
- **When** Navigating
- **Then** Should announce:
  - "Destructive action warning"
  - Each warning clearly
  - Checkbox requirement
  - Button state changes
  - Dialog content

#### Focus Management
- **Given** Keyboard navigation
- **When** Using screen
- **Then** Should:
  - Focus on warnings first
  - Logical tab order
  - Trap focus in dialog
  - Return focus properly
  - Clear navigation

### 20. Visual Accessibility

#### High Contrast
- **Given** High contrast mode
- **When** Viewing
- **Then** Should provide:
  - Enhanced borders
  - Clear text contrast
  - Visible warnings
  - Distinct buttons
  - Red still visible

#### Color Blind Mode
- **Given** Color blindness
- **When** Viewing warnings
- **Then** Should:
  - Not rely on red alone
  - Use icons effectively
  - Text clarity paramount
  - Pattern differences
  - Shape distinctions

## Testing Scenarios

### 21. Happy Path

1. Navigate to reset screen
2. Read all warnings
3. Check confirmation checkbox
4. Button becomes enabled
5. Tap reset button
6. See confirmation dialog
7. Confirm in dialog
8. Wallet resets successfully

### 22. Cancellation Path

1. Navigate to reset screen
2. Read warnings
3. Decide against reset
4. Tap back button
5. Return safely
6. No data affected
7. Wallet intact

### 23. Safety Verification

1. Try clicking disabled button
2. Verify no action
3. Check checkbox
4. Button enables
5. Tap reset
6. Cancel in dialog
7. Verify no reset occurred

### 24. Edge Cases

#### Interaction Edge Cases
- Double-tap reset button: Single dialog only
- Quick check/uncheck: State syncs properly
- Dialog dismiss methods: All work correctly
- Background tap: Dismisses dialog
- Rotation during dialog: Maintains state

#### State Edge Cases
- Navigate away and back: Reset checkbox
- App background/foreground: Maintain state
- Memory pressure: Handle gracefully
- Network loss: Not required for UI
- Multiple quick navigations: Stable state

## Performance Requirements

### 25. Response Times

- **Screen load**: < 100ms
- **Animation start**: < 100ms
- **Checkbox response**: Immediate
- **Button enable animation**: 300ms
- **Dialog appearance**: < 50ms

### 26. Resource Usage

- **Memory footprint**: < 20MB
- **CPU during idle**: < 5%
- **CPU during animation**: < 15%
- **Battery impact**: Minimal
- **Network**: Not required

## Security Requirements

### 27. Data Protection

- **No data exposure**: During reset
- **Secure deletion**: Overwrite data
- **Memory clearing**: After reset
- **Session invalidation**: All tokens
- **Clipboard clearing**: Any copied data

### 28. Reset Security

- **Irreversibility**: Truly permanent
- **No backdoors**: No hidden recovery
- **Audit trail**: Log attempts
- **Rate limiting**: Prevent abuse
- **Device binding**: Consider device ID

## Acceptance Criteria Summary

### Must Have (Current Implementation)
✅ Platform-specific messaging
✅ Three warning cards with icons
✅ Red destructive color scheme
✅ Checkbox confirmation requirement
✅ Button disabled until checked
✅ Confirmation dialog
✅ Animated content entry
✅ Back navigation
❌ **MISSING**: Actual reset logic implementation

### Should Have (Reasonable Enhancements)
⭐ Implement actual wallet reset logic
⭐ Type "DELETE" confirmation
⭐ Backup verification before reset
⭐ Export data option
⭐ PIN/biometric verification
⭐ Audit logging
⭐ Support contact integration
⭐ 24-hour grace period
⭐ Enhanced accessibility
⭐ Clear success indication

### Nice to Have (Future Enhancements)
💡 Recovery period with encryption
💡 Assisted reset via support
💡 Video tutorial integration
💡 Blockchain-specific cleanup
💡 Multi-device coordination
💡 Progressive reset (partial)
💡 Reset reason tracking
💡 Automated backup creation
💡 Legal compliance features
💡 Reset scheduling option