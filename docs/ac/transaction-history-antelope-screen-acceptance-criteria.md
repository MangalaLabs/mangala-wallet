# Transaction History Antelope Screen - Acceptance Criteria

## Overview
This document defines the acceptance criteria for the Transaction History Antelope Screen in the Mangala Wallet application. This screen displays a paginated list of blockchain transactions for Antelope-based accounts (EOS, WAX, Telos), with date grouping, pull-to-refresh functionality, and filtering capabilities. The screen provides users with a comprehensive view of their transaction history with intuitive navigation and organization.

## Screen Purpose

### 1. Transaction History Display

#### Primary Function
- **Given** User has Antelope account
- **When** Viewing transaction history
- **Then** Should provide:
  - Paginated transaction list
  - Date-based grouping
  - Transaction details
  - Real-time updates
  - Search and filter options
  - Export capabilities

#### Transaction Types
- **Given** Various blockchain actions
- **When** Displayed
- **Then** Should show:
  - Transfers (sent/received)
  - Resource transactions (RAM/CPU/NET)
  - Token swaps
  - Contract interactions
  - Staking/unstaking
  - All blockchain actions

## Screen Parameters

### 2. Initialization Parameters

#### Required Parameters
- **Given** Screen initialization
- **When** Opening history
- **Then** Must provide:
  - accountName: String (Antelope account)

#### Parameter Usage
- **Given** Account name provided
- **When** Screen loads
- **Then** Should:
  - Load transaction history
  - Apply correct filters
  - Set up pagination
  - Initialize UI state
  - Track analytics event

## Visual Design

### 3. Screen Layout

#### Overall Structure
- **Given** Screen displays
- **When** Rendered
- **Then** Should show:
  - Theme background color
  - Safe area padding
  - Centered title bar
  - Transaction list
  - Pull-to-refresh indicator
  - Empty state when needed

#### List Organization
- **Given** Transaction list
- **When** Displayed
- **Then** Should have:
  - Date group headers
  - Transaction items
  - 1dp spacing between items
  - Rounded corners for groups
  - Default padding (16dp)
  - Bottom padding (large)

## Navigation Bar

### 4. Top Bar

#### Bar Elements
- **Given** Navigation bar
- **When** Displayed
- **Then** Should show:
  - Back button (left)
  - "Transaction" title (centered)
  - Filter button (right - commented)
  - Standard height
  - Theme styling
  - Consistent spacing

#### Filter Button (Currently Disabled)
- **Given** Filter option
- **When** Implemented
- **Then** Should show:
  - Filter icon
  - Click handler
  - Visual feedback
  - Badge for active filters
  - Accessibility label
  - Disabled state handling

## Transaction List

### 5. Pagination

#### Loading Strategy
- **Given** Large transaction history
- **When** Loading
- **Then** Should:
  - Load 60 items per page
  - Sort descending (newest first)
  - Load more on scroll
  - Show loading indicator
  - Handle errors gracefully
  - Cache loaded pages

#### Pagination States
- **Given** Various load states
- **When** Managing data
- **Then** Should handle:
  - Initial loading
  - Append loading
  - Refresh loading
  - Error states
  - Empty states
  - End of data

### 6. Date Grouping and Separators

#### insertSeparators Logic
- **Given** Transaction stream
- **When** Processing pagination
- **Then** Should:
  - Map raw transactions to TransactionItem types
  - Apply insertSeparators between items
  - Compare blockTime dates
  - Insert HeaderItem separators
  - Maintain chronological order
  - Handle null blockTime gracefully

#### Header Insertion Rules
- **Given** Transaction sequence
- **When** Evaluating separators
- **Then** Should apply:
  - **First Item**: If beforeItem is null and afterItem exists, insert header from afterItem's blockTime
  - **Date Change**: If beforeItem and afterItem have different dates, insert afterItem's header
  - **Same Date**: No separator needed, return null
  - **Missing Data**: Handle null blockTime safely
  - **Empty Lists**: No separators for empty data

#### Header Item Generation
- **Given** BlockTime data
- **When** Creating headers
- **Then** Should:
  - Extract first actionTrace blockTime
  - Call getHeaderItem() extension
  - Generate TransactionHistoryItemAntelope.HeaderItem
  - Apply date formatting rules
  - Handle timezone conversion
  - Support localization

#### Group Headers Display
- **Given** Generated headers
- **When** Rendered
- **Then** Should show:
  - Date header items
  - Smart date formatting
  - Today/Yesterday labels
  - Full date for older items
  - Consistent styling
  - Proper spacing

#### Separator Edge Cases
- **Given** Various data states
- **When** Processing
- **Then** Should handle:
  - Single transaction: No separators
  - Same-day transactions: Single header
  - Cross-midnight transactions: New headers
  - Missing timestamps: Skip separator
  - Duplicate headers: Prevent duplicates
  - Empty pages: No header insertion

### 7. Transaction Items

#### Item Display
- **Given** Transaction data
- **When** Rendered
- **Then** Should show:
  - Transaction summary
  - Action type
  - Amount/value
  - Timestamp
  - Status indicator
  - Direction (in/out)

#### Item Styling
- **Given** List position
- **When** Styled
- **Then** Should apply:
  - Top corners for first item
  - Bottom corners for last item
  - No corners for middle items
  - Full corners for single item
  - Consistent background
  - Touch feedback

### 8. Corner Radius Logic

#### Rounded Corners
- **Given** Item position in group
- **When** Determining shape
- **Then** Should apply:
  - Full rounded (single item or isolated)
  - Top rounded (first in group)
  - Bottom rounded (last in group)
  - No rounding (middle items)
  - Small corner radius (8dp)
  - Smooth transitions

## Pull to Refresh

### 9. Refresh Functionality

#### Refresh Gesture
- **Given** Pull down gesture
- **When** Triggered
- **Then** Should:
  - Show refresh indicator
  - Clear existing data
  - Reload from beginning
  - Maintain scroll position
  - Update all groups
  - Complete animation

#### Refresh States
- **Given** Refreshing data
- **When** In progress
- **Then** Should:
  - Show pull indicator
  - Disable during refresh
  - Update isRefreshing state
  - Handle errors
  - Restore on completion
  - Smooth animation

## Empty State

### 10. No Transactions

#### Empty Display
- **Given** No transaction history
- **When** List empty
- **Then** Should show:
  - Empty state illustration
  - Size: 213dp x 178dp
  - "No transactions" message
  - Centered alignment
  - Scrollable container
  - Proper spacing

#### Empty State Conditions
- **Given** Data states
- **When** Checking
- **Then** Show empty when:
  - Not loading (refresh)
  - Not loading (append)
  - Item count is zero
  - After filter applied
  - Initial load complete
  - No error state

## Date Filtering (Currently Commented)

### 11. Filter Bottom Sheet

#### Filter Options
- **Given** Filter functionality
- **When** Implemented
- **Then** Should offer:
  - Start date selection
  - End date selection
  - Date range presets
  - Clear filters option
  - Apply button
  - Cancel option

#### Filter Application
- **Given** Filters selected
- **When** Applied
- **Then** Should:
  - Update date range
  - Refresh transaction list
  - Show filtered results
  - Display active filters
  - Allow filter clearing
  - Persist selection

### 12. Date Filter Logic

#### Date Selection
- **Given** Date filters
- **When** Setting
- **Then** Should:
  - Convert to ISO format using instantToIsoString()
  - Truncate to milliseconds precision
  - Format as "{date}T00:00:00.000Z"
  - Use UTC timezone for consistency
  - Handle timezones properly
  - Validate date range (Start <= End)

#### ISO String Conversion
- **Given** Instant timestamp
- **When** Converting for API
- **Then** Should:
  - Truncate to epoch milliseconds
  - Convert back to Instant
  - Transform to LocalDateTime (UTC)
  - Format as ISO string with time reset to midnight
  - Append ".000Z" for UTC indication
  - Handle null values gracefully

#### Filter State Management
- **Given** Date filter changes
- **When** Updated
- **Then** Should:
  - Update _startDateFilter StateFlow
  - Update _endDateFilter StateFlow
  - Trigger refreshListActions() via LaunchedEffect
  - Pass filter to getActionsPaginated (currently null)
  - Reset pagination to first page
  - Maintain filter state across screen lifecycle

## Transaction Data Processing

### 13. Data Transformation Pipeline

#### Raw Transaction Processing
- **Given** Blockchain transaction data
- **When** Loading from API
- **Then** Should:
  - Fetch via GetActionsUseCase
  - Apply pagination (60 items per page)
  - Sort descending by timestamp
  - Map to internal data models
  - Extract action traces
  - Handle blockchain-specific formats

#### UI Model Transformation
- **Given** Raw transaction data
- **When** Converting for UI
- **Then** Should:
  - Call toListActionDataUiModel(accountName)
  - Create ListActionDataUiModel instances
  - Wrap in TransactionHistoryItemAntelope.TransactionItem
  - Apply account-specific filtering
  - Calculate display values
  - Format amounts and timestamps

#### Account Context Processing
- **Given** Transaction with account context
- **When** Transforming
- **Then** Should:
  - Determine transaction direction (sent/received)
  - Calculate net amount change
  - Identify account role in transaction
  - Extract relevant action data
  - Apply account-specific styling
  - Set appropriate icons and colors

### 14. Blockchain Network Handling

#### Network Selection
- **Given** Multiple blockchain networks
- **When** Loading transactions
- **Then** Should:
  - Get selected network via GetSelectedNetworkUseCase
  - Extract blockchainType
  - Use flatMapLatest for network changes
  - Reload data when network switches
  - Handle network-specific formatting
  - Support EOS/WAX/Telos differences

#### Network State Flow
- **Given** Network changes
- **When** User switches chains
- **Then** Should:
  - Emit new blockchainType
  - Trigger data reload
  - Clear existing pagination
  - Reset to first page
  - Update UI immediately
  - Handle network errors

### 15. Action Trace Processing

#### Block Time Extraction
- **Given** Transaction action traces
- **When** Processing for grouping
- **Then** Should:
  - Extract first().blockTime from actionTraces
  - Default to "00:00" if null
  - Use for date comparison
  - Apply to header generation
  - Support timezone conversion
  - Maintain chronological accuracy

#### Multi-Action Transactions
- **Given** Transactions with multiple actions
- **When** Processing
- **Then** Should:
  - Process all action traces
  - Use first action for timing
  - Summarize all actions
  - Show aggregate effects
  - Handle complex transactions
  - Maintain readability

### 16. Action Grouping Logic

#### Composite Action Groups
- **Given** Transaction action traces
- **When** Grouping by type
- **Then** Should categorize into:
  - BUY_RAM: RAM purchase transactions
  - SELL_RAM: RAM sale transactions
  - RAM_TRANSFER: RAM transfers between accounts
  - TOKEN_TRANSFER: Token movements
  - RESOURCE_PROVIDER_FEE: Greymass Fuel fees
  - CREATE_ACCOUNT: New account creation
  - LINK_AUTH/UPDATE_AUTH: Permission updates
  - RENT_CPU/RENT_NET: Resource rentals
  - POWERUP: PowerUp transactions
  - DELEGATE_BANDWIDTH: Resource delegation
  - MSIG_* : Multi-signature operations
  - CONTRACT_CALL: Unknown/generic actions

#### Action Mapping Strategy
- **Given** Action traces to group
- **When** Processing with getGroupedActionTraces()
- **Then** Should:
  - Sort by creatorActionOrdinal
  - Map action ID to CompositeActionGroup
  - Handle single-group actions directly
  - Process multi-group actions with context
  - Default unknown actions to CONTRACT_CALL
  - Maintain grouping context between actions

#### Context-Aware Grouping
- **Given** Actions with multiple possible groups
- **When** Determining correct group
- **Then** Should apply context logic:
  - LOG_RAM_CHANGE: Check account context for buy/sell/transfer
  - LOG_BUY_RAM: Could be regular buy or resource provider fee
  - BUY_RAM_BYTES: Check if for self or others
  - TOKEN_TRANSFER: Consider transaction context
  - Use previous action context for disambiguation
  - Update context for subsequent actions

### 17. Summary Header Generation

#### Summary Header Types
- **Given** Grouped action traces
- **When** Creating summary headers
- **Then** Should generate:
  - **RamBuy**: Shows bytes bought, fee, total cost, price/KB, new balance
  - **RamSell**: Shows bytes sold, proceeds, fee, price/KB, new balance
  - **RamTransfer**: Shows sender, recipient, amount transferred
  - **TokenTransfer**: Shows direction, amount, token, from/to
  - **ResourceProviderFee**: Shows fee amount and provider
  - **CreateAccount**: Shows new account name, resources allocated
  - **ContractCall**: Generic contract interaction details

#### RamBuy Header Details
- **Given** RAM purchase actions
- **When** Creating RamBuy header
- **Then** Should calculate:
  - ramBytesBought: Total bytes purchased
  - ramFee: 0.5% transaction fee
  - totalCost: Full EOS/token amount spent
  - pricePerKbFormatted: "X.XXX EOS/KB" format
  - newRamBalance: Updated RAM after purchase
  - buyRamType: BUY_FOR_SELF, BUY_FOR_OTHERS, or BOUGHT_BY_OTHERS
  - payerAccount: Who paid for RAM
  - recipientAccount: Who received RAM
  - currentAccountName: Context account

#### Transaction Summary Display
- **Given** ListActionDataUiModel
- **When** Displayed in list
- **Then** Should show:
  - Multiple summary headers per transaction
  - Formatted transaction ID (first 8...last 8 chars)
  - Block time for sorting/grouping
  - Account-specific context (sent/received)
  - Net effect on account resources
  - Visual indicators for transaction type

#### Summary Calculation Logic
- **Given** Action traces for account
- **When** Calculating summaries
- **Then** Should:
  - Call toActionDataSummaryHeaderUiModels(accountName)
  - Process each CompositeActionGroup
  - Extract relevant data from action traces
  - Calculate net effects for account
  - Format amounts and balances
  - Handle edge cases (null values, missing data)

## State Management

### 13. Screen States

#### Loading States
- **Given** Various operations
- **When** Processing
- **Then** Should handle:
  - Initial load
  - Pagination load
  - Refresh load
  - Filter application
  - Error recovery
  - State transitions

#### Data Flow
- **Given** State updates
- **When** Managing
- **Then** Should:
  - Use StateFlow for filters
  - Flow for pagination
  - Compose state for UI
  - Handle recomposition
  - Prevent memory leaks
  - Clean up properly

## Performance

### 14. List Performance

#### Rendering Optimization
- **Given** Large lists
- **When** Displaying
- **Then** Should:
  - Use LazyColumn
  - Stable keys (trxId)
  - Efficient recomposition
  - Item recycling
  - Smooth scrolling
  - 60 FPS target

#### Memory Management
- **Given** Pagination data
- **When** Managing
- **Then** Should:
  - Cache reasonable pages
  - Release old pages
  - Prevent memory leaks
  - Efficient data structures
  - Lazy loading images
  - Resource cleanup

## Enhanced Features *[Enhancements]*

### 15. Search Functionality

#### Search Bar
- **Given** Search needs
- **When** Implemented
- **Then** Could offer:
  - Text search input
  - Search by amount
  - Search by date
  - Search by type
  - Search by account
  - Real-time results

#### Search Filters
- **Given** Advanced search
- **When** Available
- **Then** Could include:
  - Transaction type filter
  - Amount range filter
  - Direction filter (in/out)
  - Status filter
  - Token filter
  - Contract filter

### 16. Transaction Details

#### Detail View
- **Given** Transaction item
- **When** Tapped
- **Then** Could show:
  - Full transaction details
  - Block information
  - Transaction hash
  - All action traces
  - Resource usage
  - Raw data view

#### Detail Actions
- **Given** Detail screen
- **When** Viewing
- **Then** Could offer:
  - Copy transaction ID
  - View on explorer
  - Share transaction
  - Download receipt
  - Report issue
  - Add notes

### 17. Export Functionality

#### Export Options
- **Given** Transaction data
- **When** Exporting
- **Then** Could support:
  - CSV export
  - PDF report
  - JSON format
  - Date range selection
  - Filter application
  - Email/share options

#### Export Formats
- **Given** Different needs
- **When** Formatting
- **Then** Could include:
  - Tax report format
  - Accounting format
  - Simple list
  - Detailed report
  - Custom templates
  - Scheduled exports

### 18. Analytics and Insights

#### Transaction Analytics
- **Given** Historical data
- **When** Analyzed
- **Then** Could show:
  - Spending patterns
  - Transaction frequency
  - Average amounts
  - Peak activity times
  - Category breakdown
  - Trend analysis

#### Visual Charts
- **Given** Data visualization
- **When** Displayed
- **Then** Could include:
  - Line charts
  - Bar graphs
  - Pie charts
  - Heat maps
  - Activity calendar
  - Comparison views

### 19. Notification Integration

#### Transaction Alerts
- **Given** New transactions
- **When** Detected
- **Then** Could:
  - Send push notifications
  - Show in-app alerts
  - Email summaries
  - Custom thresholds
  - Watchlist alerts
  - Suspicious activity

### 20. Filtering Enhancements

#### Advanced Filters
- **Given** Complex filtering
- **When** Needed
- **Then** Could offer:
  - Multiple filter combination
  - Saved filter presets
  - Quick filters
  - Smart suggestions
  - Filter history
  - Bulk selection

#### Filter Presets
- **Given** Common filters
- **When** Available
- **Then** Could include:
  - Last 7 days
  - Last 30 days
  - This month
  - This year
  - Large transactions
  - Failed transactions

### 21. Batch Operations

#### Multi-selection
- **Given** Multiple items
- **When** Selected
- **Then** Could:
  - Select multiple transactions
  - Batch export
  - Bulk categorization
  - Mass deletion (cache)
  - Group operations
  - Select all/none

### 22. Category Management

#### Transaction Categories
- **Given** Organization needs
- **When** Categorizing
- **Then** Could:
  - Auto-categorization
  - Custom categories
  - Category colors
  - Category icons
  - Rules engine
  - ML suggestions

### 23. Offline Support

#### Cached Data
- **Given** No network
- **When** Offline
- **Then** Could:
  - Show cached transactions
  - Queue refresh requests
  - Sync when online
  - Offline indicators
  - Limited functionality
  - Data persistence

### 24. Integration Features

#### External Services
- **Given** Third-party needs
- **When** Integrated
- **Then** Could connect:
  - Block explorers
  - Tax services
  - Accounting software
  - Analytics platforms
  - Portfolio trackers
  - DeFi protocols

### 25. Accessibility Enhancements

#### Voice Features
- **Given** Accessibility needs
- **When** Implemented
- **Then** Could offer:
  - Voice search
  - Transaction reading
  - Voice commands
  - Audio summaries
  - Screen reader optimization
  - High contrast mode

## Testing Scenarios

### 26. Happy Path - View History

1. Open transaction history
2. View loading state
3. Transactions load successfully
4. See date headers
5. Scroll through list
6. Load more on scroll
7. Pull to refresh
8. Updated data shown
9. Navigate back
10. State preserved

### 27. Empty State Path

1. Open for new account
2. Loading completes
3. No transactions found
4. Empty state displayed
5. See illustration
6. Read message
7. Pull to refresh
8. Still empty
9. Navigate back

### 28. Pagination Path

1. Open with many transactions
2. First page loads
3. Scroll to bottom
4. Loading indicator shown
5. Next page appends
6. Continue scrolling
7. More pages load
8. Reach end of data
9. No more loading

### 29. Edge Cases

#### Data Edge Cases
- Empty account: Show empty state
- Single transaction: Show with full corners
- Network error: Show error state
- Partial load: Show available data
- Duplicate data: Handle deduplication

#### UI Edge Cases
- Rapid scrolling: Smooth performance
- Quick refresh: Debounce properly
- Background/foreground: Maintain state
- Screen rotation: Preserve position
- Memory pressure: Handle gracefully

## Security Requirements

### 30. Data Security

- **Transaction privacy**: No logging sensitive data
- **Secure caching**: Encrypted storage
- **API security**: Authenticated requests
- **Data validation**: Verify integrity
- **Session management**: Timeout handling
- **Access control**: Account verification

## Analytics

### 31. Event Tracking

#### Usage Analytics
- **Given** Analytics enabled
- **When** User interacts
- **Then** Should track:
  - Screen view: TRANSACTION_HISTORY
  - Pagination events
  - Refresh actions
  - Filter usage
  - Error occurrences
  - Performance metrics

## Acceptance Criteria Summary

### Must Have (Current Implementation)
✅ Paginated transaction list (60 per page)
✅ Date grouping with headers
✅ Pull-to-refresh functionality
✅ Empty state display
✅ Rounded corners for grouped items
✅ Back navigation
✅ Descending sort (newest first)
✅ Loading states
✅ LazyColumn performance
✅ Transaction ID as stable key

### Should Have (Reasonable Enhancements)
⭐ Working filter functionality
⭐ Date range selection
⭐ Search capability
⭐ Transaction details view
⭐ Export to CSV
⭐ Category filters
⭐ Loading skeletons
⭐ Error recovery
⭐ Sticky date headers
⭐ Share transaction

### Nice to Have (Future Enhancements)
💡 Advanced analytics
💡 Voice search
💡 Batch operations
💡 Auto-categorization
💡 Offline support
💡 Push notifications
💡 Tax reporting
💡 Custom categories
💡 ML insights
💡 Integration APIs