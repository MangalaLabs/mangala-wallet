# Privacy Mode Integration Plan

## Overview
This document outlines the concrete integration plan for implementing the Privacy Mode feature into the existing Contact and Group screens in the addressbook module.

## Current State Analysis

### Key Components That Display Addresses:
1. **ContactListScreen** - Main screen with tabs (Favorites, Recent, Contacts, Groups, Tags)
2. **ContactDetailScreen** - Shows detailed contact info including wallet addresses  
3. **GroupDetailScreen** - Shows group members and their wallet addresses
4. **ContactItem** - List item component used in contact lists
5. **WalletAddressesSection** - Shows wallet addresses in contact detail
6. **AddressListItem** - Shows addresses in group detail screen

### Current Address Display Logic:
- `ContactModel.displayAddress()` - Already handles sensitive addresses with asterisks
- `WalletAddressEntity.displayWallet()` - Similar masking logic
- Basic sensitivity handling exists but no global privacy mode

## Integration Points

### 1. Privacy Mode Toggle Location

#### Primary Location: ContactListScreen Header
Add privacy toggle in the top navigation area of ContactListScreen:

```kotlin
// In ContactListScreen.kt - SendTokenHeader component
SendTokenHeader(
    onBackClick = onBackClicked,
    onMenuClick = { /* Handle menu click */ },
    addClick = fabAction,
    privacyModeEnabled = privacyViewModel.isEnabled.collectAsState().value,
    onPrivacyToggle = { privacyViewModel.toggle() }
)
```

#### Secondary Locations:
- Settings screen (future implementation)
- Quick settings in profile section

### 2. Screen-Specific Integrations

#### 2.1 ContactListScreen Changes

**Files to modify:**
- `/features/addressbook/src/commonMain/kotlin/com/mangala/wallet/features/addressbook/presentation/contact/list/ContactListScreen.kt`
- `/features/addressbook/src/commonMain/kotlin/com/mangala/wallet/features/addressbook/presentation/contact/list/ContactListScreenModel.kt`

**Changes:**
1. Inject PrivacyModeViewModel into ContactListScreenModel
2. Pass privacy state to all child components
3. Update ContactItem to use ObfuscatedAddress component

```kotlin
// ContactListScreenModel.kt
class ContactListScreenModel(
    // existing dependencies...
    private val privacyModeViewModel: PrivacyModeViewModel
) : BaseScreenModel() {
    
    val privacyModeEnabled = privacyModeViewModel.isEnabled
    
    fun togglePrivacyMode() {
        privacyModeViewModel.toggle()
    }
}
```

#### 2.2 ContactItem Component Updates

**File:** `/features/addressbook/src/commonMain/kotlin/com/mangala/wallet/features/addressbook/presentation/contact/recent/ContactItem.kt`

Replace current address display with ObfuscatedAddress:

```kotlin
// Replace lines 97-104 with:
ObfuscatedAddress(
    address = contact.walletAddress,
    isPrivacyModeEnabled = privacyModeEnabled,
    isSensitive = contact.isSensitive,
    onRevealRequest = { address ->
        // Handle authentication and reveal
        addressRevealAuthenticator.requestReveal(address) { success ->
            if (success) {
                // Address will be revealed temporarily
            }
        }
    },
    textStyle = TextStyle(
        fontSize = 14.sp,
        color = ColorsNew.primary_600
    ),
    modifier = Modifier.weight(1f, fill = false)
)
```

#### 2.3 ContactDetailScreen Integration

**Files to modify:**
- `/features/addressbook/src/commonMain/kotlin/com/mangala/wallet/features/addressbook/presentation/contact/detail/ContactDetailScreen.kt`
- `/features/addressbook/src/commonMain/kotlin/com/mangala/wallet/features/addressbook/presentation/contact/detail/WalletAddressesSection.kt`

**Changes in WalletAddressesSection:**
```kotlin
@Composable
fun WalletAddressesSection(
    walletAddresses: List<WalletAddressWithBlockchainModel>,
    privacyModeEnabled: Boolean, // Add this parameter
    onRevealAddress: (String) -> Unit, // Add for reveal handling
    // existing parameters...
) {
    // In BlockchainAddressItem, replace address text with:
    ObfuscatedAddress(
        address = walletAddress.address,
        isPrivacyModeEnabled = privacyModeEnabled,
        isSensitive = walletAddress.isSensitive,
        onRevealRequest = onRevealAddress,
        // styling...
    )
}
```

#### 2.4 GroupDetailScreen Integration  

**Files to modify:**
- `/features/addressbook/src/commonMain/kotlin/com/mangala/wallet/features/addressbook/presentation/group/detail/GroupDetailScreenNew.kt`
- `/features/addressbook/src/commonMain/kotlin/com/mangala/wallet/features/addressbook/presentation/group/detail/AddressListItem.kt`

**Changes in AddressListItem:**
```kotlin
@Composable
fun AddressListItem(
    // existing parameters...
    privacyModeEnabled: Boolean,
    onRevealAddress: (String) -> Unit
) {
    // Replace address Text (lines 117-122) with:
    ObfuscatedAddress(
        address = fullWalletAddress,
        displayAddress = walletAddress, // Already shortened
        isPrivacyModeEnabled = privacyModeEnabled,
        isSensitive = false, // Groups might not have individual sensitivity
        onRevealRequest = onRevealAddress
    )
}
```

### 3. Header Component Updates

Create new privacy toggle component for the header:

**New file:** `/features/addressbook/src/commonMain/kotlin/com/mangala/wallet/features/addressbook/presentation/components/PrivacyToggleButton.kt`

```kotlin
@Composable
fun PrivacyToggleButton(
    isEnabled: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onToggle,
        modifier = modifier
    ) {
        Icon(
            imageVector = if (isEnabled) Icons.Default.VisibilityOff else Icons.Default.Visibility,
            contentDescription = if (isEnabled) "Disable Privacy Mode" else "Enable Privacy Mode",
            tint = ColorsNew.primary_900
        )
    }
}
```

### 4. Authentication Flow Integration

**New file:** `/features/addressbook/src/commonMain/kotlin/com/mangala/wallet/features/addressbook/presentation/privacy/AddressRevealDialog.kt`

```kotlin
@Composable
fun AddressRevealDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onAuthenticate: () -> Unit,
    authenticator: AddressRevealAuthenticator
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Authentication Required") },
            text = { Text("Please authenticate to reveal this address") },
            confirmButton = {
                TextButton(onClick = {
                    authenticator.authenticate { success ->
                        if (success) onAuthenticate()
                        onDismiss()
                    }
                }) {
                    Text("Authenticate")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }
}
```

### 5. State Management

**Update ContactListScreenModel:**
```kotlin
class ContactListScreenModel(
    // dependencies...
) : BaseScreenModel() {
    
    // Reveal state management
    private val _revealedAddresses = MutableStateFlow<Set<String>>(emptySet())
    val revealedAddresses = _revealedAddresses.asStateFlow()
    
    fun revealAddress(address: String) {
        viewModelScope.launch {
            _revealedAddresses.update { it + address }
            // Auto-hide after timeout
            delay(30_000) // 30 seconds
            _revealedAddresses.update { it - address }
        }
    }
}
```

### 6. Dependency Injection Updates

**File:** `/features/addressbook/src/commonMain/kotlin/com/mangala/wallet/features/addressbook/di/AddressBookModule.kt`

Ensure PrivacyModeViewModel is available:
```kotlin
// Add to existing module
single { PrivacyModeViewModel(get(), get(), get()) }
single { AddressRevealAuthenticator(get(), get()) }
```

## Implementation Steps

### Phase 1: Core Components (Priority 1)
1. ✅ Create ObfuscatedAddress component
2. ✅ Create PrivacyModeToggle component  
3. ✅ Create PrivacyModeViewModel
4. ✅ Create AddressRevealAuthenticator

### Phase 2: Contact List Integration (Priority 2)
1. Add privacy toggle to ContactListScreen header
2. Update ContactItem to use ObfuscatedAddress
3. Update ProfileCard in ContactsContent
4. Add reveal functionality with authentication

### Phase 3: Detail Screen Integration (Priority 3)
1. Update ContactDetailScreen to pass privacy state
2. Modify WalletAddressesSection to use ObfuscatedAddress
3. Update BlockchainAddressItem component
4. Add reveal dialog handling

### Phase 4: Group Screen Integration (Priority 4)
1. Update GroupDetailScreenNew with privacy support
2. Modify AddressListItem to use ObfuscatedAddress
3. Add batch reveal option for groups
4. Update group member list components

### Phase 5: Additional Features (Priority 5)
1. Add privacy mode indicator/badge
2. Implement auto-hide after reveal timeout
3. Add privacy mode tips/onboarding
4. Create settings screen integration

## Testing Checklist

### Unit Tests
- [ ] PrivacyModeViewModel state management
- [ ] ObfuscatedAddress display logic
- [ ] AddressRevealAuthenticator flow
- [ ] Screen model integration

### UI Tests
- [ ] Privacy toggle interaction
- [ ] Address obfuscation display
- [ ] Reveal authentication flow
- [ ] Auto-hide functionality

### Integration Tests
- [ ] End-to-end privacy mode flow
- [ ] State persistence across screens
- [ ] Multiple address reveal handling
- [ ] Error case handling

## Migration Considerations

1. **Existing Sensitive Addresses**: Current `isSensitive` flag should work with privacy mode
2. **User Preferences**: Privacy mode state should persist across app sessions
3. **Performance**: Lazy loading for revealed addresses to avoid memory issues
4. **Accessibility**: Ensure screen readers handle obfuscated addresses properly

## Security Considerations

1. **Authentication**: Reuse existing biometric/PIN authentication
2. **Timeout**: Revealed addresses auto-hide after 30 seconds
3. **Memory**: Clear revealed addresses from memory on screen exit
4. **Audit**: Log privacy mode toggles and reveal attempts

## Next Steps

1. Review and approve this integration plan
2. Create feature branch: `feature/privacy-mode-integration`
3. Implement Phase 1 components (already done)
4. Begin Phase 2 implementation with ContactListScreen
5. Progressively implement remaining phases
6. Conduct thorough testing
7. Create user documentation