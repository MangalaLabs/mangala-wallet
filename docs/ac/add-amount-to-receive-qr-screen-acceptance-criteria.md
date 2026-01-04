# Add Amount to Receive QR Screen - Acceptance Criteria

## Overview
This document defines the acceptance criteria for the Add Amount to Receive QR screen in the Mangala Wallet application. This bottom sheet screen allows users to specify a specific amount to be encoded in the QR code when receiving cryptocurrency, enabling payment requests with precise amounts.

## Screen Purpose

### 1. Amount Specification for QR Code

#### Primary Function
- **Given** User wants to receive a specific amount
- **When** On the receive token screen
- **Then** Can open this sheet to:
  - Enter desired amount
  - See amount with coin name
  - Validate decimal input
  - Save amount to QR
  - Update parent screen

#### Use Cases
- **Given** Various payment scenarios
- **When** Amount needed
- **Then** Supports:
  - Invoice payment requests
  - Specific payment amounts
  - Donation requests
  - Purchase amounts
  - Subscription fees

## Screen Parameters

### 2. Initialization Parameters

#### Required Parameters
- **Given** Screen initialization
- **When** Bottom sheet opens
- **Then** Must provide:
  - initialAmount: String (empty or existing)
  - coinName: String (token name)
  - decimals: Long? (decimal places)
  - onSaveAmount: (String) -> Unit callback

#### Parameter Validation
- **Given** Parameters provided
- **When** Screen loads
- **Then** Should:
  - Display initial amount if present
  - Show coin name in field
  - Apply decimal restrictions
  - Enable save callback
  - Focus input automatically

## Visual Design

### 3. Screen Layout

#### Background Design
- **Given** Bottom sheet display
- **When** Rendered
- **Then** Should show:
  - OnboardingGradientBackground
  - Safe drawing padding
  - Default padding (24dp assumed)
  - Modal sheet behavior
  - Swipe-to-dismiss support *[Enhancement]*

#### Content Structure
- **Given** Sheet content
- **When** Displayed
- **Then** Should contain:
  - Instruction text at top
  - Amount input row
  - Spacer for flexibility
  - Save button at bottom
  - Proper spacing throughout

### 4. Typography and Styling

#### Text Styles
- **Given** Text elements
- **When** Displayed
- **Then** Should use:
  - Instruction: Size17SemiBold, textPrimary
  - Coin name: Size17SemiBold, textPrimary
  - Input text: Size17SemiBold, textLink (blue)
  - Placeholder: Size17SemiBold, textSecondary
  - Consistent font family (Inter)

#### Spacing
- **Given** Layout spacing
- **When** Applied
- **Then** Should have:
  - Top spacing: XXXBASE (32dp)
  - After instruction: TINY (4dp)
  - Bottom button: Full width
  - Flexible middle space
  - Responsive to keyboard

## Input Field

### 5. Amount Input Field

#### Field Behavior
- **Given** Amount input field
- **When** Interacting
- **Then** Should:
  - Auto-focus on open
  - Show decimal keyboard
  - Display coin name prefix
  - Accept decimal values
  - Show placeholder when empty

#### Input Validation
- **Given** User enters amount
- **When** Typing
- **Then** Should:
  - Allow only numeric input
  - Support decimal point
  - Respect decimal places limit
  - Prevent negative values
  - Handle copy/paste

### 6. Keyboard Configuration

#### Keyboard Type
- **Given** Input field focused
- **When** Keyboard appears
- **Then** Should show:
  - Decimal keyboard type
  - Done action button
  - Numeric keys prominently
  - Decimal separator
  - No text suggestions

#### Keyboard Actions
- **Given** Keyboard visible
- **When** User taps Done
- **Then** Should:
  - Trigger save action
  - Dismiss keyboard
  - Close bottom sheet
  - Update parent screen
  - Preserve amount value

## Visual Components

### 7. Instruction Text

#### Message Content
- **Given** Top instruction
- **When** Displayed
- **Then** Should show:
  - Localized message: "message_receiveToken_addAmountBottomSheet"
  - Clear instruction about purpose
  - Proper text wrapping
  - Appropriate line height
  - Context-aware messaging

### 8. Amount Input Row

#### Row Layout
- **Given** Input row design
- **When** Rendered
- **Then** Should display:
  - Coin name on left
  - Space after coin name
  - Input field taking remaining width
  - Vertical center alignment
  - MaxWidthRow constraint

#### Coin Name Display
- **Given** Coin identifier
- **When** Shown
- **Then** Should:
  - Display before input
  - Use primary text color
  - Include trailing space
  - Match input text size
  - Support long names *[Enhancement]*

### 9. Placeholder Text

#### Placeholder Display
- **Given** Empty input field
- **When** No value entered
- **Then** Should show:
  - Localized placeholder: "placeholder_receiveToken_addAmountBottomSheet"
  - Secondary text color (gray)
  - Same font size as input
  - Disappear on typing
  - Reappear when cleared

## Decimal Handling

### 10. Decimal Places Management

#### Decimal Restrictions
- **Given** Decimals parameter provided
- **When** User enters amount
- **Then** Should:
  - Limit decimal places to specified count
  - Prevent excess decimal digits
  - Handle rounding if needed
  - Show validation feedback
  - Maintain precision

#### No Decimals Case
- **Given** Decimals is null or 0
- **When** User types
- **Then** Should:
  - Allow integers only
  - Prevent decimal point
  - Show whole numbers
  - Adjust keyboard if possible
  - Clear validation

### 11. Amount Validation *[Enhancement]*

#### Minimum Amount
- **Given** Network requirements
- **When** Validating
- **Then** Could check:
  - Minimum transaction amount
  - Dust limit for Bitcoin
  - Gas requirements for EVM
  - Network-specific minimums
  - Show inline validation

#### Maximum Amount
- **Given** Practical limits
- **When** Validating
- **Then** Could check:
  - Maximum QR code capacity
  - Reasonable payment amounts
  - Overflow prevention
  - Scientific notation handling
  - Warning for large amounts

## Save Functionality

### 12. Save Button

#### Button State
- **Given** Save button
- **When** Displayed
- **Then** Should be:
  - Always enabled (currently)
  - Full width layout
  - Gradient background
  - Prominent positioning
  - Clear label text

#### Button Action
- **Given** Save button tapped
- **When** Processing
- **Then** Should:
  - Extract current amount
  - Call onSaveAmount callback
  - Pass amount as string
  - Dismiss bottom sheet
  - Update parent QR code

### 13. Save Validation *[Enhancement]*

#### Empty Amount Handling
- **Given** No amount entered
- **When** Save pressed
- **Then** Could:
  - Clear amount from QR
  - Return to basic QR
  - Show confirmation
  - Handle as "0"
  - Prevent save

#### Invalid Amount Handling
- **Given** Invalid input
- **When** Save attempted
- **Then** Could:
  - Show error message
  - Highlight problem
  - Prevent dismissal
  - Suggest correction
  - Log validation error

## Navigation

### 14. Sheet Dismissal

#### Save Dismissal
- **Given** Successful save
- **When** Completed
- **Then** Should:
  - Close bottom sheet
  - Return to parent screen
  - Show updated QR
  - Maintain navigation state
  - Smooth animation

#### Cancel Options *[Enhancement]*
- **Given** User wants to cancel
- **When** Without saving
- **Then** Could provide:
  - Swipe down to dismiss
  - Cancel button
  - Back gesture
  - Outside tap (optional)
  - Escape key (desktop)

### 15. Auto-Focus Management

#### Initial Focus
- **Given** Sheet opens
- **When** Rendered
- **Then** Should:
  - Request focus immediately
  - Show keyboard automatically
  - Position cursor at end
  - Select all text (optional)
  - Ensure field visible

#### Focus Loss Handling
- **Given** Focus lost
- **When** User navigates
- **Then** Should:
  - Maintain entered value
  - Hide keyboard
  - Keep sheet open
  - Allow refocus
  - Preserve state

## Platform Considerations

### 16. Mobile Adaptations

#### iOS Keyboard
- **Given** iOS platform
- **When** Keyboard appears
- **Then** Should:
  - Show number pad
  - Include decimal key
  - Show Done button
  - Handle safe area
  - Support keyboard toolbar

#### Android Keyboard
- **Given** Android platform
- **When** Keyboard appears
- **Then** Should:
  - Show numeric keyboard
  - Include decimal separator
  - Show action button
  - Handle back button
  - Support keyboard switching

### 17. Desktop Support *[Enhancement]*

#### Desktop Input
- **Given** Desktop platform
- **When** Using keyboard
- **Then** Could support:
  - Full keyboard input
  - Number pad usage
  - Enter key to save
  - Tab navigation
  - Escape to cancel

#### Desktop Enhancements
- **Given** Larger screen
- **When** Space available
- **Then** Could show:
  - Currency conversion
  - Fiat equivalent
  - Recent amounts
  - Calculator mode
  - Amount suggestions

## Enhanced Features *[Enhancements]*

### 18. Currency Conversion

#### Fiat Display
- **Given** Amount entered
- **When** Exchange rate available
- **Then** Could show:
  - USD equivalent
  - Local currency value
  - Real-time conversion
  - Multiple currencies
  - Rate update indicator

#### Conversion Controls
- **Given** Conversion feature
- **When** Implemented
- **Then** Could offer:
  - Toggle conversion display
  - Select display currency
  - Enter fiat amount instead
  - Reverse calculation
  - Rate source selection

### 19. Amount Suggestions

#### Quick Amounts
- **Given** Common amounts
- **When** Sheet opens
- **Then** Could show:
  - Preset amount buttons
  - Recent amounts used
  - Round number suggestions
  - Common payment amounts
  - One-tap selection

#### Smart Suggestions
- **Given** Usage patterns
- **When** Analyzing
- **Then** Could suggest:
  - Frequently used amounts
  - Contact-specific amounts
  - Category-based amounts
  - Time-based patterns
  - Network fee inclusion

### 20. Calculator Mode

#### Built-in Calculator
- **Given** Complex calculations
- **When** Needed
- **Then** Could provide:
  - Basic math operations
  - Percentage calculations
  - Split bill feature
  - Tax calculations
  - Expression evaluation

#### Calculator UI
- **Given** Calculator mode
- **When** Activated
- **Then** Could show:
  - Number pad layout
  - Operation buttons
  - Clear/delete options
  - Result preview
  - History of calculations

## Input Formatting

### 21. Number Formatting

#### Display Formatting
- **Given** Amount entered
- **When** Displaying
- **Then** Should:
  - Group thousands (optional)
  - Use locale separator
  - Maintain precision
  - Handle leading zeros
  - Format consistently

#### Input Formatting
- **Given** User typing
- **When** Entering amount
- **Then** Could:
  - Auto-format as typing
  - Add thousand separators
  - Handle decimal point
  - Prevent invalid chars
  - Support backspace properly

### 22. Paste Handling

#### Clipboard Paste
- **Given** Amount in clipboard
- **When** Pasting
- **Then** Should:
  - Accept numeric values
  - Strip non-numeric chars
  - Validate decimal places
  - Handle different formats
  - Show paste result

#### Smart Paste *[Enhancement]*
- **Given** Various formats
- **When** Pasting
- **Then** Could parse:
  - "$100.50" → "100.50"
  - "1,234.56" → "1234.56"
  - "€ 50,00" → "50.00"
  - "100 USD" → "100"
  - Multiple format support

## Accessibility

### 23. Screen Reader Support

#### Content Announcements
- **Given** Screen reader active
- **When** Sheet opens
- **Then** Should announce:
  - "Add amount for receiving [coin]"
  - Current amount if present
  - Input field focus
  - Placeholder text
  - Save button availability

#### Input Announcements
- **Given** Typing amount
- **When** Screen reader active
- **Then** Should:
  - Announce each digit
  - Confirm decimal point
  - Read total amount
  - Announce validation
  - Confirm save action

### 24. Visual Accessibility

#### High Contrast Mode
- **Given** High contrast enabled
- **When** Viewing sheet
- **Then** Should provide:
  - Clear input borders
  - Enhanced text contrast
  - Visible focus indicators
  - Distinct button states
  - Clear placeholder text

#### Text Scaling
- **Given** Large text enabled
- **When** Viewing sheet
- **Then** Should:
  - Scale text appropriately
  - Maintain layout integrity
  - Keep input readable
  - Adjust sheet height
  - Preserve functionality

## Error Handling

### 25. Input Errors

#### Invalid Character Entry
- **Given** Invalid input attempted
- **When** User types
- **Then** Should:
  - Prevent invalid characters
  - Ignore alphabetic input
  - Handle special characters
  - Maintain valid state
  - No visual disruption

#### Overflow Handling
- **Given** Very large number
- **When** Entered
- **Then** Should:
  - Prevent overflow
  - Show max value warning
  - Truncate if needed
  - Maintain stability
  - Clear user feedback

### 26. Network Errors *[Enhancement]*

#### API Validation
- **Given** Network validation
- **When** Checking amount
- **Then** Could handle:
  - Connection timeout
  - Invalid response
  - Rate limit errors
  - Fallback to offline
  - Retry mechanism

## Testing Scenarios

### 27. Happy Path - Add Amount

1. Tap "Add amount" on receive screen
2. Sheet opens with auto-focus
3. Keyboard appears automatically
4. Type "100.50"
5. See amount in blue color
6. Tap Save button
7. Sheet closes
8. QR code updated with amount

### 28. Happy Path - Edit Amount

1. Open sheet with existing amount
2. See "50.00" pre-filled
3. Clear and type "75.25"
4. Tap Done on keyboard
5. Sheet closes with new amount
6. Parent screen updated
7. QR reflects new amount

### 29. Edge Cases

#### Input Edge Cases
- Very long decimal: Respect decimal limit
- Leading zeros: Handle gracefully
- Multiple decimal points: Prevent entry
- Only decimal point: Handle as "0."
- Paste invalid text: Strip non-numeric

#### Interaction Edge Cases
- Double-tap save: Single action only
- Rotate during input: Maintain value
- Background app: Preserve state
- Memory pressure: Keep amount
- Quick open/close: Handle smoothly

## Performance Requirements

### 30. Response Times

- **Sheet open**: < 100ms
- **Auto-focus**: < 50ms
- **Keyboard show**: < 200ms
- **Input response**: Immediate
- **Save action**: < 100ms

### 31. Resource Usage

- **Memory footprint**: < 10MB
- **CPU during input**: < 10%
- **Keyboard response**: 60 FPS
- **Animation smoothness**: 60 FPS
- **Battery impact**: Minimal

## Security Considerations

### 32. Data Protection

- **Amount validation**: Client-side
- **No sensitive logging**: Amount only
- **Secure callbacks**: Protected
- **Input sanitization**: Always applied
- **Memory clearing**: On dismiss

## Analytics

### 33. Event Tracking

#### Screen Events
- **Given** Analytics enabled
- **When** User interacts
- **Then** Should track:
  - Screen view: ADD_AMOUNT_TO_RECEIVE_QR
  - Amount entered (range only)
  - Save action
  - Cancel action
  - Time to complete

## Acceptance Criteria Summary

### Must Have (Current Implementation)
✅ Bottom sheet with gradient background
✅ Amount input with coin name prefix
✅ Auto-focus on open
✅ Decimal keyboard type
✅ Placeholder text support
✅ Save button functionality
✅ Done keyboard action
✅ Callback to parent screen
✅ Proper text styling
✅ Analytics tracking

### Should Have (Reasonable Enhancements)
⭐ Amount validation (min/max)
⭐ Empty amount handling
⭐ Swipe-to-dismiss gesture
⭐ Cancel button option
⭐ Thousand separator formatting
⭐ Paste handling improvement
⭐ Error feedback for invalid amounts
⭐ Loading state during save
⭐ Decimal place enforcement
⭐ Platform-specific optimizations

### Nice to Have (Future Enhancements)
💡 Currency conversion display
💡 Quick amount suggestions
💡 Calculator mode
💡 Recent amounts history
💡 Smart paste parsing
💡 Fiat amount entry
💡 Split bill calculator
💡 Network fee inclusion
💡 Amount templates
💡 Voice input support