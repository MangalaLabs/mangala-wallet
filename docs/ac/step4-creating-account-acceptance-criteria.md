# Step 4: Creating Account - Acceptance Criteria

## Overview
This document defines the acceptance criteria for Step 4 of the Vaulta account creation flow - the blockchain account creation screen. This screen performs the actual account creation on the blockchain after payment has been confirmed, showing real-time progress with animated steps.

## Screen Modes

### 1. Operation Types

#### CREATE Mode
- **Given** I have completed payment for a new account
- **When** I reach the creation screen
- **Then** I should see:
  - Title: "Building your account..."
  - Account creation steps specific to new accounts
  - Progress through key generation, purchase verification, and blockchain creation
  - Navigation to backup options on success

#### IMPORT Mode
- **Given** I am importing an existing account
- **When** I reach the import screen
- **Then** I should see:
  - Title: "Importing your account..."
  - Import-specific steps (verification, status check, data import)
  - Progress through import validation steps
  - Navigation to home screen on success

## Screen Structure

### 2. Navigation and Progress

#### Top Navigation Bar
- **Given** I am on Step 4 of account creation
- **When** I view the top bar
- **Then** I should see:
  - Back button (left side, 40dp circular touch area)
  - Step indicator showing 3 of 4 steps completed (75% progress)
  - Balanced spacing (40dp spacer on right)
  - Status bar padding respected

#### Back Navigation
- **Given** Account creation is in progress
- **When** I tap the back button
- **Then** I should:
  - See a confirmation dialog *[Enhancement]*
  - Warning about losing progress
  - Option to continue or cancel
  - Navigate back only on confirmation

#### Progress Indicator
- **Given** I am viewing the progress indicator
- **When** The screen loads
- **Then** I should see:
  - 4 total steps with 3 completed (75% progress)
  - Visual distinction between completed and pending steps
  - Accurate representation of user journey

### 3. Screen Layout

#### Visual Environment
- **Given** The screen has loaded
- **When** I view the layout
- **Then** I should see:
  - Onboarding gradient background with circle pattern
  - Navigation bar and IME padding
  - Content container with dark background (#1D263E)
  - Staggered fade-in animations (200ms, 400ms, 600ms delays)

## Main Content

### 4. Header Section

#### Account Icon
- **Given** The creation process is active
- **When** I view the header
- **Then** I should see:
  - Vaulta account icon (64dp size)
  - Centered in the content container
  - Static display (no animation)

#### Status Title
- **Given** I am viewing the title
- **When** The process state changes
- **Then** I should see dynamic text:
  - CREATE in progress: "Building your account..."
  - CREATE success: "Account created successfully!"
  - CREATE failure: "Account creation failed"
  - IMPORT in progress: "Importing your account..."
  - IMPORT success: "Account imported successfully!"
  - IMPORT failure: "Account import failed"

#### Title Styling
- **Given** The title is displayed
- **When** I view its appearance
- **Then** I should see:
  - Font size: 17sp
  - Font weight: Medium
  - Color: White (#F1F5F9) for normal/success
  - Color: Red (#EF4444) for error
  - Letter spacing: -0.17sp
  - Line height: 23.8sp

### 5. Progress Bar

#### Progress Bar Display
- **Given** The creation process is active
- **When** Steps are being executed
- **Then** I should see:
  - Container width: 240dp
  - Height: 8dp
  - Background: Dark (#0A0E1A)
  - Rounded corners (100dp radius)

#### Progress Animation
- **Given** Steps are progressing
- **When** Each step completes
- **Then** I should see:
  - Smooth animation from 0% to 33% (Step 1)
  - Smooth animation from 33% to 66% (Step 2)
  - Smooth animation from 66% to 100% (Step 3)
  - Purple fill color (#8647F3)
  - 2-second duration per segment

## Process Steps

### 6. CREATE Mode Steps

#### Step 1: Generating Security Keys
- **Given** CREATE mode is active
- **When** Process starts
- **Then** I should see:
  - Text: "Generating security keys..."
  - Status: PENDING → IN_PROGRESS → COMPLETE
  - Loading animation while in progress
  - Check mark when complete
  - Progress bar: 0% → 33%

#### Step 2: Verifying Purchase
- **Given** Key generation is complete
- **When** Moving to step 2
- **Then** I should see:
  - Text: "Verifying purchase..."
  - Status progression through states
  - Purchase validation with backend
  - Progress bar: 33% → 66%

#### Step 3: Creating Blockchain Identity
- **Given** Purchase is verified
- **When** Creating account on blockchain
- **Then** I should see:
  - Text: "Creating blockchain identity..."
  - Status progression through states
  - Blockchain transaction execution
  - Progress bar: 66% → 100%

### 7. IMPORT Mode Steps

#### Step 1: Verifying Account Keys
- **Given** IMPORT mode is active
- **When** Process starts
- **Then** I should see:
  - Text: "Verifying account keys..."
  - Status: PENDING → IN_PROGRESS → COMPLETE
  - Key validation process
  - Progress bar: 0% → 33%

#### Step 2: Checking Account Status
- **Given** Keys are verified
- **When** Moving to step 2
- **Then** I should see:
  - Text: "Checking account status..."
  - Blockchain account lookup
  - Ownership verification
  - Progress bar: 33% → 66%

#### Step 3: Importing Account Data
- **Given** Account status is confirmed
- **When** Importing account
- **Then** I should see:
  - Text: "Importing account data..."
  - Local database update
  - Account activation
  - Progress bar: 66% → 100%

### 8. Step Status Indicators

#### Pending State
- **Given** A step hasn't started
- **When** Displayed in the list
- **Then** I should see:
  - Gray circle outline (#A5B4CB)
  - 1.5dp stroke width
  - 16dp size
  - Gray text color (#A5B4CB)

#### In Progress State
- **Given** A step is active
- **When** Processing
- **Then** I should see:
  - Animated rotating circle
  - Gradient arc (purple to gray)
  - 360° rotation in 1 second
  - Continuous animation
  - Gray text color (#A5B4CB)

#### Complete State
- **Given** A step is finished
- **When** Displayed
- **Then** I should see:
  - Purple check mark (#8647F3)
  - No background circle
  - 16dp icon size
  - White text color (#F1F5F9)

## Error Handling

### 9. Error States

#### Network Error
- **Given** Network connection fails
- **When** During any step
- **Then** I should see:
  - Error message: "Network error. Please check your connection and try again."
  - Red error text (#EF4444)
  - Retry button visible
  - Failed step highlighted

#### Purchase Already Consumed
- **Given** Purchase was used elsewhere
- **When** During verification
- **Then** I should see:
  - Error message: "This purchase has already been used. Please contact support."
  - Support contact option *[Enhancement]*
  - No retry button (unrecoverable)

#### Purchase Cancelled
- **Given** Purchase was cancelled
- **When** During verification
- **Then** I should see:
  - Error message: "Purchase was cancelled. Please try again."
  - Retry button visible
  - Option to go back to purchase

#### Blockchain Node Error
- **Given** Blockchain is unreachable
- **When** During account creation
- **Then** I should see:
  - Error message: "Blockchain error. Please try again later."
  - Retry button visible
  - Estimated wait time *[Enhancement]*

#### Generic Error
- **Given** Unexpected error occurs
- **When** During any step
- **Then** I should see:
  - Error message: "An unexpected error occurred: [details]"
  - Retry button visible
  - Report issue option *[Enhancement]*

### 10. Retry Mechanism

#### Retry Button
- **Given** An error has occurred
- **When** Retry button is displayed
- **Then** I should see:
  - Full-width gradient button
  - Text: "Retry"
  - Below the step list
  - 16dp top spacing

#### Retry Action
- **Given** I tap retry
- **When** Process restarts
- **Then** The system should:
  - Reset all step statuses to PENDING
  - Clear error message
  - Reset progress bar to 0%
  - Start process from beginning
  - *[Enhancement]* Resume from last successful step

## Success Flow

### 11. Success State

#### Visual Success
- **Given** All steps complete successfully
- **When** Final step finishes
- **Then** I should see:
  - Success title text
  - All steps with check marks
  - 100% progress bar
  - *[Enhancement]* Success animation (confetti/celebration)
  - *[Enhancement]* Success sound effect
  - 500ms delay before navigation

#### Navigation After Success
- **Given** Account creation succeeds
- **When** After success delay
- **Then** I should:
  - CREATE mode: Navigate to Step5BackupOptionsScreen
  - IMPORT mode: Navigate to HomeScreen
  - Smooth transition animation
  - No ability to go back

## Advanced Features *[Enhancements]*

### 12. Real-time Status Updates

#### Live Transaction Monitoring
- **Given** Blockchain transaction is pending
- **When** Creating account
- **Then** I should see:
  - Transaction hash display
  - Link to blockchain explorer
  - Real-time confirmation count
  - Estimated completion time

#### WebSocket Connection
- **Given** Real-time updates are enabled
- **When** Process is active
- **Then** The system should:
  - Establish WebSocket connection
  - Receive live status updates
  - Update UI immediately
  - Handle connection loss gracefully

### 13. Progress Persistence

#### Background Processing
- **Given** App goes to background
- **When** Creation in progress
- **Then** The system should:
  - Continue processing in background
  - Save progress state
  - Resume on app return
  - Show notification on completion

#### Process Recovery
- **Given** App crashes during creation
- **When** User returns
- **Then** The system should:
  - Detect incomplete process
  - Check actual blockchain status
  - Resume or complete as needed
  - Show recovery status

### 14. Detailed Step Information

#### Expandable Step Details
- **Given** I want more information
- **When** I tap on a step
- **Then** I should see:
  - Expanded view with details
  - What's happening technically
  - Why this step is needed
  - Estimated time remaining

#### Technical Details Mode
- **Given** Advanced user preference
- **When** Enabled in settings
- **Then** I should see:
  - Transaction IDs
  - API endpoints being used
  - Request/response data
  - Error stack traces

### 15. Alternative Creation Methods

#### Batch Account Creation *[Enhancement]*
- **Given** I have multiple purchases
- **When** Creating accounts
- **Then** I should see:
  - Queue of accounts to create
  - Parallel processing where possible
  - Overall progress indicator
  - Individual account status

#### Delegated Creation *[Enhancement]*
- **Given** Someone else pays for account
- **When** Using delegation
- **Then** I should see:
  - Delegation code entry
  - Sponsor information
  - Modified flow without payment
  - Thank you message to sponsor

### 16. Performance Optimization

#### Parallel Processing
- **Given** Multiple independent tasks
- **When** Creating account
- **Then** The system should:
  - Generate keys while verifying purchase
  - Prepare transaction while keys generate
  - Optimize total creation time
  - Show accurate progress

#### Retry Optimization
- **Given** Previous attempt failed
- **When** Retrying
- **Then** The system should:
  - Skip already completed steps
  - Verify previous work is valid
  - Resume from failure point
  - Reduce total retry time

## Accessibility

### 17. Screen Reader Support

#### Content Announcement
- **All text** must be accessible
- **Step status changes** announced
- **Progress updates** announced at intervals
- **Error messages** read immediately
- **Success state** clearly announced

#### Navigation Assistance
- **Given** Screen reader is active
- **When** Navigating
- **Then** User should hear:
  - Current step being processed
  - Progress percentage
  - Time estimates
  - Clear error descriptions

### 18. Visual Accessibility

#### High Contrast Mode
- **Given** High contrast enabled
- **When** Viewing screen
- **Then** I should see:
  - Enhanced color contrast
  - Thicker progress bar
  - Larger status indicators
  - Clear step boundaries

#### Motion Sensitivity
- **Given** Reduced motion enabled
- **When** Viewing animations
- **Then** I should see:
  - No rotating indicators
  - Simple progress fill
  - Minimal transitions
  - Static success state

## Testing Scenarios

### 19. Happy Path - CREATE

1. Enter from Step 3 after purchase
2. View "Building your account..."
3. Watch key generation complete (33%)
4. Watch purchase verification (66%)
5. Watch blockchain creation (100%)
6. See success message
7. Navigate to backup options

### 20. Happy Path - IMPORT

1. Enter from import flow
2. View "Importing your account..."
3. Watch key verification (33%)
4. Watch status check (66%)
5. Watch data import (100%)
6. See success message
7. Navigate to home screen

### 21. Error Recovery Scenarios

#### Network Failure During Creation
1. Start account creation
2. Disconnect network at 50%
3. See network error message
4. Reconnect network
5. Tap retry
6. Process resumes/restarts
7. Successfully completes

#### Purchase Verification Failure
1. Start with invalid purchase
2. See verification step fail
3. Get clear error message
4. Option to contact support
5. Option to go back
6. Cannot retry (unrecoverable)

### 22. Edge Cases

- **Multiple rapid retries**: Debounce/throttle
- **Screen rotation**: Maintain progress state
- **Low memory**: Handle gracefully
- **Slow network**: Show timeouts
- **Invalid blockchain state**: Clear messaging
- **Duplicate account name**: Specific error

## Performance Requirements

### 23. Loading Times

- **Screen load**: < 200ms
- **Step transitions**: < 100ms
- **Animation frame rate**: 60 FPS
- **Progress updates**: Every 100ms
- **Success navigation**: 500ms delay

### 24. Resource Usage

- **Memory usage**: < 50MB additional
- **CPU usage**: < 30% during animations
- **Network requests**: Retry with backoff
- **Battery impact**: Minimal
- **Storage**: Cache progress state

## Security Requirements

### 25. Key Generation Security

- **Entropy source**: Cryptographically secure
- **Key storage**: Encrypted immediately
- **Memory clearing**: After use
- **No logging**: Keys never logged
- **Secure transport**: TLS only

### 26. Transaction Security

- **Transaction signing**: Local only
- **Verification**: Multiple confirmations
- **Replay protection**: Nonce/timestamp
- **Man-in-the-middle**: Certificate pinning
- **Error details**: Sanitized for display

## Acceptance Criteria Summary

### Must Have (Current Implementation)
✅ Two operation modes (CREATE/IMPORT)
✅ Three-step progress with animations
✅ Real-time status indicators (pending/progress/complete)
✅ Progress bar with smooth animations
✅ Error handling with retry capability
✅ Dynamic status messages
✅ Success state with navigation
✅ Gradient background and styling
✅ Back navigation (with warning enhancement needed)

### Should Have (Reasonable Enhancements)
⭐ Back navigation confirmation dialog
⭐ Success celebration animation
⭐ Transaction hash display
⭐ Blockchain explorer links
⭐ Background processing continuation
⭐ Progress state persistence
⭐ Expandable step details
⭐ Resume from failure point
⭐ Support contact on errors
⭐ Time estimates for steps

### Nice to Have (Future Enhancements)
💡 WebSocket real-time updates
💡 Push notifications on completion
💡 Batch account creation
💡 Delegated account creation
💡 Technical details mode
💡 Parallel step processing
💡 Voice progress updates
💡 AR visualization of process
💡 Blockchain selection (mainnet/testnet)
💡 Custom resource allocation