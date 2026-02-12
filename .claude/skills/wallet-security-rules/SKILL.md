---
name: wallet-security-rules
description: Security rules for crypto wallet code - private key handling, signing operations, transaction safety, air-gap integrity, address validation. Auto-applies when editing security-sensitive code.
user-invocable: false
---

# Wallet Security Rules

You are working on a **cryptocurrency wallet** that handles real user funds. These rules are NON-NEGOTIABLE when writing or editing code that touches cryptographic operations, key management, signing, or transaction handling.

## Private Key Rules

1. **Never log keys**: No `println`, `Log.d`, `Timber`, or any logging of private keys, mnemonics, seeds, or derived keys
2. **Never serialize keys to disk unencrypted**: Keys at rest must use platform keystore (Android Keystore / iOS Keychain)
3. **Minimize key lifetime in memory**: Zero out byte arrays after use. Don't hold key references in long-lived objects (ScreenModel, singleton)
4. **Never pass keys via Intent/Bundle/Parcelable**: Use in-memory references scoped to the operation
5. **Never include keys in crash reports or analytics**: Check that error handlers don't capture key-containing variables

## Signing Operation Rules

1. **Always gate signing with PIN/biometric**: Every sign operation must re-authenticate
2. **Display transaction details BEFORE signing**: User must see and confirm recipient, amount, chain, fee
3. **Validate transaction parameters before signing**: Check address format, amount bounds, nonce, chain ID
4. **Never auto-sign**: Every transaction requires explicit user confirmation
5. **Use secure random for nonce generation**: `java.security.SecureRandom` or platform equivalent

## Cold Variant Isolation (CRITICAL)

The Cold variant is an **air-gapped signing device**. Violations here are CRITICAL severity:

1. **Zero network access**: Cold variant code must NEVER import or reference Ktor, HttpClient, WebSocket, or any network class
2. **No URL construction**: Cold variant must never construct URLs or endpoints
3. **Data transfer only via QR code**: Camera scan in, QR display out. No Bluetooth, NFC, USB
4. **Verify at build time**: Cold variant Gradle module must NOT depend on `data:remote`

## Input Validation

1. **Address validation**: Validate format per chain type before ANY operation
   - EVM: `0x` prefix + 40 hex chars, checksum validation (EIP-55)
   - Bitcoin: Bech32 or Base58Check validation
   - Antelope: 12-char account name validation
2. **Amount validation**: Check for overflow, negative values, zero, dust limits
3. **Chain ID validation**: Ensure chain ID matches intended network
4. **Sanitize all external input**: RPC responses, QR code content, clipboard data

## API Key Protection

1. **Never hardcode API keys**: All keys in `local.properties` (gitignored)
2. **Access via BuildConfig**: `BuildConfig.ALCHEMY_API_KEY`, never string literals
3. **Check before commit**: Scan for patterns like `"sk-"`, `"key-"`, `"0x"` + 64 hex chars in source files

## When You Encounter Security-Sensitive Code

If you're editing code in these modules, apply extra scrutiny:
- `core/security/` - Cryptographic operations
- `core/hdwallet/` - Key derivation
- `core/auth/` - Authentication
- `core/pin/` - PIN management
- `core/biometry/` - Biometric auth
- `antelope/keymanager/` - Antelope key management
- Any file containing `sign`, `encrypt`, `decrypt`, `mnemonic`, `seed`, `privateKey`

**Flag to user** if you notice: keys in logs, unencrypted key storage, missing auth gates, network calls in Cold variant code.
