# Step 2: Select Account Name V2 - Acceptance Criteria

## Overview
This document defines the acceptance criteria for Step 2 of the Vaulta account creation flow - the account name selection screen. This screen allows users to choose their permanent blockchain account name with real-time validation, availability checking, and premium account options.

## Screen Structure

### 1. Navigation and Progress

#### Top Navigation Bar
- **Given** I am on Step 2 of account creation
- **When** I view the top bar
- **Then** I should see:
  - Back button (left side, 40dp circular touch area)
  - Step indicator showing 1 of 4 steps completed
  - Balanced spacing (40dp spacer on right)
  - Status bar padding respected

#### Back Navigation
- **Given** I am on the account name selection screen
- **When** I tap the back button
- **Then** I should:
  - Navigate back to Step 1 (account type selection)
  - Lose any entered account name (no state persistence)
  - See smooth navigation transition

#### Step Progress Indicator
- **Given** I am viewing the progress indicator
- **When** The screen loads
- **Then** I should see:
  - 4 total steps with step 1 completed (25% progress)
  - Visual distinction between completed and pending steps
  - Smooth animation when transitioning between screens

### 2. Screen Layout and Background

#### Visual Environment
- **Given** The screen has loaded
- **When** I view the layout
- **Then** I should see:
  - Onboarding gradient background with circle pattern
  - Safe area padding (navigation bar and IME)
  - Full-screen scrollable content
  - Tap-to-dismiss keyboard functionality

#### Content Animation
- **Given** The screen is loading
- **When** Content appears
- **Then** I should see:
  - Staggered fade-in animations (200ms, 600ms, 800ms delays)
  - Slide-up effect for each section
  - 600ms animation duration with smooth easing

## Main Content

### 3. Title and Description

#### Title Section
- **Given** I am viewing the title area
- **When** The content loads
- **Then** I should see:
  - Title: "Pick Your VAULTA Account Name"
  - Font size: 20sp
  - Font weight: Medium
  - White color
  - Left alignment
  - Letter spacing: -0.2sp
  - Line height: 28sp

#### Description Text
- **Given** I am reading the description
- **When** Below the title
- **Then** I should see:
  - Text: "Your account name is your wallet address. Once claimed, it's yours permanently."
  - Font size: 14sp
  - Color: #A5B4CB
  - Left alignment
  - 12dp spacing from title

### 4. Account Name Input Field

#### Input Field Display
- **Given** I am viewing the input section
- **When** The screen loads
- **Then** I should see:
  - Label: "Account name" with red asterisk (*)
  - Dark background (#0A0E1A)
  - 56dp height
  - 12dp rounded corners
  - Border color changes:
    - Default: #2A3E6C
    - Focused: #3B90FF
    - Error: #FF5252

#### Input Placeholder
- **Given** The input field is empty
- **When** I haven't entered text
- **Then** I should see:
  - Placeholder: "Enter account name"
  - Color: #A5B4CB
  - Disappears when typing begins

#### Text Entry
- **Given** I am entering an account name
- **When** I type characters
- **Then** I should:
  - See text in white (14sp)
  - See cursor in blue (#3B90FF)
  - Be limited to valid characters (auto-correction disabled)
  - See suffix automatically appended (if premium account)

#### Clear Button
- **Given** I have entered text
- **When** Text is present in the field
- **Then** I should see:
  - Clear button (X icon) on the right
  - 20dp circular gray background (#6B7280)
  - Tapping clears all text
  - Button disappears when field is empty

### 5. Character Counter and Suggestions

#### Character Count Display
- **Given** I am typing an account name
- **When** I view below the input field
- **Then** I should see:
  - Format: "X/12 characters" (includes suffix in count)
  - Updates in real-time
  - Color: #A5B4CB
  - Font size: 12sp

#### Suggest Feature
- **Given** I need help with a name
- **When** I tap "Suggest"
- **Then** The system should:
  - Generate a valid random account name
  - Replace current input
  - Ensure generated name meets all validation rules
  - Check availability automatically

## Validation System

### 6. Real-Time Validation Checklist

#### Validation Container
- **Given** I am viewing the validation section
- **When** Below the input field
- **Then** I should see:
  - Dark rounded container (#1D263E background)
  - 16dp border radius
  - 12dp vertical, 16dp horizontal padding
  - 12dp spacing between items

#### Validation Rules Display
- **Given** I am viewing validation items
- **When** The checklist displays
- **Then** I should see these rules:

1. **Character Validation**
   - Text: "Use only a-z and 1-5"
   - Check mark icon (16dp)
   - Gray when empty/invalid (#6B7280)
   - Purple when valid (#C27DFF)

2. **Period Rules**
   - Text: "Can't start/end with period (.)"
   - Same icon and color states

3. **Number Start Rule**
   - Text: "Can't start with numbers"
   - Same icon and color states

4. **Length Validation**
   - Text: "Maximum X characters + .suffix" (for premium)
   - Or: "Maximum 12 characters" (for standard)
   - Dynamic based on account type

#### Validation States
- **Given** I am typing an account name
- **When** Each character is entered
- **Then** Validation should:
  - Update immediately (< 50ms)
  - Show check marks turn purple when rules pass
  - Maintain gray for failed/incomplete rules
  - All checks must pass for valid state

### 7. Availability Checking

#### Loading State
- **Given** A valid name format is entered
- **When** Checking availability
- **Then** I should see:
  - Animated loading spinner (rotating gradient arc)
  - Text: "Checking availability..."
  - 1-second rotation animation
  - Gradient colors: #8647F3 to #A5B4CB

#### Available State
- **Given** The name is available
- **When** Check completes
- **Then** I should see:
  - New validation item: "Name is available"
  - Purple check mark (#C27DFF)
  - Continue button becomes enabled

#### Unavailable State
- **Given** The name is taken
- **When** Check completes
- **Then** I should see:
  - Error state on input field (red border)
  - Validation item shows unavailable
  - Continue button remains disabled
  - *[Enhancement]* Suggested alternatives appear

## Premium Account Features

### 8. Account Suffix System

#### Suffix Display
- **Given** I have a premium account type
- **When** Entering a name less than 12 characters
- **Then** I should see:
  - Suffix automatically displayed (e.g., ".gm")
  - Suffix counted in character limit
  - Suffix shown in input field
  - Cannot be edited or removed

#### Suffix Selection *[Enhancement]*
- **Given** I want to customize my suffix
- **When** I tap on suffix options
- **Then** I should see:
  - List of available suffixes (.gm, .x, .dao, .nft)
  - Current selection highlighted
  - Instant preview in input field
  - Price difference for each suffix

### 9. Premium Pricing Display *[Enhancement]*

#### Price Information
- **Given** I am creating a premium account
- **When** Viewing the screen
- **Then** I should see:
  - Current price for selected name length
  - Price breakdown:
    - 1-3 characters: $500+ 
    - 4-6 characters: $100-$499
    - 7-11 characters: $10-$99
  - Currency display in both USD and crypto
  - *[Enhancement]* Price history/trends

## Terms and Conditions

### 10. Terms Checkbox

#### Checkbox Display
- **Given** I am ready to continue
- **When** Viewing the bottom section
- **Then** I should see:
  - Gradient-styled checkbox
  - Text: "I agree to the Terms of Service and Privacy Policy"
  - Clickable terms link (underlined)
  - Unchecked by default

#### Terms Navigation
- **Given** I want to read the terms
- **When** I tap the terms link
- **Then** I should:
  - Navigate to Terms and Policy screen
  - Be able to return to this screen
  - Maintain entered account name

#### Checkbox Interaction
- **Given** The checkbox is unchecked
- **When** I tap it
- **Then**:
  - Checkbox becomes checked with animation
  - Continue button becomes enabled (if name valid)
  - State persists during screen lifecycle

## Action Buttons

### 11. Create Account Button

#### Button Display
- **Given** I am viewing the primary action
- **When** At the bottom of the screen
- **Then** I should see:
  - Text: "Create Vaulta account"
  - Gradient button style
  - Full width with 24dp horizontal padding
  - Disabled state when requirements not met

#### Button Enablement Logic
- **Given** The create button exists
- **When** Checking if enabled
- **Then** It requires:
  - All validation rules pass ✓
  - Name is available ✓
  - Terms accepted ✓
  - All three conditions must be true

#### Button Action
- **Given** All requirements are met
- **When** I tap "Create Vaulta account"
- **Then** I should:
  - Navigate to Step 3 (Account Ready to Claim)
  - Pass account name and suffix
  - Pass account type (Premium)
  - See loading state during navigation

### 12. Alternative Creation Methods

#### Create from EVM *[Currently Commented Out]*
- **Given** I want to create via EVM
- **When** This feature is enabled
- **Then** I should see:
  - Secondary button: "Create from EVM"
  - Below primary create button
  - Same validation requirements
  - Navigate to EVM creation flow

#### Import Existing Account *[Enhancement]*
- **Given** I already have an account
- **When** On this screen
- **Then** I should see:
  - Link: "Already have an account? Import instead"
  - Navigate to import flow
  - Skip account creation process

## Advanced Features *[Enhancements]*

### 13. Name Reservation System

#### Temporary Reservation
- **Given** I found an available name
- **When** It passes all checks
- **Then** The system should:
  - Reserve name for 5 minutes
  - Show countdown timer
  - Warn before expiration
  - Release if user navigates away

### 14. Search History

#### Recent Searches
- **Given** I've searched names before
- **When** I focus the input field
- **Then** I should see:
  - Dropdown with recent searches
  - Availability status for each
  - Quick select functionality
  - Clear history option

### 15. Advanced Validation

#### Profanity Filter
- **Given** I enter inappropriate content
- **When** The name is validated
- **Then** The system should:
  - Detect profanity/slurs
  - Show warning message
  - Suggest alternatives
  - Block continuation

#### Trademark Protection
- **Given** I enter a trademarked name
- **When** Checking availability
- **Then** The system should:
  - Flag potential trademark issues
  - Show warning (not blocking)
  - Provide disclaimer
  - Allow override with acknowledgment

### 16. Smart Suggestions

#### AI-Powered Suggestions
- **Given** I tap "Suggest" multiple times
- **When** Generating suggestions
- **Then** The system should:
  - Learn from rejections
  - Suggest based on patterns
  - Offer themed suggestions
  - Remember preferences

#### Contextual Suggestions
- **Given** An unavailable name is entered
- **When** The check fails
- **Then** Show:
  - Similar available alternatives
  - Add numbers/variations
  - Different suffix options
  - One-tap application

## Accessibility Features

### 17. Screen Reader Support

#### Content Announcement
- **All text** must be accessible
- **Validation states** announced on change
- **Loading states** announced
- **Error messages** read immediately
- **Character count** updates announced

### 18. Keyboard Navigation

#### Tab Order
- Input field → Clear button → Suggest link → Terms checkbox → Create button

#### Keyboard Shortcuts *[Enhancement]*
- **Enter**: Check availability
- **Escape**: Clear field
- **Tab**: Navigate elements
- **Space**: Toggle checkbox

## Error Handling

### 19. Network Errors

#### Availability Check Failure
- **Given** Network is unavailable
- **When** Checking name availability
- **Then** Show:
  - Error message: "Unable to check availability"
  - Retry button
  - Offline mode option
  - Cache recent checks

### 20. Validation Errors

#### Input Errors
- **Given** Invalid characters entered
- **When** Real-time validation runs
- **Then**:
  - Prevent invalid character entry
  - Show specific error message
  - Highlight problematic rule
  - Suggest corrections

## Performance Requirements

### 21. Response Times

- **Character validation**: < 50ms
- **Availability check**: < 2 seconds
- **Screen load**: < 200ms
- **Animation frame rate**: 60 FPS
- **Keyboard response**: Instant

### 22. Optimization

- **Debounce availability checks**: 500ms after typing stops
- **Cache validation results**: During session
- **Preload next screen**: When name valid
- **Minimize re-renders**: Only affected components

## Platform Considerations

### 23. Mobile Specific

#### iOS
- Keyboard avoidance with proper padding
- Haptic feedback on validation success
- Password autofill disabled
- Smooth keyboard animations

#### Android
- Back button handling
- Keyboard type optimization
- Material Design compliance
- Proper IME padding

### 24. Desktop *[Enhancement]*

- Wider layout utilization
- Mouse hover states
- Copy/paste support
- Keyboard focus indicators

## Testing Scenarios

### 25. Happy Path
1. Enter valid name (e.g., "alice")
2. See validations turn green
3. Wait for availability check ✓
4. Accept terms
5. Tap create account
6. Navigate to Step 3

### 26. Edge Cases
- Empty name → All validations gray
- Name too long → Length validation fails
- Special characters → Character validation fails
- Network timeout → Retry mechanism
- Rapid typing → Debounced checks
- Screen rotation → State preserved

### 27. Security Testing
- SQL injection attempts blocked
- XSS prevention in input
- Rate limiting on availability checks
- No sensitive data in logs

## Acceptance Criteria Summary

### Must Have (Current Implementation)
✅ Real-time validation with visual feedback
✅ Account name availability checking
✅ Premium account suffix system
✅ Terms and conditions acceptance
✅ Character count display
✅ Suggest name functionality
✅ Clear input functionality
✅ Animated loading states
✅ Step progress indicator
✅ Responsive validation checklist

### Should Have (Reasonable Enhancements)
⭐ Name reservation system (5-minute hold)
⭐ Premium pricing display
⭐ Suggested alternatives for taken names
⭐ Recent search history
⭐ Profanity/trademark filtering
⭐ Multiple suffix options
⭐ Offline mode support
⭐ Advanced error recovery

### Nice to Have (Future Enhancements)
💡 AI-powered smart suggestions
💡 Price history and trends
💡 Bulk name checking
💡 Name marketplace integration
💡 Social verification
💡 ENS/DNS integration
💡 QR code generation
💡 Share functionality