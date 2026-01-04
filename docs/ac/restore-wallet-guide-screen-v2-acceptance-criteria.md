# Restore Wallet Guide Screen V2 - Acceptance Criteria

## Overview
This document defines the acceptance criteria for the Restore Wallet Guide screen in the Mangala Wallet application. This screen serves as an informational gateway before users begin the wallet restoration process, setting expectations and ensuring users have everything they need for a successful recovery.

## Screen Purpose

### 1. Pre-Restoration Preparation

#### Information Gateway
- **Given** User wants to restore their wallet
- **When** They access restore wallet flow
- **Then** They should see:
  - Clear requirements checklist
  - Time expectations
  - Security reminders
  - Helpful tips
  - Platform-appropriate guidance

#### User Readiness Check
- **Given** The guide screen displays
- **When** User reviews requirements
- **Then** They should understand:
  - What they need (recovery phrase)
  - How long it takes
  - Security considerations
  - Next steps in process
  - Potential challenges

## Platform-Specific Content

### 2. Android Platform

#### Android Messaging
- **Given** I am on Android device
- **When** Viewing the guide
- **Then** I should see:
  - Title: "Welcome back! 👋"
  - Subtitle: "Let's restore your wallet"
  - Description: Localized restoration message
  - Button: "Start Restoration 🚀"
  - Friendly, emoji-rich tone

#### Android Requirements
- **Given** Android platform
- **When** Viewing requirements
- **Then** I should see:
  - "What you'll need:"
  - 📝 "Your recovery phrase" - "12 or 24 words in the exact order you saved them"
  - ⏱️ "A few minutes" - "The restoration process is quick and secure"
  - 🔒 "Private space" - "Make sure no one can see your screen"

#### Android Tips
- **Given** Android tips section
- **When** Displayed
- **Then** I should see:
  - 💡 "Pro tip"
  - "Double-check each word as you type. Auto-complete will help you!"
  - Green gradient background
  - Encouraging tone

### 3. iOS Platform

#### iOS Messaging
- **Given** I am on iOS device
- **When** Viewing the guide
- **Then** I should see:
  - Title: "RESTORE WALLET" (from localized strings)
  - Subtitle: "Restore from Recovery Phrase"
  - Description: Localized restoration message
  - Button: "Continue"
  - Professional, minimal emoji usage

#### iOS Requirements
- **Given** iOS platform
- **When** Viewing requirements
- **Then** I should see:
  - "Before you begin:"
  - 📝 "Recovery phrase ready" - "Your 12 or 24-word backup phrase"
  - ⏱️ "Time required" - "Approximately 2-3 minutes"
  - 🔒 "Secure environment" - "Ensure your privacy during restoration"

#### iOS Tips
- **Given** iOS tips section
- **When** Displayed
- **Then** I should see:
  - 💡 "Helpful hint"
  - "Words will auto-suggest as you type for accuracy"
  - Professional presentation
  - Clear guidance

### 4. Desktop Platform

#### Desktop Messaging
- **Given** I am on Desktop
- **When** Viewing the guide
- **Then** I should see:
  - Title: "Wallet Recovery"
  - Subtitle: "Import Existing Wallet"
  - Description: Technical restoration message
  - Button: "Begin Recovery Process"
  - Technical, professional tone

#### Desktop Requirements
- **Given** Desktop platform
- **When** Viewing requirements
- **Then** I should see:
  - "Prerequisites:"
  - 📝 "Mnemonic phrase" - "12 or 24-word seed phrase in correct sequence"
  - ⏱️ "Estimated time" - "Process takes 2-5 minutes"
  - 🔒 "Security check" - "Verify no screen recording or observers"

#### Desktop Tips
- **Given** Desktop tips section
- **When** Displayed
- **Then** I should see:
  - 💡 "Important note"
  - "Use tab key to navigate between word fields efficiently"
  - Keyboard-focused guidance
  - Efficiency tips

## Visual Design

### 5. Screen Layout

#### Overall Structure
- **Given** The screen displays
- **When** I view the layout
- **Then** I should see:
  - OnboardingGradientBackground
  - Safe area insets respected
  - Back navigation (top left)
  - Title/subtitle/description section
  - Requirements cards section
  - Tips card at bottom
  - Action button
  - Proper spacing throughout

#### Typography Hierarchy
- **Given** Text elements
- **When** Displayed
- **Then** I should see:
  - Title: 28sp, Bold, White
  - Subtitle: 18sp, Medium, Blue (#3B90FF)
  - Description: 16sp, Normal, Light gray (#A5B4CB)
  - Requirements title: 16sp, SemiBold, White
  - Card titles: 16sp, Medium, White
  - Card descriptions: 14sp, Normal, Light gray

### 6. Requirements Cards

#### Card Design
- **Given** Requirement cards
- **When** Displayed
- **Then** Each card should have:
  - Blue-purple gradient background (10% opacity)
  - 12dp rounded corners
  - 16dp internal padding
  - Icon on left (24sp emoji)
  - Title and description on right
  - 16dp spacing between cards

#### Card Gradient
- **Given** Card backgrounds
- **When** Rendered
- **Then** Should show:
  - Start color: #3B90FF at 10% opacity
  - End color: #C27DFF at 10% opacity
  - Linear gradient direction
  - Subtle, professional appearance

### 7. Tips Section

#### Tips Card Design
- **Given** Tips card at bottom
- **When** Displayed
- **Then** Should show:
  - Green gradient background
  - Start: #22C55E at 10% opacity
  - End: #16A34A at 10% opacity
  - 💡 Light bulb emoji (20sp)
  - Tip title in green (#22C55E)
  - Description in light gray

#### Tips Card Layout
- **Given** Tips content
- **When** Arranged
- **Then** Should have:
  - Row layout with icon left
  - 12dp spacing between icon and text
  - Column for title and description
  - 4dp spacing between title and description
  - 12dp rounded corners

### 8. Animations

#### Content Entry Animation
- **Given** Screen loads
- **When** Content appears
- **Then** Should animate:
  - 100ms initial delay
  - Requirements: Fade in 600ms, slide up from 25%, 300ms delay
  - Bottom section: Fade in 600ms, slide up from 33%, 450ms delay
  - Staggered appearance for visual flow
  - Smooth transitions

#### Exit Animation
- **Given** Navigating away
- **When** Screen exits
- **Then** Should animate:
  - Fade out 400ms
  - Slide down motion
  - Clean transition
  - No visual artifacts

## Navigation Flow

### 9. Entry Points

#### From Forgot PIN Screen
- **Given** User chose restore option
- **When** From forgot PIN screen
- **Then** Should:
  - Navigate to this guide
  - Maintain back navigation
  - Show appropriate context
  - Track analytics

#### From Onboarding *[Common flow]*
- **Given** New user choosing restore
- **When** From onboarding
- **Then** Should:
  - Show this guide first
  - Set proper navigation stack
  - Enable back navigation
  - Track user journey

### 10. Navigation Actions

#### Back Navigation
- **Given** Back button pressed
- **When** User taps back
- **Then** Should:
  - Pop to previous screen
  - Maintain navigation stack
  - No data loss
  - Smooth transition

#### Continue Navigation *[Current Bug]*
- **Given** User taps continue button
- **When** Processing
- **Then** Currently:
  - Navigates to ImportPrivateKeyScreen (incorrect)
  - Should navigate to RestoreRecoveryPhraseScreen
  - *[Fix Required]* Correct navigation destination

## Content Requirements

### 11. Recovery Phrase Information

#### Phrase Types Supported
- **Given** Information displayed
- **When** About recovery phrases
- **Then** Should mention:
  - 12-word phrases supported
  - 24-word phrases supported
  - BIP39 standard compliance
  - Word order importance
  - Case sensitivity (lowercase)

#### Security Warnings
- **Given** Security section
- **When** Displayed
- **Then** Should emphasize:
  - Privacy during entry
  - No screenshots
  - No screen recording
  - Check for observers
  - Secure environment needed

### 12. Time Expectations

#### Restoration Duration
- **Given** Time requirement shown
- **When** User reads
- **Then** Should indicate:
  - 2-3 minutes typical (iOS)
  - 2-5 minutes range (Desktop)
  - "A few minutes" (Android)
  - Network sync time additional
  - Platform-appropriate estimates

#### Process Steps Preview *[Enhancement]*
- **Given** User wants details
- **When** Viewing guide
- **Then** Could show:
  - Step 1: Enter recovery phrase
  - Step 2: Verify phrase validity
  - Step 3: Sync with blockchain
  - Step 4: Set new PIN
  - Step 5: Access restored wallet

## Enhanced Features *[Enhancements]*

### 13. Interactive Checklist

#### Pre-flight Checklist
- **Given** Requirements section
- **When** Enhanced version
- **Then** Could provide:
  - Checkable items
  - User confirms each requirement
  - Continue button enables after all checked
  - Visual progress indication
  - Prevents unprepared attempts

#### Dynamic Requirements
- **Given** User context known
- **When** Displaying requirements
- **Then** Could show:
  - Platform-specific requirements
  - Network status check
  - Battery level warning (mobile)
  - Storage space check
  - Internet speed indicator

### 14. Help Integration

#### FAQ Section *[Enhancement]*
- **Given** Common questions
- **When** User needs help
- **Then** Could provide:
  - "What if I lost some words?"
  - "Wrong word order?"
  - "Restoration failed?"
  - "How long for sync?"
  - Expandable answers

#### Video Tutorial *[Enhancement]*
- **Given** Visual learners
- **When** Guide displayed
- **Then** Could offer:
  - Short video tutorial
  - Step-by-step walkthrough
  - Security best practices
  - Common mistakes to avoid
  - Platform-specific demos

### 15. Recovery Options

#### Recovery Method Selection *[Enhancement]*
- **Given** Multiple recovery methods
- **When** Starting restoration
- **Then** Could offer:
  - Recovery phrase (primary)
  - Private key import
  - Keystore file upload
  - Hardware wallet recovery
  - QR code scanning

#### Partial Recovery *[Enhancement]*
- **Given** Incomplete phrase
- **When** User missing words
- **Then** Could provide:
  - Partial recovery attempt
  - Word suggestion system
  - Brute force last words (if few)
  - Recovery service recommendation
  - Support escalation

### 16. Security Enhancements

#### Environment Check *[Enhancement]*
- **Given** Security concerns
- **When** Before proceeding
- **Then** Could verify:
  - Screen recording not active
  - No remote access detected
  - Secure network connection
  - VPN status check
  - Public WiFi warning

#### Secure Input Mode *[Enhancement]*
- **Given** Phrase entry upcoming
- **When** User proceeds
- **Then** Could enable:
  - Keyboard privacy mode
  - Clipboard disabled
  - Auto-complete restricted
  - Screenshot prevention
  - Input masking option

## Progress Indication

### 17. Step Progress *[Enhancement]*

#### Visual Progress Bar
- **Given** Multi-step process
- **When** User proceeds
- **Then** Could show:
  - Step 1 of 5: Preparation (current)
  - Visual progress indicator
  - Estimated time remaining
  - Clear next steps
  - Ability to go back

#### Breadcrumb Navigation *[Enhancement]*
- **Given** Complex flow
- **When** Navigating
- **Then** Could display:
  - Home > Restore > Guide > [Entry]
  - Clickable breadcrumbs
  - Current position highlighted
  - Quick navigation
  - Context awareness

## Error Prevention

### 18. Common Mistakes Warning

#### Mistake Prevention Tips
- **Given** Known issues
- **When** Displaying guide
- **Then** Should warn about:
  - Word order importance
  - Spelling accuracy
  - No extra spaces
  - Lowercase only
  - Complete phrase needed

#### Error Recovery Guidance *[Enhancement]*
- **Given** Potential errors
- **When** User might encounter
- **Then** Could provide:
  - "What if it fails?" section
  - Common error solutions
  - Troubleshooting steps
  - Support contact
  - Alternative methods

## Accessibility

### 19. Screen Reader Support

#### Content Announcements
- **Given** Screen reader active
- **When** Navigating
- **Then** Should announce:
  - "Wallet restoration guide"
  - Each requirement clearly
  - Tips and warnings
  - Button purposes
  - Navigation options

#### Semantic Structure
- **Given** Accessibility needs
- **When** Screen structured
- **Then** Should have:
  - Proper heading hierarchy
  - Landmark regions
  - Descriptive labels
  - Focus management
  - Skip navigation options

### 20. Visual Accessibility

#### High Contrast Mode
- **Given** High contrast enabled
- **When** Viewing screen
- **Then** Should provide:
  - Enhanced card borders
  - Higher text contrast
  - Clear visual hierarchy
  - Distinct interactive elements
  - Maintained readability

#### Text Scaling
- **Given** Large text enabled
- **When** Viewing guide
- **Then** Should:
  - Scale text appropriately
  - Maintain layout integrity
  - Wrap text properly
  - Keep cards readable
  - Preserve functionality

## Testing Scenarios

### 21. Happy Path

1. Navigate from forgot PIN screen
2. View platform-specific content
3. Read all requirements
4. Review tips section
5. Tap continue button
6. Navigate to recovery phrase screen
7. Complete restoration flow

### 22. Information Review Path

1. Enter guide screen
2. Read each requirement card
3. Scroll to tips section
4. Go back to review
5. Feel prepared
6. Continue confidently
7. Success in restoration

### 23. Edge Cases

#### Navigation Edge Cases
- Quick back-forward: Maintain state
- Double-tap continue: Prevent duplicate
- Animation interruption: Handle gracefully
- Screen rotation: Preserve content (mobile)
- Memory pressure: Maintain functionality

#### Content Edge Cases
- Very long localized text: Proper wrapping
- Missing translations: Fallback to English
- Emoji rendering issues: Text alternatives
- Gradient rendering: Solid color fallback
- Animation disabled: Static display

## Performance Requirements

### 24. Loading Performance

- **Screen load**: < 100ms
- **Content render**: < 200ms
- **Animation start**: < 100ms
- **Navigation response**: < 50ms
- **Smooth scrolling**: 60 FPS

### 25. Resource Usage

- **Memory footprint**: < 30MB
- **CPU during idle**: < 5%
- **CPU during animation**: < 20%
- **Battery impact**: Minimal
- **Network**: None required

## Analytics Requirements

### 26. Event Tracking

#### Screen View Events
- **Given** Analytics enabled
- **When** Screen viewed
- **Then** Should track:
  - Screen name: EVM_IMPORT_WALLET_GUIDE
  - Entry source
  - Platform type
  - Time spent
  - Interaction events

#### User Actions *[Enhancement]*
- **Given** User interactions
- **When** Actions taken
- **Then** Could track:
  - Continue button tap
  - Back navigation
  - Time on screen
  - Scroll depth
  - Help interactions

## Acceptance Criteria Summary

### Must Have (Current Implementation)
✅ Platform-specific content (Android/iOS/Desktop)
✅ Three requirement cards with icons
✅ Tips section with green gradient
✅ Animated content entry
✅ Back navigation support
✅ Continue button to proceed
✅ Gradient card backgrounds
✅ Proper typography hierarchy
✅ Analytics tracking

### Should Have (Reasonable Enhancements)
⭐ Fix navigation to RestoreRecoveryPhraseScreen
⭐ Interactive requirements checklist
⭐ FAQ section for common questions
⭐ Recovery method selection
⭐ Environment security check
⭐ Progress indication
⭐ Error prevention tips
⭐ Video tutorial option
⭐ Better accessibility support
⭐ Breadcrumb navigation

### Nice to Have (Future Enhancements)
💡 Partial phrase recovery
💡 Multiple recovery methods
💡 AI-powered assistance
💡 Live chat support
💡 Secure input mode
💡 Cloud backup detection
💡 Hardware wallet support
💡 Biometric protection
💡 Recovery simulation mode
💡 Gamified preparation