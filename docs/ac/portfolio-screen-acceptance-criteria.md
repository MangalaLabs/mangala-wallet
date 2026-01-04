# Portfolio Screen - Acceptance Criteria

## Overview
This document defines the acceptance criteria for the Portfolio Screen in the Mangala Wallet application. This screen serves as the main wallet details view, displaying account balances, token holdings, profit/loss information, and providing access to core wallet functions like sending, receiving, and viewing transaction history.

## Screen Purpose

### 1. Portfolio Management Hub

#### Primary Function
- **Given** User selects an account/wallet
- **When** Portfolio screen opens
- **Then** Should display:
  - Total portfolio value
  - Individual token balances
  - Price charts and trends
  - PnL information
  - Action buttons for transactions
  - Search and filter capabilities

#### Multi-Network Support
- **Given** Different blockchain networks
- **When** Viewing portfolio
- **Then** Supports:
  - EVM networks (Ethereum, BSC, etc.)
  - Antelope networks (EOS, WAX, etc.)
  - Bitcoin network
  - Network-specific features
  - Unified interface across networks

## Screen Parameters

### 2. Initialization Parameters

#### Required Parameters
- **Given** Screen initialization
- **When** Navigating to portfolio
- **Then** Must provide:
  - accountId: String (account identifier)
  - address: String (wallet address)
  - networkType: NetworkType (EVM/Antelope/Bitcoin)
  - initialAccountName: String (display name)

#### Parameter Validation
- **Given** Parameters provided
- **When** Screen loads
- **Then** Should:
  - Load correct account data
  - Display appropriate network UI
  - Show account name in header
  - Initialize with correct address
  - Handle network-specific features

## Visual Design

### 3. Screen Layout

#### Overall Structure
- **Given** Screen displays
- **When** Rendered
- **Then** Should show:
  - Status bar padding
  - Top navigation bar with account name
  - Account overview section
  - Horizontal divider
  - Sticky balances header
  - Scrollable token list
  - Safe area bottom padding
  - Pull-to-refresh indicator

#### Background Design
- **Given** Theme applied
- **When** Viewing
- **Then** Should use:
  - bgInnerCard background color
  - Consistent padding (24dp default)
  - Proper spacing between sections
  - Material theme colors
  - Responsive to theme changes

### 4. Navigation Bar

#### Top Bar Elements
- **Given** Navigation bar
- **When** Displayed
- **Then** Should contain:
  - Back arrow button (left)
  - Account name (center-left)
  - Transaction history icon (right)
  - Proper icon sizing (40dp buttons)
  - Correct color scheme

#### Navigation Actions
- **Given** User interactions
- **When** Tapping buttons
- **Then** Should:
  - Back button: Pop navigation stack
  - Transaction history: Navigate to appropriate screen
  - Disable during loading states
  - Show proper touch feedback
  - Maintain state on return

## Account Overview Section

### 5. Total Balance Display

#### Balance Header
- **Given** Account overview
- **When** Displayed
- **Then** Should show:
  - "Total Amount" label
  - Visibility toggle button
  - Balance amount or hidden placeholder
  - Currency symbol (for Antelope)
  - Proper font hierarchy

#### Balance Visibility Toggle
- **Given** Visibility button
- **When** Toggled
- **Then** Should:
  - Show/hide all balance amounts
  - Display "••••" when hidden
  - Persist preference
  - Apply to all token balances
  - Animate state change

### 6. Balance Amount Display

#### Amount Formatting
- **Given** Balance value
- **When** Visible
- **Then** Should show:
  - Large font size (36sp)
  - Semi-bold weight
  - Formatted with commas
  - Currency symbol where applicable
  - Proper decimal places

#### Loading State
- **Given** Data loading
- **When** Fetching balances
- **Then** Should show:
  - Skeleton placeholder
  - Shimmer animation
  - Maintain layout structure
  - Smooth transition when loaded
  - No layout shift

### 7. PnL (Profit & Loss) Display

#### PnL Information
- **Given** PnL data available
- **When** Displayed
- **Then** Should show:
  - "PnL" label
  - Amount with +/- indicator
  - Color coding (green/red)
  - Hidden when balance hidden
  - Currently hidden for non-Antelope

#### PnL Color Coding
- **Given** PnL value
- **When** Rendered
- **Then** Should use:
  - Green for positive values
  - Red for negative values
  - Gray for zero/neutral
  - Consistent with design system
  - Clear visual indication

### 8. Action Buttons

#### Button Layout
- **Given** Action buttons row
- **When** Displayed
- **Then** Should show:
  - Send button (left)
  - Receive button (right)
  - Equal width distribution
  - 8dp spacing between
  - Outlined button style

#### Button States
- **Given** Various states
- **When** Displayed
- **Then** Should:
  - Enable when loaded
  - Disable during loading
  - Show proper touch feedback
  - Navigate to correct screens
  - Maintain consistent height (56dp)

#### Swap Button *[Currently Disabled]*
- **Given** Swap functionality
- **When** Implemented
- **Then** Should:
  - Show for EVM networks only
  - Three-button layout
  - Navigate to swap screen
  - Currently commented out
  - Future enhancement

## Token List Section

### 9. Balances Header

#### Header Layout
- **Given** Balances section
- **When** Displayed
- **Then** Should show:
  - "Balances" title
  - Search icon button
  - Hide zero balances checkbox
  - Sticky header behavior
  - Proper padding

#### Search Functionality
- **Given** Search icon tapped
- **When** Search activated
- **Then** Should:
  - Expand search field inline
  - Show text input with hint
  - Auto-focus keyboard
  - Show clear button when text entered
  - Border highlight when active

### 10. Search Bar

#### Search Input
- **Given** Search field active
- **When** User types
- **Then** Should:
  - Filter tokens in real-time
  - Case-insensitive search
  - Search by symbol and name
  - Show placeholder text
  - Support clearing

#### Search UI States
- **Given** Search interaction
- **When** Various states
- **Then** Should show:
  - Collapsed: Just search icon
  - Expanded: Full search field
  - With text: Clear button visible
  - Animation between states
  - Proper keyboard handling

### 11. Hide Zero Balances

#### Checkbox Control
- **Given** Hide zero balances option
- **When** Displayed
- **Then** Should show:
  - Custom circle checkbox
  - Label text beside it
  - Clickable entire row
  - Check icon when selected
  - Border when unselected

#### Filter Behavior
- **Given** Option toggled
- **When** Filtering tokens
- **Then** Should:
  - Hide tokens with zero balance
  - Update list immediately
  - Persist preference
  - Work with search filter
  - Show feedback on change

## Token Display

### 12. Token List Items

#### Token Item Layout
- **Given** Token in list
- **When** Displayed
- **Then** Should show:
  - Token icon (40dp circular)
  - Symbol and name (left)
  - Price chart (center)
  - Balance and value (right)
  - Consistent padding

#### Network-Specific Display
- **Given** Different networks
- **When** Rendering tokens
- **Then** Should:
  - EVM: Standard token display
  - Antelope: Include RAM token
  - Bitcoin: Single BTC token
  - Proper icon handling
  - Network-appropriate data

### 13. Token Information

#### Left Section - Identity
- **Given** Token identity
- **When** Displayed
- **Then** Should show:
  - Circular token icon/logo
  - Token symbol (bold)
  - Token name (below symbol)
  - Proper text truncation
  - Loading placeholders

#### Center Section - Chart
- **Given** Price data available
- **When** Chart displayed
- **Then** Should show:
  - 7-day sparkline chart
  - 70dp width, 20dp height
  - Color based on trend
  - Empty space if no data
  - Loading skeleton

#### Right Section - Balances
- **Given** Token balance
- **When** Displayed
- **Then** Should show:
  - Token amount (formatted)
  - Fiat value below
  - Right-aligned text
  - Hidden when privacy on
  - Proper decimal places

### 14. Special Tokens

#### RAM Token (Antelope)
- **Given** Antelope network
- **When** Displaying tokens
- **Then** Should show:
  - RAM icon (special)
  - RAM balance in bytes
  - RAM value if available
  - Formatted appropriately
  - Listed first in order

#### Native Tokens
- **Given** Native blockchain token
- **When** Displayed
- **Then** Should:
  - Show prominently
  - Include network fees info
  - Display accurate balance
  - Update in real-time
  - Show correct decimals

## Price Charts

### 15. Sparkline Charts

#### Chart Display
- **Given** Price history available
- **When** Rendering chart
- **Then** Should show:
  - 7-day price trend
  - Smooth line graph
  - No axes or labels
  - Appropriate scaling
  - Performance optimized

#### Chart Colors
- **Given** Price change
- **When** Coloring chart
- **Then** Should use:
  - Green for positive change
  - Red for negative change
  - Gray for no change
  - 24-hour change basis
  - Clear visual indication

### 16. Chart Loading States

#### Loading Behavior
- **Given** Chart data loading
- **When** Displayed
- **Then** Should show:
  - Skeleton placeholder
  - Consistent size maintained
  - No layout shift
  - Smooth transition
  - Graceful error handling

## Pull to Refresh

### 17. Refresh Gesture

#### Pull Behavior
- **Given** User pulls down
- **When** Threshold reached
- **Then** Should:
  - Show refresh indicator
  - Trigger data reload
  - Update all sections
  - Show loading states
  - Complete with animation

#### Refresh Indicator
- **Given** Refreshing state
- **When** Active
- **Then** Should show:
  - Material refresh indicator
  - Centered horizontally
  - Above content
  - Spinning animation
  - Disappear when complete

## Navigation Actions

### 18. Send Navigation

#### Send Button Action
- **Given** Send button tapped
- **When** Processing
- **Then** Should:
  - Navigate to contact list (current)
  - Pass account context
  - Maintain back stack
  - Support network types
  - Handle loading states

#### Send Flow *[Enhancement]*
- **Given** Enhanced send flow
- **When** Implemented
- **Then** Could:
  - Show recipient type selection
  - Support QR scanning
  - Recent recipients
  - Address book integration
  - Multi-asset selection

### 19. Receive Navigation

#### Receive Button Action
- **Given** Receive button tapped
- **When** Processing
- **Then** Should:
  - Navigate to receive screen
  - Pass account ID
  - Pass network type
  - Generate QR code
  - Show address

### 20. Transaction History

#### History Navigation
- **Given** History icon tapped
- **When** Navigating
- **Then** Should:
  - Open network-specific screen
  - Antelope: TransactionHistoryAntelopeScreen
  - EVM: TransactionHistoryScreen
  - Bitcoin: TransactionHistoryBitcoinScreen
  - Pass correct parameters

## Loading States

### 21. Skeleton Loading

#### Skeleton Display
- **Given** Data loading
- **When** Screen renders
- **Then** Should show:
  - 6 skeleton token items
  - Placeholder for balance
  - Placeholder for charts
  - Shimmer animation
  - Proper sizing

#### Progressive Loading
- **Given** Partial data available
- **When** Loading
- **Then** Should:
  - Show available data immediately
  - Load remaining progressively
  - No full-screen loading
  - Smooth transitions
  - Optimistic updates

## Search and Filter

### 22. Token Search

#### Search Algorithm
- **Given** Search query entered
- **When** Filtering
- **Then** Should:
  - Search token symbols
  - Search token names
  - Case-insensitive matching
  - Real-time results
  - Clear feedback

#### No Results State *[Enhancement]*
- **Given** No matches found
- **When** Searching
- **Then** Could show:
  - "No tokens found" message
  - Clear search suggestion
  - Empty state illustration
  - Add token option
  - Help text

### 23. Combined Filters

#### Filter Interaction
- **Given** Multiple filters
- **When** Applied together
- **Then** Should:
  - Apply search first
  - Then hide zero balances
  - Maintain performance
  - Show accurate count
  - Clear indication of filters

## Enhanced Features *[Enhancements]*

### 24. Portfolio Analytics

#### Advanced Metrics
- **Given** Analytics feature
- **When** Implemented
- **Then** Could show:
  - 24h/7d/30d changes
  - Portfolio composition chart
  - Historical performance
  - Best/worst performers
  - Volume metrics

#### Portfolio Insights
- **Given** Data analysis
- **When** Available
- **Then** Could provide:
  - Diversification score
  - Risk assessment
  - Trend predictions
  - Rebalancing suggestions
  - Performance benchmarks

### 25. Token Management

#### Add Custom Tokens
- **Given** Token not listed
- **When** User wants to add
- **Then** Could:
  - Add token by contract
  - Verify token details
  - Show security warnings
  - Save to preferences
  - Sync across devices

#### Token Hiding
- **Given** Unwanted tokens
- **When** Managing list
- **Then** Could:
  - Hide specific tokens
  - Manage hidden list
  - Unhide when needed
  - Bulk selection
  - Quick actions menu

### 26. Quick Actions

#### Swipe Actions
- **Given** Token item
- **When** Swiping
- **Then** Could reveal:
  - Send button
  - Receive button
  - Hide token
  - View details
  - Add to favorites

#### Long Press Menu
- **Given** Token selected
- **When** Long pressing
- **Then** Could show:
  - Copy address
  - View on explorer
  - Price alerts
  - Transaction history
  - Token information

### 27. Price Alerts

#### Alert Configuration
- **Given** Price monitoring
- **When** Setting alerts
- **Then** Could:
  - Set price thresholds
  - Percentage changes
  - Volume alerts
  - Multiple conditions
  - Notification preferences

### 28. Export Features

#### Data Export
- **Given** Portfolio data
- **When** Exporting
- **Then** Could:
  - Export to CSV
  - Generate PDF report
  - Tax documentation
  - Transaction history
  - Performance metrics

## Multi-Network Features

### 29. Network Switching

#### Network Selector *[Enhancement]*
- **Given** Multiple networks
- **When** Switching
- **Then** Could:
  - Show network dropdown
  - Quick switch gesture
  - Remember last selected
  - Show network status
  - Gas/fee indicators

### 30. Cross-Chain View *[Enhancement]*

#### Unified Portfolio
- **Given** Multiple chains
- **When** Viewing all
- **Then** Could show:
  - Combined total value
  - All tokens across chains
  - Grouped by network
  - Aggregate PnL
  - Portfolio distribution

## Performance Requirements

### 31. Loading Performance

- **Initial load**: < 500ms
- **Pull refresh**: < 2 seconds
- **Search response**: < 100ms
- **Chart rendering**: < 200ms
- **Scroll performance**: 60 FPS

### 32. Data Updates

- **Price updates**: Every 30 seconds
- **Balance refresh**: On focus
- **Chart data**: Cached 5 minutes
- **Search debounce**: 300ms
- **State persistence**: Immediate

## Accessibility

### 33. Screen Reader Support

#### Content Announcements
- **Given** Screen reader active
- **When** Navigating
- **Then** Should announce:
  - Account name and network
  - Total balance (if visible)
  - Token details
  - Button purposes
  - Filter states

### 34. Visual Accessibility

#### High Contrast Mode
- **Given** High contrast enabled
- **When** Viewing
- **Then** Should provide:
  - Clear boundaries
  - Enhanced text contrast
  - Visible focus indicators
  - Chart alternatives
  - Clear button states

## Testing Scenarios

### 35. Happy Path - View Portfolio

1. Navigate to portfolio screen
2. See account name in header
3. View total balance
4. Scroll through token list
5. See price charts
6. Tap transaction history
7. View transaction list

### 36. Happy Path - Search and Filter

1. Open portfolio screen
2. Tap search icon
3. Type token name
4. See filtered results
5. Check hide zero balances
6. See further filtering
7. Clear search
8. Uncheck filter

### 37. Edge Cases

#### Data Edge Cases
- Empty portfolio: Show empty state
- Single token: Proper display
- Many tokens (100+): Performance maintained
- No price data: Graceful degradation
- Network error: Cached data shown

#### Interaction Edge Cases
- Quick navigation: State preserved
- Rapid filtering: Debounced properly
- Pull refresh spam: Rate limited
- Background/foreground: Data refreshed
- Screen rotation: State maintained

## Security Requirements

### 38. Data Protection

- **Balance hiding**: Privacy feature
- **No screenshots**: Sensitive data (optional)
- **Secure storage**: Encrypted cache
- **API security**: Authenticated requests
- **Session management**: Timeout handling

## Analytics

### 39. Event Tracking

#### Screen Events
- **Given** Analytics enabled
- **When** User interacts
- **Then** Should track:
  - Screen view: PORTFOLIO
  - Balance visibility toggle
  - Search usage
  - Filter usage
  - Navigation actions

## Acceptance Criteria Summary

### Must Have (Current Implementation)
✅ Multi-network support (EVM, Antelope, Bitcoin)
✅ Total balance display with visibility toggle
✅ Token list with icons, charts, balances
✅ Search functionality
✅ Hide zero balances filter
✅ Send and Receive buttons
✅ Transaction history navigation
✅ Pull to refresh
✅ Loading skeletons
✅ PnL display (Antelope only currently)

### Should Have (Reasonable Enhancements)
⭐ Swap button activation
⭐ No results state for search
⭐ Token details on tap
⭐ Add custom tokens
⭐ Hide specific tokens
⭐ Price alerts
⭐ Portfolio analytics
⭐ Better loading states
⭐ Network indicators
⭐ Improved accessibility

### Nice to Have (Future Enhancements)
💡 Cross-chain portfolio view
💡 Advanced analytics dashboard
💡 Export functionality
💡 Swipe actions on tokens
💡 Token favorites
💡 Historical charts
💡 DeFi protocol integration
💡 Yield tracking
💡 Tax reporting
💡 AI-powered insights