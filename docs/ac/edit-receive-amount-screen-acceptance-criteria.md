# Edit Receive Amount Screen - Acceptance Criteria

## Overview
This document defines the acceptance criteria for the Edit Receive Amount Screen in the Mangala Wallet application. This is a bottom sheet modal that provides options to edit or remove the amount from a payment request QR code. The screen appears when users want to modify an existing payment request amount, allowing them to either update the requested amount or remove it entirely to create a standard address-only QR code.

## Critical Implementation Note

### 1. Current Implementation Status

#### Core Components Implemented
- **Given** The current implementation
- **When** Reviewing the code
- **Then** Note that:
  - Bottom sheet modal design
  - Two action options (Edit/Remove)
  - Gradient background styling
  - Icon-based option rows
  - Callback functions for actions
  - Navigation bar padding
  - Clean, minimal interface
  - No form validation needed (handled elsewhere)
  - No state management in this screen

#### Screen Purpose
- **Given** Payment request QR with amount
- **When** User needs to modify
- **Then** This screen provides:
  - Quick access to edit amount
  - Option to remove amount entirely
  - Simple two-option interface
  - Immediate action callbacks
  - Bottom sheet interaction pattern

## Visual Design

### 2. Bottom Sheet Layout

#### Sheet Appearance
- **Given** Bottom sheet opened
- **When** Screen displayed
- **Then** Should show:
  - Rounded top corners
  - Gradient background
  - Drag handle indicator
  - Smooth slide animation
  - Proper elevation/shadow
  - Backdrop dimming

#### Drag Handle
- **Given** Sheet drag handle
- **When** Displayed at top
- **Then** Should:
  - Center horizontally
  - Use standard dimensions (width: 40dp)
  - Use border color
  - Rounded corners (Medium radius)
  - Visual affordance for dragging
  - Accessibility support

### 3. Content Layout

#### Vertical Spacing
- **Given** Content arrangement
- **When** Elements positioned
- **Then** Should maintain:
  - Half padding at top/bottom
  - Default horizontal padding
  - XXBASE + BASE space after handle
  - Default padding around divider
  - Small space at bottom
  - Navigation bar padding

#### Option Rows
- **Given** Action options
- **When** Displayed
- **Then** Should show:
  - Icon on left (24dp size)
  - Text label next to icon
  - XSMALL spacing between icon/text
  - Half padding vertically
  - Full width clickable area
  - Centered alignment

### 4. Visual Styling

#### Color Scheme
- **Given** Theme colors
- **When** Applied
- **Then** Should use:
  - Primary icon color
  - Primary text color
  - Border color for divider
  - Border color for handle
  - Gradient background
  - Consistent with app theme

#### Typography
- **Given** Text elements
- **When** Displayed
- **Then** Should use:
  - Size14Medium for labels
  - Primary text color
  - Consistent font family
  - Proper line height
  - Clear readability
  - Localized strings

## User Interactions

### 5. Edit Amount Option

#### Visual Feedback
- **Given** Edit option row
- **When** User taps
- **Then** Should:
  - Show press state
  - Ripple effect (Android)
  - Highlight (iOS)
  - Immediate response
  - Call onEditAmount callback
  - Dismiss sheet

#### Icon Display
- **Given** Edit icon
- **When** Shown
- **Then** Should:
  - Use IcEdit vector icon
  - Display at 24dp size
  - Primary icon color
  - Clear visual meaning
  - Proper alignment
  - Accessibility label

### 6. Remove Amount Option

#### Visual Feedback
- **Given** Remove option row
- **When** User taps
- **Then** Should:
  - Show press state
  - Visual feedback
  - Call onRemoveAmount callback
  - Dismiss sheet immediately
  - No confirmation needed
  - Smooth transition

#### Icon Display
- **Given** Delete icon
- **When** Shown
- **Then** Should:
  - Use Delete vector icon
  - Display at 24dp size
  - Primary icon color
  - Clear warning indication
  - Proper alignment
  - Accessibility label

### 7. Divider

#### Divider Appearance
- **Given** Horizontal divider
- **When** Between options
- **Then** Should:
  - Span full width
  - Use border color
  - Default vertical padding
  - 1dp thickness
  - Visual separation
  - Not interactive

## Navigation & Flow

### 8. Sheet Behavior

#### Opening Animation
- **Given** Sheet triggered
- **When** Opening
- **Then** Should:
  - Slide from bottom
  - Smooth animation (300ms)
  - Fade backdrop
  - Block background interaction
  - Maintain state
  - Handle quick open/close

#### Dismissal Methods
- **Given** Sheet open
- **When** User wants to dismiss
- **Then** Can dismiss via:
  - Swipe down gesture
  - Backdrop tap
  - Option selection
  - Back button (Android)
  - Programmatic dismissal
  - Hardware back

### 9. Callback Handling

#### Edit Amount Callback
- **Given** Edit tapped
- **When** Callback triggered
- **Then** Should:
  - Execute onEditAmount
  - Dismiss sheet first
  - Navigate to amount input
  - Pass current amount
  - Handle navigation
  - Track analytics

#### Remove Amount Callback
- **Given** Remove tapped
- **When** Callback triggered
- **Then** Should:
  - Execute onRemoveAmount
  - Dismiss sheet first
  - Clear amount from QR
  - Update parent screen
  - Show confirmation toast
  - Track action

### 10. Parent Screen Integration

#### State Updates
- **Given** Action completed
- **When** Returning to parent
- **Then** Should:
  - Update QR display
  - Reflect changes immediately
  - Maintain other settings
  - Show success feedback
  - Update UI labels
  - Cache changes

## Accessibility

### 11. Screen Reader Support

#### Content Description
- **Given** Screen reader active
- **When** Sheet opened
- **Then** Should announce:
  - "Edit amount options"
  - Each option clearly
  - Current amount if relevant
  - Actions available
  - Dismissal instructions
  - Focus management

#### Navigation
- **Given** Keyboard/switch navigation
- **When** Using assistive tech
- **Then** Should:
  - Focus on first option
  - Tab through options
  - Clear focus indicators
  - Escape to dismiss
  - Logical tab order
  - Action announcements

### 12. Touch Targets

#### Minimum Sizes
- **Given** Interactive elements
- **When** Touch targets
- **Then** Should maintain:
  - 48dp minimum height
  - Full row width
  - Adequate padding
  - No overlapping targets
  - Clear boundaries
  - Edge-to-edge tappable

#### Visual Indicators
- **Given** Interactive areas
- **When** Displayed
- **Then** Should show:
  - Clear boundaries
  - Hover states (desktop)
  - Focus indicators
  - Press states
  - Disabled states (if applicable)
  - Selection feedback

## Localization

### 13. Text Content

#### String Resources
- **Given** UI text
- **When** Displayed
- **Then** Should use:
  - Localized edit label
  - Localized remove label
  - Proper translations
  - RTL support
  - Text wrapping
  - Dynamic sizing

#### Language Support
- **Given** Multiple languages
- **When** Switched
- **Then** Should:
  - Update immediately
  - Maintain layout
  - Handle long text
  - Support RTL
  - Proper formatting
  - Cultural appropriateness

## Error Handling

### 14. Callback Failures

#### Error Recovery
- **Given** Callback error
- **When** Action fails
- **Then** Should:
  - Catch exceptions
  - Show error toast
  - Keep sheet open
  - Allow retry
  - Log error
  - Maintain state

#### Network Issues
- **Given** Offline state
- **When** Actions triggered
- **Then** Should:
  - Work offline (local operation)
  - Update UI immediately
  - Queue if needed
  - Show status
  - Handle gracefully
  - No data loss

## Enhanced Features *[Enhancements]*

### 15. Amount Preview

#### Current Amount Display
- **Given** Existing amount
- **When** Sheet opens
- **Then** Could show:
  - Current amount value
  - Currency symbol
  - Formatted display
  - Conversion to fiat
  - Network fees note
  - Visual context

#### Quick Amount Presets
- **Given** Common amounts
- **When** Editing
- **Then** Could offer:
  - Preset amounts
  - Recent amounts
  - Round numbers
  - Quick selection
  - Custom presets
  - One-tap update

### 16. Advanced Options

#### Additional Actions
- **Given** More functionality
- **When** Needed
- **Then** Could add:
  - Copy amount
  - Share amount
  - Set expiration
  - Add memo/note
  - Currency conversion
  - Fee adjustment

#### QR Code Preview
- **Given** Visual feedback
- **When** Making changes
- **Then** Could show:
  - Mini QR preview
  - Before/after comparison
  - Live updates
  - Visual diff
  - Instant feedback
  - Undo option

### 17. Confirmation Dialog

#### Remove Confirmation
- **Given** Destructive action
- **When** Remove tapped
- **Then** Could show:
  - Confirmation dialog
  - Impact explanation
  - Undo option
  - Remember choice
  - Skip for session
  - Quick restore

#### Edit Validation
- **Given** Amount limits
- **When** Editing
- **Then** Could validate:
  - Minimum amount
  - Maximum amount
  - Network limits
  - Decimal places
  - Valid formats
  - Warning thresholds

### 18. History & Analytics

#### Action History
- **Given** User actions
- **When** Tracked
- **Then** Could record:
  - Edit history
  - Remove actions
  - Timestamps
  - Amount changes
  - Frequency data
  - Usage patterns

#### Analytics Events
- **Given** User behavior
- **When** Analyzed
- **Then** Could track:
  - Option selection
  - Time to action
  - Dismissal method
  - Error frequency
  - Success rate
  - User flow

### 19. Smart Suggestions

#### Amount Recommendations
- **Given** Transaction patterns
- **When** Editing amount
- **Then** Could suggest:
  - Common amounts
  - Previous amounts
  - Network averages
  - Round numbers
  - Gas-optimized amounts
  - Market rates

#### Context Awareness
- **Given** User context
- **When** Opening sheet
- **Then** Could consider:
  - Time of day
  - Transaction history
  - Network congestion
  - Market conditions
  - User preferences
  - Regional standards

### 20. Gesture Enhancements

#### Swipe Actions
- **Given** Gesture support
- **When** Implemented
- **Then** Could support:
  - Swipe to edit
  - Swipe to remove
  - Partial swipe preview
  - Haptic feedback
  - Gesture hints
  - Accessibility alternatives

#### Long Press
- **Given** Alternative interaction
- **When** Long pressing
- **Then** Could show:
  - Tooltip information
  - Extended options
  - Help text
  - Preview changes
  - Context menu
  - Quick actions

### 21. Batch Operations

#### Multiple QR Management
- **Given** Multiple QRs
- **When** Managing amounts
- **Then** Could:
  - Edit multiple
  - Bulk remove
  - Copy settings
  - Apply to all
  - Template system
  - Group actions

### 22. Integration Features

#### External Apps
- **Given** App integration
- **When** Sharing amounts
- **Then** Could:
  - Export to contacts
  - Send via messaging
  - Calendar reminders
  - Invoice generation
  - Accounting export
  - API access

#### Notification Settings
- **Given** Payment requests
- **When** Amount set
- **Then** Could:
  - Set reminders
  - Payment notifications
  - Expiration alerts
  - Amount received alerts
  - Partial payment tracking
  - Follow-up actions

## Testing Scenarios

### 23. Happy Path - Edit Amount

1. Open receive screen with amount
2. Tap edit amount option
3. Bottom sheet appears smoothly
4. Tap "Edit Amount"
5. Sheet dismisses
6. Navigate to amount input
7. Update amount
8. Return to QR display
9. See updated QR

### 24. Happy Path - Remove Amount

1. Open receive screen with amount
2. Tap edit amount option
3. Bottom sheet appears
4. Tap "Remove Amount"
5. Sheet dismisses
6. Amount cleared from QR
7. QR shows address only
8. UI updated accordingly

### 25. Edge Cases

#### Rapid Interactions
- Double-tap option: Single action only
- Quick open/close: Handle gracefully
- Multiple sheets: Prevent stacking
- Background tap: Dismiss properly
- Gesture conflicts: Prioritize correctly

#### State Management
- Rotation during display: Maintain state
- Background/foreground: Preserve position
- Memory pressure: Handle gracefully
- Process death: Recover state
- Deep linking: Handle correctly

### 26. Accessibility Testing

#### Screen Reader Flow
1. Enable screen reader
2. Open bottom sheet
3. Hear announcement
4. Navigate options
5. Activate option
6. Confirm action
7. Return to parent

#### Keyboard Navigation
1. Connect keyboard
2. Open sheet with keyboard
3. Tab through options
4. Select with Enter
5. Escape to dismiss
6. Verify focus management

## Performance Requirements

### 27. Animation Performance

- **Sheet opening**: < 300ms
- **Sheet dismissal**: < 200ms
- **Touch response**: < 50ms
- **Callback execution**: < 100ms
- **UI update**: Immediate
- **Frame rate**: 60 FPS

### 28. Resource Usage

- **Memory footprint**: < 5MB
- **CPU usage**: < 5%
- **Battery impact**: Minimal
- **Layout passes**: Optimized
- **Overdraw**: Minimized
- **Render time**: < 16ms

## Security Requirements

### 29. Data Protection

- **Amount values**: Not logged
- **User actions**: Anonymous tracking
- **Callbacks**: Secure execution
- **Memory**: Cleared on dismiss
- **Screenshots**: Allowed (non-sensitive)
- **State**: No persistence needed

### 30. Input Validation

- **Callback safety**: Null checks
- **Exception handling**: Try-catch blocks
- **Thread safety**: Main thread only
- **Memory leaks**: Prevent references
- **Lifecycle**: Respect boundaries
- **Navigation**: Safe transitions

## Acceptance Criteria Summary

### Must Have (Current Implementation)
✅ Bottom sheet modal design
✅ Edit amount option with icon
✅ Remove amount option with icon
✅ Horizontal divider
✅ Gradient background
✅ Callback functions
✅ Proper spacing and padding
✅ Localized strings
✅ Touch feedback
✅ Navigation bar padding

### Should Have (Reasonable Enhancements)
⭐ Current amount display
⭐ Confirmation for remove
⭐ Amount validation hints
⭐ Analytics tracking
⭐ Error handling
⭐ Loading states
⭐ Success feedback
⭐ Undo capability
⭐ Help text
⭐ Accessibility improvements

### Nice to Have (Future Enhancements)
💡 Amount presets
💡 QR preview
💡 History tracking
💡 Smart suggestions
💡 Swipe gestures
💡 Batch operations
💡 External app integration
💡 Advanced analytics
💡 Currency conversion
💡 Template system