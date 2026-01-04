# Wallet Main Screen (Pro) - Acceptance Criteria

## Overview
This document defines the acceptance criteria for the Pro Wallet Main Screen in the Mangala Wallet application. The Pro variant is the full-featured wallet combining both hot and cold wallet capabilities, supporting online transactions, balance queries, multi-network operations, and comprehensive portfolio management. This screen serves as the primary interface for all wallet operations including sending, receiving, portfolio tracking, QR code scanning, and cross-chain asset management.

## Critical Implementation Note

### 1. Current Implementation Status

#### Core Components Implemented
- **Given** The current implementation
- **When** Reviewing the code
- **Then** Note that:
  - Full multi-network support (EVM, Antelope, Bitcoin)
  - QR code scanning with haptic feedback
  - Portfolio and asset management
  - Balance visibility toggle
  - Pull-to-refresh functionality
  - Analytics tracking integration
  - Bottom navigation control
  - ESR (EOSIO Signing Request) support
  - Account import via QR
  - TODO: Login QR handling (line 108)
  - TODO: WalletConnect implementation (line 163)
  - TODO: Invalid QR handling (line 167)
  - TODO: Transaction signing QRs (lines 171-180)
  - TODO: Buy functionality (line 390)
  - TODO: EVM asset click handling (line 550)
  - TODO: Antelope token handling (line 410)

#### Network Capabilities
- **Given** Pro wallet features
- **When** Operating
- **Then** Supports:
  - Full network connectivity
  - Real-time balance updates
  - Transaction broadcasting
  - Price data fetching
  - Portfolio tracking
  - Cross-chain operations

## Multi-Network Support

### 2. Network Types

#### EVM Networks
- **Given** EVM network selected
- **When** Displaying accounts
- **Then** Should:
  - Show Ethereum-compatible accounts
  - Display ERC-20 tokens
  - Show gas prices in Gwei
  - Support EIP-1559 transactions
  - Display USD values
  - Show PnL calculations

#### Antelope Networks
- **Given** Antelope network selected
- **When** Displaying accounts
- **Then** Should:
  - Show EOS/WAX/Telos accounts
  - Display RAM usage
  - Show CPU/NET resources
  - Support PowerUp feature
  - Handle account names
  - Show resource staking

#### Bitcoin Network
- **Given** Bitcoin network selected
- **When** Displaying accounts
- **Then** Should:
  - Show BTC balance
  - Display UTXO count
  - Show transaction fees
  - Support SegWit addresses
  - Display satoshi values
  - Show mempool status

### 3. Network Switching

#### Network Selection
- **Given** Multiple networks available
- **When** User switches network
- **Then** Should:
  - Update account display
  - Refresh appropriate data
  - Change asset list
  - Update QR code format
  - Maintain UI state
  - Show network indicator

## Account Display

### 4. Account Cards

#### Balance Display
- **Given** Account card shown
- **When** Viewing account
- **Then** Should display:
  - Total balance in fiat
  - Percentage change (PnL)
  - Account name/address
  - Network badge
  - Visibility toggle
  - Loading placeholders

#### Balance Visibility Toggle
- **Given** Privacy feature
- **When** Toggle clicked
- **Then** Should:
  - Hide/show balance
  - Replace with asterisks
  - Maintain toggle state
  - Apply to all values
  - Show in all cards
  - Persist preference

#### PnL Display
- **Given** Portfolio tracking
- **When** Balance changes
- **Then** Should show:
  - Percentage change
  - Color coding (red/green)
  - Time period (24h)
  - Formatted values
  - Update on refresh
  - Handle missing data

### 5. Account Actions

#### Portfolio Button
- **Given** Portfolio feature
- **When** Button tapped
- **Then** Should:
  - Navigate to PortfolioScreen
  - Pass account details
  - Show token breakdown
  - Display charts
  - Track navigation
  - Support all networks

#### Send Button
- **Given** Send functionality
- **When** Button tapped
- **Then** Should:
  - Navigate to ContactListScreen
  - Pre-select account
  - Support all networks
  - Handle empty balance
  - Show gas estimates
  - Track analytics

#### Receive Button
- **Given** Receive functionality
- **When** Button tapped
- **Then** Should:
  - Open ReceiveTokenScreen
  - Generate QR code
  - Show correct address
  - Support amount requests
  - Allow sharing
  - Network-specific format

#### Buy Button (TODO)
- **Given** Buy crypto feature
- **When** Implemented
- **Then** Should:
  - Open purchase flow
  - Show payment methods
  - Display exchange rates
  - Support multiple providers
  - Handle KYC requirements
  - Track conversions

### 6. Add Account Card

#### Create Account
- **Given** Add new account
- **When** Button tapped
- **Then** Should:
  - Navigate by network type
  - EVM → EvmCreateAccountScreen
  - Antelope → AntelopeCreateAccountV2Screen
  - Bitcoin → BitcoinCreateAccountScreen
  - Generate keys
  - Save securely

#### Import Account
- **Given** Import option
- **When** Selected
- **Then** Should:
  - Navigate by network type
  - Support private keys
  - Support seed phrases
  - Validate format
  - Check duplicates
  - Show success

## QR Code Handling

### 7. QR Scanner Integration

#### Scanner Activation
- **Given** QR scanner available
- **When** Scan button tapped
- **Then** Should:
  - Check platform support
  - Request permissions
  - Open native scanner
  - Show current network
  - Track analytics event
  - Provide haptic feedback

#### QR Type Detection
- **Given** QR code scanned
- **When** Processing result
- **Then** Should detect:
  - Payment addresses
  - Payment requests with amount
  - ESR (EOSIO Signing Requests)
  - Account import keys
  - Anchor KeyCert
  - Create account requests
  - WalletConnect URIs
  - Transaction signing requests

### 8. Payment QR Processing

#### Simple Payment
- **Given** Address-only QR
- **When** Scanned
- **Then** Should:
  - Navigate to Step2SelectNetwork
  - Pre-fill address
  - Allow network selection
  - Continue to amount
  - Track analytics
  - Handle errors

#### Payment with Amount
- **Given** QR with amount
- **When** Scanned
- **Then** Should:
  - Skip network selection
  - Navigate to Step3SelectAmount
  - Pre-fill address and amount
  - Show in correct units
  - Validate amount
  - Track conversion

### 9. Antelope-Specific QR

#### ESR Processing
- **Given** ESR QR scanned
- **When** Valid ESR
- **Then** Should:
  - Navigate to EsrScreen
  - Parse signing request
  - Display transaction details
  - Allow approval/rejection
  - Track analytics
  - Handle expiration

#### Account Import QR
- **Given** Private key QR
- **When** Scanned
- **Then** Should:
  - Navigate to AntelopeImportAccountScreen
  - Validate key format
  - Check account existence
  - Import if valid
  - Show error if invalid
  - Secure key handling

#### KeyCert Import
- **Given** Anchor KeyCert QR
- **When** Scanned
- **Then** Should:
  - Navigate to ImportAccountByKeyCertScreen
  - Process certificate
  - Validate authenticity
  - Import account
  - Show success
  - Handle errors

#### Create Account for Friend
- **Given** Friend account request
- **When** Scanned
- **Then** Should:
  - Parse account details
  - Navigate to CreateAccountForFriendScreen
  - Show account name
  - Display public keys
  - Allow creation
  - Track completion

### 10. Unimplemented QR Types

#### Login QR (TODO)
- **Given** Login QR scanned
- **When** Implemented
- **Then** Should:
  - Parse authentication data
  - Validate request source
  - Show approval screen
  - Sign authentication
  - Return response
  - Track login

#### WalletConnect (TODO)
- **Given** WC URI scanned
- **When** Implemented
- **Then** Should:
  - Parse WC version
  - Establish session
  - Show dApp info
  - Request approval
  - Maintain connection
  - Handle disconnection

#### Transaction Signing (TODO)
- **Given** Unsigned tx QR
- **When** Implemented
- **Then** Should:
  - Parse transaction
  - Display details
  - Request signature
  - Generate signed QR
  - Support cold wallet flow
  - Handle timeout

## Asset Management

### 11. Asset Display

#### EVM Assets
- **Given** EVM account selected
- **When** Displaying assets
- **Then** Should show:
  - Token balances
  - USD values
  - Token icons
  - Price changes
  - Scrollable list
  - Loading states

#### Antelope Assets
- **Given** Antelope account
- **When** Displaying assets
- **Then** Should show:
  - Token balances
  - RAM allocation
  - CPU/NET resources
  - Staked amounts
  - REX balance
  - PowerUp status

#### Bitcoin Assets
- **Given** Bitcoin account
- **When** Displaying assets
- **Then** Should show:
  - BTC balance
  - USD value
  - UTXO count
  - Pending transactions
  - Fee estimates
  - Network status

### 12. Asset Cards

#### Card Interaction
- **Given** Asset card displayed
- **When** User taps
- **Then** Should:
  - Navigate to detail screen (when implemented)
  - Show transaction history
  - Display price chart
  - Show token info
  - Allow send/receive
  - Track interaction

#### Loading States
- **Given** Data loading
- **When** Fetching prices
- **Then** Should:
  - Show placeholders
  - Animate loading
  - Maintain layout
  - Update smoothly
  - Handle errors
  - Show cached data

## Resource Management (Antelope)

### 13. Resource Display

#### CPU/NET Resources
- **Given** Antelope account
- **When** Viewing resources
- **Then** Should display:
  - CPU usage percentage
  - NET usage percentage
  - Staked amounts
  - Available resources
  - Refresh on pull
  - Color coding

#### RAM Management
- **Given** RAM resource
- **When** Displayed
- **Then** Should show:
  - Used/Total RAM
  - RAM price
  - Buy/Sell options
  - Market value
  - Usage breakdown
  - Historical data

### 14. PowerUp Feature

#### PowerUp Access
- **Given** Low resources
- **When** PowerUp clicked
- **Then** Should:
  - Navigate to PowerUpScreen
  - Show resource needs
  - Display pricing
  - Allow purchase
  - Track usage
  - Show success

## Navigation

### 15. Top Bar

#### Menu Button
- **Given** Menu icon
- **When** Currently disabled
- **Then** Should (when enabled):
  - Open MenuScreen
  - Show settings
  - Display options
  - Support themes
  - Show version
  - Handle logout

#### QR Scanner Button
- **Given** Scanner icon
- **When** Tapped
- **Then** Should:
  - Check platform support
  - Trigger haptic feedback
  - Open scanner
  - Pass network type
  - Handle results
  - Track usage

### 16. Bottom Navigation

#### Navigation Visibility
- **Given** Bottom nav bar
- **When** Screen active
- **Then** Should:
  - Show navigation
  - Highlight current tab
  - Support gestures
  - Animate transitions
  - Hide when needed
  - Persist state

### 17. Bottom Sheet

#### Sheet Navigation
- **Given** Bottom sheet navigator
- **When** Content shown
- **Then** Should:
  - Slide from bottom
  - Show rounded corners
  - Support swipe dismiss
  - Transparent background
  - Handle back press
  - Smooth animations

## Data Management

### 18. Pull to Refresh

#### Refresh Gesture
- **Given** Pull down gesture
- **When** Refreshing
- **Then** Should:
  - Show refresh indicator
  - Update balances
  - Fetch latest prices
  - Update resources
  - Complete < 3 seconds
  - Handle errors

#### Data Updates
- **Given** Refresh triggered
- **When** Updating
- **Then** Should:
  - Query blockchain
  - Update cache
  - Show new values
  - Animate changes
  - Track refresh
  - Handle offline

### 19. State Management

#### Loading States
- **Given** Initial load
- **When** Fetching data
- **Then** Should:
  - Show loading indicator
  - Use placeholders
  - Maintain layout
  - Progressive loading
  - Cache previous
  - Handle timeout

#### Error States
- **Given** Data fetch fails
- **When** Error occurs
- **Then** Should:
  - Show error message
  - Provide retry
  - Use cached data
  - Log errors
  - Track failures
  - Graceful degradation

## Analytics

### 20. Event Tracking

#### User Actions
- **Given** Analytics enabled
- **When** User interacts
- **Then** Should track:
  - Screen views
  - QR scan events
  - Button clicks
  - Network switches
  - Send/receive actions
  - Error events

#### QR Analytics
- **Given** QR code scanned
- **When** Result parsed
- **Then** Should track:
  - QR type detected
  - Success/failure
  - Network type
  - Time to scan
  - User flow
  - Conversion rate

## Security Features

### 21. Privacy Protection

#### Balance Privacy
- **Given** Privacy concern
- **When** Toggle enabled
- **Then** Should:
  - Hide all balances
  - Obscure amounts
  - Hide portfolio value
  - Maintain throughout app
  - Persist setting
  - Quick toggle access

#### Screenshot Prevention
- **Given** Sensitive data
- **When** On secure screens
- **Then** Should:
  - Prevent screenshots
  - Block recording
  - Show warning
  - Clear on background
  - Protect keys
  - Audit access

### 22. Transaction Security

#### Transaction Validation
- **Given** Transaction initiated
- **When** Signing required
- **Then** Should:
  - Validate addresses
  - Check amounts
  - Verify gas fees
  - Prevent replay
  - Require confirmation
  - Track attempts

## Enhanced Features *[Enhancements]*

### 23. Portfolio Analytics

#### Advanced Charts
- **Given** Portfolio data
- **When** Viewing trends
- **Then** Could show:
  - Historical charts
  - Asset allocation
  - Performance metrics
  - Comparison tools
  - Export reports
  - Custom timeframes

#### DeFi Integration
- **Given** DeFi protocols
- **When** Integrated
- **Then** Could:
  - Show yield farming
  - Display staking rewards
  - Track liquidity pools
  - Monitor positions
  - Calculate APY
  - Auto-compound

### 24. Cross-Chain Features

#### Bridge Integration
- **Given** Multi-chain assets
- **When** Bridging needed
- **Then** Could:
  - Show bridge options
  - Compare fees
  - Estimate time
  - Track progress
  - Handle failures
  - Multi-step flow

#### Unified Balance
- **Given** Multiple networks
- **When** Viewing portfolio
- **Then** Could:
  - Aggregate balances
  - Show total USD
  - Cross-chain search
  - Unified history
  - Combined export
  - Single view

### 25. Social Features

#### Contact Integration
- **Given** Frequent recipients
- **When** Managing contacts
- **Then** Could:
  - Save contacts
  - Show avatars
  - Recent transactions
  - Quick send
  - Address book sync
  - ENS/UNS support

#### Activity Feed
- **Given** Transaction history
- **When** Viewing activity
- **Then** Could:
  - Show social feed
  - Transaction comments
  - Shared receipts
  - Follow accounts
  - Notifications
  - Export history

### 26. Advanced Trading

#### DEX Integration
- **Given** Trading needs
- **When** Swapping tokens
- **Then** Could:
  - In-app swaps
  - Price comparison
  - Slippage settings
  - Limit orders
  - Trade history
  - Gas optimization

#### Price Alerts
- **Given** Price monitoring
- **When** Thresholds set
- **Then** Could:
  - Set price alerts
  - Push notifications
  - Email alerts
  - Multiple conditions
  - Alert history
  - Quick actions

### 27. Automation

#### Recurring Transactions
- **Given** Regular payments
- **When** Scheduled
- **Then** Could:
  - Schedule sends
  - Recurring payments
  - DCA automation
  - Bill payments
  - Approval required
  - Cancel anytime

#### Smart Contracts
- **Given** Contract interaction
- **When** Advanced user
- **Then** Could:
  - Deploy contracts
  - Interact with ABIs
  - Save templates
  - Batch transactions
  - Gas management
  - Debug tools

### 28. Hardware Wallet

#### Ledger Support
- **Given** Hardware security
- **When** Ledger connected
- **Then** Could:
  - Pair via Bluetooth
  - Sign with Ledger
  - Display on device
  - Manage accounts
  - Firmware updates
  - Recovery options

### 29. Backup & Recovery

#### Cloud Backup
- **Given** Data protection
- **When** Backup enabled
- **Then** Could:
  - Encrypted backup
  - Cloud sync
  - Multi-device
  - Versioning
  - Selective restore
  - Audit trail

#### Social Recovery
- **Given** Key recovery
- **When** Setting up
- **Then** Could:
  - Guardian system
  - M-of-N recovery
  - Time-locked recovery
  - Emergency contacts
  - Recovery testing
  - Documentation

### 30. Compliance

#### Tax Reporting
- **Given** Tax requirements
- **When** Year end
- **Then** Could:
  - Generate reports
  - CSV export
  - API integration
  - Cost basis
  - Capital gains
  - Multiple formats

#### KYC Integration
- **Given** Regulatory needs
- **When** Required
- **Then** Could:
  - Identity verification
  - Document upload
  - Compliance check
  - Limits management
  - Status tracking
  - Privacy controls

## Testing Scenarios

### 31. Happy Path - Send Flow

1. Open Pro wallet
2. Select account with balance
3. Tap Send button
4. Select contact
5. Enter amount
6. Review transaction
7. Confirm and sign
8. View confirmation
9. Return to main screen

### 32. Happy Path - QR Payment

1. Tap QR scanner
2. Scan payment QR
3. Auto-navigate to send
4. Review pre-filled data
5. Confirm transaction
6. Track completion
7. Show success

### 33. Multi-Network Scenario

1. Start on EVM network
2. View EVM accounts
3. Switch to Antelope
4. View Antelope accounts
5. Switch to Bitcoin
6. View Bitcoin account
7. Verify correct display

### 34. Edge Cases

#### Account Management
- Maximum accounts: Handle limit gracefully
- Empty state: Show create/import options
- Network failure: Use cached data
- Rapid switching: Maintain stability
- Deep navigation: Preserve state

#### Data Loading
- Slow network: Show placeholders
- Partial failure: Display available data
- Cache expiry: Refresh automatically
- Background updates: Sync properly
- Memory pressure: Release resources

## Performance Requirements

### 35. Response Times

- **Screen load**: < 200ms
- **Account switch**: < 100ms
- **Network switch**: < 300ms
- **Pull to refresh**: < 3 seconds
- **QR scan to navigate**: < 500ms
- **Balance update**: < 2 seconds

### 36. Resource Usage

- **Memory footprint**: < 100MB
- **Cache size**: < 50MB
- **Network requests**: Optimized batching
- **Battery usage**: < 5% per hour active
- **CPU usage**: < 30% peak
- **Smooth scrolling**: 60 FPS

## Security Requirements

### 37. Data Protection

- **Encryption**: AES-256 for storage
- **Key management**: Secure enclave when available
- **Network**: TLS 1.3 minimum
- **Authentication**: Biometric + PIN
- **Session management**: Auto-lock
- **Audit logging**: Security events

### 38. Transaction Safety

- **Address validation**: Checksum verification
- **Amount limits**: Configurable warnings
- **Gas estimation**: Accurate predictions
- **Simulation**: Pre-execution testing
- **Confirmation**: Multiple steps for large amounts
- **Rate limiting**: Prevent rapid transactions

## Accessibility

### 39. Screen Reader Support

- **Content labels**: All elements labeled
- **Navigation**: Logical focus order
- **Announcements**: State changes announced
- **Actions**: Clear descriptions
- **Gestures**: Alternative inputs
- **Help**: Context-sensitive

### 40. Visual Accessibility

- **Text scaling**: Support system settings
- **Color contrast**: WCAG AA minimum
- **Color blind mode**: Alternative indicators
- **Reduce motion**: Respect system preference
- **Large touch targets**: 44pt minimum
- **Clear typography**: Readable fonts

## Acceptance Criteria Summary

### Must Have (Core Functionality)
✅ Multi-network support (EVM, Antelope, Bitcoin)
✅ Account management and display
✅ Send and receive functionality
✅ QR code scanning
✅ Balance display with privacy toggle
✅ Pull to refresh
✅ Asset display
✅ Portfolio navigation
✅ ESR support (Antelope)
✅ Analytics tracking

### Should Have (Important Enhancements)
⭐ Complete TODO implementations
⭐ WalletConnect integration
⭐ Buy crypto functionality
⭐ Transaction signing QRs
⭐ Asset detail screens
⭐ Price alerts
⭐ Contact management
⭐ Transaction history
⭐ Export functionality
⭐ Hardware wallet support

### Nice to Have (Future Enhancements)
💡 DeFi integration
💡 Cross-chain bridges
💡 Social features
💡 DEX integration
💡 Recurring transactions
💡 Tax reporting
💡 Cloud backup
💡 Advanced charts
💡 Smart contract interaction
💡 Multi-device sync