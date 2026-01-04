# PrivacyAwareAddressText Usage Guide

## Overview

`PrivacyAwareAddressText` is a reusable Compose component that automatically obfuscates cryptocurrency addresses based on privacy settings. It serves as a drop-in replacement for `Text` components displaying addresses throughout the application.

## Features

- **Automatic Obfuscation**: Addresses are automatically obfuscated when privacy mode is enabled or marked as sensitive
- **Multiple Display Modes**: Support for FULL, HIDDEN, and SECRET display modes
- **Customizable Patterns**: Choose from dots (••••), asterisks (****), or bullets for obfuscation
- **Full Text Styling**: Supports all standard Text component properties
- **Performance Optimized**: Built-in caching for efficient rendering
- **Easy Integration**: Simple drop-in replacement for existing Text components

## Basic Usage

### Simple Address Display

```kotlin
@Composable
fun MyScreen(privacyModeEnabled: Boolean) {
    val address = "0x742d35Cc6634C0532925a3b844Bc9e7595f6b123"
    
    PrivacyAwareAddressText(
        address = address,
        privacyModeEnabled = privacyModeEnabled
    )
}
```

### With Styling

```kotlin
PrivacyAwareAddressText(
    address = walletAddress,
    privacyModeEnabled = viewModel.privacyMode,
    style = MaterialTheme.typography.bodyMedium,
    color = MaterialTheme.colorScheme.primary,
    maxLines = 1,
    overflow = TextOverflow.Ellipsis
)
```

### Always Sensitive Address

```kotlin
PrivacyAwareAddressText(
    address = sensitiveAddress,
    privacyModeEnabled = privacyMode,
    isSensitive = true  // Always obfuscated when true
)
```

## Display Modes

### HIDDEN Mode (Default)
Shows partial address information:
- Short addresses: `0x12••••`
- Medium addresses: `0x1234••••5678`
- Long addresses: `0x123456••••abcdef`

```kotlin
PrivacyAwareAddressText(
    address = address,
    privacyModeEnabled = true,
    displayMode = DisplayMode.HIDDEN
)
```

### SECRET Mode
Shows a fixed pattern revealing no information:
- Ethereum: `0x••••••••••••••••••••••••••••••••••••••••`
- Bitcoin: `bc1••••••••••••••••••••••••••••••••••••••`

```kotlin
PrivacyAwareAddressText(
    address = address,
    privacyModeEnabled = true,
    displayMode = DisplayMode.SECRET
)
```

### FULL Mode
Currently also obfuscates addresses (as per business requirements):

```kotlin
PrivacyAwareAddressText(
    address = address,
    privacyModeEnabled = true,
    displayMode = DisplayMode.FULL
)
```

## Obfuscation Patterns

### Dots Pattern
```kotlin
PrivacyAwareAddressText(
    address = address,
    privacyModeEnabled = true,
    obfuscationPattern = ObfuscationPattern.DOTS
)
// Output: 0x74••••b123
```

### Asterisks Pattern
```kotlin
PrivacyAwareAddressText(
    address = address,
    privacyModeEnabled = true,
    obfuscationPattern = ObfuscationPattern.ASTERISKS
)
// Output: 0x74****b123
```

### Bullets Pattern (Default)
```kotlin
PrivacyAwareAddressText(
    address = address,
    privacyModeEnabled = true,
    obfuscationPattern = ObfuscationPattern.BULLETS
)
// Output: 0x74••••b123
```

## Real-World Examples

### Transaction List Item
```kotlin
@Composable
fun TransactionItem(
    transaction: Transaction,
    privacyModeEnabled: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("From", style = MaterialTheme.typography.labelSmall)
                PrivacyAwareAddressText(
                    address = transaction.fromAddress,
                    privacyModeEnabled = privacyModeEnabled,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                "${transaction.amount} ${transaction.currency}",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
```

### Address Book Entry
```kotlin
@Composable
fun AddressBookItem(
    contact: Contact,
    privacyModeEnabled: Boolean
) {
    ListItem(
        headlineContent = {
            Text(contact.name)
        },
        supportingContent = {
            PrivacyAwareAddressText(
                address = contact.walletAddress,
                privacyModeEnabled = privacyModeEnabled,
                isSensitive = contact.isSensitive,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    )
}
```

### Copy Address Button
```kotlin
@Composable
fun CopyAddressButton(
    address: String,
    privacyModeEnabled: Boolean,
    onCopy: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .clickable { onCopy(address) }
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
        PrivacyAwareAddressText(
            address = address,
            privacyModeEnabled = privacyModeEnabled,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
```

## Extension Function

Use the extension function for more concise code:

```kotlin
@Composable
fun MyComponent(address: String, privacyMode: Boolean) {
    address.toPrivacyAwareText(
        privacyModeEnabled = privacyMode,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.primary
    )
}
```

## Migration Guide

### Before (Plain Text)
```kotlin
Text(
    text = walletAddress,
    style = MaterialTheme.typography.bodyMedium,
    maxLines = 1,
    overflow = TextOverflow.Ellipsis
)
```

### After (Privacy-Aware)
```kotlin
PrivacyAwareAddressText(
    address = walletAddress,
    privacyModeEnabled = viewModel.privacyModeEnabled,
    style = MaterialTheme.typography.bodyMedium,
    maxLines = 1,
    overflow = TextOverflow.Ellipsis
)
```

## Advanced Usage

### With Text Layout Callback
```kotlin
PrivacyAwareAddressTextWithLayout(
    address = address,
    privacyModeEnabled = privacyModeEnabled,
    onTextLayout = { layoutResult ->
        // Handle text layout measurements
    }
)
```

### Conditional Sensitivity
```kotlin
PrivacyAwareAddressText(
    address = address,
    privacyModeEnabled = privacyModeEnabled,
    isSensitive = contact.isVIP || address.isHighValue(),
    displayMode = if (contact.isVIP) DisplayMode.SECRET else DisplayMode.HIDDEN
)
```

## Best Practices

1. **Consistent Privacy State**: Use a shared privacy mode state across your app
2. **Appropriate Display Modes**: Use SECRET mode for highly sensitive addresses
3. **Styling Consistency**: Apply consistent styling across all address displays
4. **Performance**: The component uses caching automatically - no additional optimization needed
5. **Accessibility**: Obfuscated addresses are still selectable and copyable with the full address value

## Testing

The component is fully tested. When writing tests for screens using this component:

```kotlin
// Test that address is obfuscated when privacy mode is on
composeTestRule.setContent {
    PrivacyAwareAddressText(
        address = "0x12345",
        privacyModeEnabled = true,
        modifier = Modifier.testTag("address")
    )
}

composeTestRule
    .onNodeWithTag("address")
    .assertTextContains("••••", substring = true)
```

## Troubleshooting

### Address Not Obfuscating
- Check that `privacyModeEnabled` is true
- Verify the address isn't empty
- Check if `isSensitive` is set correctly

### Custom Pattern Not Applying
- Ensure you're using the correct `ObfuscationPattern` enum value
- Check that privacy mode is enabled

### Performance Issues
- The component uses automatic caching
- Call `AddressObfuscator.clearCache()` only when necessary (e.g., theme changes)