# TryWithAIScreen - Acceptance Criteria

## Overview
This document defines the acceptance criteria for the Try with AI screen in Mangala Wallet. This screen introduces users to the AI assistant feature, explains passkey authentication, and provides a streamlined onboarding path through the conversational UI.

## Screen Layout

### 1. Background and Structure

#### Visual Environment
- **Given** I navigate to the Try with AI screen
- **When** The screen loads
- **Then** I should see:
  - Onboarding gradient background
  - Safe area padding (status bar and navigation)
  - IME padding for keyboard interactions
  - Full-screen scrollable content
  - Character mascot overlay

#### Screen Hierarchy
- **Given** I am viewing the screen structure
- **When** I examine the layout
- **Then** I should see:
  - Top navigation bar with back button
  - Scrollable content area (16dp horizontal padding)
  - Conversation demo image at top
  - Text content in middle
  - Action buttons at bottom
  - Character mascot positioned at right edge (120dp size, 300dp from top)

### 2. Navigation Bar

#### Top Bar Display
- **Given** I am on the Try with AI screen
- **When** I view the top bar
- **Then** I should see:
  - Centered title area (empty/no title text)
  - Back button on the left
  - Transparent background

#### Back Navigation
- **Given** I am on the Try with AI screen
- **When** I tap the back button
- **Then** I should:
  - Navigate back to the previous screen (OnboardingScreen)
  - See smooth navigation transition

## Main Content

### 3. Conversation Demo Image

#### Image Display
- **Given** The screen has loaded
- **When** I view the top content area
- **Then** I should see:
  - Conversation demo image (MR.images.conversation_demo)
  - Full width display with 8dp horizontal padding
  - 450dp height
  - Centered alignment

### 4. Text Content

#### Title Section
- **Given** I am viewing the main content
- **When** I look at the title area
- **Then** I should see:
  - First line: "Meet Mangala" (with brand highlighting)
    - Font size: 28sp
    - Font weight: Bold
    - Line height: 39.2sp
    - Letter spacing: -0.28sp
  - Second line: "Your Own AI Assistant"
    - Font size: 28sp
    - Font weight: Bold
    - Color: White
    - Center alignment

#### Description Text
- **Given** I am viewing the description
- **When** I read the subtitle
- **Then** I should see:
  - Text: "Send and receive assets, manage contacts, and handle accounts with simple commands"
  - Font size: 17sp
  - Font weight: Normal
  - Color: #D1D1D1
  - Center alignment
  - Letter spacing: -0.17sp
  - Line height: 23.8sp
  - Font family: Inter

### 5. Terms and Conditions

#### Checkbox Component
- **Given** I am viewing the terms section
- **When** I see the checkbox
- **Then** I should see:
  - Gradient-styled checkbox (unchecked by default)
  - Text: "I agree to the Terms of Service and Privacy Policy"
  - Clickable terms link

#### Terms Interaction
- **Given** The terms checkbox is displayed
- **When** I click on the terms link
- **Then** I should:
  - Navigate to TermsOfServiceScreen
  - Be able to read full terms
  - Return to Try with AI screen

#### Checkbox State
- **Given** The checkbox is unchecked
- **When** I tap the checkbox
- **Then**:
  - Checkbox becomes checked
  - Continue button becomes enabled
  - State is maintained if I navigate away and return

## Action Buttons

### 6. Continue with Passkey Button

#### Button Display
- **Given** I am viewing the primary action button
- **When** The screen loads
- **Then** I should see:
  - Text: "Continue with passkey"
  - Gradient button style
  - Full width layout
  - Disabled state (grayed out) when terms not accepted
  - Enabled state (full color) when terms accepted

#### Button Interaction - Terms Not Accepted
- **Given** Terms checkbox is unchecked
- **When** I tap "Continue with passkey"
- **Then**:
  - Nothing happens (button is disabled)
  - No navigation occurs
  - Visual feedback shows disabled state

#### Button Interaction - Terms Accepted
- **Given** Terms checkbox is checked
- **When** I tap "Continue with passkey"
- **Then** The system should:
  1. Call `completeOnboarding()` on the screen model
  2. Mark onboarding as complete in data store
  3. Navigate to HomeScreen with CONVERSATION_UI as initial tab
  4. Replace entire navigation stack (no back navigation)

### 7. What is Passkey Button

#### Button Display
- **Given** I am viewing the secondary button
- **When** The screen loads
- **Then** I should see:
  - Text: "What is passkey?"
  - Transparent background with subtle border (1dp, 1% opacity white)
  - Full width layout
  - 52dp height
  - Pill shape (1000dp rounded corners)
  - Gray text color (#A5B4CB)
  - Font size: 14sp

#### Button Interaction
- **Given** I tap "What is passkey?"
- **When** The button is pressed
- **Then**:
  - Bottom sheet opens with passkey information
  - Screen content remains visible behind sheet
  - Sheet can be dismissed by dragging or tapping outside

## Bottom Sheet - Passkey Information

### 8. Bottom Sheet Appearance

#### Visual Design
- **Given** The passkey bottom sheet is opened
- **When** I view the sheet
- **Then** I should see:
  - Dark background (#1D263E)
  - White text content
  - Drag handle (40dp x 4dp, 30% opacity white)
  - Rounded top corners
  - Modal overlay on main content

### 9. Bottom Sheet Content

#### Title Section
- **Given** The bottom sheet is open
- **When** I view the title
- **Then** I should see:
  - Text: "🔐 What is a Passkey?"
  - Font size: 24sp
  - Font weight: Bold
  - White color
  - Center alignment

#### Description
- **Given** I am reading the passkey explanation
- **When** I view the description
- **Then** I should see:
  - Text: "Passkey helps you log in without passwords."
  - Font size: 17sp
  - Color: #D1D1D1
  - Center alignment

#### Benefits List
- **Given** I am viewing the benefits section
- **When** I read the list
- **Then** I should see three items:
  1. "✅ Super secure (even safer than OTP)"
  2. "✅ No need to remember anything"
  3. "✅ Works with your fingerprint or Face ID"
  - Font size: 16sp
  - White color
  - 12dp spacing between items

#### Security Note
- **Given** I am viewing the security information
- **When** I read the privacy note
- **Then** I should see:
  - Text: "Your private key stays on your phone. We can't see it."
  - Font size: 16sp
  - Color: #D1D1D1
  - Center alignment

#### Got It Button
- **Given** The bottom sheet is open
- **When** I view the action button
- **Then** I should see:
  - Text: "Got it"
  - Primary button style (OnboardingButton)
  - Full width layout
  - Located at bottom with 40dp padding

### 10. Bottom Sheet Interactions

#### Dismissal Methods
- **Given** The passkey bottom sheet is open
- **When** I want to close it
- **Then** I can:
  - Tap the "Got it" button
  - Drag the sheet down
  - Tap outside the sheet (on the overlay)
  - Each method should smoothly animate the sheet closed

#### State Management
- **Given** I open and close the bottom sheet
- **When** I reopen it
- **Then**:
  - Content displays exactly the same
  - No state is persisted
  - Animation plays each time

## Character Mascot

### 11. Mascot Display

#### Positioning
- **Given** The screen is displayed
- **When** I view the character mascot
- **Then** I should see:
  - Character image (MR.images.character)
  - Size: 120dp x 120dp
  - Position: Right edge of screen
  - Vertical offset: 300dp from top
  - Horizontal offset: 10dp from right edge
  - Overlaid on top of other content

#### Responsive Behavior
- **Given** I scroll the content
- **When** The page moves
- **Then** The mascot:
  - Remains fixed in position
  - Does not scroll with content
  - Maintains overlay position

## User Flows

### 12. Happy Path Flow

1. **Entry** → Navigate from OnboardingScreen via "Try with AI Assistant"
2. **View Content** → Read about AI assistant capabilities
3. **Learn About Passkey** → Tap "What is passkey?" and read information
4. **Close Sheet** → Tap "Got it" to dismiss
5. **Accept Terms** → Check the terms checkbox
6. **Continue** → Tap "Continue with passkey"
7. **Complete** → Navigate to HomeScreen with Conversation UI tab

### 13. Terms Review Flow

1. **Entry** → Arrive at Try with AI screen
2. **Click Terms Link** → Navigate to TermsOfServiceScreen
3. **Read Terms** → Scroll through terms content
4. **Return** → Navigate back to Try with AI screen
5. **Accept** → Check the checkbox
6. **Continue** → Proceed with passkey

### 14. Information Seeking Flow

1. **Entry** → Arrive at Try with AI screen
2. **Question** → Tap "What is passkey?"
3. **Read Info** → Review passkey benefits
4. **Dismiss** → Tap "Got it" or drag down
5. **Repeat** → Can reopen sheet multiple times
6. **Decision** → Either continue or go back

## Analytics Requirements

### Events to Track

#### Screen Events
- `screen_view`: Screen name "TRY_WITH_AI"
- `passkey_info_opened`: When bottom sheet opens
- `passkey_info_closed`: When bottom sheet closes
- `terms_checkbox_toggled`: State change (checked/unchecked)
- `terms_link_clicked`: Navigation to terms

#### Navigation Events
- `continue_with_passkey_clicked`: Only when enabled
- `onboarding_completed`: When continuing to home
- `back_navigation`: When using back button

## Accessibility Requirements

### Screen Reader Support
- **All text** must be announced by screen readers
- **Checkbox state** must be clearly announced
- **Button states** (enabled/disabled) must be announced
- **Bottom sheet** opening/closing must be announced
- **Character mascot** should have appropriate content description

### Touch Targets
- **Minimum button height:** 52dp
- **Checkbox touch area:** Minimum 44dp x 44dp
- **Terms link:** Adequate touch target around text
- **Back button:** Standard navigation icon size

### Keyboard Navigation (Desktop)
- **Tab order:** Logical flow through interactive elements
- **Enter/Space:** Activate buttons and checkbox
- **Escape:** Close bottom sheet
- **Arrow keys:** Navigate through content

## Platform-Specific Requirements

### iOS
- Passkey integration with iOS Keychain
- Face ID/Touch ID mentioned in benefits
- Safe area handling for notch/dynamic island
- Smooth sheet presentation matching iOS patterns

### Android
- Passkey integration with Google Password Manager
- Biometric prompt for fingerprint
- Navigation bar color matching background
- Material Design bottom sheet behavior

### Desktop
- Mouse hover states for all buttons
- Keyboard shortcuts for navigation
- Click outside to dismiss bottom sheet
- Scrollbar styling to match theme

## Performance Requirements

### Loading
- **Screen render:** < 100ms
- **Image loading:** Asynchronous with placeholder
- **Bottom sheet animation:** 60 FPS
- **Scroll performance:** No jank or stuttering

### State Management
- **Terms checkbox:** Instant response
- **Button state changes:** < 16ms
- **Navigation:** Smooth transition animations

## Error Handling

### Navigation Failures
- **Given** Navigation to HomeScreen fails
- **When** User taps "Continue with passkey"
- **Then**:
  - Show error message
  - Allow retry
  - Maintain screen state

### Image Loading Failures
- **Given** Conversation demo or character image fails
- **When** Screen loads
- **Then**:
  - Show placeholder or fallback
  - Don't block user interaction
  - Log error for debugging

### Onboarding Completion Failure
- **Given** completeOnboarding() fails
- **When** User continues
- **Then**:
  - Show error toast
  - Allow retry
  - Don't navigate until successful

## Testing Scenarios

### Acceptance Flow Test
1. Navigate to screen
2. Verify terms unchecked
3. Verify continue button disabled
4. Check terms checkbox
5. Verify continue button enabled
6. Tap continue
7. Verify navigation to HomeScreen

### Bottom Sheet Test
1. Tap "What is passkey?"
2. Verify sheet opens with animation
3. Read all content
4. Tap "Got it"
5. Verify sheet closes
6. Reopen sheet
7. Drag to dismiss

### Terms Navigation Test
1. Click terms link
2. Navigate to terms screen
3. Use back button
4. Return to Try with AI
5. Verify state preserved

### Edge Cases
- **Rapid tapping:** Continue button should only trigger once
- **Background/foreground:** State should persist
- **Rotation (mobile):** Layout should adapt
- **Small screens:** Content should remain accessible

## Acceptance Criteria Summary

✅ Screen displays with gradient background and proper padding
✅ Conversation demo image loads at top
✅ Title and description text properly formatted
✅ Character mascot positioned correctly as overlay
✅ Terms checkbox functional with link navigation
✅ Continue button enables only with accepted terms
✅ Passkey information accessible via bottom sheet
✅ Bottom sheet contains complete passkey explanation
✅ Navigation to HomeScreen with Conversation UI tab
✅ Onboarding marked complete in data store
✅ All interactions tracked with analytics
✅ Accessible to screen readers and keyboard navigation
✅ Smooth animations at 60 FPS
✅ Error states handled gracefully
✅ Platform-specific integrations functional