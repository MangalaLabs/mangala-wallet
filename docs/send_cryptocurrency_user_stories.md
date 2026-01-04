# User Stories for Send Cryptocurrency - Conversational UI

Based on the Mangala Wallet use case documentation, this document outlines comprehensive user stories for sending cryptocurrency (UC16-UC23) adapted for a conversational UI where users interact with an LLM assistant.

## Epic 5: Send Cryptocurrency via Conversational Interface
As a user with cryptocurrency accounts, I want to send crypto to others by chatting with an AI assistant so that I can easily transfer funds through natural conversation without navigating complex transaction forms.

## Epic 6: Receive Cryptocurrency via Conversational Interface
As a user who wants to receive cryptocurrency, I want to get my receiving addresses and share them with others through conversation with an AI assistant so that I can easily receive funds without navigating complex address management.

## User Stories

### Story 5.1: Conversational Send Initiation
**As a** user with cryptocurrency  
**I want to** tell the AI I want to send crypto to someone  
**So that** I can start the sending process through natural conversation  

**Acceptance Criteria:**
- Given I have cryptocurrency accounts with balances
- When I say something like "I want to send Bitcoin" or "send money to my friend" or "transfer some EOS"
- Then the AI should understand my intent to send cryptocurrency
- And the AI should ask clarifying questions if the request is ambiguous:
  - Which cryptocurrency I want to send (if not specified)
  - From which account (if I have multiple)
- And the AI should explain what information will be needed for the transfer
- And the AI should confirm I have sufficient balance before proceeding
- And the AI should guide me to the next step (token selection or recipient)

### Story 5.2: Conversational Token and Account Selection
**As a** user wanting to send cryptocurrency  
**I want to** specify which token and account to send from through conversation  
**So that** I can choose the right asset and network without confusion  

**Acceptance Criteria:**
- Given I have initiated a send request
- When the AI needs to clarify which token to send
- Then the AI should show me my available tokens with balances in simple terms
- And the AI should explain network differences for the same asset (e.g., "Bitcoin on Bitcoin network vs Bitcoin on BSC")
- And the AI should ask which token and network I want to use
- And the AI should warn about network compatibility ("Make sure the recipient can receive Bitcoin on this network")
- And the AI should confirm my selection before proceeding
- And the AI should display my available balance for the selected token

### Story 5.3: Conversational Recipient Selection
**As a** user sending cryptocurrency  
**I want to** specify who I'm sending to through conversation  
**So that** I can easily provide recipient information without typing complex addresses  

**Acceptance Criteria:**
- Given I have selected the token to send
- When the AI asks for the recipient
- Then the AI should offer multiple ways to specify the recipient:
  - "I have their address" → guide to address input
  - "They're in my contacts" → show contact list
  - "I want to scan a QR code" → activate QR scanner
  - "I don't know their address" → explain how to get it
- And the AI should validate addresses for the selected network
- And the AI should warn if the address format doesn't match the selected network
- And the AI should show a shortened version of the address for confirmation
- And the AI should allow me to add recipients to contacts for future use

### Story 5.4: Conversational Amount Specification
**As a** user sending cryptocurrency  
**I want to** specify how much to send through conversation  
**So that** I can easily set the transfer amount in familiar terms  

**Acceptance Criteria:**
- Given I have specified the recipient
- When the AI asks for the amount
- Then the AI should allow me to specify amounts in different ways:
  - Token amount: "Send 0.5 Bitcoin"
  - Fiat equivalent: "Send $100 worth"
  - Percentage: "Send half my balance"
  - "Send all" for maximum amount
- And the AI should show both token amount and fiat equivalent for confirmation
- And the AI should validate I have sufficient balance including fees
- And the AI should warn if I'm sending a very large percentage of my balance
- And the AI should confirm the exact amount before proceeding

### Story 5.5: Conversational Fee Configuration (Bitcoin/EVM Networks)
**As a** user sending Bitcoin or EVM-based cryptocurrency  
**I want to** understand and configure transaction fees through conversation  
**So that** I can choose appropriate fees without technical complexity  

**Acceptance Criteria:**
- Given I am sending Bitcoin or EVM-based tokens
- When the AI discusses fees
- Then the AI should explain gas fees in simple terms ("network fees to process your transaction")
- And the AI should offer fee options in user-friendly terms:
  - "Fast" (higher fee, quicker confirmation)
  - "Standard" (medium fee, normal confirmation)
  - "Slow" (lower fee, slower confirmation)
- And the AI should show estimated confirmation times for each option
- And the AI should display fees in both token and fiat amounts
- And the AI should explain that fees go to network miners/validators, not the wallet
- And the AI should allow me to choose my preferred fee level
- And the AI should warn if fees are unusually high due to network congestion

### Story 5.6: Conversational Resource Management (Antelope Networks)
**As a** user sending Antelope-based cryptocurrency  
**I want to** handle resource requirements through conversation  
**So that** I can complete transactions even when I need additional resources  

**Acceptance Criteria:**
- Given I am sending EOS or other Antelope tokens
- When the AI checks my resources
- Then the AI should check my CPU/NET resources automatically
- And if I have sufficient resources, the AI should proceed without mentioning them
- And if I need additional resources, the AI should explain in simple terms:
  - "Your account needs more resources for this transaction"
  - "I can use a resource provider for a small fee"
- And the AI should offer resource provider options if available
- And the AI should explain any additional fees for using resource providers
- And the AI should get my confirmation before using paid resource providers
- And the AI should handle resource provider confirmation automatically after my approval

### Story 5.7: Conversational Transaction Review and Confirmation
**As a** user ready to send cryptocurrency  
**I want to** review all transaction details through conversation  
**So that** I can verify everything is correct before sending  

**Acceptance Criteria:**
- Given all transaction details have been specified
- When the AI presents the transaction summary
- Then the AI should clearly show all transaction details:
  - Token and amount being sent
  - Recipient address (shortened with "..." in middle)
  - Network being used
  - Total fees (gas fees or resource provider fees)
  - Final amount I'll have remaining
- And the AI should ask me to confirm each detail is correct
- And the AI should warn about irreversible nature of crypto transactions
- And the AI should require explicit confirmation like "Yes, send it" before proceeding
- And the AI should allow me to go back and change any detail if needed

### Story 5.8: Conversational Transaction Signing and Broadcasting
**As a** user confirming a transaction  
**I want to** securely sign and send my transaction through conversation  
**So that** I understand what's happening during the sending process  

**Acceptance Criteria:**
- Given I have confirmed the transaction details
- When the AI begins transaction signing
- Then the AI should explain what's happening: "Signing your transaction securely..."
- And the AI should handle key access securely (PIN/biometric if required)
- And the AI should sign the transaction locally without exposing keys
- And the AI should broadcast the transaction to the network
- And the AI should provide real-time updates: "Transaction sent! Waiting for confirmation..."
- And the AI should show the transaction ID/hash for reference
- And the AI should explain next steps and expected confirmation time

### Story 5.9: Conversational Transaction Success Handling
**As a** user who has sent cryptocurrency  
**I want to** receive confirmation and next steps through conversation  
**So that** I know my transaction was successful and what to expect  

**Acceptance Criteria:**
- Given my transaction has been successfully broadcast
- When the AI receives confirmation
- Then the AI should congratulate me on successful sending
- And the AI should show the transaction details one more time
- And the AI should provide the transaction ID for tracking
- And the AI should explain how to track the transaction status
- And the AI should update my balance to reflect the sent amount
- And the AI should offer to add the recipient to contacts if not already added
- And the AI should offer to set up notifications for transaction confirmation

### Story 5.10: Conversational Transaction Error Handling
**As a** user whose transaction failed  
**I want to** understand what went wrong and how to fix it through conversation  
**So that** I can successfully complete my transfer  

**Acceptance Criteria:**
- Given my transaction has failed or encountered an error
- When the AI detects the failure
- Then the AI should explain what went wrong in friendly, non-technical language
- And the AI should handle common issues like:
  - Insufficient balance (including fees)
  - Network congestion/high fees
  - Invalid recipient address
  - Resource limitations (Antelope)
  - Network connectivity issues
- And the AI should suggest specific solutions for each error type
- And the AI should offer to retry the transaction with corrections
- And the AI should reassure me that no funds were lost if the transaction failed
- And the AI should escalate to human support for persistent technical issues

### Story 5.11: Conversational Cold Wallet QR Flow
**As a** Cold wallet user  
**I want to** generate transaction QR codes through conversation  
**So that** I can sign transactions offline securely  

**Acceptance Criteria:**
- Given I am using the Cold wallet variant
- When I complete transaction details
- Then the AI should explain the Cold wallet process: "I'll generate a QR code for your UI wallet to scan"
- And the AI should generate an unsigned transaction QR code
- And the AI should display the QR code with clear instructions
- And the AI should explain what the UI wallet user needs to do
- And the AI should wait for the signed transaction to be scanned back
- And the AI should verify the signed transaction matches what was generated
- And the AI should broadcast the signed transaction to the network

### Story 5.12: Conversational Send to Contacts
**As a** user with saved contacts  
**I want to** easily send to people in my contacts through conversation  
**So that** I can quickly send to frequent recipients  

**Acceptance Criteria:**
- Given I have contacts saved in my wallet
- When I say "send to John" or "send to my contact"
- Then the AI should search my contacts for matches
- And the AI should show matching contacts with their saved addresses
- And the AI should confirm which contact I want to send to
- And the AI should display the contact's address and network for verification
- And the AI should proceed with the send flow using the contact's address
- And the AI should offer to update contact information if the address seems wrong

## Epic 6 User Stories

### Story 6.1: Conversational Receive Request and Account Selection
**As a** user who wants to receive cryptocurrency  
**I want to** tell the AI I need to receive crypto and select which account through conversation  
**So that** I can get the correct address without confusion about which wallet/account to use  

**Acceptance Criteria:**
- Given I have cryptocurrency accounts set up
- When I say something like "I want to receive Bitcoin" or "show me my EOS address" or "someone wants to send me crypto"
- Then the AI should understand I want to receive cryptocurrency
- And the AI should ask which cryptocurrency I want to receive (if not specified)
- And the AI should ask which network if the crypto exists on multiple networks
- And if I have multiple accounts/wallets for the same crypto, the AI should ask "Which account would you like to receive to?"
- And the AI should show my accounts with identifying information (account name, balance, or wallet name)
- And the AI should explain the importance of using the correct network and account
- And the AI should guide me to the appropriate receiving flow

### Story 6.2: Conversational Address Display and Explanation
**As a** user requesting a receiving address  
**I want to** get my address with clear explanations through conversation  
**So that** I understand how to safely share it with others  

**Acceptance Criteria:**
- Given I have specified which crypto and account I want to receive to
- When the AI provides my address
- Then the AI should display my receiving address clearly
- And the AI should confirm which account this address belongs to
- And the AI should explain what this address is for: "This is your Bitcoin address for Account 1 - anyone can send Bitcoin here"
- And the AI should distinguish between different address types:
  - Bitcoin/Ethereum: "This address starts with..." 
  - EOS: "This is your account name: yourname12345"
- And the AI should explain that addresses are safe to share publicly
- And the AI should warn about network compatibility: "Make sure they send Bitcoin on the Bitcoin network"
- And the AI should offer multiple ways to share the address (QR code, copy text, etc.)

### Story 6.3: Conversational QR Code Generation
**As a** user who wants to receive cryptocurrency in person  
**I want to** generate a QR code through conversation  
**So that** others can easily scan and send to my address  

**Acceptance Criteria:**
- Given I have my receiving address displayed for a specific account
- When I ask for a QR code or say "they want to scan it"
- Then the AI should generate a QR code for my selected account's address
- And the AI should explain what the QR code contains: "This QR code contains your Bitcoin address for Account 1"
- And the AI should display the QR code prominently
- And the AI should provide instructions: "Have them scan this with their wallet app"
- And the AI should offer to include an amount in the QR code if I specify one
- And the AI should allow me to regenerate the QR code with different parameters

### Story 6.4: Conversational Payment Request Creation
**As a** user requesting a specific amount  
**I want to** create a payment request through conversation  
**So that** I can specify exactly how much I need to receive to a specific account  

**Acceptance Criteria:**
- Given I want to receive a specific amount to a selected account
- When I say "I need them to send me $100 in Bitcoin" or specify an amount
- Then the AI should create a payment request with the specified amount for the selected account
- And the AI should show both the crypto amount and fiat equivalent
- And the AI should confirm which account will receive the funds
- And the AI should generate a QR code that includes the amount and correct address
- And the AI should explain that this makes it easier for the sender
- And the AI should allow me to modify the amount or switch accounts if needed
- And the AI should provide shareable text with all payment details

### Story 6.5: Conversational Address Sharing Options
**As a** user who needs to share my address  
**I want to** choose how to share my address through conversation  
**So that** I can send it to others in the most convenient way  

**Acceptance Criteria:**
- Given I have my receiving address ready for a specific account
- When the AI offers sharing options
- Then the AI should provide multiple sharing methods:
  - "Copy to clipboard" - for pasting into messages
  - "Share as QR code" - for in-person or image sharing
  - "Send via message" - if messaging integration available
  - "Show as text" - for manual copying
- And the AI should explain when to use each method
- And the AI should confirm which account's address is being shared
- And the AI should confirm successful copying/sharing
- And the AI should remind me about network compatibility when sharing

### Story 6.6: Conversational Multi-Account and Multi-Network Management
**As a** user with multiple accounts on multiple networks  
**I want to** manage different addresses through conversation  
**So that** I can receive the same asset on different networks and accounts safely  

**Acceptance Criteria:**
- Given I have multiple accounts and the same asset on multiple networks
- When someone wants to send me that asset
- Then the AI should first ask which account I want to receive to
- And the AI should ask which network they're using
- And the AI should explain the differences: "Bitcoin on Bitcoin network vs Bitcoin on BSC, and you have accounts on both"
- And the AI should provide the correct address for their network and my selected account
- And the AI should warn about sending to the wrong network: "Bitcoin sent to your BSC address will be lost"
- And the AI should offer to show addresses for all accounts and networks if needed
- And the AI should help educate the sender about network compatibility

### Story 6.7: Conversational Transaction Monitoring
**As a** user expecting to receive cryptocurrency  
**I want to** monitor incoming transactions through conversation  
**So that** I know when payments arrive to specific accounts without constantly checking  

**Acceptance Criteria:**
- Given I have shared my address for a specific account with someone
- When the AI detects incoming transactions
- Then the AI should notify me about pending incoming transactions to the specific account
- And the AI should specify which account is receiving: "Your Bitcoin Account 1 is receiving a payment!"
- And the AI should explain the confirmation process: "Your Bitcoin is on the way! Waiting for network confirmation..."
- And the AI should provide estimated confirmation times
- And the AI should update me when transactions are confirmed
- And the AI should show the final received amount in both crypto and fiat
- And the AI should offer to add the sender to my contacts if it's a new address

### Story 6.8: Conversational Receive History by Account
**As a** user who has received cryptocurrency across multiple accounts  
**I want to** review my receiving history through conversation  
**So that** I can track what I've received and to which accounts  

**Acceptance Criteria:**
- Given I have received cryptocurrency transactions across multiple accounts
- When I ask about my receiving history like "show me what I've received" or "who sent me money"
- Then the AI should ask if I want to see all accounts or a specific account
- And the AI should display my recent incoming transactions with account information
- And the AI should show transaction details in user-friendly format:
  - Amount received (crypto and fiat)
  - Account that received it
  - Date and time
  - Sender address (shortened)
  - Network used
  - Confirmation status
- And the AI should allow me to filter by account, cryptocurrency type, or date
- And the AI should offer to show transaction details on blockchain explorer

### Story 6.9: Conversational Address Security Education
**As a** user sharing addresses for the first time  
**I want to** learn about address security through conversation  
**So that** I understand how to safely receive cryptocurrency across multiple accounts  

**Acceptance Criteria:**
- Given I am new to receiving cryptocurrency or managing multiple accounts
- When the AI provides my address
- Then the AI should educate me about address security:
  - "Addresses are safe to share - they're like your bank account number"
  - "Never share your private keys or seed phrase"
  - "Double-check the network and account before giving your address"
  - "Each account has its own address - make sure you're sharing the right one"
- And the AI should explain that addresses can be reused safely
- And the AI should warn about common scams: "Never give your address to someone asking for fees upfront"
- And the AI should encourage questions and provide patient explanations
- And the AI should offer ongoing security reminders

### Story 6.10: Conversational Receive Error Handling
**As a** user having trouble receiving cryptocurrency  
**I want to** get help troubleshooting through conversation  
**So that** I can resolve issues and successfully receive funds to the correct account  

**Acceptance Criteria:**
- Given I am having trouble receiving cryptocurrency
- When issues arise with receiving
- Then the AI should help troubleshoot common problems:
  - "I shared my address but haven't received anything" → explain confirmation times, verify correct account
  - "They sent to the wrong network" → explain recovery options if any
  - "They sent to my other account by mistake" → help locate transaction
  - "The amount is different than expected" → explain network fees
  - "I can't find the transaction" → help check transaction status across accounts
- And the AI should help verify which account was supposed to receive the funds
- And the AI should provide step-by-step guidance for each issue
- And the AI should explain when funds might be recoverable vs permanently lost
- And the AI should escalate to human support for complex recovery scenarios

## Technical Requirements
- **Conversational AI Integration**: LLM must understand various ways users express sending intents
- **Multi-Network Support**: Handle different fee mechanisms (gas fees vs resource providers)
- **Address Validation**: Real-time validation of recipient addresses for different networks
- **Balance Checking**: Real-time balance and fee calculation
- **Resource Provider Integration**: Integration with Antelope resource provider services
- **Gas Fee Estimation**: Real-time gas fee estimation for Bitcoin/EVM networks
- **Transaction Broadcasting**: Integration with blockchain network APIs
- **QR Code Generation**: For Cold wallet variant transaction signing
- **Contact Management**: Integration with wallet contacts system
- **Security**: All signing operations must be performed locally
- **Error Recovery**: Comprehensive error handling for all failure scenarios
- **Multi-platform Support**: Conversational UI must work across all platforms

## Definition of Done
- [ ] User can successfully initiate send through conversation with AI
- [ ] AI correctly interprets various send intents and cryptocurrency names
- [ ] Token and account selection works with clear explanations
- [ ] Recipient specification works through multiple input methods
- [ ] Amount specification supports multiple formats (token, fiat, percentage)
- [ ] Fee configuration works differently for Bitcoin/EVM (gas) vs Antelope (resources)
- [ ] Resource provider integration works for Antelope networks
- [ ] Transaction review shows all details clearly before confirmation
- [ ] Transaction signing and broadcasting works securely
- [ ] Success confirmation provides useful information and next steps
- [ ] Error handling covers all common failure scenarios with helpful guidance
- [ ] Cold wallet QR flow works for offline transaction signing
- [ ] Contact integration allows easy sending to saved recipients
- [ ] Works across all build variants (Pro/Cold/UI) with appropriate limitations
- [ ] Integration tests pass for all send flows across different networks
- [ ] Security audit completed for transaction signing and key handling

## Conversational Interaction Examples

### Example Flow - Bitcoin Send:
```
User: "I want to send some Bitcoin to my friend"
AI: "I can help you send Bitcoin! I see you have Bitcoin on two networks: Bitcoin (main network) and BSC (cheaper fees). Which would you like to send?"

User: "The main Bitcoin network"
AI: "Great choice! You have 0.05 BTC ($2,150) available. How much would you like to send?"

User: "Send $500 worth"
AI: "That's about 0.0116 BTC. What's your friend's Bitcoin address?"

User: "I have it saved in contacts as 'Sarah'"
AI: "Found Sarah! Address: 1A2B...8X9Y. For network fees, you can choose: Fast ($15, ~10 min), Standard ($8, ~30 min), or Slow ($3, ~60 min). Which do you prefer?"

User: "Standard is fine"
AI: "Perfect! Here's your transaction summary:
     Sending: 0.0116 BTC ($500)
     To: Sarah (1A2B...8X9Y)
     Network Fee: $8
     You'll have: 0.0384 BTC remaining
     
     This transaction cannot be reversed. Confirm by saying 'Yes, send it'"
```

### Example Flow - EOS Send with Resource Provider:
```
User: "Send 100 EOS to myeosfriend"
AI: "I can send 100 EOS to myeosfriend! You have 500 EOS available. Let me check your account resources..."

AI: "Your account needs additional CPU resources for this transaction. I can use a resource provider for about $0.50. Is that okay?"

User: "Yes that's fine"
AI: "Great! Here's your transaction summary:
     Sending: 100 EOS ($85)
     To: myeosfriend
     Resource Provider Fee: $0.50
     You'll have: 400 EOS remaining
     
     Confirm by saying 'Yes, send it'"
```

## Related Use Cases
- UC16: Send Cryptocurrency (main conversational flow)
- UC19: Select Token for Operations (conversational token selection)
- UC20: Set Transaction Amount (conversational amount specification)
- UC21: Configure Transaction Fees (conversational fee configuration)
- UC22: Verify and Confirm Transaction (conversational transaction review)
- UC23: View Transaction Success (conversational success handling)
- UC59: Generate Transaction QR Code (Cold wallet integration)
- UC61: Sign Transactions Offline (Cold wallet signing)

## AI Assistant Capabilities Needed
- Natural language understanding for send intents and cryptocurrency names
- Amount parsing in multiple formats (token amounts, fiat values, percentages)
- Address validation and network compatibility checking
- Fee estimation and explanation for different network types
- Resource management for Antelope networks
- Transaction detail summarization and confirmation
- Error explanation and troubleshooting guidance
- Contact system integration
- Multi-step process management across different network types