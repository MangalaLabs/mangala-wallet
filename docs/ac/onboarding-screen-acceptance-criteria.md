# OnboardingScreen - Acceptance Criteria

## Overview
This document defines the acceptance criteria for the main onboarding screen in Mangala Wallet. The onboarding screen is the first experience new users have with the application, featuring a swipeable carousel of informational pages and three primary action paths.

## Screen Components

### 1. Visual Layout

#### Gradient Background
- **Given** I open the app for the first time
- **When** I see the onboarding screen
- **Then** I should see:
  - Animated gradient background
  - Safe area padding respected (status bar and navigation bar)
  - IME padding for keyboard appearance
  - Smooth color transitions

#### Screen Structure
- **Given** I am on the onboarding screen
- **When** I view the layout
- **Then** I should see:
  - Full-screen vertical scrollable container
  - Content area with horizontal pager (carousel)
  - Page indicator below the pager
  - Action buttons section at the bottom
  - Proper spacing between all sections

### 2. Onboarding Carousel

#### Page Content
- **Given** I am viewing the onboarding carousel
- **When** The screen loads
- **Then** I should see 4 distinct pages with the following content:

**Page 1 - Welcome**
- Title: "Welcome to Mangala"
- Description: "To get started, create a new wallet or import an existing one."
- Image: Character/logo illustration (200dp size)

**Page 2 - Security**
- Title: "Bank-grade security for Vaulta & beyond"
- Description: "Biometric access, hardware encryption, and AI threat detection."
- Image: Character illustration (200dp size)

**Page 3 - AI Features**
- Title: "Your AI-powered crypto companion"
- Description: "Smart insights, automated security, and intelligent portfolio management."
- Image: Character illustration (200dp size)

**Page 4 - Multi-Chain Support**
- Title: "One wallet for all your assets"
- Description: "Native Vaulta, full EVM support, and Bitcoin - all in one place."
- Image: Character illustration (200dp size)

#### Swipe Navigation
- **Given** I am on any onboarding page
- **When** I swipe left or right
- **Then** I should:
  - Navigate to the next/previous page smoothly
  - See smooth animation transition between pages
  - Be able to swipe from any page to any other page
  - Experience responsive touch handling

#### Page Indicator
- **Given** I am viewing the carousel
- **When** I navigate between pages
- **Then** The page indicator should:
  - Display 4 dots representing each page
  - Highlight the current page with increased size and opacity
  - Animate smoothly between page changes
  - Show intermediate states during swipe gestures
  - Use spring animation (dampingRatio: 0.8, stiffness: 400)

### 3. Text Styling

#### Title Text (MangalaBrandText)
- **Font Size:** 28sp
- **Font Weight:** Bold
- **Line Height:** 39.2sp
- **Letter Spacing:** -0.28sp
- **Alignment:** Center
- **Brand highlighting:** "Mangala" or "Vaulta" keywords highlighted

#### Description Text
- **Font Size:** 17sp
- **Font Weight:** Normal
- **Color:** #D1D1D1
- **Line Height:** 23.8sp
- **Letter Spacing:** -0.17sp
- **Font Family:** Inter
- **Alignment:** Center
- **Padding:** 16dp horizontal

### 4. Action Buttons

#### AI Assistant Button
- **Given** I am on the onboarding screen
- **When** I view the AI Assistant button
- **Then** I should see:
  - Gradient background with semi-transparent overlay
  - Robot emoji (🤖) icon
  - Text: "Try with AI Assistant"
  - Gradient text color (blue to purple to red)
  - Full width button
  - Height: 52dp
  - Rounded corners (1000dp radius for pill shape)

- **Given** I tap the AI Assistant button
- **When** The button is pressed
- **Then** The system should:
  - Track analytics event: `ONBOARDING_INITIATED` with parameter `ONBOARDING_STEP_CONVERSATION_UI`
  - Navigate to `TryWithAIScreen`
  - Show smooth navigation transition

#### Create Wallet Button
- **Given** I am on the onboarding screen
- **When** I view the Create Wallet button
- **Then** I should see:
  - Gradient button style
  - Text: "Create a new wallet"
  - Full width button
  - Primary visual emphasis

- **Given** I tap the Create Wallet button
- **When** The button is pressed
- **Then** The system should:
  - Track analytics event: `ONBOARDING_INITIATED` with parameter `ONBOARDING_STEP_CREATE_WALLET`
  - Navigate to `AntelopeCreateAccountV2Screen`
  - Begin wallet creation flow

#### Import Wallet Button
- **Given** I am on the onboarding screen
- **When** I view the Import Wallet button
- **Then** I should see:
  - Transparent button style with border
  - Text: "I already have a wallet"
  - Full width button
  - Secondary visual emphasis

- **Given** I tap the Import Wallet button
- **When** The button is pressed
- **Then** The system should:
  - Track analytics event: `ONBOARDING_INITIATED` with parameter `ONBOARDING_STEP_IMPORT_WALLET`
  - Navigate to `ImportPrivateKeyScreen`
  - Begin wallet import flow

### 5. Spacing Requirements

#### Component Spacing
- **Flexible top space:** 0.1f weight
- **Image to text:** 48dp
- **Title to description:** 12dp
- **Flexible bottom space:** 0.3f weight
- **Page indicator to buttons:** 32dp
- **Between AI button and other buttons:** 4dp
- **Between action buttons:** 12dp
- **Horizontal padding:** 24dp for pages, 24dp for buttons
- **Bottom padding:** 8dp

### 6. Image Handling

#### Image Display
- **Given** A page has an image resource
- **When** The page is displayed
- **Then** The image should:
  - Display at 200dp x 200dp size
  - Use LocalImage component for resource loading
  - Center align within the container

#### Fallback Display
- **Given** A page has no image or image fails to load
- **When** The page is displayed
- **Then** A fallback should appear:
  - 200dp x 200dp box
  - White background with 0.1 alpha
  - 16dp rounded corners
  - Rocket emoji (🚀) centered at 60sp

## User Flows

### First Time User Flow
1. **App Launch** → Onboarding Screen displays
2. **View Page 1** → Can swipe or view action buttons
3. **Swipe through pages** → Learn about app features
4. **Choose action:**
   - Try AI → Navigate to AI assistant
   - Create wallet → Start account creation
   - Import wallet → Start import flow

### Page Navigation Flow
1. **Initial state** → Page 1 displayed, indicator shows position
2. **Swipe right** → Cannot go before page 1
3. **Swipe left** → Navigate to page 2, 3, or 4
4. **Swipe left on page 4** → Cannot go beyond page 4
5. **Quick swipe** → Navigate multiple pages with momentum

## Analytics Requirements

### Events to Track
1. **Screen View**
   - Event: `screen_view`
   - Screen name: "Onboarding"
   - Screen class: "OnboardingScreen"

2. **Page View**
   - Event: `onboarding_page_viewed`
   - Parameters: page_index, page_title

3. **Button Interactions**
   - AI Assistant: `ONBOARDING_INITIATED` → `ONBOARDING_STEP_CONVERSATION_UI`
   - Create Wallet: `ONBOARDING_INITIATED` → `ONBOARDING_STEP_CREATE_WALLET`
   - Import Wallet: `ONBOARDING_INITIATED` → `ONBOARDING_STEP_IMPORT_WALLET`

4. **Completion Metrics**
   - Time spent on onboarding
   - Number of pages viewed
   - Exit point if abandoned

## Accessibility Requirements

### Screen Reader Support
- **All text content** must be accessible to screen readers
- **Page indicator** should announce current page position
- **Buttons** should have clear action descriptions
- **Images** should have content descriptions

### Touch Targets
- **Minimum button height:** 52dp
- **Full width buttons** for easy tapping
- **Swipe gestures** should work across entire content area

### Visual Accessibility
- **Text contrast:** Description text (#D1D1D1) on gradient background must meet WCAG AA standards
- **Font sizes:** Minimum 17sp for body text, 28sp for titles
- **Animation:** Respect system animation settings

## Platform-Specific Requirements

### iOS
- Safe area insets properly handled
- Smooth swipe gestures matching iOS standards
- Haptic feedback on button taps (if enabled)

### Android
- Navigation bar padding respected
- Back button behavior (should exit app or show confirmation)
- Material Design gesture navigation compatibility

### Desktop
- Keyboard navigation support (arrow keys for pages)
- Mouse wheel scrolling support
- Hover states for buttons

## Performance Requirements

### Loading Performance
- **Initial render:** < 100ms
- **Image loading:** Asynchronous with smooth appearance
- **Page transitions:** 60 FPS minimum

### Memory Management
- **Images:** Properly cached and released
- **Animation:** Should not cause jank
- **Scroll performance:** Smooth vertical scrolling

## Error Handling

### Image Loading Failure
- **Given** An image fails to load
- **When** The page is displayed
- **Then** Show fallback UI (emoji in box)

### Navigation Failure
- **Given** A navigation action fails
- **When** User taps a button
- **Then** 
  - Show error toast/snackbar
  - Keep user on onboarding screen
  - Allow retry

### Analytics Failure
- **Given** Analytics event fails to send
- **When** User interacts with buttons
- **Then** 
  - Continue with navigation (non-blocking)
  - Queue event for retry
  - No user-facing error

## Testing Scenarios

### Happy Path
1. Launch app → View onboarding
2. Swipe through all 4 pages
3. Return to page 1
4. Tap "Create a new wallet"
5. Navigate to account creation

### Swipe Navigation Test
1. Swipe left from page 1 → Go to page 2
2. Swipe right from page 2 → Return to page 1
3. Multiple quick swipes → Navigate smoothly
4. Swipe past boundaries → Stay on first/last page

### Button Interaction Test
1. Tap AI Assistant → Navigate to AI screen
2. Tap Create Wallet → Navigate to creation flow
3. Tap Import Wallet → Navigate to import flow
4. Rapid taps → Prevent multiple navigations

### Orientation Change (Mobile)
1. View in portrait → All content visible
2. Rotate to landscape → Layout adjusts properly
3. Swipe in landscape → Navigation works
4. Rotate during swipe → State preserved

### Accessibility Test
1. Enable screen reader → All content announced
2. Navigate with keyboard (desktop) → All interactive elements reachable
3. Increase font size → Text scales appropriately
4. Enable reduced motion → Animations simplified

## Edge Cases

### Rapid Navigation
- **Multiple quick taps** on buttons should only trigger one navigation
- **Swipe during button tap** should be handled gracefully

### Memory Pressure
- **Low memory conditions** should not crash the app
- **Images should gracefully degrade** if needed

### Screen Size Variations
- **Small screens** (< 360dp width) should still show all content
- **Large tablets** should scale appropriately
- **Foldable devices** should handle fold/unfold

## Acceptance Criteria Summary

✅ Onboarding screen displays on first app launch
✅ Four informational pages with swipe navigation
✅ Smooth page indicator animations
✅ Three action buttons with distinct styling
✅ AI Assistant button with gradient styling and emoji
✅ Analytics events tracked for all interactions
✅ Responsive to different screen sizes
✅ Accessible to screen readers
✅ Smooth 60 FPS animations
✅ Proper error handling for navigation failures
✅ Platform-specific optimizations implemented