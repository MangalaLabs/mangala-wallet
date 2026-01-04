# Buy/Sell RAM Screen - Acceptance Criteria

## Overview
This document defines the acceptance criteria for the Buy/Sell RAM Screen in the Mangala Wallet application. This dual-purpose screen handles both buying and selling RAM on Antelope blockchains (EOS, WAX, Telos). RAM is a critical blockchain resource that can be traded as a commodity. The screen provides an intuitive interface for users to specify amounts, view current prices, execute transactions, and even purchase RAM for other accounts. The implementation adapts its UI and functionality based on whether the user is buying or selling RAM.

## Critical Implementation Note

### 1. Current Implementation Status

#### Core Components Implemented
- **Given** The current implementation
- **When** Reviewing the code
- **Then** Note that:
  - Dual-mode screen (buy/sell) via `isBuyRam` parameter
  - Dynamic input switching (RAM kb ↔ Native token)
  - Percentage-based quick selections
  - Buy for others feature (buy mode only)
  - Real-time price calculations
  - PIN authentication integration
  - Resource provider fee dialog
  - Success state handling
  - Pull-to-refresh functionality
  - QR code scanning for recipients
  - Input validation with error messages
  - Navigation result passing

#### RAM Trading Mechanism
- **Given** RAM on Antelope chains
- **When** Trading
- **Then** Operates as:
  - Market-based pricing (Bancor algorithm)
  - Instant execution
  - 0.5% trading fee
  - Price impact on large trades
  - Minimum RAM retention requirement
  - Reversible trades (buy then sell)

## Screen Modes

### 2. Buy RAM Mode

#### Buy Mode Display
- **Given** isBuyRam = true
- **When** Screen displayed
- **Then** Should show:
  - "Buy RAM" title
  - Green color scheme (mintGreen)
  - Total RAM after purchase
  - Input swap button
  - Buy for others option
  - EOS balance available
  - Buy success message

#### Buy Calculations
- **Given** Buy amount entered
- **When** Calculating
- **Then** Should:
  - Convert EOS to RAM kb
  - Show approximate (~) values
  - Include 0.5% fee
  - Update in real-time
  - Handle price slippage
  - Validate balance

### 3. Sell RAM Mode

#### Sell Mode Display
- **Given** isBuyRam = false
- **When** Screen displayed
- **Then** Should show:
  - "Sell RAM" title
  - Red color scheme
  - Available RAM to sell
  - RAM input only (no swap)
  - No buy for others
  - Expected EOS return
  - Sell success message

#### Sell Constraints
- **Given** Sell amount entered
- **When** Validating
- **Then** Should:
  - Check available RAM
  - Ensure minimum retained
  - Calculate EOS return
  - Deduct 0.5% fee
  - Prevent overselling
  - Show clear errors

## User Interface Components

### 4. Top Bar

#### Navigation Header
- **Given** Screen header
- **When** Displayed
- **Then** Should show:
  - Centered title (Buy/Sell RAM)
  - Back button
  - Safe area padding
  - Consistent styling
  - Smooth navigation
  - Track analytics

### 5. RAM Information Card

#### Information Display
- **Given** RAM stats section
- **When** Rendered
- **Then** Should show:
  - Total/Available RAM
  - RAM usage percentage
  - Visual progress bar
  - 24h price change
  - EOS balance
  - Current RAM price per kb

#### Visual Indicators
- **Given** Mode-specific styling
- **When** Displayed
- **Then** Should use:
  - Green icons for buy (mintGreen)
  - Red icons for sell
  - Percentage display
  - Loading placeholders
  - Animated updates
  - Color-coded changes

## Input Section

### 6. Amount Input

#### Input Field Configuration
- **Given** Amount input field
- **When** Active
- **Then** Should:
  - Accept numeric input
  - Support decimals
  - Show unit label (kb/EOS)
  - Clear on focus
  - Validate in real-time
  - Handle paste

#### Input Modes (Buy Only)
- **Given** Buy RAM mode
- **When** Swap button available
- **Then** Can toggle between:
  - RAM amount input (kb)
  - Native token input (EOS/WAX)
  - Instant conversion
  - Preserved precision
  - Updated labels
  - Recalculated values

### 7. Swap Button (Buy Only)

#### Button Functionality
- **Given** Buy mode active
- **When** Swap clicked
- **Then** Should:
  - Toggle input mode
  - Animate rotation
  - Update field labels
  - Recalculate values
  - Maintain amount logic
  - Show visual feedback

#### Button Styling
- **Given** Swap button
- **When** Displayed
- **Then** Should show:
  - Circular shape
  - Mode-specific color
  - White swap icon
  - 1dp border
  - Enabled/disabled states
  - Touch feedback

### 8. Percentage Suggestions

#### Quick Selection
- **Given** Suggestion buttons
- **When** Displayed
- **Then** Should offer:
  - 10%, 15%, 25%, 50%, 75%, 100% options
  - Calculate from available balance/RAM
  - One-tap selection
  - Update input immediately
  - Recalculate conversions
  - Visual feedback

#### Buy Mode - EOS Input Mode
- **Given** Buy RAM with EOS input active
- **When** Percentage chip selected
- **Then** Should:
  - Calculate percentage of EOS balance
  - Apply directly to EOS amount field
  - Round down to balance precision
  - Auto-convert to approximate RAM kb
  - Note: 0.5% fee deducted from entered EOS
  - Remainder buys maximum RAM possible

#### Buy Mode - RAM Input Mode
- **Given** Buy RAM with kb input active
- **When** Percentage chip selected
- **Then** Should:
  - Calculate max RAM buyable with percentage of balance
  - Account for 0.5% fee + 0.1% buffer (0.6% total)
  - Subtract fees BEFORE calculating RAM amount
  - Prevent transaction failure from insufficient balance
  - Round down to 3 decimal places
  - Fill RAM amount field with calculated value

#### Sell Mode Behavior
- **Given** Sell RAM mode active
- **When** Percentage chip selected
- **Then** Should:
  - Calculate percentage of available RAM
  - Apply to RAM amount field only
  - Round down to 3 decimal places
  - No input mode switching (RAM only)
  - Show expected EOS return
  - Account for 0.5% selling fee

#### Suggestion Behavior
- **Given** Percentage selected
- **When** Applied
- **Then** Should:
  - Fill input field
  - Trigger calculations
  - Update notifications
  - Clear errors
  - Enable execute button
  - Track selection
  - Highlight selected chip

## Conversion Display

### 9. Conversion Notification

#### Buy Notification
- **Given** Buy mode with amount
- **When** Displayed below input
- **Then** Should show:
  - "Buy X kb for ~Y EOS"
  - Approximate (~) for estimates
  - Exact for entered values
  - Update dynamically
  - Include fees
  - Proper formatting

#### Sell Notification
- **Given** Sell mode with amount
- **When** Displayed
- **Then** Should show:
  - "Sell X kb for ~Y EOS"
  - Expected return
  - Fee deduction noted
  - Real-time updates
  - Clear formatting
  - Accurate calculations

### 10. Error Messages

#### Input Validation Errors
- **Given** Invalid input
- **When** Detected
- **Then** Should show:
  - Specific error message
  - Red text color
  - Replace conversion text
  - Disable execute button
  - Clear on correction
  - Helpful guidance

#### Common Errors
- **Given** Error conditions
- **When** Occurring
- **Then** Should handle:
  - Insufficient balance
  - Amount too small
  - Exceeds available RAM
  - Invalid recipient
  - Network errors
  - Price fetch failures

## Buy for Others Feature

### 11. Buy for Others Toggle

#### Toggle Switch
- **Given** Buy mode only
- **When** Feature displayed
- **Then** Should show:
  - Detective icon
  - "Buy for others" label
  - Toggle switch
  - Enabled by default false
  - Smooth animation
  - State persistence

#### Toggle Behavior
- **Given** Toggle activated
- **When** Switched on
- **Then** Should:
  - Show recipient field
  - Maintain amount
  - Enable QR scanner
  - Validate recipient
  - Update button state
  - Track usage

### 12. Recipient Input

#### Account Name Field
- **Given** Buy for others active
- **When** Field shown
- **Then** Should:
  - Accept account names
  - Show placeholder text
  - Validate format
  - Support 12 characters
  - Check existence
  - Show QR button

#### QR Code Scanning
- **Given** QR button
- **When** Clicked
- **Then** Should:
  - Open QR scanner
  - Scan account names
  - Auto-fill field
  - Validate scanned data
  - Handle errors
  - Close scanner

### 13. Recipient Validation

#### Validation States
- **Given** Account name entered
- **When** Validating
- **Then** Should show:
  - Loading state
  - Valid checkmark
  - Invalid error
  - Empty state
  - Real-time feedback
  - Clear messaging

#### Invalid Account
- **Given** Invalid recipient
- **When** Detected
- **Then** Should:
  - Show red error text
  - "Invalid account name"
  - Disable execute button
  - Suggest corrections
  - Allow retry
  - Log attempts

## Transaction Flow

### 14. Execute Button

#### Button States
- **Given** Execute button
- **When** Displayed
- **Then** Should be:
  - Enabled when valid input
  - Disabled during loading
  - Show mode label (Buy/Sell RAM)
  - Full width layout
  - Gradient background
  - Loading indicator

#### Button Validation
- **Given** Execution requirements
- **When** Checking
- **Then** Must have:
  - Valid amount > 0
  - Sufficient balance
  - Valid recipient (if buying for others)
  - No pending transactions
  - Network connection
  - Price data

### 15. Resource Provider Fee

#### Fee Dialog
- **Given** Transaction initiated
- **When** Fees required
- **Then** Should show:
  - Fee breakdown dialog
  - Resource costs
  - Total amount
  - Confirm button
  - Cancel option
  - Transparent calculation

#### Fee Confirmation
- **Given** Fee dialog open
- **When** User responds
- **Then** Can:
  - Confirm and proceed
  - Cancel transaction
  - View fee details
  - Understand costs
  - Make informed decision
  - Return to edit

### 16. PIN Authentication

#### PIN Prompt
- **Given** Transaction confirmed
- **When** Authentication needed
- **Then** Should:
  - Show UnlockPinScreen
  - Verify transaction type
  - Handle success callback
  - Pop on completion
  - Maintain state
  - Secure verification

#### Authentication Flow
- **Given** PIN entered
- **When** Verified
- **Then** Should:
  - Execute transaction
  - Show loading
  - Handle errors
  - Update UI
  - Track attempts
  - Timeout handling

## Success State

### 17. Transaction Success

#### Success Screen
- **Given** Transaction complete
- **When** Success state
- **Then** Should show:
  - Success animation/icon
  - "Buy/Sell RAM Success" title
  - Transaction details
  - Continue button
  - Home button
  - No back navigation

#### Navigation Options
- **Given** Success screen
- **When** Buttons displayed
- **Then** Should offer:
  - Continue (buy/sell more)
  - Back to home
  - Result passed to parent
  - State reset on continue
  - Clean navigation
  - Analytics tracking

## Data Management

### 18. Pull to Refresh

#### Refresh Functionality
- **Given** Pull gesture
- **When** Refreshing
- **Then** Should:
  - Update RAM price
  - Refresh balances
  - Update available RAM
  - Recalculate conversions
  - Show refresh indicator
  - Complete quickly

#### Loading States
- **Given** Data loading
- **When** In progress
- **Then** Should:
  - Show circular progress
  - Center on screen
  - Disable inputs
  - Maintain layout
  - Block interactions
  - Handle timeout

### 19. State Management

#### Screen States
- **Given** State transitions
- **When** Managing
- **Then** Should handle:
  - Initial loading
  - Success state
  - Execute success
  - Error states
  - Input validation
  - Navigation results

#### State Persistence
- **Given** User input
- **When** Navigating
- **Then** Should:
  - Preserve amounts
  - Maintain mode
  - Remember recipient
  - Cache prices
  - Handle rotation
  - Restore on return

## Keyboard Management

### 20. Soft Keyboard

#### Keyboard Behavior
- **Given** Input focused
- **When** Keyboard shown
- **Then** Should:
  - Push content up appropriately
  - Maintain input visibility
  - Show numeric keyboard
  - Dismiss on tap outside
  - Handle Done action
  - Clear focus properly

## Enhanced Features *[Enhancements]*

### 21. Advanced Trading

#### Limit Orders
- **Given** Advanced trading
- **When** Implemented
- **Then** Could offer:
  - Set buy price limits
  - Set sell price targets
  - Order expiration
  - Partial fills
  - Order history
  - Cancel orders

#### Market Analysis
- **Given** Trading decisions
- **When** Needed
- **Then** Could show:
  - Price charts
  - Volume data
  - Order book depth
  - Recent trades
  - Price alerts
  - Technical indicators

### 22. Batch Operations

#### Multiple Transactions
- **Given** Bulk needs
- **When** Supported
- **Then** Could:
  - Queue multiple buys/sells
  - Batch execution
  - Cost optimization
  - Progress tracking
  - Partial completion
  - Error recovery

#### Recurring Purchases
- **Given** DCA strategy
- **When** Configured
- **Then** Could:
  - Schedule regular buys
  - Auto-execute
  - Amount/frequency settings
  - Stop conditions
  - Performance tracking
  - Notification alerts

### 23. Price Optimization

#### Slippage Protection
- **Given** Large trades
- **When** Executing
- **Then** Could:
  - Calculate price impact
  - Warn of high slippage
  - Suggest splitting orders
  - Set max slippage
  - Preview final price
  - Cancel if exceeded

#### Best Price Routing
- **Given** Multiple sources
- **When** Available
- **Then** Could:
  - Compare prices
  - Route to best rate
  - Aggregate liquidity
  - Show savings
  - Multi-path execution
  - Arbitrage detection

### 24. Social Features

#### Copy Trading
- **Given** Successful traders
- **When** Following
- **Then** Could:
  - Mirror trades
  - Set allocation limits
  - Performance tracking
  - Risk controls
  - Stop-loss rules
  - Profit sharing

#### Trade Sharing
- **Given** Successful trade
- **When** Sharing
- **Then** Could:
  - Share to social media
  - Generate trade card
  - Include profit/loss
  - Anonymous option
  - Referral tracking
  - Community feed

### 25. Risk Management

#### Stop-Loss Orders
- **Given** Risk limits
- **When** Set
- **Then** Could:
  - Auto-sell triggers
  - Loss prevention
  - Notification alerts
  - Emergency sell
  - Circuit breakers
  - Position sizing

#### Portfolio Warnings
- **Given** Concentration risk
- **When** Detected
- **Then** Could warn:
  - Over-allocation to RAM
  - Liquidity concerns
  - Market volatility
  - Suggested rebalancing
  - Risk scores
  - Diversification tips

### 26. Educational Features

#### Trading Tutorials
- **Given** New users
- **When** Learning
- **Then** Could provide:
  - RAM basics guide
  - Trading strategies
  - Video tutorials
  - Practice mode
  - FAQ section
  - Glossary

#### Market Education
- **Given** Market complexity
- **When** Explaining
- **Then** Could show:
  - Price formation
  - Bancor algorithm
  - Fee structure
  - Best practices
  - Common mistakes
  - Case studiesthe

### 27. Integration Features

#### DEX Integration
- **Given** Alternative markets
- **When** Available
- **Then** Could:
  - Compare DEX prices
  - Arbitrage opportunities
  - Liquidity aggregation
  - Cross-market orders
  - Best execution
  - Fee comparison

#### API Access
- **Given** Advanced users
- **When** Requested
- **Then** Could provide:
  - Trading API
  - Webhook notifications
  - Bot integration
  - Custom strategies
  - Data export
  - Backtesting

## Testing Scenarios

### 28. Happy Path - Buy RAM

1. Open Buy RAM screen
2. View current price and balance
3. Enter amount (kb or EOS)
4. See conversion calculation
5. Tap execute button
6. Review fees
7. Enter PIN
8. See success screen
9. Continue or go home
10. Balance updated

### 29. Happy Path - Buy for Others

1. Open Buy RAM screen
2. Toggle "Buy for others"
3. Enter recipient account
4. Validate account exists
5. Enter RAM amount
6. Execute transaction
7. Authenticate
8. Success confirmation
9. RAM sent to recipient

### 30. Edge Cases

#### Input Edge Cases
- Zero amount: Show error
- Decimal overflow: Truncate
- Negative values: Prevent
- Copy-paste large numbers: Validate
- Scientific notation: Convert

#### Transaction Edge Cases
- Network timeout: Retry mechanism
- Insufficient RAM to sell: Clear error
- Price change during transaction: Recalculate
- Account doesn't exist: Validation error
- Multiple quick taps: Prevent duplicates

## Performance Requirements

### 31. Response Times

- **Screen load**: < 300ms
- **Price updates**: < 500ms
- **Input validation**: < 100ms
- **Conversion calculation**: Instant
- **Transaction execution**: < 3 seconds
- **Success navigation**: < 200ms

### 32. Resource Usage

- **Memory footprint**: < 30MB
- **CPU usage**: < 15%
- **Network requests**: Optimized
- **Battery impact**: Minimal
- **Cache efficiency**: 5MB max
- **Smooth animations**: 60 FPS

## Security Requirements

### 33. Transaction Security

- **Amount validation**: Client and server
- **Price manipulation**: Prevention
- **Double-spend**: Protection
- **PIN verification**: Required
- **Session timeout**: 5 minutes
- **Audit logging**: Complete

### 34. Data Protection

- **Input sanitization**: All fields
- **Recipient validation**: Blockchain verified
- **Price data**: Validated sources
- **Transaction signing**: Secure
- **Memory clearing**: After use
- **No sensitive logging**: Enforced

## Accessibility

### 35. Screen Reader Support

- **All elements**: Labeled
- **Values**: Announced clearly
- **State changes**: Notified
- **Errors**: Clearly spoken
- **Success**: Confirmed
- **Navigation**: Logical

### 36. Visual Accessibility

- **Color contrast**: WCAG AA
- **Text scaling**: Supported
- **Touch targets**: 48dp minimum
- **Focus indicators**: Visible
- **Error states**: Multi-modal
- **Loading states**: Announced

## Acceptance Criteria Summary

### Must Have (Core Functionality)
✅ Buy and Sell modes
✅ Amount input with conversion
✅ Input mode switching (buy only)
✅ Percentage suggestions
✅ Buy for others feature
✅ Real-time calculations
✅ PIN authentication
✅ Fee dialog
✅ Success screen
✅ Pull to refresh

### Should Have (Important Enhancements)
⭐ Price impact warnings
⭐ Slippage protection
⭐ Transaction history
⭐ Price charts
⭐ Limit orders
⭐ Better error messages
⭐ Keyboard management
⭐ Export functionality
⭐ Educational content
⭐ Market analysis

### Nice to Have (Future Enhancements)
💡 Recurring purchases
💡 Copy trading
💡 Stop-loss orders
💡 DEX integration
💡 API access
💡 Bot support
💡 Social sharing
💡 Practice mode
💡 Multi-transaction batching
💡 Advanced charting