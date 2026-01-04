# Import Wallet Flow - Acceptance Criteria

## Overview
This document defines the acceptance criteria for the import wallet functionality in Mangala Wallet, supporting multiple blockchain types and import methods across Android, iOS, and Desktop platforms.

## Supported Import Methods

### 1. Recovery Phrase (Mnemonic) Import
**Applicable to:** All blockchain types (EVM, Bitcoin, Binance, Antelope)

#### Entry Point
- **Given** I am on the wallet main screen or onboarding screen
- **When** I tap "Import Wallet" or navigate to Settings > Add Wallet > Import Wallet
- **Then** I should see the Import Wallet Guide screen

#### Recovery Phrase Input
- **Given** I am on the Recovery Phrase input screen
- **When** I view the screen
- **Then** I should see:
  - A text field for entering the recovery phrase
  - A "Paste" button for clipboard paste functionality
  - Clear instructions about recovery phrase format
  - Real-time word validation with visual feedback

#### Validation Requirements
- **Given** I am entering a recovery phrase
- **When** I type or paste words
- **Then** the system should:
  - Accept 12, 15, 18, 21, or 24 words
  - Validate each word against BIP39 wordlist
  - Show valid words in normal color
  - Show invalid words in error color (red/warning)
  - Enable "Import Wallet" button only when all words are valid
  - Verify checksum validity before proceeding

#### Error Handling
- **Given** I enter an invalid recovery phrase
- **When** validation fails
- **Then** I should see appropriate error messages:
  - "Invalid word count" for incorrect number of words
  - "Invalid word: [word]" for non-BIP39 words
  - "Invalid recovery phrase checksum" for wrong word order

### 2. Private Key Import
**Applicable to:** Antelope chains (EOS, WAX, Telos, etc.)

#### Entry Point
- **Given** I am on an Antelope blockchain wallet screen
- **When** I tap "Import Account"
- **Then** I should see the Import Private Key screen

#### Private Key Input
- **Given** I am on the Import Private Key screen
- **When** I view the screen
- **Then** I should see:
  - A secure text field for private key entry
  - Toggle button to show/hide private key
  - QR code scanner button
  - Paste button for clipboard functionality
  - Terms and conditions checkbox

#### Validation & Account Discovery
- **Given** I enter a private key
- **When** the key is validated
- **Then** the system should:
  - Validate the private key format
  - Search for associated accounts on the blockchain
  - Display found accounts with permission levels (Active/Owner)
  - Allow selection of accounts to import
  - Show error if no accounts found

### 3. QR Code Import
**Applicable to:** Private keys and wallet addresses

#### QR Scanner
- **Given** I tap the QR scanner button
- **When** the camera opens
- **Then** I should:
  - See camera preview with QR code overlay
  - Have camera permissions requested if not granted
  - Be able to scan QR codes containing private keys or mnemonics
  - See immediate feedback upon successful scan

## Security Requirements

### PIN Setup
- **Given** I am importing a wallet for the first time
- **When** import validation succeeds
- **Then** I must:
  - Create a 6-digit PIN
  - Confirm the PIN by entering it again
  - See error if PINs don't match

### Existing PIN
- **Given** I already have a PIN set up
- **When** I import an additional wallet
- **Then** the new wallet should be protected by the existing PIN

### Biometric Authentication
- **Given** biometrics are available on the device
- **When** I complete wallet import
- **Then** I should be offered to enable biometric authentication

## Post-Import Requirements

### Wallet Creation
- **Given** successful import validation
- **When** the import process completes
- **Then** the system should:
  - Generate wallet addresses for supported blockchains
  - Store encrypted wallet data securely
  - Mark wallet as "not backed up" if applicable
  - Set the imported wallet as selected/active
  - Navigate to the main wallet screen

### Account Display
- **Given** wallet import is successful
- **When** I view the wallet screen
- **Then** I should see:
  - Wallet name (customizable)
  - Account addresses for each blockchain
  - Current balances (fetched from blockchain)
  - Transaction history (if any)

## Build Variant Specific Requirements

### Cold Wallet Variant
- **Given** I am using the Cold wallet variant
- **When** I import a wallet
- **Then** the wallet should:
  - Function completely offline
  - Support transaction signing only
  - Not attempt network connections

### UI Wallet Variant
- **Given** I am using the UI wallet variant
- **When** I import a wallet
- **Then** I should be able to:
  - Send transactions
  - View real-time balances
  - Connect with Cold wallet for signing

### Pro Wallet Variant
- **Given** I am using the Pro wallet variant
- **When** I import a wallet
- **Then** I should have all features from both Cold and UI variants

## Platform-Specific Requirements

### Android
- Minimum API level 24 (Android 7.0)
- Support for biometric authentication via BiometricPrompt API
- Clipboard access with user permission

### iOS
- Minimum iOS 13.0
- Support for Face ID/Touch ID
- Keychain storage for secure data
- Clipboard access with user notification

### Desktop
- Support for Windows, macOS, and Linux
- Secure storage using platform-specific secure storage APIs
- Clipboard functionality across all desktop platforms

## Performance Requirements
- Recovery phrase validation should complete within 100ms
- Private key validation should complete within 500ms
- Account discovery should timeout after 10 seconds
- QR code scanning should recognize codes within 2 seconds

## Accessibility Requirements
- All input fields must have appropriate labels for screen readers
- Error messages must be announced to screen readers
- Buttons must have sufficient touch target size (minimum 44x44 points on iOS, 48x48 dp on Android)
- Color-based feedback must have additional non-color indicators

## Data Privacy Requirements
- Recovery phrases must never be logged or transmitted
- Private keys must be encrypted at rest
- Clipboard should be cleared after paste operations
- No analytics should track actual wallet addresses or keys

## Edge Cases

### Network Connectivity
- **Given** I have no network connection (UI/Pro variants)
- **When** I import a wallet
- **Then** I should:
  - Still be able to complete the import
  - See a message that balances will update when online
  - Have wallet ready for offline signing

### Multiple Wallet Import
- **Given** I already have wallets imported
- **When** I import another wallet
- **Then** I should:
  - See all wallets in the wallet list
  - Be able to switch between wallets
  - Have each wallet's data isolated

### Duplicate Import
- **Given** I try to import a wallet that already exists
- **When** the system detects the duplicate
- **Then** I should see:
  - Warning message about existing wallet
  - Option to view existing wallet
  - Option to continue anyway (creating duplicate)

## Testing Requirements

### Unit Tests
- BIP39 word validation
- Checksum verification
- Private key format validation
- Address generation from seed

### Integration Tests
- End-to-end import flow for each method
- PIN setup and verification
- Wallet storage and retrieval
- Account discovery on blockchain

### UI Tests
- Navigation through import screens
- Input field validation feedback
- Error message display
- QR scanner functionality

### Security Tests
- Encryption of stored wallet data
- PIN brute force protection
- Secure storage implementation
- Memory cleanup after sensitive operations

## Acceptance Test Scenarios

### Scenario 1: Successful Mnemonic Import
1. Navigate to Import Wallet
2. Enter valid 12-word mnemonic
3. Verify all words show as valid
4. Tap Import
5. Set up PIN (if first wallet)
6. Verify wallet appears in wallet list
7. Verify correct addresses generated

### Scenario 2: Invalid Mnemonic Handling
1. Navigate to Import Wallet
2. Enter 11 words (invalid count)
3. Verify error message appears
4. Add 12th word (invalid word)
5. Verify word shows as invalid
6. Replace with valid word
7. Verify import button becomes enabled

### Scenario 3: QR Code Import
1. Navigate to Import screen
2. Tap QR scanner
3. Scan valid private key QR
4. Verify key populates in field
5. Complete import process
6. Verify wallet imported successfully

### Scenario 4: Antelope Private Key Import
1. Select Antelope blockchain
2. Navigate to Import Account
3. Enter valid private key
4. Verify accounts found
5. Select accounts to import
6. Complete import
7. Verify accounts appear in wallet