# Mangala Wallet - Voice & Tone Guidelines

## Brand Voice

Mangala Wallet speaks with four qualities:

### 1. Clear
- No jargon without explanation
- Technical terms used precisely and consistently
- One idea per sentence
- Active voice preferred

**Do**: "Enter the recipient's address"
**Don't**: "Input the destination wallet address for the transaction recipient"

### 2. Confident
- Assertive but not arrogant
- The wallet knows what it's doing
- Avoids hedging ("maybe", "might", "we think")
- States facts directly

**Do**: "Transaction sent"
**Don't**: "Your transaction has been successfully submitted and should be processed soon"

### 3. Calm
- Especially in error states
- Never alarming or panic-inducing
- Always provides a next step
- Reassures about fund safety when relevant

**Do**: "Could not connect to network. Check your connection and try again."
**Don't**: "ERROR: Network connection failed. Transaction may be lost."

### 4. Respectful
- User's money, user's choice
- Inform, don't lecture
- No exclamation marks in errors
- No condescending explanations

**Do**: "This action cannot be undone."
**Don't**: "WARNING: Are you SURE you want to do this? This is very dangerous!"

## Tone by Context

| Context | Tone | Approach |
|---------|------|----------|
| **Success** | Warm, brief | State what happened. No celebration overkill. |
| **Error** | Calm, actionable | What happened + what to do. Never blame user. |
| **Warning** | Direct, clear | State consequences. No scary language. |
| **Confirmation** | Precise, complete | Show all details. No ambiguity about what will happen. |
| **Loading** | Informative | Say what's loading, not just "Loading..." |
| **Empty state** | Helpful, inviting | Explain why empty + give a clear CTA. |
| **Onboarding** | Welcoming, guiding | Short steps. Progress indication. Skip option. |
| **Destructive** | Serious, explicit | Name the action. State irreversibility. Require conscious choice. |

## Formatting Rules

### Capitalization
- **Sentence case** everywhere: "Send transaction" not "Send Transaction"
- Exception: proper nouns (Ethereum, Bitcoin, Mangala)
- Exception: abbreviations (ETH, BTC, EOS)

### Punctuation
- No period for single-sentence labels or buttons
- Period for multi-sentence body text
- No exclamation marks in errors or warnings
- Question mark for confirmation titles ("Send 0.5 ETH?")

### Numbers & Currency
- Crypto: use symbol after amount: "0.5 ETH", "100 USDT"
- Fiat: use symbol before amount: "$50.00", "50,000 VND"
- Addresses: truncate with ellipsis: "0x1234...5678"
- Large numbers: use comma separator: "1,234,567"
- Decimal precision: match token decimals, max 8 shown

### Buttons & Actions
- Use verbs for primary actions: "Send", "Sign", "Confirm"
- Be specific: "Confirm and send" not just "Confirm"
- Cancel labels: "Cancel", "Not now", "Keep wallet" (specific to context)
- Destructive actions: name the action: "Remove wallet" not "Delete"

## Variant Voice Adjustments

| Element | Pro | Cold | UI |
|---------|-----|------|-----|
| **Terminology** | Technical OK (gas, nonce, wei) | Security-focused (air-gapped, offline signing) | Simplified (fee, send) |
| **Detail level** | Full details available on demand | Signing details always prominent | Essential info only |
| **Warnings** | Standard confirmation | Extra security confirmations + verification prompts | Standard confirmation |
| **Tone** | Power user, efficient | Security-conscious, careful | Simple, approachable |
