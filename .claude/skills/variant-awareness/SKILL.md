---
name: variant-awareness
description: Build variant rules for Mangala Wallet Pro/Cold/UI modes - module placement, feature isolation, variant-specific implementations. Auto-applies when editing flavor modules or build configuration.
user-invocable: false
---

# Variant Awareness

Mangala Wallet has three build variants controlled by `currentFlavor` in `gradle.properties`. Every code change must consider variant impact.

## Variant Capabilities

| Capability | Pro | Cold | UI |
|-----------|-----|------|-----|
| Network access | YES | **NO** | YES |
| Private key access | YES | YES | **NO** |
| Transaction signing | YES | YES | **NO** |
| Transaction broadcasting | YES | **NO** | YES |
| Full blockchain interaction | YES | **NO** | **NO** |
| QR code export (signed tx) | NO | YES | NO |
| QR code import (signed tx) | NO | NO | YES |

## Module Naming Convention

```
features/
├── feature_base/    # Shared code used by ALL variants
├── feature_pro/     # Pro-specific implementation
├── feature_cold/    # Cold-specific implementation (no network)
└── feature_ui/      # UI-specific implementation (no signing)
```

**Rule**: Shared logic goes in `_base`. Variant-specific behavior goes in `_pro`/`_cold`/`_ui`.

## Dependency Rules

```kotlin
// In composeApp/build.gradle.kts:
implementation(project(":features:wallet_${currentFlavor}"))
// This resolves to :features:wallet_pro, :features:wallet_cold, or :features:wallet_ui
```

**CRITICAL constraints**:
- `_cold` modules must NEVER depend on `data:remote` or any network module
- `_ui` modules must NEVER depend on `core:hdwallet`, `core:security` signing functions, or key management
- `_base` modules must contain ONLY code that works in ALL variants
- `_base` can define interfaces; variants provide implementations

## When Editing Code

### Adding a new feature
1. Ask: Does this feature behave differently across variants?
   - YES → Create `_base` + variant modules
   - NO → Put in shared module (domain, common, core)

### Modifying existing feature
1. Check: Is there a `_base` and variant split for this feature?
2. If YES: Make shared changes in `_base`, variant-specific in `_pro`/`_cold`/`_ui`
3. If NO: Consider if the change should be variant-aware

### Adding a dependency
1. Network dependency (Ktor, HTTP, WebSocket) → NEVER in `_cold` modules
2. Signing dependency (secp256k1, key manager) → NEVER in `_ui` modules
3. UI-only dependency (Compose) → OK in all variants

## Common Mistakes to Avoid

1. **Network call in _base**: If `_base` makes a network call, Cold variant breaks
2. **Signing in _base**: If `_base` signs transactions, UI variant breaks
3. **Missing variant module**: Adding a feature in `_pro` but forgetting `_cold` and `_ui`
4. **Hardcoded variant check**: Don't use `if (isPro)` in shared code. Use interfaces and DI instead
