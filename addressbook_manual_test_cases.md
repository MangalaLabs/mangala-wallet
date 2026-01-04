# Address Book Manual Test Cases

## Test Environment Setup
- Ensure you have access to both mainnet and testnet versions of the app
- Have test accounts ready for each blockchain type
- Note: VAULTA uses EOS blockchain infrastructure (greymass.com endpoints)

## 1. VAULTA (Antelope) Test Cases

### 1.1 VAULTA Mainnet
**Test Case**: Add VAULTA mainnet system account
- **Steps**:
  1. Go to Address Book → Add Contact
  2. Enter contact name: "VAULTA System Account"
  3. Add wallet address
  4. Select blockchain: "Vaulta" (mainnet)
  5. Enter system account: `eosio` or `eosio.token` or `eosio.ram`
  6. Click Save
- **Expected Result**: Warning message "⚠️ This is a system account. Please verify you intend to send to this address." but allows saving
- **Note**: System accounts bypass API existence check

**Test Case**: Add VAULTA mainnet regular account
- **Steps**:
  1. Go to Address Book → Add Contact
  2. Enter contact name: "VAULTA Regular Account"
  3. Add wallet address
  4. Select blockchain: "Vaulta" (mainnet)
  5. Enter existing account: `binancecleos` or `huobideposit` or `okbtothemoon`
  6. Click Save
- **Expected Result**: Contact saved successfully with green checkmark validation
- **Note**: Regular accounts are checked via API (userres table)

**Test Case**: Add invalid VAULTA mainnet account
- **Steps**:
  1. Go to Address Book → Add Contact
  2. Enter contact name: "Invalid VAULTA"
  3. Add wallet address
  4. Select blockchain: "Vaulta" (mainnet)
  5. Enter non-existent account: `notexist1234` or `vaultawallet`
  6. Observe validation
- **Expected Result**: Error message "⚠️ VAULTA account 'notexist1234' does not exist. Please verify the account name or create it first on VAULTA network."

**Test Case**: Add premium VAULTA account
- **Steps**:
  1. Go to Address Book → Add Contact
  2. Enter contact name: "Premium VAULTA"
  3. Add wallet address
  4. Select blockchain: "Vaulta" (mainnet)
  5. Enter short account: `eos` or `b1` or `gm`
  6. Observe validation
- **Expected Result**: Warning message about premium account and validation passes if account exists

### 1.2 VAULTA Testnet
**Test Case**: Add VAULTA testnet account
- **Steps**:
  1. Go to Address Book → Add Contact
  2. Enter contact name: "VAULTA Testnet Test"
  3. Add wallet address
  4. Select blockchain: "Vaulta Jungle Testnet"
  5. Enter testnet account: `testaccount1` or `jungle` or any valid format
  6. Click Save
- **Expected Result**: Warning message "This is a VAULTA testnet address. Account existence is not verified for testnet." but allows saving

**Test Case**: Verify testnet account format validation
- **Steps**:
  1. Add wallet address for VAULTA testnet
  2. Enter invalid formats:
     - `TEST-ACCOUNT` (contains invalid characters)
     - `UpperCase123` (contains uppercase)
     - `test@account` (contains @ symbol)
     - `toolongaccountname` (more than 12 characters)
  3. Observe validation
- **Expected Result**: Format validation error with message about allowed characters (only a-z, 1-5, and dots)

### 1.3 VAULTA Special Accounts
**Test Case**: Add system and burn addresses
- **Steps**:
  1. Try adding these special accounts:
     - `eosio.null` (burn address)
     - `vaulta.null` (VAULTA burn address)
     - `eosio.ram` (system account)
     - `eosio.stake` (system account)
- **Expected Result**: 
  - Burn addresses: Warning about burn address
  - System accounts: Normal validation, saved if exists

## 2. VAULTA EVM Test Cases

### 2.1 VAULTA EVM Mainnet
**Test Case**: Add VAULTA EVM mainnet address
- **Steps**:
  1. Go to Address Book → Add Contact
  2. Enter contact name: "VAULTA EVM Mainnet"
  3. Add wallet address
  4. Select blockchain: "Vaulta EVM" (mainnet)
  5. Enter valid EVM address: `0x742d35Cc6634C0532925a3b844Bc9e7595f6bEd1`
  6. Click Save
- **Expected Result**: Contact saved successfully with proper EVM validation

**Test Case**: Add invalid VAULTA EVM address
- **Steps**:
  1. Add wallet address for VAULTA EVM
  2. Try invalid formats:
     - VAULTA account format: `eosio` or `testaccount1`
     - Invalid EVM: `0x123` (too short)
     - Without 0x prefix: `742d35Cc6634C0532925a3b844Bc9e7595f6bEd1`
  3. Observe validation
- **Expected Result**: Error message indicating invalid EVM address format

### 2.2 VAULTA EVM Testnet
**Test Case**: Add VAULTA EVM testnet address
- **Steps**:
  1. Go to Address Book → Add Contact
  2. Enter contact name: "VAULTA EVM Testnet"
  3. Add wallet address
  4. Select blockchain: "Vaulta EVM" with testnet network
  5. Enter EVM address: `0x1234567890123456789012345678901234567890`
  6. Click Save
- **Expected Result**: Warning message "This is a VAULTA_EVM testnet address. Account existence is not verified for testnet." but allows saving

## 3. Cross-Validation Test Cases

### 3.1 Symbol Conflict Resolution
**Test Case**: Verify VAULTA vs VAULTA EVM validation
- **Steps**:
  1. Create two contacts with different address formats
  2. First: Select "Vaulta" and enter `binancecleos`
  3. Second: Select "Vaulta EVM" and enter `binancecleos`
- **Expected Result**: 
  - VAULTA: Validates as Antelope account (passes if exists)
  - VAULTA EVM: Shows error for invalid EVM format

### 3.2 Edit Contact Validation
**Test Case**: Edit existing contact blockchain type
- **Steps**:
  1. Create contact with VAULTA address (e.g., `huobideposit`)
  2. Edit contact and change blockchain to VAULTA EVM
  3. Keep same address
- **Expected Result**: Validation error when switching to incompatible blockchain type

## 4. Edge Cases

### 4.1 Account Format Validation
**Test Cases**:
- VAULTA valid formats:
  - With dots: `my.account` → Should validate format (exists check separate)
  - With numbers: `account12345` → Should validate format
  - All numbers: `12345` → Should validate format
  - Single char: `a` → Premium account warning
- VAULTA invalid formats:
  - Uppercase: `MyAccount` → Format error
  - Special chars: `my@account` → Format error
  - Too long: `verylongaccountname` → Format error (max 12 chars)
  - Start with dot: `.account` → Format error
  - End with dot: `account.` → Format error
  - Double dots: `my..account` → Format error

### 4.2 EVM Address Validation
**Test Cases**:
- Valid EVM addresses:
  - With checksum: `0x742d35Cc6634C0532925a3b844Bc9e7595f6bEd1` → Pass
  - All lowercase: `0x742d35cc6634c0532925a3b844bc9e7595f6bed1` → Pass
  - All uppercase: `0x742D35CC6634C0532925A3B844BC9E7595F6BED1` → Pass
- Invalid EVM addresses:
  - Wrong length: `0x123` → Fail
  - No 0x prefix: `742d35Cc6634C0532925a3b844Bc9e7595f6bEd1` → Fail
  - Invalid chars: `0xGGGG35Cc6634C0532925a3b844Bc9e7595f6bEd1` → Fail

### 4.3 Duplicate Detection
**Test Case**: Add duplicate addresses
- **Steps**:
  1. Add contact with address `0x742d35Cc6634C0532925a3b844Bc9e7595f6bEd1`
  2. Try to add another contact with same address (case insensitive)
- **Expected Result**: Warning "Address already exists in your contacts"

## 5. Performance Test Cases

### 5.1 Validation Debouncing
**Test Case**: Fast typing validation
- **Steps**:
  1. Add wallet address
  2. Type quickly: `binancecleos`
  3. Observe validation behavior
- **Expected Result**: 
  - Validation should debounce (400ms for <4 chars, 600ms for ≥4 chars)
  - API calls only after user stops typing

### 5.2 Network Timeout Handling
**Test Case**: Validation timeout
- **Steps**:
  1. Add VAULTA mainnet address
  2. If network is slow, wait for validation
- **Expected Result**: 
  - Timeout warning after 5 seconds
  - Shows timeout message but allows user to continue
  - Save button enabled with warning

### 5.3 Retry Logic
**Test Case**: Network error retry
- **Steps**:
  1. Simulate network error (airplane mode briefly)
  2. Add VAULTA address
  3. Restore network
- **Expected Result**: Automatic retry with exponential backoff

## 6. Real VAULTA/EOS Mainnet Test Accounts

### Valid Existing Accounts (for testing)
- **System**: `eosio`, `eosio.token`, `eosio.ram`
- **Exchanges**: `binancecleos`, `huobideposit`, `okbtothemoon`, `bitfinexdep1`
- **Block Producers**: `eoshuobipool`, `atticlabeosb`, `eosflytomars`
- **Short Premium**: `eos`, `b1`, `gm`, `x`

### Testnet Accounts (Jungle)
- Can use any valid format as testnet doesn't verify existence
- Examples: `testaccount1`, `jungle`, `mytest.acc`

## 7. Validation State Messages

### Success States
- ✅ No message (green checkmark) - Valid and verified
- ⚠️ "This is a VAULTA testnet address..." - Valid format, testnet

### Error States
- ❌ "VAULTA account 'X' does not exist..." - Valid format, not on chain
- ❌ "Invalid address format..." - Format validation failed
- ❌ "Network timeout..." - API timeout after 5 seconds
- ❌ "Unable to verify..." - Network error

### Warning States
- ⚠️ "Premium account name..." - 1-3 character accounts
- ⚠️ "Burn address..." - eosio.null, vaulta.null
- ⚠️ "Address already exists..." - Duplicate in contacts

## Notes for Testers

1. **VAULTA = EOS**: VAULTA uses EOS blockchain, so use real EOS accounts for testing
2. **Mainnet vs Testnet**: Mainnet checks actual account existence, testnet only validates format
3. **Real-time Validation**: Validation happens as you type with debouncing
4. **Save Button**: Disabled when validation fails (red error), enabled for warnings
5. **Network Dependencies**: Mainnet validation requires internet connection
6. **Case Sensitivity**: VAULTA accounts are lowercase only, EVM addresses are case-insensitive

## Debugging Tips

If validation fails unexpectedly:
1. Check if using real EOS account for VAULTA mainnet
2. Verify internet connection for mainnet validation
3. Check account format (lowercase, 1-12 chars, a-z/1-5/dots)
4. For EVM addresses, ensure 0x prefix and 40 hex chars
5. Try known existing accounts from the list above