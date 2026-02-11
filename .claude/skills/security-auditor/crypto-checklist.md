# Crypto Wallet Security Checklist

## 1. Key Management

### Storage
- [ ] Private keys encrypted at rest using platform keystore
- [ ] Android: AndroidKeyStore with hardware-backed keys (StrongBox if available)
- [ ] iOS: Keychain with kSecAttrAccessibleWhenUnlockedThisDeviceOnly
- [ ] Desktop: Encrypted file with user-derived key
- [ ] Recovery phrase never stored in plaintext
- [ ] Key material not in SharedPreferences / UserDefaults / plain files

### Lifecycle
- [ ] Keys loaded into memory only when needed
- [ ] Byte arrays zeroed after cryptographic operations (`array.fill(0)`)
- [ ] No key references held by long-lived objects (singletons, ScreenModel beyond operation)
- [ ] Key derivation uses BIP-32/BIP-39/BIP-44 standard paths
- [ ] Secure random source for all key generation (`SecureRandom`)

### Exposure Prevention
- [ ] No keys in log output (check all log levels: debug, info, warn, error)
- [ ] No keys in crash reports (Crashlytics, Sentry exclusion rules)
- [ ] No keys in analytics events
- [ ] No keys passed via Intent extras, Bundle, or Parcelable
- [ ] No keys in URL query parameters
- [ ] No keys displayed in UI (except during backup flow with explicit user action)
- [ ] Clipboard cleared after key-related paste operations

## 2. Transaction Safety

### Validation
- [ ] Address format validated per chain type before display and before signing
- [ ] Amount validated: positive, non-zero (unless zero-value tx), within balance, no overflow
- [ ] Chain ID validated: matches user's intended network
- [ ] Nonce validated: sequential, no gaps (EVM)
- [ ] Fee/gas parameters validated: reasonable bounds, not zero
- [ ] Token contract address validated against known token list (EVM ERC-20)

### Confirmation
- [ ] Transaction details shown to user BEFORE signing: recipient, amount, fee, network
- [ ] User must explicitly confirm (tap button, not auto-sign)
- [ ] PIN or biometric required before every signing operation
- [ ] Confirmation screen not skippable via back gesture
- [ ] Amount displayed in both token and fiat equivalent

### Signing
- [ ] Signing happens in isolated scope (not in UI thread)
- [ ] Signed transaction verified before broadcasting
- [ ] Raw transaction hex not logged or exposed
- [ ] Signing errors don't leak key material in error messages

## 3. Cold Variant (Air-Gap Integrity)

- [ ] Zero network dependencies: no Ktor, HttpClient, OkHttp, WebSocket imports
- [ ] No URL construction or DNS resolution
- [ ] No Bluetooth, NFC, WiFi, or USB data transfer
- [ ] Data exchange only via QR code (camera input, QR display output)
- [ ] QR payload contains ONLY the signed transaction, no metadata leaking device info
- [ ] Cold variant Gradle module dependency tree verified: no path to `data:remote`
- [ ] Build verification: Cold APK size significantly smaller than Pro (no network libs)

## 4. UI Variant (No-Signing Integrity)

- [ ] No private key access: no imports from `core:hdwallet` signing functions
- [ ] No key derivation code
- [ ] No signing operations
- [ ] Only broadcasts pre-signed transactions received via QR
- [ ] QR import validates signed transaction format before broadcasting

## 5. Network Security

- [ ] HTTPS for all API calls (no HTTP fallback)
- [ ] Certificate pinning for critical endpoints (Alchemy, Infura)
- [ ] API keys not in source code (in `local.properties` via BuildConfig)
- [ ] RPC response validation: don't trust node responses blindly
- [ ] Rate limiting on repeated requests
- [ ] Timeout configuration for all network calls
- [ ] No sensitive data in URL query parameters (use headers or body)

## 6. Input Sanitization

- [ ] QR code content validated before parsing
- [ ] Clipboard data validated before use
- [ ] Deep link parameters sanitized
- [ ] User text input sanitized (no SQL injection via SQLDelight parameterized queries)
- [ ] RPC response data validated before display

## 7. Platform Security

### Android
- [ ] ProGuard/R8 enabled for release builds
- [ ] `android:debuggable="false"` in release manifest
- [ ] `android:allowBackup="false"` (prevent adb backup of wallet data)
- [ ] Root detection (warn user, don't block)
- [ ] Screenshot prevention on sensitive screens (FLAG_SECURE)

### iOS
- [ ] App Transport Security enabled (enforces HTTPS)
- [ ] Jailbreak detection (warn user)
- [ ] Sensitive data excluded from iCloud backup

### Desktop
- [ ] Wallet data in user-only-readable directory
- [ ] Memory not swapped to disk (mlock if possible)

## 8. Dependency Security

- [ ] Regular dependency update checks
- [ ] Known CVE scan: `./gradlew dependencyCheckAnalyze`
- [ ] Minimal dependency principle: don't add unnecessary libraries
- [ ] Verify dependency sources (Maven Central, Google, trusted repos only)
- [ ] Lock file integrity (verify checksums)
