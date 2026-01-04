# AntelopeStep4VerifyAndSendScreen Acceptance Criteria

## Overview
The AntelopeStep4VerifyAndSendScreen is the final verification step in the Antelope send flow where users review transaction details, confirm each element, authenticate with PIN, and execute the transaction. This screen is specific to Antelope blockchain networks (EOS, WAX, Telos).

## Screen Navigation
### Prerequisites
- User must be on Step3 with selected amount and memo
- Screen receives: contactId, senderAccount, toAccount, blockchainUid, tokenKey, amount, memo

### Navigation Flow
1. **Entry Points**
   - From Step3SelectAmountScreen after entering amount/memo
   - Cannot be accessed directly without prior steps

2. **Exit Points**
   - Back button returns to Step3
   - Successful transaction navigates to Step5SendSuccessScreen
   - Transaction failure allows retry or back navigation

## Transaction Display
### 3. Header
- Title: "Verify Transaction"
- Back button to return to Step3
- Clean, focused layout for verification

### 4. Recipient Information
- **Contact Display**:
  - Show contact name if saved
  - Fall back to account name if not a contact
  - Display format: Name or account
  
- **Account Address**:
  - Show full Antelope account name
  - No truncation (12 character max)
  - Clearly visible for verification

## Confirmation Items
### 5. Network Confirmation
- Checkbox: "Network"
- Display blockchain name (EOS/WAX/Telos)
- Show network icon
- Required for transaction

### 6. Address Confirmation
- Checkbox: "Address"
- Display recipient account name
- Show full account without truncation
- Highlight for easy verification

### 7. Amount Confirmation
- Checkbox: "Amount"
- Display amount with token symbol
- Show decimal places as entered
- Display fiat value below

### 8. Memo Confirmation
- Checkbox: "Memo"
- Display memo text if provided
- Show "No memo" if empty
- Support multi-line display

### 9. Checkbox Behavior
- All checkboxes start unchecked
- Toggle on click/tap
- Visual feedback on state change
- Track confirmation state

## Token Information
### 10. Token Display
- Show token logo/icon
- Display token symbol
- Show amount being sent
- Format with appropriate decimals

### 11. Fiat Value
- Calculate based on current price
- Display in user's selected currency
- Show currency symbol
- Update if price changes

## Transaction Summary
### 12. Total Transaction Value
- Display total fiat value
- Include any fees if applicable
- Prominent display at bottom
- Currency symbol visible

### 13. Fee Information
- Show if resource provider fees apply
- Display breakdown if requested
- Clear fee structure
- Optional fee confirmation

## Resource Provider (Greymass Fuel)
### 14. Resource Provider Detection
- Check if transaction uses Greymass Fuel
- Detect resource requirements
- Calculate resource costs
- Show when applicable

### 15. Fee Breakdown Dialog
- Show detailed fee breakdown
- Display CPU/NET/RAM costs
- Show total resource cost
- Confirmation button

### 16. Resource Confirmation
- Require explicit confirmation
- Explain resource provider service
- Show benefits (free transactions)
- Allow dismissal

## Authentication
### 17. PIN Prompt
- Trigger when "Confirm and Send" clicked
- All checkboxes must be checked first
- Navigate to UnlockPinScreen
- Pass transaction context

### 18. PIN Verification
- Verify PIN against stored hash
- Support biometric if enabled
- Return to verify screen on success
- Show error on failure

### 19. Authentication Flow
- Request → PIN Screen → Verify → Return
- Maintain transaction state
- Handle navigation properly
- Prevent duplicate prompts

## Transaction Execution
### 20. Confirm and Send Button
- Label: "Confirm and Send"
- **Disabled when**:
  - Any checkbox unchecked
  - Transaction in progress
  - Error state active
  - Loading data

- **Enabled when**:
  - All checkboxes checked
  - Data loaded
  - No errors
  - Not loading

### 21. Transaction Processing
- Show loading state
- Disable all interactions
- Display progress indicator
- Prevent navigation

### 22. Transaction Signing
- Use AntelopeSendCryptoUseCase
- Sign with authenticated account
- Include memo in transaction
- Handle resource provider

### 23. Transaction Broadcasting
- Submit to blockchain
- Wait for confirmation
- Capture transaction hash
- Handle timeout

## Success Handling
### 24. Success Navigation
- Navigate to Step5SendSuccessScreen
- Pass transaction hash
- Pass blockchain UID
- Clear navigation state

### 25. Transaction Hash
- Capture from successful transaction
- Store temporarily in state
- Consume after navigation
- Prevent duplicate navigation

## Error Handling
### 26. Loading Errors
- Show error if token data fails to load
- Display message: "Error loading token info"
- Allow back navigation
- Show retry option

### 27. Transaction Errors
- Display inline error messages
- Common errors:
  - Insufficient resources (CPU/NET/RAM)
  - Account doesn't exist
  - Insufficient balance
  - Network timeout
  - Invalid transaction

### 28. Error Recovery
- Allow retry after error
- Maintain form state
- Clear error on retry
- Show detailed error info

## State Management
### 29. UI State
- Loading: Initial data fetch
- Data: Transaction ready for confirmation
- Error: Failed to load or transact

### 30. Data State
- contact: ContactEntity if saved
- recipientAccount: Target account
- selectedToken: Token being sent
- tokenFiatValue: Calculated fiat value
- txHash: Transaction hash after success
- totalTransactionFiatValue: Total including fees
- resourceRequiredBreakdown: Fee details
- promptConfirmTransaction: PIN trigger
- isLoading: Processing state
- error: Error message

## Loading States
### 31. Initial Loading
- Show loading spinner
- Fetch token information
- Calculate fiat values
- Prepare transaction

### 32. Transaction Loading
- Show processing indicator
- Disable all inputs
- Show "Sending..." message
- Prevent back navigation

## UI/UX Requirements
### 33. Layout
- Gradient background
- Safe area padding
- Scrollable content
- Fixed button at bottom

### 34. Visual Hierarchy
- Clear section separation
- Prominent total value
- Easy-to-read amounts
- Accessible checkboxes

### 35. Animations
- Smooth checkbox transitions
- Loading spinner animation
- Success feedback
- Error shake animation

## Analytics
### 36. Events to Track
- Screen view: "send_token_verify_and_send_antelope"
- Checkbox interactions
- Confirm button clicked
- PIN screen shown
- Transaction success/failure
- Resource provider fee shown
- Error types encountered

## Performance
### 37. Optimization
- Cache token prices
- Minimize re-renders
- Efficient state updates
- Quick PIN verification

### 38. Network Handling
- Timeout after 30 seconds
- Show timeout error
- Allow retry
- Handle network changes

## Security
### 39. Transaction Security
- Require PIN for every transaction
- No PIN caching
- Clear sensitive data after use
- Validate transaction data

### 40. Data Validation
- Verify amounts match
- Check account validity
- Validate memo length
- Confirm token contract

## Accessibility
### 41. Screen Reader
- Announce all values
- Describe checkboxes state
- Read error messages
- Guide through flow

### 42. Visual Accessibility
- High contrast mode
- Large touch targets (48dp)
- Clear visual states
- Color-blind friendly

## Enhancements (Reasonable Additions)
### 43. Transaction Preview
- Show simulated balance after
- Display account resources after
- Preview state changes
- Warning for low resources

### 44. Advanced Confirmation
- Swipe to confirm gesture
- Hold to confirm button
- Pattern confirmation
- Voice confirmation

### 45. Fee Options
- Choose resource provider
- Pay with different tokens
- Resource staking option
- Fee comparison

### 46. Transaction Details
- Show more transaction details
- Display contract actions
- Show authorization list
- Technical details toggle

### 47. Risk Assessment
- High-value transaction warning
- First-time recipient alert
- Unusual amount detection
- Smart contract risk score

### 48. Notification Settings
- Success notification preferences
- Email confirmation option
- Push notification setup
- SMS alerts for high value

### 49. Transaction Scheduling
- Schedule for later execution
- Recurring transaction setup
- Conditional execution
- Batch transaction support

### 50. Export Options
- Export transaction receipt
- Generate PDF confirmation
- Share transaction details
- Save for tax purposes

### 51. Multi-Signature Support
- Show required signatures
- Pending approval status
- Signature collection UI
- Timeout handling

### 52. Resource Management
- Show current resources
- Suggest resource actions
- Auto-stake if needed
- Resource provider comparison

### 53. Contact Integration
- Save new recipient option
- Update contact details
- Show transaction history with contact
- Contact verification badge

### 54. Custom Memo Templates
- Saved memo templates
- Auto-fill common memos
- Memo encryption option
- Memo validation rules

### 55. Transaction History
- Show recent similar transactions
- Quick repeat transaction
- Transaction patterns
- Spending insights

### 56. Network Status
- Show network congestion
- Estimated confirmation time
- Block producer info
- Network health indicator

### 57. Enhanced Security
- Two-factor authentication
- Hardware wallet support
- Threshold signatures
- Time-locked transactions

### 58. Audit Trail
- Complete transaction log
- State change history
- Signature verification
- Blockchain explorer link

### 59. Smart Assistance
- Transaction recommendations
- Optimal timing suggestions
- Fee optimization tips
- Resource usage advice

### 60. Recovery Options
- Transaction cancellation (if pending)
- Stuck transaction handling
- Failed transaction analysis
- Automatic retry logic

### 61. Developer Mode
- Show raw transaction data
- Display serialized transaction
- Network request/response
- Debug information

### 62. Compliance Features
- KYC/AML checks
- Transaction limits
- Regulatory compliance
- Audit reports

### 63. Integration Features
- DApp transaction support
- Cross-chain transactions
- Exchange integration
- Payment gateway support