---
name: moko-strings-guide
description: Moko Resources string management for Mangala Wallet - XML string format, i18n parameterization, resource file locations, localization best practices. Auto-applies when editing string resources or user-facing text.
user-invocable: false
---

# Moko Resources String Guide

Mangala Wallet uses **Moko Resources** (`SharedMR`) for shared string resources across platforms.

## File Locations

```
common/mokoresources/src/commonMain/moko-resources/
├── base/
│   └── strings.xml          # English (default)
├── vi/
│   └── strings.xml          # Vietnamese
└── files/
    └── mangalawallet.db     # Prepopulated SQLite database
```

## String Format

```xml
<!-- Simple string -->
<string name="screen_title">Send</string>

<!-- Parameterized string (positional) -->
<string name="balance_display">%1$s %2$s</string>
<!-- Usage: SharedMR.strings.balance_display.format("0.5", "ETH") -->

<!-- Parameterized string (named in code, positional in XML) -->
<string name="insufficient_funds">Not enough %1$s. Available: %2$s %1$s</string>

<!-- Plurals -->
<plurals name="token_count">
    <item quantity="one">%d token</item>
    <item quantity="other">%d tokens</item>
</plurals>
```

## Naming Convention

```
<feature>_<screen>_<element>_<qualifier>
```

Examples:
```xml
<string name="send_title">Send</string>
<string name="send_address_label">Recipient address</string>
<string name="send_address_error_invalid">Invalid address format</string>
<string name="send_amount_placeholder">0.00</string>
<string name="send_confirm_button">Confirm and send</string>

<!-- Common/shared strings -->
<string name="common_cancel">Cancel</string>
<string name="common_retry">Try again</string>
<string name="common_loading">Loading...</string>
<string name="error_network">Could not connect. Check your connection and try again.</string>
```

## Code Usage

```kotlin
import com.mangala.wallet.SharedMR

// In composable
Text(text = stringResource(SharedMR.strings.send_title))

// With parameters
Text(text = stringResource(SharedMR.strings.balance_display, amount, symbol))
```

## i18n Rules

1. **Never concatenate strings**: Use parameterized strings (`%1$s`) instead of `"Hello " + name`
2. **Never hardcode user-facing text**: All visible text must come from `strings.xml`
3. **No embedded numbers/currencies**: Use parameters for dynamic values
4. **Keep strings short**: Some languages expand 30-50% (German, Finnish)
5. **Avoid idioms**: Phrases like "piece of cake" don't translate well
6. **Escape apostrophes**: Use `\'` in XML strings (`<string name="x">can\'t</string>`)
7. **Use sentence case**: "Send transaction" not "Send Transaction" (title case varies by language)
