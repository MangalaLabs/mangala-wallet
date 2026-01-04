# RAM Detail Screen - Acceptance Criteria

## Overview
This document defines the acceptance criteria for the RAM Detail Screen in the Mangala Wallet application. RAM (Random Access Memory) is a critical resource on Antelope blockchains (EOS, WAX, Telos) that must be purchased to store account data, smart contracts, and other blockchain information. This screen provides a comprehensive interface for managing RAM as a tradeable asset, including buying, selling, transferring, tracking profit/loss, viewing transaction history, and monitoring market prices. RAM can be treated as both a utility resource and an investment opportunity.

## Critical Implementation Note

### 1. Current Implementation Status

#### Core Components Implemented
- **Given** The current implementation
- **When** Reviewing the code
- **Then** Note that:
  - RAM balance display with unit conversion
  - Profit/Loss (PnL) tracking
  - Buy/Sell/Transfer functionality
  - Gift RAM feature (development only)
  - Transaction history display
  - Real-time price monitoring
  - Dollar Cost Average (DCA) calculation
  - Pull-to-refresh functionality
  - Balance visibility toggle
  - Chart integration via bottom sheet
  - TODO: WRAM button functionality (line 225)
  - TODO: Loading state inference (lines 324, 328)

#### RAM as an Asset
- **Given** RAM on Antelope chains
- **When** Understanding its nature
- **Then** RAM is:
  - A tradeable commodity
  - Required for blockchain storage
  - Subject to market pricing
  - Scarce resource with speculation potential
  - Can appreciate/depreciate in value
  - Transferable between accounts

## RAM Overview Section

### 2. Balance Display

#### Total RAM Amount
- **Given** Account RAM holdings
- **When** Displayed at top
- **Then** Should show:
  - Total RAM amount owned
  - Selected unit format
  - Visibility toggle icon
  - Loading placeholder when fetching
  - Hidden when privacy enabled
  - Real-time updates

#### Unit Selection
- **Given** Multiple unit options
- **When** Dropdown clicked
- **Then** Should offer:
  - Bytes
  - KB (Kilobytes)
  - MB (Megabytes)
  - GB (Gigabytes)
  - Native coin value (EOS/WAX/etc)
  - Instant conversion
  - Persist selection

### 3. Profit & Loss Display

#### PnL Calculation
- **Given** RAM trading history
- **When** Calculating PnL
- **Then** Should show:
  - Total profit/loss value
  - Percentage change
  - Color coding (green profit/red loss)
  - Include fees in calculation
  - Update with transactions
  - Accurate tracking

#### PnL Components
- **Given** PnL breakdown
- **When** Displayed
- **Then** Should include:
  - Current value vs cost basis
  - Realized gains/losses
  - Unrealized gains/losses
  - Fee impact
  - Time-weighted returns
  - Historical performance

## Trading Statistics

### 4. Statistics Grid

#### Total Buy
- **Given** Purchase history
- **When** Displayed in grid
- **Then** Should show:
  - Sum of all RAM purchases PLUS total fees
  - Native token amount spent (including transaction fees)
  - Calculation: buyRamTransferActions.sum() + totalFeeBigDecimal
  - Formatted with currency symbol
  - Hidden when privacy on
  - Loading state when buyRam.isLoading() or totalFeeBigDecimal is null
  - Accurate calculation using BigDecimal precision

#### Total Sell
- **Given** Sale history
- **When** Displayed in grid
- **Then** Should show:
  - Sum of all RAM sales (sellRamTransferActions only)
  - Native tokens received from sales
  - Calculation: sellRamTransferActions.map(getDataAmountAsDouble).sum()
  - Formatted value with currency symbol
  - Privacy respect (hidden when privacy enabled)
  - Loading state when sellRamTransferActions.isLoading() and data is empty
  - Real-time update with BigDecimal precision

#### Total Fee
- **Given** Transaction fees
- **When** Calculated
- **Then** Should show:
  - Cumulative fees paid for RAM transactions only
  - Calculation: ramFee.data.map(getDataAmountAsDouble).sum()
  - RAM-specific network fees included
  - Proper formatting with currency symbol
  - Hidden when privacy enabled
  - Loading state when ramFee.isLoading() and data is empty
  - Impact on profit calculation (added to total costs)

#### Total Profit
- **Given** Trading activity
- **When** Profit calculated
- **Then** Should show:
  - Net profit calculation: (totalRamInNativeCoin + totalSell) - (totalBuy + totalFee)
  - Current RAM value plus realized sales minus total costs including fees
  - Positive/negative indicator with proper sign display
  - Color coding (green for profit, red for loss)
  - Currency format with native coin symbol
  - Privacy mode support (hidden when privacy enabled)
  - Accurate BigDecimal calculation with proper precision

### 5. Dollar Cost Average (DCA)

#### DCA Display
- **Given** Multiple purchases
- **When** DCA calculated
- **Then** Should show:
  - Average purchase price
  - Per unit cost
  - Selected unit format
  - Full width display
  - Loading state
  - Helpful for investors

#### DCA Calculation
- **Given** Purchase history
- **When** Computing DCA
- **Then** Should:
  - Only include BUY_FOR_SELF transactions with ramBytesBought > 0
  - Calculate: totalCost × 1024 ÷ totalRamBytesBought
  - Weight by quantity of RAM bytes purchased
  - Include transaction fees in totalCost calculation
  - Filter from allRamActionsSorted → ActionDataSummaryHeaderUiModel.RamBuy
  - Exclude sells and transfers (only self-purchases)
  - Update with new purchases automatically
  - Show in current unit format with "/KB" suffix
  - Return null when totalRamBytesBought is zero or no purchases exist

## Action Buttons

### 6. Buy RAM

#### Buy Button
- **Given** Buy RAM option
- **When** Button clicked
- **Then** Should:
  - Navigate to BuySellRamScreen
  - Pass account name
  - Set isBuyRam = true
  - Maintain navigation stack
  - Track analytics
  - Pre-fill account

#### Buy Flow
- **Given** Buy screen opened
- **When** Transaction complete
- **Then** Should:
  - Return to detail screen
  - Trigger data refresh
  - Update statistics
  - Show new transaction
  - Update balance
  - Recalculate PnL

### 7. Sell RAM

#### Sell Button
- **Given** Sell RAM option
- **When** Button clicked
- **Then** Should:
  - Navigate to BuySellRamScreen
  - Pass account name
  - Set isBuyRam = false
  - Check available RAM
  - Track user action
  - Validate minimum RAM

#### Sell Constraints
- **Given** RAM sale attempt
- **When** Validating
- **Then** Must:
  - Ensure minimum RAM retained
  - Check account needs
  - Prevent overselling
  - Show warnings
  - Calculate proceeds
  - Display fees

### 8. Transfer RAM

#### Transfer Button
- **Given** Transfer option
- **When** Button clicked
- **Then** Should:
  - Navigate to RamTransferScreen
  - Pass current account
  - Allow recipient selection
  - No value exchange
  - Track transfer
  - Update balances

#### Transfer Use Cases
- **Given** RAM transfer needs
- **When** Transferring
- **Then** Can:
  - Move between own accounts
  - Send to other users
  - No monetary exchange
  - Instant transfer
  - Fee-free operation
  - Maintain minimum RAM

### 9. Gift RAM

#### Gift Button (Dev Only)
- **Given** Development environment
- **When** Feature enabled
- **Then** Should:
  - Show gift button
  - Navigate to GiftRamScreen
  - Allow recipient entry
  - Purchase RAM for others
  - Track gifting
  - Social feature

#### Gift Process
- **Given** RAM gifting
- **When** Executing
- **Then** Should:
  - Buy RAM for recipient
  - Pay from sender account
  - Show in history
  - Send notification
  - Track analytics
  - Update both accounts

### 10. WRAM Integration (TODO)

#### WRAM Button
- **Given** WRAM token feature
- **When** Implemented
- **Then** Should:
  - Convert RAM to WRAM tokens
  - Enable DeFi participation
  - Show conversion rate
  - Explain benefits
  - Handle wrapping/unwrapping
  - Track WRAM balance

## Transaction History

### 11. History Display

#### Transaction List
- **Given** RAM transactions
- **When** Displayed
- **Then** Should show:
  - Chronological order
  - Buy/Sell indicators
  - Amount changed
  - Price at transaction
  - Time and date
  - Transaction hash

#### Transaction Headers
- **Given** Grouped transactions
- **When** By date
- **Then** Should:
  - Show date headers
  - Group same-day transactions
  - Sticky headers while scrolling
  - Clear separation
  - Proper formatting
  - Localized dates

### 12. Transaction Items

#### Transaction Details
- **Given** Individual transaction
- **When** Displayed
- **Then** Should show:
  - Transaction type icon
  - RAM amount (+ or -)
  - Token amount spent/received
  - Timestamp
  - Status indicator
  - Click for details

#### Loading State
- **Given** History loading
- **When** Fetching data
- **Then** Should:
  - Show skeleton items
  - Maintain layout
  - Animate placeholders
  - Load progressively
  - Handle empty state
  - Show error state

## Bottom Price Panel

### 13. Current Price Display

#### Price Information
- **Given** Current RAM market
- **When** Bottom panel shown
- **Then** Should display:
  - Current RAM price
  - Price per KB/MB
  - 24h change percentage
  - Color-coded change
  - Update real-time
  - Loading state

#### Chart Access
- **Given** Arrow button
- **When** Clicked
- **Then** Should:
  - Open ChartRamScreen
  - Show price history
  - Display in bottom sheet
  - Pass current data
  - Allow time selection
  - Interactive chart

### 14. Price Updates

#### Real-time Pricing
- **Given** Market changes
- **When** Price updates
- **Then** Should:
  - Update immediately
  - Show trend direction
  - Calculate impact
  - Update PnL
  - Notify if significant
  - Maintain accuracy

## Data Management

### 15. Pull to Refresh

#### Refresh Gesture
- **Given** Pull down gesture
- **When** Refreshing
- **Then** Should:
  - Show refresh indicator
  - Update all data
  - Fetch latest price
  - Reload transactions
  - Recalculate statistics
  - Complete < 3 seconds

#### Data Synchronization
- **Given** Multiple data sources
- **When** Refreshing
- **Then** Should:
  - Query blockchain
  - Update price feed
  - Refresh history
  - Recalculate PnL
  - Sync UI state
  - Handle errors

### 16. Navigation & State

#### Screen Navigation
- **Given** Navigation events
- **When** Returning from actions
- **Then** Should:
  - Auto-refresh if needed
  - Maintain scroll position
  - Preserve selections
  - Update changed data
  - Show success feedback
  - Handle deep links

#### State Persistence
- **Given** Screen state
- **When** Navigating
- **Then** Should:
  - Save unit selection
  - Remember visibility
  - Cache transaction list
  - Preserve scroll
  - Maintain filters
  - Quick restore

## Privacy Features

### 17. Balance Visibility

#### Hide/Show Toggle
- **Given** Privacy toggle
- **When** Clicked
- **Then** Should:
  - Toggle all values
  - Show/hide amounts
  - Replace with asterisks
  - Persist preference
  - Apply to all sections
  - Smooth transition

#### Privacy Scope
- **Given** Privacy enabled
- **When** Active
- **Then** Should hide:
  - Total RAM balance
  - PnL values
  - Statistics grid
  - Transaction amounts
  - Current price (optional)
  - Chart data

## Enhanced Features *[Enhancements]*

### 18. Advanced Analytics

#### Performance Metrics
- **Given** Trading history
- **When** Analyzing
- **Then** Could show:
  - ROI calculation
  - Sharpe ratio
  - Win/loss ratio
  - Average hold time
  - Best/worst trades
  - Performance chart

#### Market Analysis
- **Given** Price data
- **When** Enhanced view
- **Then** Could display:
  - Technical indicators
  - Support/resistance
  - Volume analysis
  - Market depth
  - Trend analysis
  - Price predictions

### 19. Trading Tools

#### Limit Orders
- **Given** Advanced trading
- **When** Implemented
- **Then** Could offer:
  - Set buy limits
  - Set sell limits
  - Order book view
  - Cancel orders
  - Order history
  - Execution alerts

#### Auto-Trading
- **Given** Automation needs
- **When** Configured
- **Then** Could:
  - DCA automation
  - Rebalancing rules
  - Stop-loss orders
  - Take-profit targets
  - Grid trading
  - Strategy backtesting

### 20. Portfolio Integration

#### Multi-Account View
- **Given** Multiple accounts
- **When** Viewing RAM
- **Then** Could show:
  - Aggregate RAM holdings
  - Account breakdown
  - Portfolio allocation
  - Cross-account transfer
  - Consolidated PnL
  - Bulk operations

#### Asset Correlation
- **Given** Portfolio context
- **When** Analyzing
- **Then** Could show:
  - RAM vs token price
  - Correlation metrics
  - Diversification score
  - Risk assessment
  - Allocation suggestions
  - Rebalance alerts

### 21. Social Features

#### RAM Leaderboard
- **Given** Community features
- **When** Enabled
- **Then** Could show:
  - Top RAM holders
  - Best traders
  - Recent large trades
  - Community sentiment
  - Follow traders
  - Copy trading

#### Sharing & Export
- **Given** Data sharing needs
- **When** Requested
- **Then** Could:
  - Export transaction CSV
  - Share PnL screenshot
  - Generate reports
  - Tax documentation
  - API access
  - Social media sharing

### 22. Educational Content

#### RAM Education
- **Given** User education
- **When** Needed
- **Then** Could provide:
  - RAM basics guide
  - Trading strategies
  - Risk warnings
  - Video tutorials
  - FAQ section
  - Best practices

#### Market Insights
- **Given** Market information
- **When** Available
- **Then** Could show:
  - News affecting RAM
  - Network upgrades
  - Supply changes
  - Demand drivers
  - Expert analysis
  - Community updates

### 23. Advanced Features

#### RAM Staking/Lending
- **Given** DeFi integration
- **When** Available
- **Then** Could:
  - Stake RAM for rewards
  - Lend RAM to others
  - Borrow against RAM
  - Yield farming
  - Liquidity provision
  - Interest tracking

#### RAM Derivatives
- **Given** Advanced instruments
- **When** Supported
- **Then** Could offer:
  - RAM futures
  - Options trading
  - Perpetual contracts
  - Synthetic RAM
  - Leveraged positions
  - Risk management

### 24. Notifications & Alerts

#### Price Alerts
- **Given** Monitoring needs
- **When** Configured
- **Then** Could notify:
  - Price thresholds
  - Large price moves
  - PnL milestones
  - Low RAM warnings
  - Market opportunities
  - Order execution

#### Activity Alerts
- **Given** Account monitoring
- **When** Events occur
- **Then** Could alert:
  - Large transactions
  - Unusual activity
  - Gift received
  - Transfer complete
  - Price targets hit
  - News updates

### 25. Integration Features

#### DApp Integration
- **Given** DApp usage
- **When** RAM needed
- **Then** Could:
  - Auto-purchase RAM
  - Estimate RAM needs
  - Optimize usage
  - Track DApp RAM
  - Cost allocation
  - Usage analytics

#### External Tools
- **Given** Third-party tools
- **When** Integrated
- **Then** Could:
  - Connect to exchanges
  - Price aggregation
  - Trading bots
  - Analytics platforms
  - Tax software
  - Portfolio trackers

## Testing Scenarios

### 26. Happy Path - Buy RAM

1. Open RAM Detail screen
2. View current holdings
3. Check current price
4. Tap Buy RAM button
5. Enter purchase amount
6. Complete transaction
7. Return to detail screen
8. See updated balance
9. View new transaction
10. Check updated PnL

### 27. Happy Path - View Analytics

1. Open RAM Detail screen
2. Toggle balance visibility
3. Switch unit to MB
4. Pull to refresh
5. View transaction history
6. Check statistics grid
7. Open price chart
8. Analyze trends
9. Close chart
10. Navigate back

### 28. Edge Cases

#### Data Loading
- Empty transaction history: Show empty state
- Network failure: Show cached data
- Price feed down: Show last known
- Partial data: Display available
- Slow loading: Progressive display

#### Calculation Edge Cases
- First purchase: No DCA yet
- All RAM sold: Zero balance
- Negative PnL: Red display
- Large numbers: Proper formatting
- Unit conversion: Accurate math

### 29. Error Scenarios

#### Transaction Failures
1. Attempt buy with insufficient funds
2. Try sell below minimum
3. Transfer to invalid account
4. Network congestion
5. Show appropriate errors
6. Allow retry
7. Maintain state

## Performance Requirements

### 30. Response Times

- **Screen load**: < 500ms
- **Price update**: < 1 second
- **Transaction list**: < 2 seconds
- **Statistics calculation**: < 500ms
- **Unit conversion**: Instant
- **Navigation**: < 300ms

### 31. Resource Usage

- **Memory footprint**: < 50MB
- **Cache size**: < 20MB
- **Network requests**: Optimized
- **CPU usage**: < 20%
- **Battery impact**: Minimal
- **Smooth scrolling**: 60 FPS

## Security Requirements

### 32. Transaction Security

- **Buy/sell validation**: Server-side
- **Price manipulation**: Prevention
- **Transaction signing**: Secure
- **Rate limiting**: Implemented
- **Audit logging**: Complete
- **Error handling**: Graceful

### 33. Data Protection

- **Balance privacy**: User controlled
- **Transaction history**: Encrypted cache
- **Price data**: Validated sources
- **User preferences**: Secure storage
- **Analytics**: Anonymous
- **Export data**: Sanitized

## Accessibility

### 34. Screen Reader Support

- **All elements**: Labeled
- **Values**: Announced clearly
- **Actions**: Described
- **Navigation**: Logical order
- **Updates**: Announced
- **Errors**: Clear feedback

### 35. Visual Accessibility

- **Text scaling**: Supported
- **Color contrast**: WCAG AA
- **Color coding**: Not sole indicator
- **Touch targets**: 48dp minimum
- **Focus indicators**: Visible
- **Animations**: Respectable

## Acceptance Criteria Summary

### Must Have (Core Functionality)
✅ RAM balance display with units
✅ PnL tracking and display
✅ Buy/Sell/Transfer buttons
✅ Transaction history
✅ Statistics grid (buy/sell/fee/profit)
✅ DCA calculation
✅ Current price display
✅ Pull to refresh
✅ Balance visibility toggle
✅ Chart integration

### Should Have (Important Enhancements)
⭐ Complete WRAM integration
⭐ Gift RAM (production ready)
⭐ Advanced analytics
⭐ Price alerts
⭐ Export functionality
⭐ Limit orders
⭐ Multi-account view
⭐ Educational content
⭐ Performance metrics
⭐ Market insights

### Nice to Have (Future Enhancements)
💡 Auto-trading strategies
💡 RAM staking/lending
💡 Social features
💡 Copy trading
💡 Derivatives trading
💡 DApp integration
💡 Tax reporting
💡 AI predictions
💡 Portfolio optimization
💡 Advanced charting tools