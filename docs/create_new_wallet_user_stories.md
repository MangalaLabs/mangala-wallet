# User Stories for Wallet Setup - Conversational UI

Based on the Mangala Wallet use case documentation, this document outlines user stories for wallet planning and traditional cryptocurrency wallet creation adapted for a conversational UI where users interact with an LLM assistant.

**Note:** Antelope account creation is covered in a separate document: `create_antelope_account_user_stories.md`

## Epic 1: Wallet Planning & Network Selection
As a new user, I want to discuss my crypto needs with an AI assistant so that I can understand different blockchain options and choose the right approach for my goals.

### Story 1.1: Conversational Network Discovery
**As a** new user interested in cryptocurrency  
**I want to** tell the AI what I want to do with crypto so it can guide me to the right blockchain networks  
**So that** I don't get overwhelmed by technical choices and pick networks that fit my needs  

**Acceptance Criteria:**
- Given I am a new user starting a conversation with the AI assistant
- When I say something like "I want to create a wallet" or "help me get started with crypto" or "I'm new here"
- Then the AI should ask about my crypto goals in simple terms like "What interests you about cryptocurrency?"
- And the AI should recognize responses about specific interests:
  - Gaming → suggest Antelope networks (EOS/WAX)
  - DeFi/Trading → suggest Ethereum or BSC
  - Store of value → suggest Bitcoin
  - General exploration → suggest multiple options
- And the AI should explain available networks (Bitcoin, Ethereum, Antelope) in user-friendly terms
- And the AI should adapt its language to my experience level
- And the AI should allow me to ask questions about different networks

### Story 1.2: Conversational Starter Recommendations
**As a** complete beginner to cryptocurrency  
**I want to** receive personalized recommendations from the AI  
**So that** I can start with a sensible setup without analysis paralysis  

**Acceptance Criteria:**
- Given I am unsure about which networks to choose
- When I ask for recommendations or say "I don't know"
- Then the AI should offer beginner-friendly recommendations like:
  - "For beginners, I recommend starting with Bitcoin - it's the most established and secure"
  - "If you're interested in apps and games, Antelope networks like EOS are great"
  - "For trading and DeFi, Ethereum is the most popular choice"
- And the AI should explain why each recommendation makes sense
- And the AI should offer to set up the recommended option or explain alternatives
- And the AI should let me change my mind later if I want to explore other options

### Story 1.3: Conversational Asset-Specific Education
**As a** user who has chosen a blockchain interest  
**I want to** understand my asset storage options through conversation  
**So that** I make informed decisions about where to hold my cryptocurrency  

**Acceptance Criteria:**
- Given I have expressed interest in specific cryptocurrencies (like Bitcoin)
- When the AI explains my options
- Then the AI should educate me about different ways to hold the same asset:
  - Bitcoin: "You can hold Bitcoin on the Bitcoin network (more secure, higher fees) or on other networks like BSC (cheaper, works with apps)"
  - Ethereum: "You can hold ETH on Ethereum mainnet (most secure) or on layer 2 networks (cheaper transactions)"
- And the AI should explain trade-offs in simple terms (security vs cost vs functionality)
- And the AI should ask follow-up questions to understand my priorities
- And the AI should recommend the best option based on my answers
- And the AI should confirm my choice before proceeding

## Epic 2: Traditional Crypto Wallet Creation (Bitcoin/Ethereum)
As a user who wants traditional cryptocurrency accounts, I want to create a secure wallet through conversation so that I can safely store and use Bitcoin, Ethereum, and similar cryptocurrencies.

### Story 2.1: Conversational Seed Phrase Education and Generation
**As a** user creating a traditional crypto wallet  
**I want to** have the AI explain and provide my recovery phrase through conversation  
**So that** I understand its importance and can safely record it  

**Acceptance Criteria:**
- Given I have chosen Bitcoin, Ethereum, or similar networks
- When the AI generates my wallet
- Then the AI should first explain what a seed phrase is using simple analogies
- And the AI should explain why it's different from Antelope accounts (which use account names)
- And the AI should ask if I have a secure way to write it down
- And the AI should warn me about security risks in conversational language
- And the AI should only show the seed phrase after I confirm I'm ready
- And the AI should present the 12/24 words clearly on-screen
- And the seed phrase should never be sent to any backend or external service
- And the AI should remind me that it cannot help me recover a lost phrase
- And the AI should emphasize that this phrase controls ALL my crypto on these networks

### Story 2.2: Secure Conversational Seed Phrase Verification
**As a** user with a new traditional crypto wallet  
**I want to** verify my recovery phrase securely without exposing words to the AI  
**So that** I can ensure I recorded it correctly while maintaining complete security  

**Acceptance Criteria:**
- Given I have been shown my seed phrase
- When the AI begins verification
- Then the AI should explain why verification is important in a friendly way
- And the AI should **NEVER** ask me to speak or type actual seed phrase words
- And the AI should guide me to use a secure local verification method such as:
  - Visual word selection from a grid/list displayed on device
  - Tap/click verification interface that doesn't send data to the AI
  - Local checksum or position-based verification
- And the verification process should happen entirely on-device without sending data to any backend
- And the AI should provide conversational guidance about the verification process without seeing the actual inputs
- And if verification fails, the AI should offer encouragement and allow retries
- And the AI should congratulate me when verification is complete
- And the AI should remind me that it never sees or stores my actual seed phrase words

### Story 2.3: Conversational Security Setup
**As a** user with a newly created traditional crypto wallet  
**I want to** set up device security through conversation with the AI assistant  
**So that** I understand why security is important and can set it up confidently  

**Acceptance Criteria:**
- Given I have successfully verified my seed phrase
- When the AI begins security setup
- Then the AI should explain why a PIN is important in simple terms
- And the AI should distinguish between wallet PIN (device access) and seed phrase (money access)
- And the AI should guide me to create a 6-digit PIN through conversation
- And the AI should ask me to confirm the PIN and explain why
- And the AI should ask about biometric authentication and explain the benefits
- And the AI should respect my choice if I decline biometric setup
- And the AI should confirm security is set up and explain what happens next
- And the AI should remind me about good security practices

### Story 2.4: Conversational Selected Network Account Setup
**As a** user with a secured traditional crypto wallet  
**I want to** have the AI set up accounts for my chosen networks through conversation  
**So that** I understand what accounts I have and how to use them  

**Acceptance Criteria:**
- Given I have completed security setup
- When the AI sets up my accounts
- Then the AI should create accounts only for the networks I previously selected
- And the AI should explain what's being created: "I'm setting up your Bitcoin address" or "Creating your Ethereum account"
- And the AI should show me my addresses and explain how they work
- And the AI should explain how to share addresses for receiving funds
- And the AI should explain the difference between addresses on different networks
- And the AI should warn me about sending crypto to the wrong network
- And the AI should offer to help me understand how to use each account type

## Shared Stories (Apply to Both Epics)

### Story S.1: Conversational Error Handling
**As a** user attempting to create any type of wallet  
**I want to** receive helpful explanations from the AI when something goes wrong  
**So that** I don't get frustrated and can easily continue the process  

**Acceptance Criteria:**
- Given I am creating a wallet and an error occurs
- When the wallet creation fails
- Then the AI should explain what went wrong in friendly, non-technical language
- And the AI should suggest specific steps to fix the problem
- And the AI should offer to retry automatically or ask if I want to try again
- And the AI should reassure me that my progress is saved where possible
- And the AI should escalate to human support if the error persists
- And the AI should learn from errors to prevent similar issues

### Story S.2: Conversational Education and Onboarding
**As a** first-time cryptocurrency user  
**I want to** learn about crypto and wallets through natural conversation with the AI  
**So that** I feel confident and educated about my new wallet  

**Acceptance Criteria:**
- Given I am creating my first wallet
- When I go through the creation process
- Then the AI should gauge my crypto knowledge level through conversation
- And the AI should provide explanations appropriate to my experience level
- And the AI should encourage questions and answer them patiently
- And the AI should provide analogies and simple examples for complex concepts
- And the AI should offer to explain more or skip details based on my interest
- And the AI should check my understanding periodically ("Does that make sense?")
- And the AI should offer ongoing support and education after wallet creation

### Story S.3: Conversational Wallet Variant Selection
**As a** user with different security needs  
**I want to** have the AI help me choose the right wallet type through conversation  
**So that** I get the security level that matches my needs without confusion  

**Acceptance Criteria:**
- Given I am starting wallet creation
- When the AI begins the setup process
- Then the AI should ask about my intended use case in simple terms
- And the AI should explain the differences between Pro, Cold, and UI variants conversationally
- And the AI should ask questions like "Do you plan to make transactions often?" or "How important is maximum security to you?"
- And the AI should recommend the appropriate variant based on my answers
- And the AI should explain what my chosen variant can and cannot do
- And the AI should offer to help me set up companion wallets if needed (e.g., Cold + UI setup)

## Technical Requirements
- **Conversational AI Integration**: LLM must handle natural language understanding for wallet creation intents
- **Intent Recognition**: System must recognize various ways users express wallet creation desires
- **Network-Specific Flows**: Different conversation paths for Bitcoin/Ethereum vs Antelope
- **Conversational State Management**: AI must maintain context throughout multi-step processes
- **Secure Communication**: All sensitive operations must be handled securely within conversational interface
- **Zero-Knowledge Verification**: Seed phrase verification must never send actual words to AI backend
- **Offline Capability**: Conversational wallet creation must work offline for Cold variant
- **Cryptographic Security**: Seed phrase generation must use cryptographically secure randomness
- **Multi-modal Input**: Support for both text and voice input
- **Context Awareness**: AI must understand user experience level and adapt explanations
- **Error Recovery**: Conversational error handling with natural language explanations
- **Multi-platform Support**: Conversational UI must work across Android, iOS, and Desktop
- **Integration**: Must integrate with existing authentication and blockchain systems

## Definition of Done
- [ ] User can successfully plan their wallet needs through conversation with AI
- [ ] AI correctly interprets various wallet creation intents and requirements
- [ ] Network selection works based on user goals and interests
- [ ] Beginner recommendations help new users make good choices
- [ ] Asset-specific education helps users understand their options
- [ ] Traditional crypto wallet creation works securely with conversational interface
- [ ] Conversational seed phrase explanation, display, and verification works securely
- [ ] Security setup (PIN/biometrics) works through conversational interface
- [ ] Selected network accounts are created with appropriate explanations
- [ ] AI adapts to different user experience levels appropriately
- [ ] Conversational error handling provides helpful, friendly guidance
- [ ] Works across all build variants (Pro/Cold/UI) with variant-specific conversations
- [ ] Educational content is delivered naturally through conversation
- [ ] Voice input works for wallet creation commands
- [ ] Conversation state is maintained throughout the entire flow
- [ ] Integration tests pass for conversational wallet creation flows
- [ ] Security audit completed for conversational cryptographic operations
- [ ] AI responses are tested for clarity and user friendliness
- [ ] Fallback to traditional UI available if conversational approach fails

## Conversational Interaction Examples

### Example Flow - Network Discovery:
```
User: "Hi, I want to create a wallet"
AI: "Great! I'd love to help you set up a crypto wallet. What interests you about cryptocurrency - are you looking to invest, try out apps and games, or just explore?"

User: "I want to try some crypto games"
AI: "Perfect! For gaming, I recommend Antelope networks like EOS and WAX - they're built for apps and have lots of great games. Would you like me to help you create an account on one of these networks?"

User: "What's the difference between them?"
AI: "Great question! EOS is more general-purpose with DeFi and games, while WAX specializes in gaming and NFTs. Both are fast and cheap to use. Which sounds more interesting to you?"
```

### Example Flow - Traditional Wallet Creation:
```
User: "I want to store Bitcoin safely"
AI: "Excellent choice! For Bitcoin, you have two main options: store it on the Bitcoin network (more secure, higher fees) or on other networks like BSC (cheaper, works with apps). Which fits your needs better?"

User: "I want the most secure option"
AI: "Perfect! I'll set up a Bitcoin wallet for you. This uses something called a 'seed phrase' - think of it as a master password for your Bitcoin. Do you have a safe place to write down 12 words?"

User: "Yes, I have a notebook"
AI: "Great! Never store this digitally or take photos. I'm generating your wallet now..."
```

## Related Use Cases
- UC5: Create New Wallet (now conversational with network selection)
- UC6: Backup Wallet with Recovery Phrase (integrated into conversation)
- UC2: Setup New PIN (conversational security setup)
- UC3: Enable Biometric Authentication (conversational biometric setup)
- UC10: Add New Account (conversational account creation)
- UC71: Select Account Type (conversational variant selection)

## AI Assistant Capabilities Needed
- Natural language understanding for crypto/blockchain terminology
- Educational explanation generation at appropriate complexity levels
- Network recommendation based on user goals and interests
- Security-conscious conversation patterns
- Error recovery and clarification skills
- Multi-step process management across different wallet types
- User experience level adaptation