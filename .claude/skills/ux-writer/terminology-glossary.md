# Mangala Wallet - Terminology Glossary

Use these terms consistently across ALL screens. Never mix synonyms.

## Core Terms

| Preferred Term | Definition | Avoid |
|---------------|-----------|-------|
| **Wallet** | The user's wallet on this device | Account, Vault, Portfolio (for the wallet itself) |
| **Recovery phrase** | The 12/24 word backup phrase | Seed phrase, Mnemonic, Secret phrase (in UI) |
| **Address** | A blockchain address (receiving endpoint) | Public key (in UI), Wallet address |
| **Private key** | The signing key (only in advanced/technical contexts) | Secret key (in UI) |

## Actions

| Preferred Term | Context | Avoid |
|---------------|---------|-------|
| **Send** | Transfer tokens to another address | Transfer, Move, Pay |
| **Receive** | Get tokens from another address | Deposit, Accept |
| **Sign** | Cryptographically sign a transaction (Cold variant) | Authorize, Approve (for signing) |
| **Broadcast** | Send signed transaction to network (UI variant) | Submit, Push, Publish |
| **Import** | Add existing wallet via recovery phrase or private key | Restore, Recover, Add |
| **Create** | Generate new wallet | Set up, Initialize |
| **Remove** | Remove wallet from this device (keys may still exist elsewhere) | Delete (implies permanent), Erase |
| **Back up** | Save recovery phrase | Export (for backup context) |
| **Export** | Export signed transaction as QR (Cold variant) | Share (for QR export) |

## Blockchain

| Preferred Term | Context | Avoid |
|---------------|---------|-------|
| **Network** | Blockchain network (in UI) | Chain (technical OK, avoid in user-facing) |
| **Token** | Any cryptocurrency asset | Coin (OK for native), Currency, Asset |
| **Fee** | Transaction fee (in UI) | Gas (OK in EVM technical context, avoid as default label) |
| **Balance** | Amount of tokens held | Holdings, Funds |
| **Transaction** | A blockchain transaction | Transfer (as noun), Tx (in UI) |
| **Block explorer** | Web tool to view transactions | Blockchain scanner |

## States & Feedback

| Preferred Term | Context | Avoid |
|---------------|---------|-------|
| **Pending** | Transaction submitted, not confirmed | Processing, In progress |
| **Confirmed** | Transaction included in block | Completed, Done, Successful |
| **Failed** | Transaction rejected by network | Error (use for app errors, not tx status) |
| **Offline** | No network connectivity | Disconnected, No internet |

## Security

| Preferred Term | Context | Avoid |
|---------------|---------|-------|
| **PIN** | Numeric access code | Password, Passcode |
| **Biometrics** | Fingerprint or face authentication | Face ID, Touch ID (platform-specific OK in context) |
| **Air-gapped** | Cold variant: no network connectivity | Offline signing (OK as explanation) |
| **Verify** | Check/confirm information before acting | Validate (technical) |

## Chain-Specific Terms

### EVM (Ethereum, Polygon, etc.)
| Term | Usage |
|------|-------|
| Gas fee | OK in EVM-specific context, otherwise use "Fee" |
| Wei/Gwei | Technical only, never in user-facing primary text |
| Smart contract | OK when relevant |
| ERC-20 | OK in token details, not in primary UI |

### Bitcoin
| Term | Usage |
|------|-------|
| Satoshi/sats | OK in Bitcoin-specific context |
| UTXO | Technical only, never user-facing |
| SegWit | Technical only |

### Antelope (EOS)
| Term | Usage |
|------|-------|
| Account name | Use instead of "address" for Antelope |
| RAM/CPU/NET | OK in Antelope resource context |
| Action | Use instead of "transaction" for Antelope operations |

## Rules

1. **Consistency over creativity**: Same concept = same word everywhere
2. **User-facing vs technical**: Some terms are OK in technical contexts (logs, developer docs) but not in UI
3. **When introducing new terms**: Add to this glossary first, then use consistently
4. **Variant awareness**: Cold variant uses security-focused terms; UI variant uses simplified terms
5. **Chain awareness**: Some terms change by chain (EVM "gas" vs generic "fee"). Use generic in multi-chain contexts
