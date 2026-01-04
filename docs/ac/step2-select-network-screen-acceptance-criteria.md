# Step2SelectNetworkScreen Acceptance Criteria

## Overview
The Step2SelectNetworkScreen is the second step in the send token flow where users select the target network and enter the recipient address. The screen supports multiple blockchain types (EVM, Antelope, Bitcoin) with network-specific validation and optional recipient saving.

## Screen Navigation
### Prerequisites
- User must be on Step1 of send flow with selected account
- Screen receives: accountId, optional address (from QR), networkType

### Navigation Flow
1. **Entry Points**
   - From Step1 send screen with network type selected
   - From QR code scan with pre-filled address
   - From deep link with address parameter

2. **Exit Points**
   - Back button returns to previous screen
   - Continue button navigates to Step3SelectAmountScreen with:
     - accountId
     - contactId (if saved)
     - address
     - blockchainUid
     - amount (null initially)

## Network Selection
### 1. Network Display
- Display all networks matching the selected NetworkType (EVM/ANTELOPE/BITCOIN)
- Show network name and icon
- Networks loaded from BlockchainNetworkData based on environment

### 2. Network Search
- **Search Field Behavior**
   - Placeholder: "Network" with search icon
   - Real-time filtering as user types
   - Case-insensitive search
   - Clear button appears when text is entered
   - Auto-focus on screen load

### 3. Network List
- Display filtered networks in LazyColumn
- Vertical spacing between items (8dp)
- Selection highlights the network
- List hides after selection
- Empty state when no networks match filter

### 4. Network Selection Flow
- Selecting network:
  - Hides network list
  - Shows network name in search field
  - Reveals address input section
  - Auto-focuses address field after 500ms delay

## Address Input
### 5. Address Field Display
- Only visible after network selection
- Label: "to" 
- Placeholder varies by network type:
  - EVM: "0x... address"
  - ANTELOPE: "Account name"
  - BITCOIN: "Bitcoin address"

### 6. Address Input Methods
- **Manual Entry**
  - Keyboard type: Text
  - IME action: Done
  - Real-time validation feedback
  
- **QR Code Scan**
  - QR button in address field
  - Opens camera scanner
  - Auto-fills address on successful scan
  - Clears keyboard focus after scan

- **Clipboard Paste**
  - Paste button in address field
  - Retrieves system clipboard content
  - Auto-fills and validates
  - Clears keyboard focus after paste

### 7. Address Validation
#### EVM Networks
- Validate using AddressValidator.isAddressValid()
- Check for valid Ethereum address format (0x prefix, 40 hex chars)
- Show validation status indicator

#### ANTELOPE Networks
- Validate account name format (1-12 chars, a-z, 1-5, .)
- Check account exists on blockchain (on Done action)
- Show "Validating..." during async check
- Display error for non-existent accounts

#### BITCOIN Networks
- Validate using isValidBitcoinAddressUseCase
- Check for valid Bitcoin address format
- Network-specific validation (mainnet/testnet)

### 8. Validation States
- **NotValidated**: Initial state, no indicator
- **Validating**: Show loading spinner
- **Valid**: Green checkmark indicator
- **Invalid**: Red X indicator with error message

### 9. Validation Error Messages
- "Invalid address format"
- "Account does not exist" (ANTELOPE only)
- "Invalid Bitcoin address"
- "Address cannot be empty"

## Save Recipient Feature
### 10. Save Recipient Toggle
- **Display Conditions** (when enabled):
  - Network selected
  - Address entered and valid
  - Address not empty
  
- **Toggle Behavior**:
  - Off by default
  - Reveals name input field when enabled
  - Requires name before continuing

### 11. Recipient Name Input
- Label: "Recipient Name"
- Placeholder: "Enter a name for this recipient"
- Required when save toggle is on
- Max length: 50 characters
- Auto-focus when revealed

## Continue Button
### 12. Button States
- **Disabled when**:
  - No network selected
  - Address empty
  - Address invalid
  - Save recipient on but name empty
  - Validation in progress

- **Enabled when**:
  - Network selected
  - Valid address entered
  - Save recipient requirements met (if enabled)

### 13. Continue Action
- If address not validated: Trigger validation
- If address valid: Navigate to Step3
- Create contact if save recipient enabled
- Pass navigation parameters

## UI/UX Requirements
### 14. Layout
- Gradient background (OnboardingGradientBackground)
- Safe area padding
- Centered title: "Send Token"
- Back navigation button
- Scrollable content area
- Fixed continue button at bottom

### 15. Animations
- 500ms delay for focus transitions
- Smooth expand/collapse for sections
- Keyboard dismiss on outside tap

### 16. Keyboard Management
- Auto-show for network search on load
- Transition to address field after network selection
- Dismiss on:
  - Outside tap
  - Done action
  - QR scan
  - Paste action

## State Management
### 17. Screen State
- selectedNetwork: BlockchainNetworkData?
- showNetworkList: Boolean
- doneSelectNetwork: Boolean
- selectedAddress: String?
- doneSelectAddress: Boolean
- selectedName: String? (for saved recipient)
- recipientValidationStatus: RecipientValidationStatus

### 18. State Persistence
- Maintain state during configuration changes
- Clear navigation state after successful navigation
- Reset validation on address change

## Error Handling
### 19. Network Errors
- Show error toast for network validation failures
- Retry mechanism for blockchain calls
- Timeout handling (30 seconds)

### 20. Input Errors
- Inline validation messages
- Prevent navigation on errors
- Clear errors on correction

## Accessibility
### 21. Screen Reader Support
- Content descriptions for all buttons
- Validation status announcements
- Focus management for keyboard users
- Error announcements

### 22. Visual Accessibility
- High contrast mode support
- Minimum touch target size (48dp)
- Clear visual feedback for all interactions

## Performance
### 23. Optimization
- Debounce network search (300ms)
- Lazy loading for network list
- Cancel pending validations on address change
- Efficient recomposition for state changes

### 24. Loading States
- Show shimmer for network list loading
- Progress indicator during validation
- Smooth transitions between states

## Security
### 25. Address Validation
- Prevent homograph attacks
- Validate checksums where applicable
- Warn about similar-looking addresses
- No address logging in production

### 26. Clipboard Security
- Clear sensitive data from clipboard after paste
- Validate pasted content before use
- Sanitize input to prevent injection

## Analytics
### 27. Events to Track
- Screen view: "send_token_select_network"
- Network selected
- Address input method used (manual/QR/paste)
- Validation errors
- Continue button clicked
- Save recipient toggled

## Enhancements (Reasonable Additions)
### 28. Address Book Integration
- Show "Select from contacts" button
- Display recent recipients
- Quick select from saved addresses
- Search saved recipients

### 29. Multi-Address Support
- Allow multiple recipient addresses
- Split amount between recipients
- Batch transaction support
- CSV import for bulk sends

### 30. Enhanced Validation
- ENS/unstoppable domains support
- Real-time balance check for recipient
- Warning for first-time recipients
- Contract address detection and warning

### 31. Network Information
- Show network status (online/offline)
- Display current gas prices (EVM)
- Show resource costs (ANTELOPE)
- Network congestion indicator

### 32. Smart Suggestions
- Auto-detect network from address format
- Suggest networks based on history
- Remember last used network per contact
- Cross-chain address format conversion

### 33. Advanced Features
- Schedule send for later
- Recurring payments setup
- Add memo/message to transaction
- Transaction fee estimation

### 34. QR Code Enhancements
- Support for payment request QR codes
- Parse amount from QR if present
- Support for BIP21 URLs
- QR code generation for own address

### 35. Improved UX
- Address format helper text
- Copy address after validation 
- Show address preview with identicon
- Transaction preview before Step3

### 36. Network Switching
- Quick switch between related networks
- Show balance on each network
- Recommend network with sufficient balance
- Cross-chain swap integration

### 37. Risk Management
- Phishing address detection
- Large amount warnings
- Unusual recipient warnings
- Transaction limit settings

### 38. Offline Support
- Cache validated addresses
- Offline mode detection
- Queue transactions for later
- Sync when connection restored