# Privacy Mode Integration Summary

## Overview
This document provides a concise summary of all files and changes needed to integrate the Privacy Mode feature into the existing addressbook screens.

## Files Created (Privacy Mode Core - Already Done)
1. ✅ `ObfuscatedAddress.kt` - Main component for displaying obfuscated addresses
2. ✅ `PrivacyModeToggle.kt` - Toggle component for enabling/disabling privacy mode
3. ✅ `PrivacyModeViewModel.kt` - State management for privacy mode
4. ✅ `AddressRevealAuthenticator.kt` - Authentication handling for address reveals
5. ✅ `AddressObfuscator.kt` - Logic for obfuscating addresses

## New Files to Create (Integration)
1. `PrivacyToggleButton.kt` - Header button component for privacy toggle
2. `SendTokenHeaderWithPrivacy.kt` - Enhanced header with privacy toggle
3. `ContactItemWithPrivacy.kt` - Example of privacy-enabled contact item
4. `AddressRevealDialog.kt` - Dialog for authentication during reveal

## Existing Files to Modify

### 1. ContactListScreen Integration
**File:** `features/addressbook/src/.../presentation/contact/list/ContactListScreen.kt`
```kotlin
// Add privacy mode support:
- Import PrivacyModeViewModel
- Pass privacy state to child components
- Replace SendTokenHeader with SendTokenHeaderWithPrivacy
```

**File:** `features/addressbook/src/.../presentation/contact/list/ContactListScreenModel.kt`
```kotlin
// Add:
- Inject PrivacyModeViewModel
- Track revealed addresses state
- Handle privacy toggle and reveal requests
```

### 2. ContactItem Updates
**File:** `features/addressbook/src/.../presentation/contact/recent/ContactItem.kt`
```kotlin
// Changes:
- Add privacyModeEnabled parameter
- Replace Text with ObfuscatedAddress for address display
- Add onRevealAddress callback
- Conditionally show copy button based on privacy state
```

### 3. ContactDetailScreen Updates
**File:** `features/addressbook/src/.../presentation/contact/detail/ContactDetailScreen.kt`
```kotlin
// Add:
- Privacy mode state from ScreenModel
- Pass privacy state to WalletAddressesSection
- Handle address reveal authentication
```

**File:** `features/addressbook/src/.../presentation/contact/detail/WalletAddressesSection.kt`
```kotlin
// Update BlockchainAddressItem:
- Add privacyModeEnabled parameter
- Replace address Text with ObfuscatedAddress
- Handle reveal requests
```

### 4. GroupDetailScreen Updates
**File:** `features/addressbook/src/.../presentation/group/detail/GroupDetailScreenNew.kt`
```kotlin
// Add:
- Privacy mode support
- Pass privacy state to AddressListItem
- Handle batch reveal for groups
```

**File:** `features/addressbook/src/.../presentation/group/detail/AddressListItem.kt`
```kotlin
// Changes:
- Add privacyModeEnabled parameter
- Use ObfuscatedAddress for address display
- Handle reveal authentication
```

### 5. Component Updates
**File:** `features/addressbook/src/.../presentation/contact/recent/ContactsContent.kt`
```kotlin
// Update ProfileCard to support privacy mode
// Pass privacy state to ContactListSection
```

**File:** `features/addressbook/src/.../presentation/contact/list/FavoritesContent.kt`
```kotlin
// Pass privacy mode state to ContactItem components
```

### 6. Dependency Injection
**File:** `features/addressbook/src/.../di/AddressBookModule.kt`
```kotlin
// Ensure these are registered:
single { PrivacyModeViewModel(get(), get(), get()) }
single { AddressRevealAuthenticator(get(), get()) }
```

## Integration Flow

### User Journey
1. User opens ContactListScreen
2. Privacy toggle appears in header (off by default)
3. User enables privacy mode
4. All sensitive addresses become obfuscated (*****)
5. User taps on obfuscated address
6. Authentication prompt appears (biometric/PIN)
7. On success, address reveals temporarily (30s)
8. Address auto-hides after timeout

### State Flow
```
ContactListScreen
  ├── ContactListScreenModel (holds PrivacyModeViewModel)
  ├── SendTokenHeaderWithPrivacy (privacy toggle)
  └── Contact Lists
      ├── FavoritesContent
      │   └── ContactItem → ObfuscatedAddress
      ├── ContactsContent
      │   └── ContactItem → ObfuscatedAddress
      └── GroupsContent
          └── AddressListItem → ObfuscatedAddress
```

## Key Integration Points

### 1. Privacy Toggle Location
- Primary: ContactListScreen header (always visible)
- Secondary: Settings screen (future)
- Optional: Quick settings in profile section

### 2. Address Display Components
All these components need ObfuscatedAddress integration:
- ContactItem (contact lists)
- ProfileCard (my profile section)
- WalletAddressesSection (contact detail)
- AddressListItem (group members)
- TransactionItem (transaction history)

### 3. Authentication Flow
- Reuse existing biometric/PIN authentication
- Show dialog on reveal request
- Track revealed addresses per session
- Auto-hide after 30 seconds

### 4. Visual Indicators
- Privacy toggle button in header
- Lock icon on obfuscated addresses
- Privacy mode indicator badge
- Temporary "revealed" state styling

## Testing Requirements

### Manual Testing Checklist
- [ ] Privacy toggle works in ContactListScreen
- [ ] Addresses obfuscate when privacy enabled
- [ ] Tap to reveal shows authentication
- [ ] Successful auth reveals address
- [ ] Address auto-hides after 30s
- [ ] Copy button hidden when obfuscated
- [ ] QR code still accessible
- [ ] State persists across screens
- [ ] Works in all tabs (Favorites, Contacts, Groups)

### Edge Cases to Test
- [ ] Multiple address reveals
- [ ] Screen rotation/configuration changes
- [ ] App background/foreground
- [ ] Memory pressure scenarios
- [ ] Authentication failures
- [ ] Network disconnection

## Implementation Priority

1. **Phase 1** (High Priority - User-facing)
   - ContactListScreen header integration
   - ContactItem privacy support
   - Basic reveal functionality

2. **Phase 2** (Medium Priority - Detail screens)
   - ContactDetailScreen integration
   - WalletAddressesSection updates
   - Authentication dialog

3. **Phase 3** (Lower Priority - Groups)
   - GroupDetailScreen support
   - Batch reveal for groups
   - Group-specific privacy settings

4. **Phase 4** (Nice to have)
   - Settings screen integration
   - Privacy mode onboarding
   - Advanced privacy options

## Next Steps

1. Review and approve integration plan
2. Create feature branch
3. Implement Phase 1 changes
4. Test core functionality
5. Progressively add remaining phases
6. Conduct full regression testing
7. Prepare user documentation