# PowerUp Screen - Acceptance Criteria

## Overview
This document defines the acceptance criteria for the PowerUp Screen in the Mangala Wallet application. PowerUp is an Antelope blockchain feature that allows users to rent CPU or NET resources for 24 hours instead of permanently staking tokens. This screen provides an interface for users to specify the amount of resources they want to rent, view current pricing, and complete the PowerUp transaction. The feature is essential for users who need temporary resources for transactions without locking up their tokens permanently.

## Critical Implementation Note

### 1. Current Implementation Status

#### Core Components Implemented
- **Given** The current implementation
- **When** Reviewing the code
- **Then** Note that:
  - CPU and NET resource rental
  - Dynamic pricing display
  - Resource usage percentage
  - PIN authentication integration
  - Fee breakdown dialog
  - Success state handling
  - Pull-to-refresh functionality
  - Analytics tracking
  - Bottom sheet navigation for PIN
  - Resource rate calculations

#### PowerUp Mechanism
- **Given** Antelope PowerUp system
- **When** Operating
- **Then** Provides:
  - 24-hour resource rental
  - No permanent staking required
  - Market-based pricing
  - Instant resource availability
  - Automatic expiration
  - Cost-effective for occasional use

## Resource Types

### 2. CPU Resources

#### CPU PowerUp
- **Given** CPU resource needs
- **When** isCpu = true
- **Then** Should:
  - Display CPU-specific labels
  - Show CPU prices per ms
  - Display CPU usage percentage
  - Calculate CPU rental cost
  - Show current CPU allocation
  - Update CPU immediately after purchase

#### CPU Pricing
- **Given** CPU market rates
- **When** Displayed
- **Then** Should show:
  - Price per millisecond
  - Total cost for amount
  - Market rate fluctuations
  - 24-hour rental period
  - Comparison to staking
  - Value proposition

### 3. NET Resources

#### NET PowerUp
- **Given** NET resource needs
- **When** isCpu = false
- **Then** Should:
  - Display NET-specific labels
  - Show NET prices per KB
  - Display NET usage percentage
  - Calculate NET rental cost
  - Show current NET allocation
  - Update NET immediately after purchase

#### NET Pricing
- **Given** NET market rates
- **When** Displayed
- **Then** Should show:
  - Price per kilobyte
  - Total cost for amount
  - Bandwidth calculations
  - 24-hour rental period
  - Usage patterns
  - Cost efficiency

## User Interface

### 4. Screen Layout

#### Main Components
- **Given** Screen structure
- **When** Displayed
- **Then** Should show:
  - Title (PowerUp)
  - Resource type indicator
  - Input section for amount
  - Price information panel
  - Available balance
  - Action button
  - Back navigation

#### ResourceScreen Component
- **Given** Reusable component
- **When** Rendered
- **Then** Should display:
  - Consistent layout
  - Input field with unit
  - Balance information
  - Price details
  - Error messages
  - Loading states

### 5. Input Section

#### Amount Input
- **Given** Resource amount field
- **When** User enters value
- **Then** Should:
  - Accept numeric input
  - Show unit (EOS/WAX/etc)
  - Support decimals (4 places)
  - Update price in real-time
  - Validate against balance
  - Show input cursor

#### Input Validation
- **Given** User input
- **When** Validating
- **Then** Should check:
  - Positive values only
  - Maximum 4 decimal places
  - Within balance limits
  - Minimum PowerUp amount
  - Valid number format
  - No special characters

### 6. Price Display

#### Current Rates
- **Given** PowerUp pricing
- **When** Displayed
- **Then** Should show:
  - Current rate per unit
  - Formatted with 4 decimals
  - Currency symbol
  - "CPU prices" or "NET prices" label
  - Real-time updates
  - Loading placeholder if null

#### Resource Usage
- **Given** Current usage stats
- **When** Shown
- **Then** Should display:
  - Usage percentage
  - "X% used" format
  - Color coding (green/yellow/red)
  - Update on refresh
  - Loading state
  - Accurate calculations

### 7. Balance Information

#### Available Balance
- **Given** Account balance
- **When** Displayed
- **Then** Should show:
  - Native token balance
  - Formatted correctly
  - Currency symbol
  - "Available" label
  - Real-time updates
  - Sufficient for transaction

## Transaction Flow

### 8. PowerUp Initiation

#### Request Transaction
- **Given** Valid amount entered
- **When** PowerUp button clicked
- **Then** Should:
  - Validate input
  - Calculate total cost
  - Check balance sufficiency
  - Prepare transaction
  - Show fee breakdown
  - Request PIN authentication

#### Button States
- **Given** PowerUp button
- **When** Displayed
- **Then** Should be:
  - Disabled when invalid input
  - Disabled when insufficient balance
  - Enabled when valid
  - Show "Rent" label
  - Provide visual feedback
  - Loading during transaction

### 9. Fee Breakdown Dialog

#### Fee Display
- **Given** Transaction fees
- **When** Dialog shown
- **Then** Should display:
  - Resource provider fee
  - Network fee
  - Total cost breakdown
  - PowerUp amount
  - Total transaction value
  - Clear itemization

#### Dialog Actions
- **Given** Fee dialog open
- **When** User interacts
- **Then** Can:
  - Confirm transaction
  - Cancel/dismiss
  - View fee details
  - Understand costs
  - Make informed decision
  - Return to edit

### 10. PIN Authentication

#### Security Verification
- **Given** Transaction ready
- **When** PIN required
- **Then** Should:
  - Show UnlockPinScreen
  - In bottom sheet
  - Verify transaction type
  - Handle success callback
  - Dismiss on completion
  - Track authentication

#### Authentication Flow
- **Given** PIN prompt shown
- **When** User enters PIN
- **Then** Should:
  - Validate PIN
  - Call onAuthenticationSuccess
  - Hide bottom sheet
  - Continue transaction
  - Handle failures
  - Allow retry

### 11. Transaction Execution

#### Processing State
- **Given** Authenticated transaction
- **When** Executing
- **Then** Should:
  - Show loading indicator
  - Disable inputs
  - Prevent duplicate submission
  - Track progress
  - Handle timeouts
  - Show status updates

#### Success State
- **Given** PowerUp successful
- **When** Transaction complete
- **Then** Should:
  - Show success screen
  - Display confirmation message
  - Offer continue option
  - Offer home navigation
  - Update resource display
  - Clear form

## Success Screen

### 12. Success Display

#### Success Message
- **Given** Transaction success
- **When** Displayed
- **Then** Should show:
  - "Power up successfully" title
  - Success icon/animation
  - Transaction details
  - Resource allocation info
  - Expiration time (24 hours)
  - Confirmation number

#### Navigation Options
- **Given** Success screen
- **When** Actions available
- **Then** Should offer:
  - Continue button (primary)
  - Back to home (secondary)
  - Both full width
  - Clear visual hierarchy
  - Smooth transitions
  - State cleanup

## Data Management

### 13. Pull to Refresh

#### Refresh Functionality
- **Given** Pull gesture
- **When** Refreshing
- **Then** Should:
  - Update PowerUp rates
  - Refresh resource usage
  - Update balance
  - Show refresh indicator
  - Complete quickly
  - Handle errors

#### Data Updates
- **Given** Refresh triggered
- **When** Fetching data
- **Then** Should:
  - Query latest prices
  - Get account resources
  - Update UI smoothly
  - Cache new data
  - Show changes
  - Track refresh

### 14. State Management

#### UI States
- **Given** Screen states
- **When** Managing
- **Then** Should handle:
  - Loading state
  - Loaded state with data
  - Error states
  - Success state
  - Empty states
  - Transition animations

#### Data Persistence
- **Given** User input
- **When** Navigating
- **Then** Should:
  - Preserve entered amount
  - Maintain selection
  - Remember resource type
  - Cache pricing data
  - Handle rotation
  - Restore on return

## Error Handling

### 15. Input Errors

#### Validation Errors
- **Given** Invalid input
- **When** Detected
- **Then** Should show:
  - Specific error message
  - Red error text
  - Input field highlighting
  - Clear correction guidance
  - Prevent submission
  - Real-time validation

#### Common Errors
- **Given** Error conditions
- **When** Occurring
- **Then** Should handle:
  - Insufficient balance
  - Amount too small
  - Amount too large
  - Network errors
  - Invalid format
  - Resource unavailable

### 16. Transaction Errors

#### Failure Handling
- **Given** Transaction fails
- **When** Error received
- **Then** Should:
  - Show error message
  - Explain failure reason
  - Offer retry option
  - Preserve input
  - Log error details
  - Track failure rate

#### Recovery Options
- **Given** Transaction error
- **When** User wants to retry
- **Then** Should:
  - Keep form filled
  - Update prices if needed
  - Re-validate input
  - Allow modification
  - Clear error on retry
  - Prevent rapid retries

## Analytics

### 17. Event Tracking

#### Screen Analytics
- **Given** User interactions
- **When** Events occur
- **Then** Should track:
  - Screen views
  - Resource type (CPU/NET)
  - Amount entered
  - Success rate
  - Error types
  - Time to complete

#### Transaction Analytics
- **Given** PowerUp transactions
- **When** Completed
- **Then** Should track:
  - Transaction success
  - Resource amount
  - Cost in tokens
  - User behavior
  - Abandonment rate
  - Average amounts

## Enhanced Features *[Enhancements]*

### 18. Price Predictions

#### Market Analysis
- **Given** Historical data
- **When** Available
- **Then** Could show:
  - Price trends
  - Best times to PowerUp
  - Price predictions
  - Market volatility
  - Savings opportunities
  - Historical charts

#### Cost Optimization
- **Given** Usage patterns
- **When** Analyzing
- **Then** Could suggest:
  - Optimal amounts
  - Bulk purchases
  - Timing recommendations
  - Staking vs PowerUp comparison
  - Cost savings tips
  - Usage forecasting

### 19. Resource Planning

#### Usage Estimation
- **Given** Transaction plans
- **When** Planning resources
- **Then** Could:
  - Estimate resource needs
  - Calculate required PowerUp
  - Show transaction costs
  - Plan for operations
  - Batch recommendations
  - Resource calculator

#### Automatic PowerUp
- **Given** Low resources
- **When** Detected
- **Then** Could:
  - Auto-suggest PowerUp
  - Pre-fill optimal amount
  - One-click purchase
  - Smart notifications
  - Threshold settings
  - Auto-renewal option

### 20. Advanced Options

#### PowerUp Packages
- **Given** Common use cases
- **When** Selecting amount
- **Then** Could offer:
  - Preset packages
  - Small/Medium/Large options
  - Custom packages
  - Bundle deals
  - Frequent user discounts
  - Subscription model

#### Multi-Resource PowerUp
- **Given** Both resources needed
- **When** PowerUp required
- **Then** Could:
  - Bundle CPU and NET
  - Combined purchase
  - Discount for both
  - Single transaction
  - Optimized allocation
  - Save on fees

### 21. Monitoring & Alerts

#### Resource Monitoring
- **Given** Active PowerUps
- **When** Monitoring
- **Then** Could show:
  - Remaining time
  - Usage statistics
  - Expiration alerts
  - Resource consumption
  - Efficiency metrics
  - Usage patterns

#### Smart Notifications
- **Given** Resource management
- **When** Thresholds met
- **Then** Could notify:
  - PowerUp expiring
  - Low resources
  - Price drops
  - Usage spikes
  - Renewal reminders
  - Optimization tips

### 22. Educational Features

#### PowerUp Guide
- **Given** New users
- **When** First use
- **Then** Could provide:
  - PowerUp explanation
  - Video tutorial
  - FAQ section
  - Best practices
  - Cost comparison
  - Use case examples

#### Tooltips & Help
- **Given** Complex features
- **When** User needs help
- **Then** Could show:
  - Inline tooltips
  - Help bubbles
  - Contextual guidance
  - Calculation explanations
  - Resource descriptions
  - Support links

### 23. Integration Features

#### DApp Integration
- **Given** DApp usage
- **When** Resources needed
- **Then** Could:
  - Auto-detect needs
  - Integrate with DApps
  - Seamless PowerUp
  - Cross-app coordination
  - Resource sharing
  - Usage analytics

#### Wallet Integration
- **Given** Multi-account setup
- **When** Managing resources
- **Then** Could:
  - PowerUp for other accounts
  - Gift resources
  - Resource delegation
  - Account switching
  - Bulk management
  - Portfolio view

### 24. History & Reports

#### Transaction History
- **Given** Past PowerUps
- **When** Viewing history
- **Then** Could show:
  - PowerUp records
  - Cost history
  - Usage statistics
  - Expiration dates
  - Total spent
  - Export options

#### Analytics Dashboard
- **Given** Usage data
- **When** Analyzing
- **Then** Could provide:
  - Usage charts
  - Cost analysis
  - ROI calculations
  - Trend analysis
  - Comparative metrics
  - Custom reports

### 25. Market Features

#### PowerUp Marketplace
- **Given** Resource market
- **When** Enhanced
- **Then** Could offer:
  - P2P resource trading
  - Resource auctions
  - Bulk purchases
  - Forward contracts
  - Price discovery
  - Market making

#### Staking Comparison
- **Given** Resource options
- **When** Deciding
- **Then** Could show:
  - Staking vs PowerUp calculator
  - Break-even analysis
  - Recommendation engine
  - Scenario planning
  - Cost projections
  - Decision matrix

## Testing Scenarios

### 26. Happy Path - CPU PowerUp

1. Open PowerUp screen for CPU
2. View current CPU prices
3. Enter amount (e.g., 1.0000)
4. See cost calculation
5. Tap "Rent" button
6. Review fee breakdown
7. Confirm transaction
8. Enter PIN
9. See success screen
10. Continue or go home

### 27. Happy Path - NET PowerUp

1. Open PowerUp screen for NET
2. View current NET prices
3. Enter amount
4. Validate against balance
5. Proceed with rental
6. Authenticate transaction
7. Complete successfully
8. Resources updated

### 28. Error Scenarios

#### Insufficient Balance
1. Enter large amount
2. Exceed available balance
3. See error message
4. Button disabled
5. Reduce amount
6. Error clears
7. Can proceed

#### Network Failure
1. Enter valid amount
2. Submit transaction
3. Network error occurs
4. Error message shown
5. Retry option available
6. Input preserved

### 29. Edge Cases

#### Rapid Input Changes
- Quick value changes: Debounced validation
- Copy-paste values: Proper formatting
- Maximum decimals: Truncate excess
- Scientific notation: Convert to decimal
- Leading zeros: Handle correctly

#### State Transitions
- Background/foreground: Maintain state
- Rotation: Preserve input
- Process death: Restore state
- Deep linking: Handle navigation
- Multiple PowerUps: Sequential processing

## Performance Requirements

### 30. Response Times

- **Screen load**: < 300ms
- **Price update**: < 500ms
- **Input validation**: < 100ms
- **Transaction submission**: < 1 second
- **Success navigation**: < 200ms
- **Refresh completion**: < 2 seconds

### 31. Resource Usage

- **Memory footprint**: < 30MB
- **CPU usage**: < 10%
- **Network requests**: Optimized
- **Battery impact**: Minimal
- **Cache size**: < 5MB
- **Smooth animations**: 60 FPS

## Security Requirements

### 32. Transaction Security

- **PIN verification**: Required
- **Transaction signing**: Secure
- **Amount validation**: Server-side
- **Rate limiting**: Implemented
- **Replay protection**: Enabled
- **Audit logging**: Complete

### 33. Data Protection

- **Sensitive data**: Encrypted
- **PIN handling**: Secure
- **Transaction data**: Protected
- **Price manipulation**: Prevented
- **Input sanitization**: Enforced
- **Memory clearing**: After use

## Accessibility

### 34. Screen Reader Support

- **Labels**: All elements labeled
- **Announcements**: State changes
- **Navigation**: Logical order
- **Errors**: Clearly announced
- **Success**: Confirmed audibly
- **Help**: Available

### 35. Visual Accessibility

- **Text size**: Scalable
- **Contrast**: WCAG compliant
- **Colors**: Not sole indicator
- **Touch targets**: 48dp minimum
- **Focus indicators**: Visible
- **Error states**: Multi-modal

## Acceptance Criteria Summary

### Must Have (Core Functionality)
✅ CPU and NET PowerUp support
✅ Amount input with validation
✅ Price display with rates
✅ Resource usage percentage
✅ PIN authentication
✅ Fee breakdown dialog
✅ Success screen
✅ Pull to refresh
✅ Error handling
✅ Analytics tracking

### Should Have (Important Enhancements)
⭐ Price trend analysis
⭐ Resource planning tools
⭐ PowerUp packages
⭐ Usage monitoring
⭐ Expiration alerts
⭐ Transaction history
⭐ Cost optimization tips
⭐ Educational content
⭐ Bulk PowerUp
⭐ Auto-renewal options

### Nice to Have (Future Enhancements)
💡 Market predictions
💡 P2P resource trading
💡 DApp integration
💡 Resource delegation
💡 Analytics dashboard
💡 Subscription model
💡 Forward contracts
💡 Staking comparison calculator
💡 Multi-account management
💡 Resource marketplace