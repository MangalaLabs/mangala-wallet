# Step3SelectAmountScreen Acceptance Criteria

## Overview
The Step3SelectAmountScreen is the third step in the send token flow where users select the sending account, choose a token, enter the amount to send, and optionally add a memo. The screen adapts its behavior based on the blockchain network type (EVM, ANTELOPE, BITCOIN).

## Screen Navigation
### Prerequisites
- User must be on Step2 with selected network and recipient
- Screen receives: accountId, contactId, receivingAddress, blockchainUid, amount

### Navigation Flow
1. **Entry Points**
   - From Step2SelectNetworkScreen with recipient details
   - From QR code scan with pre-filled amount
   - From deep link with transaction parameters

2. **Exit Points**
   - Back button returns to Step2
   - Continue button navigates to Step4 verify screen based on network:
     - Step4EvmVerifyAndSendScreen (EVM)
     - Step4AntelopeVerifyAndSendScreen (ANTELOPE)
     - Step4BitcoinVerifyAndSendScreen (BITCOIN)

## Multi-Step Flow
### 3. Flow States
- **SelectAccount**: Choose sending account
- **SelectToken**: Choose token to send
- **SelectAmount**: Enter amount to send
- **EnterMemo**: Enter optional memo (ANTELOPE only)

### 4. State Progression
- Account selection → Token selection → Amount entry → Memo (if ANTELOPE) → Continue
- Each step reveals the next when completed
- Users can go back to previous steps by focusing fields

## Recipient Display
### 5. Recipient Information
- Shows recipient name if contact, otherwise address
- Display format: "to: [name/address]"
- Non-editable field at top of screen
- Clicking field navigates back to Step2

### 6. Transfer Label
- Shows "Transferring" label above recipient
- Persistent throughout all flow states
- Visual hierarchy with bold font weight

## Account Selection
### 7. Account Loading
- Load accounts based on network type:
  - EVM: GetSelectedWalletAccountsUseCase
  - ANTELOPE: GetAccountsUseCase with blockchain type
  - BITCOIN: GetSelectedWalletBitcoinAccountsUseCase

### 8. Account Display
- Show account name and formatted address
- Display in bordered cards with rounded corners
- Highlight selected account with accent border
- Show "from" label above account list

### 9. Account Search
- Search field with placeholder "Account name"
- Real-time filtering as user types
- Clear button when text entered
- Auto-focus on field when resetting

### 10. Auto-Selection
- If only one account exists, auto-select it
- Skip account selection step
- Proceed directly to token selection

### 11. Transfer to Self Prevention
- **ANTELOPE Networks**:
  - Detect if recipient matches sender
  - Show alert dialog
  - Prevent continuation
  - Reset to account selection
  
- **Other Networks**:
  - Allow self-transfers
  - No blocking dialog

## Token Selection
### 12. Token Loading
- Show loading spinner while fetching
- Network-specific token fetching:
  - EVM: Fetch ERC-20 tokens and native
  - ANTELOPE: Fetch account balances
  - BITCOIN: Fetch BTC balance only

### 13. Token List Display
- Show token icon (32dp circle)
- Display token name with search highlighting
- Show current price in selected currency
- Display balance (formatted)
- Show fiat value

### 14. Token Search
- Search field with placeholder "Token name"
- Real-time filtering
- Highlight matching text in results
- Clear button when focused

### 15. Token Information
- **Balance Display**:
  - Formatted with appropriate decimals
  - Right-aligned in list
  - Placeholder while loading
  
- **Value Display**:
  - Current fiat value
  - Currency symbol from user settings
  - Secondary text color

## Amount Input
### 16. Amount Field
- Label: "Amount"
- Placeholder: "........"
- Decimal keyboard type
- Next IME action

### 17. Amount Validation
- **Real-time Validation**:
  - Check on each character input
  - Format with max decimals per token
  - Prevent invalid characters
  
- **Validation Rules**:
  - Must be positive number
  - Cannot exceed balance
  - Must be above dust limit (Bitcoin)
  - Must have valid decimal places

### 18. MAX Button
- Shows when amount field empty
- Replaces with clear button when amount entered
- **MAX Calculation**:
  - EVM: Format balance with min(decimals, 5)
  - ANTELOPE: Use full balance
  - BITCOIN: Convert satoshis to BTC

### 19. Insufficient Balance
- Red text color when amount exceeds balance
- Show error message below field
- Disable continue button
- Clear error on valid input

### 20. Bitcoin Dust Amount
- Validate against DUST_SATS_AMOUNT (546 sats)
- Show error: "Amount too small"
- Prevent transaction below dust limit
- Display minimum required amount

## Memo Field (ANTELOPE Only)
### 21. Memo Display
- Only shown for ANTELOPE networks
- Appears after amount entry
- Label: "Memo"
- Placeholder: "Optional"

### 22. Memo Input
- Text keyboard type
- Done IME action
- Multi-line support
- Clear button when text entered
- Auto-focus when revealed

### 23. Memo Behavior
- Optional field
- Can be empty
- Included in transaction data
- Done action triggers continue

## Continue Button
### 24. Button States
- **Disabled when**:
  - No token selected
  - Amount empty or invalid
  - Insufficient balance
  - Validation errors present
  
- **Enabled when**:
  - Valid amount entered
  - Sufficient balance
  - All validations pass

### 25. Button Actions
- **Before Amount Entry**:
  - Hide keyboard
  - Validate and proceed to amount/memo
  
- **After Amount Entry**:
  - Navigate to Step4 verify screen
  - Pass transaction parameters

## Amount Formatting
### 26. Decimal Handling
- Respect token decimal places
- Format input in real-time
- Prevent excess decimals
- Handle international formats

### 27. Network-Specific Formatting
- **EVM**: Wei conversion with BigInteger
- **ANTELOPE**: String decimal with precision
- **BITCOIN**: Satoshi/BTC conversion

## Focus Management
### 28. Auto-Focus Flow
- Account field → Token field → Amount field → Memo field
- 500ms delay between transitions
- Request focus on field reveal
- Clear focus on continue

### 29. Field Reset Behavior
- Focusing completed field resets its state
- Clears subsequent steps
- Maintains previous selections
- Updates UI state accordingly

## Keyboard Management
### 30. Keyboard Behavior
- Auto-show for search fields
- Hide on search action
- Hide on outside tap
- Hide before navigation

### 31. IME Actions
- Search: Hide keyboard
- Next: Move to next field
- Done: Complete and continue

## Error Handling
### 32. Balance Fetch Errors
- Show retry option
- Display error message
- Allow manual refresh
- Cache last successful data

### 33. Validation Errors
- Inline error messages
- Red highlight on fields
- Clear on correction
- Prevent navigation

## UI/UX Requirements
### 34. Layout
- Gradient background
- Safe area padding
- Scrollable content
- Fixed continue button

### 35. Visual Feedback
- Loading spinners
- Highlighted search matches
- Selected account border
- Error state colors

## State Management
### 36. Screen State
- Track current flow step
- Maintain selections across steps
- Handle state restoration
- Clear state on navigation

### 37. Data Flow
- Account → Token → Amount → Memo
- Each step depends on previous
- Reset cascades forward
- Preserve backward navigation

## Analytics
### 38. Events to Track
- Screen view: "send_token_select_amount"
- Account selected
- Token selected
- Amount entered
- MAX button clicked
- Continue clicked
- Self-transfer attempted

## Performance
### 39. Optimization
- Lazy load token balances
- Cache account data
- Debounce search inputs
- Cancel pending requests

### 40. Loading States
- Show placeholders during fetch
- Smooth transitions
- Progressive data loading
- Optimistic UI updates

## Accessibility
### 41. Screen Reader
- Announce state changes
- Label all interactive elements
- Describe validation errors
- Focus management announcements

### 42. Visual Accessibility
- High contrast support
- Minimum touch targets (48dp)
- Clear error indicators
- Sufficient color contrast

## Enhancements (Reasonable Additions)
### 43. Token Favorites
- Star frequently used tokens
- Pin tokens to top of list
- Remember last used tokens
- Quick access section

### 44. Amount Presets
- Quick amount buttons (25%, 50%, 75%, MAX)
- Custom preset amounts
- Recent amounts used
- Percentage-based inputs

### 45. Advanced Amount Features
- Fiat amount input option
- Currency conversion display
- Real-time price updates
- Amount in multiple currencies

### 46. Fee Estimation
- Show estimated transaction fee
- Deduct fee from amount option
- Fee included in validation
- Multiple fee tiers (slow/normal/fast)

### 47. Balance Refresh
- Pull-to-refresh gesture
- Manual refresh button
- Auto-refresh on focus
- Show last updated time

### 48. Multi-Send Support
- Add multiple recipients
- Split amount between recipients
- Batch transaction creation
- Total amount validation

### 49. Templates
- Save transaction templates
- Recurring payment setup
- Quick repeat last transaction
- Named transaction templates

### 50. Enhanced Validation
- Check recipient has activated account (ANTELOPE)
- Warn about first-time recipients
- Contract interaction warnings
- Slippage tolerance settings

### 51. Token Information
- Show token contract address
- Link to block explorer
- Token verification badges
- Price change indicators

### 52. Smart Features
- Suggest optimal send time (gas prices)
- Tax calculation integration
- Transaction notes/categories
- Export transaction data

### 53. QR Code Integration
- Generate QR for transaction
- Parse amount from QR codes
- Support payment request format
- Share transaction details

### 54. Account Management
- Show account balance trends
- Multiple address types support
- Account aliases/nicknames
- Color coding for accounts

### 55. Memo Enhancements
- Memo templates
- Encrypted memo support
- Memo length validation
- Special character handling

### 56. Risk Management
- Daily send limits
- Unusual amount warnings
- Whitelist-only mode
- Two-factor authentication

### 57. Network Optimization
- Suggest cheaper networks
- Cross-chain routing options
- Bridge integration
- Network congestion warnings

### 58. History Integration
- Show recent recipients
- Transaction success rate
- Average send amounts
- Frequently used tokens

### 59. Simulation
- Preview transaction effects
- Simulate before sending
- Show balance after transaction
- Warning for token approvals

### 60. Accessibility Enhancements
- Voice input for amounts
- Haptic feedback
- Large text mode
- Simplified interface option