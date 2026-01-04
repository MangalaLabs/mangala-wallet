# User Stories for Antelope Account Creation - Conversational UI

Based on the Mangala Wallet use case documentation, this document outlines comprehensive user stories for Antelope account creation (UC70-UC75) adapted for a conversational UI where users interact with an LLM assistant.

## Epic 3: Antelope Account Creation via Conversational Interface
As a user interested in Antelope blockchains (EOS/Telos/WAX), I want to create a blockchain account by chatting with an AI assistant so that I can easily set up my account through natural conversation without navigating complex blockchain concepts.

## Epic 4: Antelope Account Import via Conversational Interface
As a user with existing Antelope accounts, I want to import my accounts by chatting with an AI assistant so that I can easily add my existing accounts to the wallet without dealing with complex private key management.

## User Stories

### Story 3.1: Conversational Account Type Selection
**As a** user wanting an Antelope account  
**I want to** understand and choose between different account types through conversation  
**So that** I can pick the right account name format without confusion  

**Acceptance Criteria:**
- Given I have selected Antelope differences in simple terms:
  - Standard: "Traditional EOS format like 'johndoe12345'"
  - Mangala: "Shorter, easier names like 'john.man' or 'crypto.man'"
- And the AI should ask which type appeals to them more
- And the AI should explain any cost differences between the types
- And the AI should allow me to change my choice before proceeding to name selection

### Story 3.2: Conversational Account Name Selection
**As a** user creating an Antelope account  
**I want to** choose my account name through conversation with the AI  
**So that** I understand the naming rules and get a name I like  

**Acceptance Criteria:**
- Given I have selected my account type
- When the AI begins name selection
- Then the AI should explain naming rules based on the selected account type:
  - Standard account: exactly 12 characters using a-z and 1-5
  - Mangala account: shorter names ending with .man suffix
- And the AI should ask what kind of name I prefer (my name, business name, random, etc.)
- And the AI should validate name availability in real-time as I suggest names
- And the AI should offer alternatives if my preferred name is taken
- And the AI should explain any additional costs for Mangala accounts if applicable
- And the AI should generate suggestions based on my preferences
- And the AI should confirm my final choice before proceeding
- And the AI should never ask me to type characters that aren't allowed

### Story 3.3: Conversational Payment Method Selection
**As a** user creating an Antelope account  
**I want to** understand and choose payment options through conversation  
**So that** I can pay for my account creation without confusion  

**Acceptance Criteria:**
- Given I have confirmed my account name
- When the AI discusses payment
- Then the AI should explain why Antelope accounts require payment (unlike other crypto wallets)
- And the AI should present available payment options in simple terms:
  - In-app purchase (mobile)
  - Cryptocurrency payment (if supported)
  - Friend referral/gift (if available)
- And the AI should explain costs clearly for my chosen account type
- And the AI should guide me through the selected payment method
- And the AI should confirm payment details before processing
- And the AI should provide clear next steps after payment
- And the AI should handle payment failures gracefully with retry options

### Story 3.4: Conversational Key Generation and Management
**As a** user with a paid Antelope account  
**I want to** understand and secure my account keys through conversation  
**So that** I know how to access and protect my account  

**Acceptance Criteria:**
- Given my account payment has been processed
- When the AI generates my account keys
- Then the AI should explain that Antelope uses key pairs (not seed phrases)
- And the AI should explain the difference between owner and active keys in simple terms
- And the AI should show me my private keys securely (never send to backend)
- And the AI should emphasize the importance of backing up keys
- And the AI should guide me to save keys securely (write down, password manager, etc.)
- And the AI should ask me to confirm I've saved the keys safely
- And the AI should explain what each key type can do
- And the AI should remind me that lost keys mean lost account access

### Story 3.5: Conversational Account Completion
**As a** user with a new Antelope account  
**I want to** have my account automatically configured and ready to use  
**So that** I can start using my account without dealing with technical complexity  

**Acceptance Criteria:**
- Given my account keys are secured
- When the AI completes account setup
- Then the AI should automatically configure account resources without showing technical details
- And the AI should confirm that my account is ready to use
- And the AI should explain that resources are managed automatically
- And the AI should only mention resources if there are usage issues later
- And the AI should focus on what the user can do next ("Your account is ready! You can now use EOS apps")
- And the AI should offer to show them popular apps or next steps

### Story 3.6: Conversational Security Setup
**As a** user with a new Antelope account  
**I want to** set up wallet security through conversation  
**So that** my account access is protected on this device  

**Acceptance Criteria:**
- Given my account setup is complete
- When the AI begins security setup
- Then the AI should explain wallet vs account security (device access vs blockchain access)
- And the AI should guide me to create a PIN for wallet access
- And the AI should ask about biometric authentication and explain benefits
- And the AI should confirm security setup is complete
- And the AI should remind me about key backup importance
- And the AI should explain what to do if I lose device access
- And the AI should distinguish between wallet PIN and account keys

### Story 3.7: Conversational Account Backup and Recovery Education
**As a** user with a complete Antelope account  
**I want to** understand how to backup and recover my account through conversation  
**So that** I know how to protect and restore my account access  

**Acceptance Criteria:**
- Given my account setup is complete
- When the AI discusses backup and recovery
- Then the AI should explain Antelope account recovery options (keys, account recovery service)
- And the AI should guide me through exporting my keys/certificates safely
- And the AI should explain the difference between device recovery and account recovery
- And the AI should offer to help set up account recovery contacts if available
- And the AI should explain how to import my account on other devices
- And the AI should provide clear instructions for account recovery scenarios
- And the AI should emphasize the importance of multiple backup methods

### Story 3.8: Conversational Error Handling
**As a** user attempting to create an Antelope account  
**I want to** receive helpful explanations from the AI when something goes wrong  
**So that** I don't get frustrated and can easily continue the process  

**Acceptance Criteria:**
- Given I am creating an Antelope account and an error occurs
- When the account creation fails
- Then the AI should explain what went wrong in friendly, non-technical language
- And the AI should suggest specific steps to fix the problem
- And the AI should offer to retry automatically or ask if I want to try again
- And the AI should handle common issues like:
  - Name already taken
  - Payment failures
  - Network connectivity issues
  - Account setup issues
- And the AI should reassure me that my progress/payment is saved where possible
- And the AI should escalate to human support if the error persists

### Story 3.9: Conversational Antelope Education and Onboarding
**As a** first-time Antelope user  
**I want to** learn about Antelope blockchains through natural conversation  
**So that** I feel confident using my new account  

**Acceptance Criteria:**
- Given I am creating my first Antelope account
- When I go through the creation process
- Then the AI should gauge my blockchain knowledge level through conversation
- And the AI should explain Antelope-specific concepts in simple terms:
  - What makes Antelope different from Bitcoin/Ethereum
  - How account names work vs addresses
  - What dApps are available on my chosen network
- And the AI should provide analogies for complex concepts (resources = fuel, etc.)
- And the AI should encourage questions and answer them patiently
- And the AI should offer to explain more or skip details based on my interest
- And the AI should suggest next steps after account creation (apps to try, etc.)
- And the AI should offer ongoing support and education

## Epic 4 User Stories

### Story 4.1: Conversational Import Initiation
**As a** user with an existing EOS account  
**I want to** tell the AI I have an existing account so it can guide me through importing it  
**So that** I can add my EOS account to the wallet without technical complexity  

**Acceptance Criteria:**
- Given I am in the wallet setup conversation
- When I say something like "I already have an EOS account" or "I want to import my account" or "I have existing keys"
- Then the AI should understand I want to import rather than create new
- And the AI should confirm that I have an EOS account to import
- And the AI should explain that import requires my private keys
- And the AI should warn that private keys are sensitive and should be handled carefully
- And the AI should ask if I have my EOS private keys available
- And the AI should guide me to the private key import flow
- And the AI should explain what will happen during the import process

### Story 4.2: Conversational Private Key Import
**As a** user wanting to import via private keys  
**I want to** securely provide my keys through conversation with the AI  
**So that** I can import my account while understanding the security implications  

**Acceptance Criteria:**
- Given I have chosen to import using private keys
- When the AI begins key import
- Then the AI should explain what private keys are and why they're sensitive
- And the AI should warn that keys should never be shared with anyone else
- And the AI should guide me to enter keys securely (local input, not sent to backend)
- And the AI should distinguish between owner and active keys if both are needed
- And the AI should validate keys locally before proceeding
- And the AI should never store or transmit the actual key values
- And the AI should confirm successful key validation
- And the AI should remind me to keep my original keys safe

### Story 4.3: Conversational Account Name Verification
**As a** user importing an Antelope account  
**I want to** verify my account details through conversation  
**So that** I know I'm importing the correct account  

**Acceptance Criteria:**
- Given I have provided valid private keys
- When the AI verifies the account
- Then the AI should look up the account name associated with the keys
- And the AI should display the account name and ask me to confirm it's correct
- And the AI should show basic account information (network, creation date if available)
- And the AI should ask me to confirm this is the account I want to import
- And if it's wrong, the AI should allow me to try different keys
- And the AI should explain what will happen when I import this account
- And the AI should proceed only after I confirm the correct account

### Story 4.4: Conversational Account Balance and Assets Display
**As a** user importing an Antelope account  
**I want to** see my account's current state through conversation  
**So that** I can verify the import was successful and understand what I have  

**Acceptance Criteria:**
- Given my account has been successfully imported
- When the AI completes the import
- Then the AI should show me my account balance and tokens
- And the AI should explain what tokens I have in simple terms
- And the AI should show any NFTs or other assets if present
- And the AI should explain that I can now use this account in the wallet
- And the AI should offer to show me what I can do next
- And the AI should confirm that my account is ready to use
- And the AI should remind me that my keys are now stored securely in the wallet

### Story 4.5: Conversational Import Security Setup
**As a** user who has imported an Antelope account  
**I want to** set up wallet security through conversation  
**So that** my imported account is protected on this device  

**Acceptance Criteria:**
- Given my account import is complete
- When the AI begins security setup
- Then the AI should explain that I need to secure wallet access (separate from account keys)
- And the AI should guide me to create a PIN for the wallet app
- And the AI should ask about biometric authentication and explain benefits
- And the AI should confirm security setup is complete
- And the AI should explain the difference between wallet PIN and account keys
- And the AI should remind me about keeping my original keys safe
- And the AI should explain what to do if I lose device access

### Story 4.6: Conversational Import Error Handling
**As a** user attempting to import an Antelope account  
**I want to** receive helpful explanations from the AI when something goes wrong  
**So that** I don't get frustrated and can successfully import my account  

**Acceptance Criteria:**
- Given I am importing an Antelope account and an error occurs
- When the import fails
- Then the AI should explain what went wrong in friendly, non-technical language
- And the AI should handle common issues like:
  - Invalid private keys
  - Wrong network selection
  - Account not found
  - Network connectivity issues
  - Account already imported
- And the AI should suggest specific steps to fix the problem
- And the AI should offer to retry with different inputs
- And the AI should reassure me that no sensitive data was stored if import failed
- And the AI should escalate to human support if the error persists

### Story 4.7: Conversational Multiple Account Import
**As a** user with multiple Antelope accounts  
**I want to** import additional accounts through conversation  
**So that** I can manage all my accounts in one wallet  

**Acceptance Criteria:**
- Given I have already imported one Antelope account
- When I want to import another account
- Then the AI should understand requests like "I have another EOS account" or "add my other account"
- And the AI should guide me through the same import process for additional accounts
- And the AI should show me all my imported accounts when complete
- And the AI should help me distinguish between accounts (by name or balance)
- And the AI should explain how to switch between accounts in the wallet
- And the AI should remind me that each account has its own keys and security

## Technical Requirements

### Epic 3 Requirements (Account Creation)
- **Conversational AI Integration**: LLM must understand Antelope-specific terminology and concepts
- **Real-time Name Validation**: AI must check account name availability during conversation
- **Payment Integration**: Must integrate with supported payment methods (IAP, crypto)
- **Key Generation Security**: Private keys must be generated locally and never sent to backend
- **Resource Management**: Must automatically configure Antelope resources without user intervention
- **Account Creation APIs**: Must integrate with EOS/Telos/WAX account creation services

### Epic 4 Requirements (Account Import)
- **Private Key Import Security**: Keys must be validated and stored locally, never transmitted to backend
- **Account Lookup APIs**: Must query blockchain networks to verify account details from private keys
- **Multi-format Key Support**: Support different private key formats (WIF, hex, etc.)
- **Network Detection**: Automatically detect which Antelope network an account belongs to
- **Duplicate Account Prevention**: Prevent importing the same account multiple times
- **Key Validation**: Local validation of private key format and network compatibility

### Shared Requirements
- **Error Recovery**: Conversational error handling for Antelope-specific failures
- **Multi-network Support**: Support for different Antelope chains (EOS, Telos, WAX)
- **Offline Capability**: Account operations must work for Cold wallet variant where possible
- **Security Auditing**: All key handling operations must be security audited

## Definition of Done

### Epic 3 (Account Creation)
- [ ] User can successfully create an Antelope account through conversation with AI
- [ ] AI correctly interprets Antelope account creation intents and requirements
- [ ] Conversational account type selection works based on user needs
- [ ] Account name selection with real-time validation and suggestions
- [ ] Payment integration works for all supported methods
- [ ] Key generation and backup guidance works securely
- [ ] Account setup completes automatically without overwhelming users with resource details
- [ ] Security setup (PIN/biometrics) works through conversational interface
- [ ] Account backup and recovery education is comprehensive
- [ ] Integration tests pass for conversational Antelope account creation

### Epic 4 (Account Import)
- [ ] User can successfully import existing Antelope accounts through conversation with AI
- [ ] AI correctly interprets account import intents and guides users appropriately
- [ ] Private key import works securely without exposing keys to backend
- [ ] Account verification and balance display works correctly
- [ ] Multiple account import and management works seamlessly
- [ ] Import error handling covers all common failure scenarios
- [ ] Security setup works for imported accounts
- [ ] Integration tests pass for conversational Antelope account import

### Shared
- [ ] Conversational error handling covers all Antelope-specific scenarios
- [ ] Educational content helps users understand Antelope concepts
- [ ] Works across all build variants (Pro/Cold/UI) with appropriate limitations
- [ ] Security audit completed for key generation, import, and storage

## Conversational Interaction Examples

### Example Conversation Flow - Account Creation:
```
User: "I want to create an account on EOS"
AI: "Great choice! For your EOS account, you have two naming options: a standard 12-character name like 'cryptogamer12' or a shorter Mangala name like 'crypto.man'. Which sounds better to you?"

User: "The shorter one sounds easier"
AI: "Great choice! Mangala accounts are more user-friendly. The account creation costs about $5 and you can pay through the app store. Should I set that up?"

User: "Yes please"
AI: "Perfect! Now let's pick your account name. It needs to end with '.man' - what would you like before that? Your name, a gaming handle, or something else?"

User: "My gaming handle is cryptogamer"
AI: "Let me check... 'cryptogamer.man' is available! This will be your permanent EOS account name. Confirm 'cryptogamer.man'?"
```

### Example Conversation Flow - Account Import:
```
User: "I already have an EOS account I want to import"
AI: "Perfect! I can help you import your existing EOS account. Do you have your private keys, or would you prefer another import method?"

User: "I have my private keys"
AI: "Great! I'll guide you through importing with your private keys. Just to be clear, I'll never see or store your actual keys - everything stays secure on your device. What's your EOS account name?"

User: "My account is johndoe12345"
AI: "Thanks! Now I'll show you a secure form to enter your private key. Once you enter it, I'll verify it matches your account 'johndoe12345' and show you your balance to confirm it's the right account."
```

## Related Use Cases

### Epic 3 (Account Creation)
- UC70: Create Antelope Account (now conversational)
- UC71: Select Account Type (conversational account type discovery)
- UC72: Choose Account Name (conversational name selection)
- UC73: Pay for Account Creation (conversational payment)
- UC74: Create Account for Friend (future enhancement)
- UC75: Purchase Account via IAP (integrated into payment options)

### Epic 4 (Account Import)
- UC11: Import Account via Private Key (now conversational)
- UC12: View Account Details (integrated into import verification)
- UC40: Backup Antelope Account (education during import)
- UC42: Import EOS Account via EVM (potential future enhancement)

## AI Assistant Capabilities Needed

### Epic 3 (Account Creation)
- Natural language understanding for Antelope/EOS/Telos/WAX terminology
- Real-time account name validation and suggestion generation
- Educational explanation generation for blockchain concepts
- Payment flow management and error handling
- Automatic resource configuration without user complexity
- Security-conscious conversation patterns for key management
- Multi-step process management for account creation flow

### Epic 4 (Account Import)
- Intent recognition for account import vs creation
- Private key security education and guidance
- Account verification and balance interpretation
- Multi-account management conversation flows
- Import error diagnosis and recovery suggestions
- Network detection and validation explanations

### Shared Capabilities
- Context switching between creation and import flows
- Security-first conversation patterns
- User experience level adaptation
- Error recovery and clarification skills