# Screen and BaseScreen Subclasses in Addressbook Module

## Screen Subclasses

Classes that extend `cafe.adriel.voyager.core.screen.Screen` directly:

1. **FilterPerformanceTestScreen** - `features/addressbook/src/commonMain/kotlin/com/mangala/wallet/features/addressbook/presentation/contact/list/FilterPerformanceTestScreen.kt`
   - *Description*: A test screen for contact list filtering performance
   - *User Actions*: Search contacts, apply filters, toggle multi-selection view
   - *Navigation*: None (test screen)

2. **AddWalletToGroupBottomSheetScreen** - `features/addressbook/src/commonMain/kotlin/com/mangala/wallet/features/addressbook/presentation/group/contact/AddWalletToGroupBottomSheetScreen.kt`
   - *Description*: Bottom sheet to select wallet addresses for adding to a group
   - *User Actions*: Search contacts, select/deselect wallet addresses, confirm selection
   - *Navigation*: Dismisses to parent screen after selection

3. **SecureAuthScreen** - `features/addressbook/src/commonMain/kotlin/com/mangala/wallet/features/addressbook/presentation/security/SecureNavigator.kt`
   - *Description*: Security authentication screen for sensitive operations
   - *User Actions*: Enter authentication credentials (PIN/biometric)
   - *Navigation*: Back to previous screen after authentication or cancellation

## BaseScreen Subclasses

Classes that extend `com.mangala.wallet.ui.utils.screenmodel.BaseScreen`:

### Contact Features
1. **AllContactIconScreen** - `features/addressbook/src/commonMain/kotlin/com/mangala/wallet/features/addressbook/presentation/AllContactIconScreen.kt`
   - *Description*: Test screen displaying all available contact icons in the system
   - *User Actions*: View icon library
   - *Navigation*: None (test screen)

2. **NewContactScreen** - `features/addressbook/src/commonMain/kotlin/com/mangala/wallet/features/addressbook/presentation/contact/create/NewContactScreen.kt`
   - *Description*: Screen to create a new contact with personal information and wallet addresses
   - *User Actions*: Add contact details, add wallet addresses, select tags, set privacy/security levels, save contact
   - *Navigation*: ContactDetailScreen (after save), Back to previous screen

3. **ContactDetailScreen** - `features/addressbook/src/commonMain/kotlin/com/mangala/wallet/features/addressbook/presentation/contact/detail/ContactDetailScreen.kt`
   - *Description*: Displays detailed information about a contact including personal info and wallet addresses
   - *User Actions*: View contact details, copy addresses, view transactions, edit contact, delete contact
   - *Navigation*: ContactEditScreen, TransactionDetailScreen, Back to ContactListScreen

4. **ContactEditScreen** - `features/addressbook/src/commonMain/kotlin/com/mangala/wallet/features/addressbook/presentation/contact/edit/ContactEditScreen.kt`
   - *Description*: Edit existing contact information and wallet addresses
   - *User Actions*: Update contact details, modify wallet addresses, change tags, update privacy/security settings, save changes
   - *Navigation*: ContactDetailScreen (after save), Back to previous screen

5. **ContactListScreen** - `features/addressbook/src/commonMain/kotlin/com/mangala/wallet/features/addressbook/presentation/contact/list/ContactListScreen.kt`
   - *Description*: Main contacts management screen with tabs for recent transactions, favorites, contacts, groups, and tags
   - *User Actions*: Search contacts/groups, switch tabs, create new contact/group/tag, view contact/group details
   - *Navigation*: NewContactScreen, ContactDetailScreen, CreateGroupScreenNew, GroupDetailScreenNew, AddTagScreen, DetailTagScreen, TransactionDetailScreen

6. **TransactionDetailScreen** - `features/addressbook/src/commonMain/kotlin/com/mangala/wallet/features/addressbook/presentation/contact/recent/TransactionDetailScreen.kt`
   - *Description*: Shows detailed information about a specific cryptocurrency transaction
   - *User Actions*: View transaction details, copy transaction ID/addresses, view on blockchain explorer
   - *Navigation*: Back to ContactListScreen or ContactDetailScreen

### Blockchain Features
1. **BlockchainScreen** - `features/addressbook/src/commonMain/kotlin/com/mangala/wallet/features/addressbook/presentation/blockchain/BlockchainScreen.kt`
   - *Description*: Manages blockchain types and tokens within the address book
   - *User Actions*: Add/remove blockchain types, manage tokens, search blockchains, toggle activation
   - *Navigation*: Back to previous screen

### Group Features
1. **CreateGroupScreenNew** - `features/addressbook/src/commonMain/kotlin/com/mangala/wallet/features/addressbook/presentation/group/create/CreateGroupScreenNew.kt`
   - *Description*: Create or edit a group of contacts with shared blockchain network
   - *User Actions*: Set group name/icon/color, select blockchain network, configure privacy/security, add contacts to group, save group
   - *Navigation*: NetworkSelectionBottomSheetScreen, AddWalletToGroupBottomSheetScreen, GroupDetailScreenNew (after save)

2. **GroupDetailScreenNew** - `features/addressbook/src/commonMain/kotlin/com/mangala/wallet/features/addressbook/presentation/group/detail/GroupDetailScreenNew.kt`
   - *Description*: Displays detailed information about a group including members and their wallet addresses
   - *User Actions*: View group info, search members, send to group, export addresses, generate QR code, copy addresses, edit group
   - *Navigation*: CreateGroupScreenNew (for editing), Back to ContactListScreen

3. **NetworkSelectionBottomSheetScreen** - `features/addressbook/src/commonMain/kotlin/com/mangala/wallet/features/addressbook/presentation/group/create/blockchainNetwork/NetworkSelectionBottomSheetScreen.kt`
   - *Description*: Bottom sheet for selecting blockchain network when creating/editing a group
   - *User Actions*: Select a blockchain network from available options
   - *Navigation*: Dismisses to CreateGroupScreenNew

### Tag Features
1. **AddressSelectionScreen** - `features/addressbook/src/commonMain/kotlin/com/mangala/wallet/features/addressbook/presentation/tag/AddressSelectionScreen.kt`
   - *Description*: Bottom sheet for selecting addresses to apply tags
   - *User Actions*: Search addresses, select/deselect addresses, apply selection
   - *Navigation*: Dismisses to parent screen after selection

2. **AddTagScreen** - `features/addressbook/src/commonMain/kotlin/com/mangala/wallet/features/addressbook/presentation/tag/add/AddTagScreen.kt`
   - *Description*: Create or edit contact/address tags for organization
   - *User Actions*: Create tag, set tag name/color/description, apply tag to contacts/addresses, save tag
   - *Navigation*: ContactDetailScreen or ContactListScreen (after save), AddressSelectionScreen

3. **DetailTagScreen** - `features/addressbook/src/commonMain/kotlin/com/mangala/wallet/features/addressbook/presentation/tag/detail/DetailTagScreen.kt`
   - *Description*: Displays information about a tag and contacts/addresses associated with it
   - *User Actions*: View tagged items, search tagged items, copy information, edit tag, delete tag
   - *Navigation*: AddTagScreen (for editing), ContactListScreen

## Notes

- The `SendTokenScreen` file exists but is currently commented out.
- Most screens in the addressbook module extend `BaseScreen` for consistent screen model management and theming.
- Three screens extend `Screen` directly: `FilterPerformanceTestScreen`, `AddWalletToGroupBottomSheetScreen`, and `SecureAuthScreen`.