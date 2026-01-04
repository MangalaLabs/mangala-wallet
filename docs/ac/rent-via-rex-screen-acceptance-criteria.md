# Rent Via REX Screen - Acceptance Criteria

## Overview
This document defines the acceptance criteria for the Rent Via REX Screen in the Mangala Wallet application. This screen allows users to rent CPU or NET resources through the REX (Resource Exchange) system on Antelope blockchain networks, providing a 30-day rental period for blockchain resources.

## Screen Purpose

### 1. REX Resource Rental

#### Primary Function
- **Given** User needs CPU/NET resources
- **When** Renting via REX system
- **Then** Should provide:
  - Resource amount specification
  - REX rate display
  - 30-day rental period
  - Cost calculation
  - Transaction execution

#### Resource Types
- **Given** Resource rental needs
- **When** Using REX
- **Then** Supports:
  - CPU rental (processing power)
  - NET rental (network bandwidth)
  - Same interface for both
  - Resource-specific pricing
  - Usage monitoring

## Screen Parameters

### 2. Initialization Parameters

#### Required Parameters
- **Given** Screen initialization
- **When** Opening REX rental
- **Then** Must provide:
  - accountName: String (renter account)
  - isCpu: Boolean (true for CPU, false for NET)

#### Parameter Usage
- **Given** Parameters provided
- **When** Screen loads
- **Then** Should:
  - Load account balance
  - Fetch REX rates
  - Initialize for correct resource
  - Set appropriate labels
  - Track analytics

## Visual Design

### 3. Screen Layout

#### Overall Structure
- **Given** Screen displays
- **When** Rendered
- **Then** Should show:
  - ResourceScreen component
  - Title: "Rent via REX"
  - Amount input section
  - Price information
  - Rent button
  - Pull-to-refresh support

#### Component Reuse
- **Given** ResourceScreen usage
- **When** Implemented
- **Then** Provides:
  - Consistent UI pattern
  - Shared functionality
  - Standard layout
  - Common interactions
  - Unified experience

## Title and Headers

### 4. Screen Title

#### Title Display
- **Given** Screen header
- **When** Shown
- **Then** Should display:
  - "Rent via REX" title
  - Back navigation button
  - Center alignment
  - Standard styling
  - Consistent height

### 5. Input Section Title

#### Resource-Specific Title
- **Given** Input section
- **When** Displayed
- **Then** Should show:
  - CPU: "CPU Amount" label
  - NET: "NET Amount" label
  - Localized strings
  - Clear identification
  - Proper formatting

## Amount Input

### 6. Resource Amount Input

#### Input Field Design
- **Given** Amount input
- **When** Displayed
- **Then** Should have:
  - Numeric input field
  - Resource unit display
  - Keyboard type numeric
  - Real-time validation
  - Error message support

#### Input Validation
- **Given** Amount entered
- **When** Validating
- **Then** Should:
  - Check against balance
  - Validate numeric format
  - Show inline errors
  - Enable/disable button
  - Provide feedback

### 7. Amount Unit Display

#### Unit Information
- **Given** Amount field
- **When** Active
- **Then** Should show:
  - Appropriate unit suffix
  - CPU: processing units
  - NET: bandwidth units
  - Clear labeling
  - Consistent display

## Balance Display

### 8. Available Balance

#### Balance Information
- **Given** Account data
- **When** Loaded
- **Then** Should show:
  - Native coin balance
  - Formatted amount
  - Loading placeholder
  - Real-time updates
  - Accurate values

#### Balance Loading
- **Given** Fetching balance
- **When** Loading
- **Then** Should:
  - Show placeholder
  - Maintain layout
  - Update smoothly
  - Handle errors
  - Display when ready

## Pricing Information

### 9. REX Rate Display

#### Rate Information
- **Given** Pricing section
- **When** Displayed
- **Then** Should show:
  - Current REX rate
  - Format: "0.0000" precision
  - Loading placeholder if null
  - Price label text
  - Real-time updates

#### Price Labels
- **Given** Resource type
- **When** Showing prices
- **Then** Should display:
  - CPU: "CPU Prices" label
  - NET: "NET Prices" label
  - Secondary text color
  - Tiny font size
  - Proper localization

### 10. Resource Usage Display

#### Usage Percentage
- **Given** Current usage
- **When** Calculated
- **Then** Should show:
  - "Used: X.X%" format
  - Current resource utilization
  - Loading placeholder
  - Secondary text color
  - Accurate calculation

## Transaction Controls

### 11. Rent Button

#### Button Design
- **Given** Action button
- **When** Displayed
- **Then** Should show:
  - "Rent" label
  - Gradient background
  - Full width layout
  - Enable/disable states
  - Loading indication

#### Button Validation
- **Given** Form state
- **When** Checking
- **Then** Requires:
  - Valid amount entered
  - Sufficient balance
  - No validation errors
  - Input enabled
  - All checks passed

### 12. PIN Authentication

#### Security Flow
- **Given** Rent initiated
- **When** Authentication needed
- **Then** Should:
  - Show PIN bottom sheet
  - Request verification
  - Handle success callback
  - Hide sheet on success
  - Cancel on failure

#### Authentication Handling
- **Given** PIN verified
- **When** Successful
- **Then** Should:
  - Execute transaction
  - Show loading state
  - Handle errors
  - Update UI
  - Track event

## Fee Management

### 13. Resource Provider Fee

#### Fee Dialog
- **Given** Transaction fees
- **When** Applicable
- **Then** Should show:
  - Fee breakdown dialog
  - Total resources required
  - Detailed breakdown
  - Confirm button
  - Dismiss option

#### Fee Actions
- **Given** Fee dialog shown
- **When** User acts
- **Then** Can:
  - Confirm and proceed
  - Dismiss and cancel
  - Review details
  - Understand costs
  - Make decision

## Success Flow

### 14. Transaction Success

#### Success Screen
- **Given** Rental successful
- **When** Displayed
- **Then** Should show:
  - Success message
  - "Rented successfully" text
  - Continue button
  - Back to home button
  - Transaction confirmation

#### Success Actions
- **Given** Success screen
- **When** User acts
- **Then** Can:
  - Continue (new rental)
  - Back to home (root)
  - View details
  - Start over
  - Close screen

## Refresh Functionality

### 15. Pull to Refresh

#### Refresh Gesture
- **Given** Pull down action
- **When** Triggered
- **Then** Should:
  - Show refresh indicator
  - Reload rates
  - Update balance
  - Refresh usage
  - Complete animation

#### Data Refresh
- **Given** Refreshing
- **When** Active
- **Then** Should:
  - Update REX rates
  - Fetch new balance
  - Recalculate usage
  - Clear cached data
  - Show current info

## Error Handling

### 16. Error States

#### Error Display
- **Given** Error occurred
- **When** Shown
- **Then** Should display:
  - Error message text
  - Clear description
  - Retry options
  - Help guidance
  - Recovery actions

#### Input Errors
- **Given** Validation fails
- **When** Displayed
- **Then** Should show:
  - Inline error text
  - Field highlighting
  - Clear message
  - How to fix
  - Real-time feedback

## State Management

### 17. Screen States

#### Loading State
- **Given** Data loading
- **When** Active
- **Then** Should:
  - Show placeholders
  - Disable inputs
  - Show progress
  - Maintain layout
  - Update smoothly

#### Loaded State
- **Given** Data ready
- **When** Displayed
- **Then** Should show:
  - All information
  - Enable interactions
  - Current values
  - Active controls
  - Ready state

## Enhanced Features *[Enhancements]*

### 18. Rental History

#### Previous Rentals
- **Given** Rental history
- **When** Available
- **Then** Could show:
  - Past rentals list
  - Expiry dates
  - Amounts rented
  - Cost history
  - Quick re-rent

### 19. Rental Calculator

#### Cost Estimation
- **Given** Amount entered
- **When** Calculating
- **Then** Could show:
  - Total cost
  - Cost breakdown
  - 30-day period info
  - Comparison with PowerUp
  - Savings calculation

### 20. Auto-Renewal

#### Subscription Feature
- **Given** Rental expiring
- **When** Configured
- **Then** Could offer:
  - Auto-renewal option
  - Reminder notifications
  - Renewal settings
  - Cancel anytime
  - Payment methods

### 21. Rental Packages

#### Bundle Options
- **Given** Common needs
- **When** Renting
- **Then** Could provide:
  - CPU + NET bundles
  - Preset packages
  - Discount offers
  - Popular choices
  - Custom packages

### 22. Market Analysis

#### REX Market Info
- **Given** Market data
- **When** Available
- **Then** Could show:
  - REX pool size
  - Current demand
  - Price trends
  - Best times to rent
  - Market insights

### 23. Resource Monitoring

#### Usage Tracking
- **Given** Active rentals
- **When** Monitoring
- **Then** Could display:
  - Current usage graph
  - Remaining resources
  - Usage patterns
  - Alerts for high usage
  - Optimization tips

### 24. Comparison View

#### Provider Comparison
- **Given** Multiple options
- **When** Choosing
- **Then** Could show:
  - REX vs PowerUp
  - Cost comparison
  - Duration differences
  - Pros and cons
  - Recommendation

### 25. Batch Rentals

#### Multiple Resources
- **Given** Various needs
- **When** Renting
- **Then** Could support:
  - Rent both CPU and NET
  - Single transaction
  - Combined pricing
  - Bulk discounts
  - Efficient process

## Accessibility

### 26. Screen Reader Support

#### Content Announcements
- **Given** Screen reader active
- **When** Navigating
- **Then** Should announce:
  - Resource type (CPU/NET)
  - Current rates
  - Input values
  - Error messages
  - Success status

#### Form Navigation
- **Given** Keyboard navigation
- **When** Using
- **Then** Should:
  - Logical tab order
  - Clear focus states
  - Input descriptions
  - Button purposes
  - State changes

### 27. Visual Accessibility

#### High Contrast Mode
- **Given** High contrast enabled
- **When** Viewing
- **Then** Should provide:
  - Clear boundaries
  - Text contrast
  - Focus indicators
  - Error visibility
  - Button states

## Performance

### 28. Loading Performance

- **Screen load**: < 300ms
- **Rate fetch**: < 1 second
- **Balance update**: < 500ms
- **Transaction submit**: < 3 seconds
- **Refresh action**: < 2 seconds

### 29. Resource Usage

- **Memory footprint**: < 30MB
- **CPU during idle**: < 5%
- **Network requests**: Optimized
- **Animation smoothness**: 60 FPS
- **Battery impact**: Minimal

## Testing Scenarios

### 30. Happy Path - CPU Rental

1. Open REX screen for CPU
2. View current rates
3. Enter rental amount
4. See cost calculation
5. Tap rent button
6. Complete PIN auth
7. View success screen
8. Continue or go home

### 31. Happy Path - NET Rental

1. Open REX screen for NET
2. Check balance
3. Enter amount
4. Review pricing
5. Proceed with rental
6. Authenticate
7. Success confirmation
8. Complete flow

### 32. Edge Cases

#### Input Edge Cases
- Zero amount: Show error
- Excess amount: Insufficient balance
- Invalid format: Validation error
- Very large amount: Handle properly
- Decimal precision: Respect limits

#### State Edge Cases
- Network failure: Error message
- PIN cancelled: Return to form
- Rate unavailable: Show placeholder
- Balance error: Retry option
- Quick navigation: Handle properly

## Security Requirements

### 33. Transaction Security

- **PIN verification**: Always required
- **Amount validation**: Prevent overflow
- **Rate verification**: Current prices
- **Session security**: Timeout handling
- **Secure transmission**: Encrypted

### 34. Data Protection

- **No sensitive logging**: Privacy
- **Secure storage**: Encrypted
- **Clear on exit**: Sensitive data
- **Audit trail**: Transactions
- **Error sanitization**: No leaks

## Analytics

### 35. Event Tracking

#### Rental Events
- **Given** Analytics enabled
- **When** User interacts
- **Then** Should track:
  - Screen view (CPU/NET specific)
  - Rental attempts
  - Success/failure rates
  - Amount ranges
  - Error types

## Acceptance Criteria Summary

### Must Have (Current Implementation)
✅ CPU and NET rental support
✅ Amount input with validation
✅ REX rate display with loading states
✅ Resource usage percentage display
✅ PIN authentication for security
✅ Resource provider fee dialog
✅ Success screen with options
✅ Pull-to-refresh functionality
✅ Error state handling
✅ Balance display

### Should Have (Reasonable Enhancements)
⭐ Rental history display
⭐ Cost calculator
⭐ Better error messages
⭐ Transaction confirmation details
⭐ Remaining rental time
⭐ Quick re-rent option
⭐ Rate comparison with PowerUp
⭐ Network status indicator
⭐ Estimated transaction time
⭐ Help tooltips

### Nice to Have (Future Enhancements)
💡 Auto-renewal subscriptions
💡 Bundle packages
💡 Market analysis tools
💡 Resource monitoring dashboard
💡 Batch rentals
💡 Price alerts
💡 Usage predictions
💡 Optimization recommendations
💡 Social features
💡 Advanced analytics