# Vaulta Create Account Flow - Acceptance Criteria

## Overview
This document defines the acceptance criteria for the Vaulta account creation flow in Mangala Wallet. Vaulta is an Antelope-based blockchain (similar to EOS) that requires specific account creation procedures including resource management (CPU, NET, RAM).

## Account Types

### Standard Account
- **Account Name Length:** 12 characters exactly
- **Character Set:** Only lowercase letters a-z and numbers 1-5
- **Cost:** Lower cost than premium accounts
- **Example:** `myaccount123`

### Premium Account
- **Account Name Length:** 1-11 characters
- **Character Set:** Only lowercase letters a-z and numbers 1-5
- **Cost:** Higher cost for shorter names
- **Pricing Tiers:**
  - 1-3 characters: Highest premium
  - 4-6 characters: High premium
  - 7-11 characters: Medium premium
- **Example:** `alice`, `bob123`, `myname`

### Friend Account
- **Description:** Account created by another user who pays the creation fee
- **Requirements:** QR code from friend containing account details
- **Process:** Scan QR code → Verify details → Friend pays fees

## Step 1: Select Account Type

### Entry Point
- **Given** I am on the wallet home screen or onboarding
- **When** I select "Create Account" for Vaulta/Antelope blockchain
- **Then** I see the account type selection screen

### Account Type Selection
- **Given** I am on Step 1: Select Account Type screen
- **When** I view the screen
- **Then** I should see:
  - Title: "Start Your Journey"
  - Description explaining account types
  - Three selection options:
    - Standard Account (12 characters)
    - Premium Account (1-11 characters)
    - Create for Friend
  - Each option shows pricing information
  - Continue button (disabled until selection)

### Navigation
- **Given** I have selected an account type
- **When** I tap "Continue"
- **Then** I navigate to:
  - Standard/Premium → Step 2: Select Account Name
  - Friend → QR Scanner screen

## Step 2: Select Account Name

### Screen Requirements
- **Given** I am on Step 2: Select Account Name
- **When** I view the screen
- **Then** I should see:
  - Account name input field
  - Real-time validation feedback
  - Character counter
  - Available/unavailable indicator
  - Account type selector (can switch between Standard/Premium)
  - Suffix options (for premium accounts)
  - Continue button

### Name Validation - Standard Account
- **Given** I selected Standard Account
- **When** I enter an account name
- **Then** the system validates:
  - Must be exactly 12 characters
  - Only lowercase a-z and numbers 1-5 allowed
  - No special characters or dots
  - Real-time availability check on blockchain
  - Show error if name already exists

### Name Validation - Premium Account
- **Given** I selected Premium Account
- **When** I enter an account name
- **Then** the system validates:
  - Must be 1-11 characters
  - Only lowercase a-z and numbers 1-5 allowed
  - Can include dots (.) but not at start/end
  - Cannot have consecutive dots
  - Real-time availability check
  - Display pricing based on length

### Suffix Selection (Premium Only)
- **Given** I have a premium account name
- **When** the name is less than 12 characters
- **Then** I can:
  - Select a suffix from available options
  - See the final account name preview
  - Suffix options: .x, .gm, .nft, .dao, etc.

### Error States
- **Invalid Format:** "Account name can only contain lowercase letters a-z and numbers 1-5"
- **Wrong Length:** "Standard accounts must be exactly 12 characters"
- **Already Taken:** "This account name is already registered"
- **Invalid Premium:** "Premium names cannot start or end with dots"
- **Network Error:** "Unable to check availability. Please try again"

## Step 3: Payment Options

### Ready to Claim Screen
- **Given** I have a valid available account name
- **When** I proceed to Step 3
- **Then** I should see:
  - Account name display (with suffix if applicable)
  - Account type badge (Standard/Premium)
  - Total cost breakdown
  - Payment method options
  - Terms and conditions checkbox
  - Create Account button

### Payment Methods
- **Given** I am on the payment screen
- **When** I view payment options
- **Then** I can choose:
  - In-App Purchase (iOS/Android)
  - Crypto payment (if wallet has funds)
  - External payment providers
  - Free account (if promotion available)

### Cost Breakdown Display
- **Given** I am reviewing payment
- **When** I view the cost breakdown
- **Then** I should see:
  - Account creation fee
  - Initial RAM allocation
  - Initial CPU stake
  - Initial NET stake
  - Total cost in USD and crypto equivalent

### Terms Acceptance
- **Given** I am ready to pay
- **When** I try to proceed
- **Then** I must:
  - Check the terms and conditions checkbox
  - See error if trying to proceed without acceptance
  - Have access to full terms via link

## Step 4: Creating Account

### Processing Screen
- **Given** Payment is initiated
- **When** I reach Step 4
- **Then** I should see:
  - Animated loading indicator
  - "Creating your account..." message
  - Progress steps:
    1. Processing payment ✓
    2. Generating keys ✓
    3. Creating blockchain account ✓
    4. Allocating resources ✓
  - Estimated time remaining

### Key Generation
- **Given** Payment is confirmed
- **When** Account creation begins
- **Then** the system should:
  - Generate Owner key pair
  - Generate Active key pair
  - Store keys securely in device keychain
  - Never transmit private keys

### Blockchain Transaction
- **Given** Keys are generated
- **When** Creating account on blockchain
- **Then** the system should:
  - Submit account creation transaction
  - Allocate initial resources (RAM, CPU, NET)
  - Verify transaction success
  - Retry on failure (up to 3 times)

### Error Handling
- **Payment Failed:** Show retry payment option
- **Network Error:** Show retry with exponential backoff
- **Account Exists:** Rare race condition - offer alternative name
- **Insufficient Resources:** Contact support option

## Step 5: Backup Options

### Backup Selection Screen
- **Given** Account is successfully created
- **When** I reach Step 5
- **Then** I should see:
  - Success confirmation
  - Backup options:
    - Vaulta Cloud Backup (recommended)
    - Manual backup (write down keys)
    - Skip backup (not recommended - warning shown)
  - Continue button

### Vaulta Cloud Backup
- **Given** I select Vaulta backup
- **When** I proceed
- **Then** I should:
  - Navigate to Step 6: Vaulta Backup
  - See secure backup process
  - Encryption with user password
  - Recovery phrase generation

### Manual Backup Warning
- **Given** I select manual backup
- **When** I proceed
- **Then** I should see:
  - Strong warning about key loss risks
  - Checkbox to confirm understanding
  - Display private keys with copy function
  - Verification quiz before proceeding

### Skip Backup Warning
- **Given** I select skip backup
- **When** I try to proceed
- **Then** I should see:
  - Critical warning dialog
  - Explanation of permanent loss risk
  - Require typing "I UNDERSTAND" to proceed
  - Mark account as "Not Backed Up" in wallet

## Step 6: Vaulta Backup Private Key

### Backup Screen Display
- **Given** I selected Vaulta backup
- **When** I reach Step 6
- **Then** I should see:
  - Step indicator (4/4 completed)
  - Account name display
  - Private key display (hidden by default)
  - QR code for private key
  - Show/Hide toggle
  - Copy button
  - Continue button

### Key Security Features
- **Given** I am on the backup screen
- **When** I interact with the private key
- **Then** the system should:
  - Hide key by default (show dots)
  - Toggle visibility with eye icon
  - Copy to clipboard with confirmation
  - Clear clipboard after 30 seconds
  - Show QR code for offline backup
  - Prevent screenshots (platform dependent)

### Backup Verification
- **Given** I have viewed the backup
- **When** I tap continue
- **Then** I should:
  - See verification prompt
  - Answer security questions
  - Confirm backup completion
  - See backup status updated

## PIN Setup (First Account Only)

### New User PIN Creation
- **Given** This is my first Vaulta account
- **When** Backup is complete
- **Then** I must:
  - Create 6-digit PIN
  - Confirm PIN by re-entering
  - See error if PINs don't match
  - Option to enable biometrics

### Existing User
- **Given** I already have a PIN set
- **When** Creating additional account
- **Then** The account is secured with existing PIN

## Post-Creation Requirements

### Account Dashboard
- **Given** Account creation is complete
- **When** I navigate to home
- **Then** I should see:
  - New Vaulta account in wallet list
  - Account name and type badge
  - Resource meters (CPU, NET, RAM)
  - Current balance (initially 0)
  - Backup status indicator

### Resource Management
- **Given** I have a new Vaulta account
- **When** I view account details
- **Then** I should see:
  - CPU: Usage bar and available/total
  - NET: Usage bar and available/total
  - RAM: Usage in KB/MB and available/total
  - Resource management options:
    - Buy/Sell RAM
    - Stake/Unstake CPU
    - Stake/Unstake NET
    - PowerUp options

### Initial Resources
- **Given** Account is newly created
- **When** I check resources
- **Then** I should have:
  - Minimum RAM allocation (3KB)
  - Minimum CPU stake (0.1 EOS equivalent)
  - Minimum NET stake (0.1 EOS equivalent)
  - Enough resources for basic transactions

## Platform-Specific Requirements

### iOS
- In-App Purchase integration for payment
- Keychain storage for private keys
- Face ID/Touch ID for PIN bypass
- iCloud backup option (future)

### Android
- Google Play billing for payment
- Android Keystore for private keys
- Biometric authentication support
- Google Drive backup (future)

### Desktop
- External payment provider integration
- Secure storage using OS keyring
- Hardware wallet support (future)

## Network Requirements

### Mainnet
- Production Vaulta/EOS network
- Real payment required
- Permanent account creation
- Full resource costs

### Testnet
- Jungle testnet for Vaulta
- Free test accounts available
- Limited resources provided
- Reset periodically

## Performance Requirements

- Account name validation: < 500ms
- Availability check: < 2 seconds
- Payment processing: < 30 seconds
- Account creation transaction: < 10 seconds
- Total flow completion: < 2 minutes

## Analytics Events

Track the following events:
1. `account_creation_started` - User begins flow
2. `account_type_selected` - Type chosen (standard/premium/friend)
3. `account_name_entered` - Name validation complete
4. `payment_initiated` - User proceeds to payment
5. `payment_completed` - Payment successful
6. `account_created` - Blockchain transaction success
7. `backup_method_selected` - Backup choice made
8. `account_creation_completed` - Full flow complete
9. `account_creation_abandoned` - User exits flow

## Error Recovery

### Payment Failure Recovery
- Save account name selection
- Allow retry with different payment method
- Prevent duplicate charges
- Show support contact for payment issues

### Network Failure Recovery
- Implement exponential backoff retry
- Cache progress locally
- Resume from last successful step
- Show offline mode message

### Account Creation Failure
- If name taken (race condition): Suggest alternatives
- If resources insufficient: Offer to increase allocation
- If blockchain error: Provide transaction ID for support

## Accessibility Requirements

- All screens must support screen readers
- Sufficient color contrast (WCAG AA)
- Touch targets minimum 44x44 points
- Error messages announced to screen readers
- Keyboard navigation support (desktop)

## Security Requirements

- Private keys generated on device only
- Keys encrypted at rest
- No key transmission to servers
- Secure backup encryption
- PIN/Biometric protection
- Auto-lock after inactivity
- Clipboard auto-clear for sensitive data

## Testing Scenarios

### Happy Path
1. Select Standard Account
2. Enter valid 12-character name
3. Name is available
4. Complete payment
5. Account created successfully
6. Complete Vaulta backup
7. Set up PIN (first time)
8. View account in wallet

### Premium Account Path
1. Select Premium Account
2. Enter 5-character name
3. Select .gm suffix
4. Verify pricing tier
5. Complete payment
6. Account created with suffix
7. Manual backup selected
8. Verify keys displayed

### Error Scenarios
1. Invalid name format → See format error
2. Name already taken → See availability error
3. Payment declined → Retry option
4. Network timeout → Automatic retry
5. Skip backup → Strong warning shown

### Edge Cases
1. Switch account type mid-flow
2. Change name after validation
3. Background app during payment
4. Network switch during creation
5. Multiple simultaneous account creations

## Acceptance Criteria Summary

✅ Users can select between Standard, Premium, and Friend account types
✅ Account name validation provides real-time feedback
✅ Premium accounts show accurate pricing based on length
✅ Payment integration works on all platforms
✅ Account creation completes within 2 minutes
✅ Backup options are clearly presented with appropriate warnings
✅ Vaulta backup securely stores encrypted keys
✅ Resources are properly allocated to new accounts
✅ Error states are handled gracefully with recovery options
✅ Analytics track the complete user journey
✅ Security requirements are met for key generation and storage
✅ Accessibility standards are maintained throughout the flow