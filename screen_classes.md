# Screen and BaseScreen Subclasses in Mangala Wallet

## Screen Subclasses

Classes that extend `cafe.adriel.voyager.core.screen.Screen` directly:

1. **ForgotPinScreen** - `core/pin/src/commonMain/kotlin/com/mangala/wallet/pin/presentation/forgot/ForgotPinScreen.kt`
   - **Description**: Recovery screen for users who have forgotten their PIN. Provides options to reset or recover access to the wallet.
   - **User Actions**: 
     - Initiate PIN recovery process
     - Navigate back to previous screen
   - **Navigation**: 
     - Recovery flow depends on implementation
     - Back navigation to previous screen
2. **LockScreen** - `core/pin/src/commonMain/kotlin/com/mangala/wallet/pin/presentation/lock/LockScreen.kt`
   - **Description**: Security screen that locks the wallet application. Requires authentication (PIN or biometrics) to unlock.
   - **User Actions**: 
     - Enter PIN to unlock
     - Use biometric authentication (if enabled)
     - Access forgot PIN option
   - **Navigation**: 
     - On successful unlock → Previous/Main screen
     - Forgot PIN → ForgotPinScreen
3. **EditReceiveAmountScreen** - `features/receive/src/commonMain/kotlin/com/mangala/wallet/features/receive/presentation/EditReceiveAmountScreen.kt`
   - **Description**: Allows users to specify an amount when generating a receive/payment QR code. Enhances the receiving flow by pre-filling the amount.
   - **User Actions**: 
     - Enter desired receive amount
     - Confirm amount selection
     - Clear/modify amount
   - **Navigation**: 
     - On confirm → Updates previous screen with amount
     - Back navigation to receive screen
4. **AccountDetailsScreen** - `features/manageaccount/src/commonMain/kotlin/com/mangala/wallet/features/manageaccount/presentation/accountdetail/AccountDetailsScreen.kt`
   - **Description**: Displays detailed information about a specific account including balance, transactions, and account settings.
   - **User Actions**: 
     - View account information
     - Copy account address
     - Access account settings
     - Navigate to transaction history
   - **Navigation**: 
     - Transaction details → TransactionInfoScreen
     - Account settings → Various settings screens
     - Back navigation to account list
5. **UpdatePermissionScreen** - `features/chains/antelope_pro/src/commonMain/kotlin/com/mangala/wallet/features/chains/antelope/presentation/permission/updatepermission/UpdatePermissionScreen.kt`
   - **Description**: Antelope-specific screen for modifying account permissions and authorities. Allows users to update keys and manage account control.
   - **User Actions**: 
     - Modify permission settings
     - Update authority keys
     - Add/remove permissions
     - Save permission changes
   - **Navigation**: 
     - On save → Transaction confirmation screen
     - Back navigation to permission list
6. **BackupWalletGuideScreen** - `core/wallet/src/commonMain/kotlin/com/mangala/wallet/wallet/presentation/backup/BackupWalletGuideScreen.kt`
   - **Description**: Two-page guide screen that educates users about backing up their wallet. Page 1 introduces the backup concept, Page 2 provides backup instructions specific to the network type.
   - **User Actions**: 
     - Navigate through backup guide pages 
     - Start backup process from page 2
     - Navigate back to previous screen
   - **Navigation**: 
     - Page 1 → Page 2 (BackupWalletGuideScreen)
     - Page 2 → UnlockPinScreen (different cases based on network type: SHOW_WORDS_PHRASE for EVM, BACKUP_ANTELOPE_ACCOUNT for Antelope)
     - Back navigation to previous screen
7. **BiometryScreen** - `core/biometry/src/commonMain/kotlin/com/mangala/wallet/biometry/presentation/BiometryScreen.kt`
   - **Description**: Configures biometric authentication (Face ID/Touch ID on iOS, Fingerprint/Face ID on Android) for wallet security. Shows appropriate biometric icon and prompts for permission.
   - **User Actions**: 
     - Enable biometric authentication 
     - Skip biometric setup 
     - Navigate back
   - **Navigation**: 
     - On successful biometric setup → CreateWalletScreen
     - On skip → CreateWalletScreen
     - Back navigation to previous screen

## BaseScreen Subclasses

Classes that extend `com.mangala.wallet.ui.utils.screenmodel.BaseScreen`:

### Antelope Chain Features
1. **ImportAccountScreen** - `features/chains/antelope_cold/src/commonMain/kotlin/com/mangala/wallet/features/chains/antelope/presentation/importaccount/ImportAccountScreen.kt`
   - **Description**: Cold wallet version for importing existing Antelope accounts using private keys. Securely stores account information offline.
   - **User Actions**: 
     - Enter private key
     - Scan QR code
     - Import account
     - Select account type
   - **Navigation**: 
     - Success → Account list or home screen
     - QR scanner → QR scanning screen
     - Back navigation
2. **ImportAccountScreen** - `features/chains/antelope_ui/src/commonMain/kotlin/com/mangala/wallet/features/chains/antelope/presentation/importaccount/ImportAccountScreen.kt`
   - **Description**: UI wallet version for importing existing Antelope accounts. Similar to cold wallet version but with network connectivity.
   - **User Actions**: 
     - Enter private key
     - Import via QR code
     - Select network
     - Verify account details
   - **Navigation**: 
     - Success → Account management screen
     - Network selection → Network picker
     - Back navigation
3. **CreateKeyPairScreen** - `features/chains/antelope_cold/src/commonMain/kotlin/com/mangala/wallet/features/chains/antelope/presentation/createkeypair/CreateKeyPairScreen.kt`
   - **Description**: Generates new Antelope key pairs for account creation. Displays public and private keys securely for cold storage.
   - **User Actions**: 
     - Generate new key pair
     - Copy keys to clipboard
     - Show/hide private key
     - Save keys securely
   - **Navigation**: 
     - Next → Account creation flow
     - Back navigation to previous screen
4. **Step1SelectAccountTypeScreen** - `features/chains/antelope_create_account/src/commonMain/kotlin/com/mangala/wallet/features/chains/antelope/create_account/presentation/step1/Step1SelectAccountTypeScreen.kt`
   - **Description**: First step in Antelope account creation flow. Allows users to choose between different account types (standard, premium, etc.).
   - **User Actions**: 
     - Select account type
     - View account type benefits
     - Continue to next step
   - **Navigation**: 
     - Continue → Step2SelectAccountNameScreen
     - Back navigation to previous screen
5. **BackupWithKeyCertScreen** - `features/chains/antelope_pro/src/commonMain/kotlin/com/mangala/wallet/features/chains/antelope/presentation/backupaccount/keycert/BackupWithKeyCertScreen.kt`
   - **Description**: Allows users to backup their Antelope account using a key certificate. Provides secure export functionality.
   - **User Actions**: 
     - Generate key certificate
     - Export/save certificate
     - Copy certificate data
     - Share certificate securely
   - **Navigation**: 
     - Success → Backup completion screen
     - Back navigation to account management
6. **ManageAntelopeAccountScreen** - `features/chains/antelope_pro/src/commonMain/kotlin/com/mangala/wallet/features/chains/antelope/presentation/manageaccount/ManageAntelopeAccountScreen.kt`
   - **Description**: Central management hub for Antelope accounts. Provides access to permissions, resources, and account settings.
   - **User Actions**: 
     - View account resources
     - Manage permissions
     - Stake/unstake resources
     - Access account tools
   - **Navigation**: 
     - Permissions → PermissionScreen
     - Resources → NetAndCpuScreen
     - Backup → BackupAntelopeAccountScreen
7. **AntelopeMultisigScreen** - `features/chains/antelope_pro/src/commonMain/kotlin/com/mangala/wallet/features/chains/antelope/presentation/multisig/AntelopeMultisigScreen.kt`
   - **Description**: Manages multi-signature proposals and transactions for Antelope accounts. Shows pending, approved, and executed proposals.
   - **User Actions**: 
     - View proposal list
     - Create new proposal
     - Approve/reject proposals
     - Filter proposals by status
   - **Navigation**: 
     - Create → CreateNewProposalScreen
     - Proposal details → ProposalDetailScreen
     - Back navigation
8. **CreateNewProposalScreen** - `features/chains/antelope_pro/src/commonMain/kotlin/com/mangala/wallet/features/chains/antelope/presentation/multisig/proposal/create/CreateNewProposalScreen.kt`
   - **Description**: Multi-step form for creating new multi-signature proposals on Antelope. Includes actions, approvers, and expiration settings.
   - **User Actions**: 
     - Add/remove actions
     - Select approvers
     - Set expiration time
     - Submit proposal
   - **Navigation**: 
     - Actions → MultisigProposalActionScreen
     - Approvers → MultisigProposalApproverScreen
     - Submit → Transaction confirmation
9. **SelectAccountPermissionScreen** - `features/chains/antelope_pro/src/commonMain/kotlin/com/mangala/wallet/features/chains/antelope/presentation/multisig/proposal/create/selectAccPer/SelectAccountPermissionScreen.kt`
   - **Description**: Allows selection of specific account permissions for multi-signature proposals. Shows available permissions and their authorities.
   - **User Actions**: 
     - Select permission level
     - View permission details
     - Confirm selection
   - **Navigation**: 
     - Confirm → Returns to proposal creation
     - Back navigation
10. **MsigScreen** - `features/chains/antelope_pro/src/commonMain/kotlin/com/mangala/wallet/features/chains/antelope/presentation/proposal/MsigScreen.kt`
   - **Description**: Main multi-signature management screen showing proposals organized by status (pending, executed, expired).
   - **User Actions**: 
     - View proposals by category
     - Search proposals
     - Filter by status
     - Navigate to details
   - **Navigation**: 
     - Proposal item → Specific proposal detail screen
     - Create new → CreateNewProposalScreen
11. **ExpiredProposalScreen** - `features/chains/antelope_pro/src/commonMain/kotlin/com/mangala/wallet/features/chains/antelope/presentation/proposal/expiredProposal/ExpiredProposalScreen.kt`
   - **Description**: Lists expired multi-signature proposals. Allows users to review and clean up old proposals.
   - **User Actions**: 
     - View expired proposals
     - Delete expired proposals
     - View proposal details
   - **Navigation**: 
     - Details → ExpiredProposalDetailScreen
     - Back navigation

### Bitcoin Features
12. **BitcoinTestScreen** - `features/chains/bitcoin/src/commonMain/kotlin/com/mangala/wallet/features/chains/bitcoin/presentation/BitcoinTestScreen.kt`
   - **Description**: Testing/development screen for Bitcoin functionality. Used during development for testing Bitcoin integration.
   - **User Actions**: 
     - Test Bitcoin operations
     - Debug functionality
     - View test results
   - **Navigation**: 
     - Various test flows
     - Back navigation

### Contract Wizard
13. **ContractWizardScreen** - `features/contract_wizard/src/commonMain/kotlin/com/mangala/contract/wizard/presentation/ContractWizardScreen.kt`
    - **Description**: Main screen for creating and deploying smart contracts on Antelope chains.
    - **User Actions**: Choose contract templates, configure parameters, preview contract code, deploy contracts.
    - **Navigation**: To SelectContractTypeScreen for template selection, to deployment confirmation screens.

14. **ContractWizardScreen** - `features/contract_wizard/src/commonMain/kotlin/com/mangala/wallet/features/contract_wizard/presentation/ContractWizardScreen.kt`
    - **Description**: Alternative implementation of the contract wizard screen (duplicate implementation).
    - **User Actions**: Same as above - create and deploy smart contracts.
    - **Navigation**: Same as above.

### E-Ticket Features
15. **CategoryScreen** - `features/eticket/src/commonMain/kotlin/com/mangala/eticket/presentation/category/CategoryScreen.kt`
    - **Description**: Browse event categories for e-ticket booking system.
    - **User Actions**: View category list, select categories to filter events, search categories.
    - **Navigation**: To EventListScreen for events in selected category.

16. **EventDetailScreen** - `features/eticket/src/commonMain/kotlin/com/mangala/eticket/presentation/event/EventDetailScreen.kt`
    - **Description**: Detailed view of a specific event showing full information and ticket options.
    - **User Actions**: View event details, select ticket types, add to favorites, begin booking.
    - **Navigation**: To BookingScreen for ticket purchase, back to event list.

17. **UserEventFavouriteScreen** - `features/eticket/src/commonMain/kotlin/com/mangala/eticket/presentation/favourite/UserEventFavouriteScreen.kt`
    - **Description**: Shows user's favorite events saved for later.
    - **User Actions**: View favorite events, remove from favorites, navigate to event details.
    - **Navigation**: To EventDetailScreen for specific events.

18. **ETicketHomeScreen** - `features/eticket/src/commonMain/kotlin/com/mangala/eticket/presentation/home/ETicketHomeScreen.kt`
    - **Description**: Main home screen for the e-ticket booking feature.
    - **User Actions**: Browse featured events, search events, view categories, access favorites.
    - **Navigation**: To CategoryScreen, EventListScreen, UserEventFavouriteScreen, EventDetailScreen.

19. **EventListScreen** - `features/eticket/src/commonMain/kotlin/com/mangala/eticket/presentation/event/list/EventListScreen.kt`
    - **Description**: List of events filtered by category or search criteria.
    - **User Actions**: Browse events, filter/sort results, search events, select event.
    - **Navigation**: To EventDetailScreen for selected event.

20. **BookingScreen** - `features/eticket/src/commonMain/kotlin/com/mangala/eticket/presentation/booking/BookingScreen.kt`
    - **Description**: Ticket booking screen for purchasing event tickets.
    - **User Actions**: Select quantities, enter attendee info, choose payment method.
    - **Navigation**: To ConfirmationScreen after successful booking.

21. **ConfirmationScreen** - `features/eticket/src/commonMain/kotlin/com/mangala/eticket/presentation/booking/ConfirmationScreen.kt`
    - **Description**: Booking confirmation screen showing ticket purchase details.
    - **User Actions**: View confirmation details, save/share tickets, return to events.
    - **Navigation**: Back to ETicketHomeScreen or EventListScreen.

### Browser Features
22. **BrowserTabScreen** - `features/browser_tab/src/commonMain/kotlin/com/mangala/features/browser/BrowserTabScreen.kt`
    - **Description**: In-app browser for dApp interaction and web3 browsing.
    - **User Actions**: Browse web3 sites, interact with dApps, manage bookmarks, navigate back/forward.
    - **Navigation**: To WalletConnect screens for dApp connections, transaction confirmations.

### Browser Bridge Features
23. **ConfirmTransactionScreen** - `features/browser_bridge_ui/src/commonMain/kotlin/com/mangala/browser/presentation/ConfirmTransactionScreen.kt`
   - **Description**: Displays transaction details from dApp requests for user confirmation. Shows recipient, value, and transaction data before signing.
   - **User Actions**: 
     - Confirm transaction (triggers QR code generation)
     - Decline transaction
     - View transaction details
   - **Navigation**: 
     - On confirm → TransactionQrScreen (shows QR code for cold wallet signing)
     - Callbacks for success/failure to parent screen
24. **SwitchChainScreen** - `features/browser_bridge_base/src/commonMain/kotlin/com/mangala/browser_bridge_base/switchchain/SwitchChainScreen.kt`
   - **Description**: Handles dApp requests to switch blockchain networks. Displays current and target chain information with icons, prompting user confirmation.
   - **User Actions**: 
     - Confirm chain switch (saves network and reloads page)
     - Decline chain switch request
     - Close screen via clear button
   - **Navigation**: 
     - On confirm → Callbacks to parent, reloads browser page
     - On decline → Callbacks to parent
     - No direct screen navigation
25. **SignPersonalMessageScreen** - `features/browser_bridge_base/src/commonMain/kotlin/com/mangala/browser_bridge_base/personal/SignPersonalMessageScreen.kt`
   - **Description**: Handles dApp requests to sign personal messages. Shows the requesting URL and message content for user verification before signing.
   - **User Actions**: 
     - Sign the message (requires PIN verification)
     - Decline signing request
     - Close screen via clear button
   - **Navigation**: 
     - On sign → UnlockPinScreen for PIN verification
     - After PIN verification → Callbacks with signed message
     - On decline → Callbacks to parent

### Account Management
26. **ManageAccountsScreen** - `features/manageaccount/src/commonMain/kotlin/com/mangala/wallet/features/manageaccount/presentation/ManageAccountsScreen.kt`
    - **Description**: Central account management screen showing all wallet accounts.
    - **User Actions**: View account list, add new accounts, remove accounts, switch active account.
    - **Navigation**: To AccountDetailsScreen, add account flows, back to main screen.

### NFT Features
27. **NftMainScreen** - `features/nft_base/src/commonMain/kotlin/com/mangala/wallet/features/nft_base/presentation/NftMainScreen.kt`
    - **Description**: Main NFT screen displaying user's NFT collection across all supported chains.
    - **User Actions**: View NFT collection, filter by chain, search NFTs, import new NFTs.
    - **Navigation**: To NftDetailsScreen, ImportNftScreen, SendNftScreen.

28. **ImportNftScreen** - `features/nft_base/src/commonMain/kotlin/com/mangala/wallet/features/nft_base/presentation/import/ImportNftScreen.kt`
    - **Description**: Import NFTs by entering contract address and token ID.
    - **User Actions**: Enter NFT details, select network, confirm import.
    - **Navigation**: Back to NftMainScreen upon successful import.

29. **SendNftScreen** - `features/nft_base/src/commonMain/kotlin/com/mangala/wallet/features/nft_base/presentation/send/SendNftScreen.kt`
    - **Description**: Send NFT to another address.
    - **User Actions**: Enter recipient address, confirm transfer details, sign transaction.
    - **Navigation**: To SendNftConfirmationScreen for transaction review.

### NFT Pro Features
30. **SendNftConfirmationScreen** - `features/nft_pro/src/commonMain/kotlin/com/mangala/wallet/features/nft/presentation/send/confirmation/SendNftConfirmationScreen.kt`
    - **Description**: NFT transfer confirmation screen showing transaction details before sending.
    - **User Actions**: Review transfer details, confirm or cancel, sign transaction.
    - **Navigation**: To success screen or back to SendNftScreen.

### Settings Features
31. **AboutUsScreen** - `features/settings/menu_base/src/commonMain/kotlin/com/mangala/wallet/menu_base/presentation/aboutus/AboutUsScreen.kt`
    - **Description**: Information screen showing app version, company info, and legal documents.
    - **User Actions**: View app information, access terms of service, privacy policy, licenses.
    - **Navigation**: To web views for legal documents, back to settings.

32. **ConnectWithUsScreen** - `features/settings/menu_base/src/commonMain/kotlin/com/mangala/wallet/menu_base/presentation/connectwithus/ConnectWithUsScreen.kt`
    - **Description**: Social media and contact links for user support.
    - **User Actions**: Access social media links, email support, visit website.
    - **Navigation**: To external apps/browsers, back to settings.

33. **HelpCenterScreen** - `features/settings/menu_base/src/commonMain/kotlin/com/mangala/wallet/menu_base/presentation/helpcenter/HelpCenterScreen.kt`
    - **Description**: Help and support center with FAQs and guides.
    - **User Actions**: Browse help topics, search FAQs, contact support.
    - **Navigation**: To specific help articles, contact forms.

34. **IconsInAppScreen** - `features/settings/menu_base/src/commonMain/kotlin/com/mangala/wallet/menu_base/presentation/iconsinapp/IconsInAppScreen.kt`
    - **Description**: Display icons and symbols used throughout the app with explanations.
    - **User Actions**: View icon meanings, search icons, understand symbology.
    - **Navigation**: Back to settings or help center.

35. **BaseMenuScreen** - `features/settings/menu_base/src/commonMain/kotlin/com/mangala/wallet/menu_base/presentation/menu/BaseMenuScreen.kt`
    - **Description**: Main settings menu screen with various configuration options.
    - **User Actions**: Navigate to different settings sections, logout, manage preferences.
    - **Navigation**: To all settings subscreens like Security, Theme, Wallet, etc.

36. **NotificationsScreen** - `features/settings/menu_base/src/commonMain/kotlin/com/mangala/wallet/menu_base/presentation/notifications/NotificationsScreen.kt`
    - **Description**: Manage notification preferences and alert settings.
    - **User Actions**: Toggle notification types, set alert preferences, manage push notifications.
    - **Navigation**: Back to settings menu.

37. **BasePreferencesScreen** - `features/settings/menu_base/src/commonMain/kotlin/com/mangala/wallet/menu_base/presentation/preferences/BasePreferencesScreen.kt`
    - **Description**: General app preferences and default settings.
    - **User Actions**: Configure default currency, time zone, display options.
    - **Navigation**: To specific preference screens, back to menu.

38. **SecurityScreen** - `features/settings/menu_base/src/commonMain/kotlin/com/mangala/wallet/menu_base/presentation/security/SecurityScreen.kt`
    - **Description**: Security settings including PIN, biometrics, and backup options.
    - **User Actions**: Change PIN, enable biometrics, manage security preferences.
    - **Navigation**: To PIN setup, biometric settings, backup flows.

39. **ShareAppScreen** - `features/settings/menu_base/src/commonMain/kotlin/com/mangala/wallet/menu_base/presentation/shareapp/ShareAppScreen.kt`
    - **Description**: Share app with friends via various methods.
    - **User Actions**: Share app link, send referral code, access social sharing.
    - **Navigation**: To system share sheet, back to settings.

40. **ThemeScreen** - `features/settings/menu_base/src/commonMain/kotlin/com/mangala/wallet/menu_base/presentation/theme/ThemeScreen.kt`
    - **Description**: Customize app appearance with theme selection.
    - **User Actions**: Switch between light/dark themes, set auto theme, preview themes.
    - **Navigation**: Back to settings with theme applied.

41. **WalletScreen** - `features/settings/menu_base/src/commonMain/kotlin/com/mangala/wallet/menu_base/presentation/wallet/WalletScreen.kt`
    - **Description**: Wallet management settings and configuration.
    - **User Actions**: Manage wallets, view backup status, configure wallet settings.
    - **Navigation**: To wallet details, backup flows, add wallet screens.

42. **LanguageScreen** - `features/settings/menu_base/src/commonMain/kotlin/com/mangala/wallet/menu_base/presentation/language/LanguageScreen.kt`
    - **Description**: Language selection screen for app localization.
    - **User Actions**: Select preferred language, search languages, apply language change.
    - **Navigation**: Back to settings with new language applied.

43. **DevMenuScreen** - `features/settings/menu_base/src/commonMain/kotlin/com/mangala/wallet/menu_base/presentation/dev/DevMenuScreen.kt`
    - **Description**: Developer settings and debugging options (usually hidden in production).
    - **User Actions**: Access debug tools, view logs, toggle test features.
    - **Navigation**: To various debug screens, back to settings.
44. **AddWalletScreen** - `features/settings/menu_cold/src/commonMain/kotlin/com/mangala/wallet/features/menu/presentation/wallet/add_wallet/AddWalletScreen.kt`
    - **Description**: Cold wallet version for adding new wallet accounts.
    - **User Actions**: Create new wallet, import existing wallet, configure cold storage.
    - **Navigation**: To wallet creation/import flows, back to wallet settings.

45. **WalletScreen** - `features/settings/menu_pro/src/commonMain/kotlin/com/mangala/wallet/features/menu/presentation/wallet/WalletScreen.kt`
    - **Description**: Pro version wallet management screen with advanced features.
    - **User Actions**: Manage multiple wallets, access advanced settings, view analytics.
    - **Navigation**: To wallet details, backup, advanced configuration.

46. **AddWalletScreen** - `features/settings/menu_pro/src/commonMain/kotlin/com/mangala/wallet/features/menu/presentation/wallet/add_wallet/AddWalletScreen.kt`
    - **Description**: Pro version for adding new wallet with full features.
    - **User Actions**: Create wallet, import via multiple methods, configure advanced options.
    - **Navigation**: To creation flows, import screens, wallet list.

47. **CurrencyScreen** - `features/settings/currency/src/commonMain/kotlin/com/mangala/wallet/features/settings/currency/presentation/CurrencyScreen.kt`
    - **Description**: Select default display currency for price conversions.
    - **User Actions**: Choose fiat currency, search currencies, set default.
    - **Navigation**: Back to settings with currency applied.

48. **NetworkScreen** - `features/settings/network/src/commonMain/kotlin/com/mangala/wallet/features/settings/network/NetworkScreen.kt`
    - **Description**: Configure blockchain network settings and RPC endpoints.
    - **User Actions**: Add custom networks, edit RPC URLs, switch networks.
    - **Navigation**: To network details, add network form.

### Swap Features
49. **SwapTokenScreen** - `features/swap_base/src/commonMain/kotlin/com/mangala/wallet/features/swap_base/presentation/SwapTokenScreen.kt`
    - **Description**: Main token swap interface for exchanging cryptocurrencies.
    - **User Actions**: Select tokens to swap, enter amounts, view rates, initiate swap.
    - **Navigation**: To SelectTokenScreen, PreviewSwapTokenScreen, transaction confirmation.

### Core Wallet Features
50. **CreateWalletGuideScreen** - `core/wallet/src/commonMain/kotlin/com/mangala/wallet/wallet/presentation/create/CreateWalletGuideScreen.kt`
    - **Description**: Educational guide for new wallet creation process.
    - **User Actions**: Read wallet creation information, understand security, proceed to creation.
    - **Navigation**: To CreateWalletScreen, back to welcome screen.

51. **ResetWalletScreen** - `core/wallet/src/commonMain/kotlin/com/mangala/wallet/wallet/presentation/reset/ResetWalletScreen.kt`
    - **Description**: Reset wallet and clear all data (factory reset).
    - **User Actions**: Confirm reset, understand data loss warning, execute reset.
    - **Navigation**: To welcome/onboarding screen after reset.

52. **RestoreWalletGuideScreen** - `core/wallet/src/commonMain/kotlin/com/mangala/wallet/wallet/presentation/restore/RestoreWalletGuideScreen.kt`
    - **Description**: Guide for restoring existing wallet from backup.
    - **User Actions**: Learn restore process, choose restore method.
    - **Navigation**: To RestoreRecoveryPhraseScreen or import flows.

53. **BackupWalletDoneScreen** - `core/wallet/src/commonMain/kotlin/com/mangala/wallet/wallet/presentation/backup/BackupWalletDoneScreen.kt`
    - **Description**: Completion screen after successful wallet backup.
    - **User Actions**: Confirm backup completion, understand backup importance.
    - **Navigation**: To HomeScreen or main wallet view.

54. **ShowRecoveryPhraseScreen** - `core/wallet/src/commonMain/kotlin/com/mangala/wallet/wallet/presentation/backup/ShowRecoveryPhraseScreen.kt`
    - **Description**: Display recovery phrase for user to write down.
    - **User Actions**: View recovery phrase, copy words, confirm written down.
    - **Navigation**: To VerifyRecoveryPhraseScreen for verification.

55. **VerifyRecoveryPhraseScreen** - `core/wallet/src/commonMain/kotlin/com/mangala/wallet/wallet/presentation/backup/VerifyRecoveryPhraseScreen.kt`
    - **Description**: Verify user has correctly saved recovery phrase.
    - **User Actions**: Enter specific words from phrase, confirm backup.
    - **Navigation**: To BackupWalletDoneScreen on success.

56. **BackupWalletAlertScreen** - `core/wallet/src/commonMain/kotlin/com/mangala/wallet/wallet/presentation/backup/BackupWalletAlertScreen.kt`
   - **Description**: Warning screen that prompts users to backup their wallet after creation. Emphasizes the importance of backing up recovery phrases or keys.
   - **User Actions**: 
     - Choose to backup now
     - Skip backup at their own risk
   - **Navigation**: 
     - "Backup now" → BackupWalletGuideScreen (page 1)
     - "I will risk it" → HomeScreen (replaces navigation stack)

### WalletConnect Features
57. **WalletConnectScreen** - `features/walletconnect/src/commonMain/kotlin/com/mangala/wallet/walletconnect/WalletConnectScreen.kt`
    - **Description**: WalletConnect integration for connecting to dApps.
    - **User Actions**: Scan QR codes, manage connections, approve requests.
    - **Navigation**: To transaction approval screens, connection management.

### Wallet UI Features
58. **WalletMainScreen** - `features/wallet_ui/src/commonMain/kotlin/com/mangala/wallet/features/wallet/presentation/main/WalletMainScreen.kt`
    - **Description**: Main wallet dashboard showing portfolio overview and quick actions.
    - **User Actions**: View balances, access features, manage accounts, refresh data.
    - **Navigation**: To all major wallet features, account management, settings.

59. **SyncAccountScreen** - `features/wallet_ui/src/commonMain/kotlin/com/mangala/wallet/features/wallet/presentation/syncaccount/SyncAccountScreen.kt`
    - **Description**: Sync account data between devices or wallet instances.
    - **User Actions**: Generate sync code, scan sync QR, confirm sync.
    - **Navigation**: To QR scanner, success screen.

### Wallet Pro Features
60. **AddAccountScreen** (EVM) - `features/wallet_pro/src/commonMain/kotlin/com/mangala/wallet/features/wallet/presentation/addaccount/evm/AddAccountScreen.kt`
    - **Description**: Add new EVM-compatible blockchain accounts.
    - **User Actions**: Create new account, import private key, derive from HD wallet.
    - **Navigation**: To account creation flows, import screens.

61. **BitcoinCreateAccountScreen** - `features/wallet_pro/src/commonMain/kotlin/com/mangala/wallet/features/wallet/presentation/addaccount/bitcoin/BitcoinCreateAccountScreen.kt`
    - **Description**: Create new Bitcoin accounts in the wallet.
    - **User Actions**: Generate Bitcoin address, configure account type, set label.
    - **Navigation**: To wallet main screen with new account.

### Home Pro Features
62. **CreateAccountNotificationScreen** - `features/home_pro/src/commonMain/kotlin/com/mangala/wallet/features/home/presentation/CreateAccountNotificationScreen.kt`
    - **Description**: Notification screen for account creation prompts.
    - **User Actions**: View account creation suggestions, dismiss or proceed.
    - **Navigation**: To account creation flows, dismiss to home.

### Transaction History Features
63. **TransactionInfoScreen** (EVM) - `features/transactionhistory/src/commonMain/kotlin/com/mangala/wallet/features/transactionhistory/presentation/evm/info/TransactionInfoScreen.kt`
    - **Description**: Detailed view of a specific EVM transaction.
    - **User Actions**: View transaction details, copy hashes, view on explorer.
    - **Navigation**: To block explorer, back to history.

64. **TransactionHistoryFilterBottomSheetScreen** (EVM) - `features/transactionhistory/src/commonMain/kotlin/com/mangala/wallet/features/transactionhistory/presentation/evm/filter/TransactionHistoryFilterBottomSheetScreen.kt`
    - **Description**: Filter options for EVM transaction history.
    - **User Actions**: Filter by type, date, status, amount.
    - **Navigation**: Apply filters and return to history.

65. **TransactionHistoryScreen** (EVM) - `features/transactionhistory/src/commonMain/kotlin/com/mangala/wallet/features/transactionhistory/presentation/evm/TransactionHistoryScreen.kt`
    - **Description**: List of EVM transactions for an account.
    - **User Actions**: View transactions, filter, search, pull to refresh.
    - **Navigation**: To TransactionInfoScreen, filter screen.

66. **TransactionInfoBitcoinScreen** - `features/transactionhistory/src/commonMain/kotlin/com/mangala/wallet/features/transactionhistory/presentation/bitcoin/info/TransactionInfoBitcoinScreen.kt`
    - **Description**: Detailed view of a Bitcoin transaction.
    - **User Actions**: View inputs/outputs, confirmations, fees.
    - **Navigation**: To block explorer, back to history.

67. **TransactionHistoryBitcoinScreen** - `features/transactionhistory/src/commonMain/kotlin/com/mangala/wallet/features/transactionhistory/presentation/bitcoin/TransactionHistoryBitcoinScreen.kt`
    - **Description**: Bitcoin transaction history list.
    - **User Actions**: View transactions, check confirmations, refresh.
    - **Navigation**: To TransactionInfoBitcoinScreen.

68. **TransactionHistoryFilterAntelopeBottomSheetScreen** - `features/transactionhistory/src/commonMain/kotlin/com/mangala/wallet/features/transactionhistory/presentation/antelope/filter/TransactionHistoryFilterAntelopeBottomSheetScreen.kt`
    - **Description**: Filter options for Antelope transaction history.
    - **User Actions**: Filter by action type, contract, date.
    - **Navigation**: Apply filters and return to history.

69. **TransactionHistoryAntelopeScreen** - `features/transactionhistory/src/commonMain/kotlin/com/mangala/wallet/features/transactionhistory/presentation/antelope/TransactionHistoryAntelopeScreen.kt`
    - **Description**: Antelope blockchain transaction history.
    - **User Actions**: View actions, filter by type, search transactions.
    - **Navigation**: To transaction details, filter screen.

### Swap UI Features
70. **PreviewSwapTokenScreen** - `features/swap_ui/src/commonMain/kotlin/com/mangala/wallet/features/swap/presentation/PreviewSwapTokenScreen.kt`
    - **Description**: Preview screen showing swap details before execution.
    - **User Actions**: Review rates, fees, slippage, confirm or cancel swap.
    - **Navigation**: To transaction confirmation, back to swap screen.

### Swap Base Features
71. **SelectTokenScreen** - `features/swap_base/src/commonMain/kotlin/com/mangala/wallet/features/swap_base/presentation/selecttoken/SelectTokenScreen.kt`
    - **Description**: Token selection screen for swap operations.
    - **User Actions**: Search tokens, select from list, view balances.
    - **Navigation**: Back to swap screen with selected token.

### Settings Network Features
72. **NetworkBottomSheetScreen** - `features/settings/network/src/commonMain/kotlin/com/mangala/wallet/features/settings/network/NetworkBottomSheetScreen.kt`
    - **Description**: Bottom sheet for quick network switching.
    - **User Actions**: Select network, view network status, add custom network.
    - **Navigation**: Closes sheet with network selected.

### Settings Menu Features
73. **WalletDetailsScreen** (Cold) - `features/settings/menu_cold/src/commonMain/kotlin/com/mangala/wallet/features/menu/presentation/wallet/details/WalletDetailsScreen.kt`
    - **Description**: Cold wallet details and management screen.
    - **User Actions**: View wallet info, export public keys, manage cold storage.
    - **Navigation**: To backup options, security settings.

74. **PreferencesScreen** (Pro) - `features/settings/menu_pro/src/commonMain/kotlin/com/mangala/wallet/features/menu/presentation/preferences/PreferencesScreen.kt`
    - **Description**: Pro version preferences with advanced options.
    - **User Actions**: Configure advanced settings, manage defaults, set preferences.
    - **Navigation**: To specific preference screens.

75. **MenuScreen** (Pro) - `features/settings/menu_pro/src/commonMain/kotlin/com/mangala/wallet/features/menu/presentation/menu/MenuScreen.kt`
    - **Description**: Pro version main menu with all features.
    - **User Actions**: Access all settings, view account info, manage app.
    - **Navigation**: To all settings subscreens and features.

### Settings Contacts Features
76. **ContactDetailScreen** - `features/settings/contacts/src/commonMain/kotlin/com/mangala/wallet/features/contacts/presentation/contactdetail/ContactDetailScreen.kt`
    - **Description**: View and edit contact details.
    - **User Actions**: Edit contact info, delete contact, copy addresses.
    - **Navigation**: To edit screen, back to contacts list.

77. **AddContactScreen** - `features/settings/contacts/src/commonMain/kotlin/com/mangala/wallet/features/contacts/presentation/addcontact/AddContactScreen.kt`
    - **Description**: Add new contact with wallet addresses.
    - **User Actions**: Enter name, add addresses for different chains, save.
    - **Navigation**: Back to contacts list after saving.

78. **ContactsScreen** - `features/settings/contacts/src/commonMain/kotlin/com/mangala/wallet/features/contacts/presentation/ContactsScreen.kt`
    - **Description**: Main contacts list management.
    - **User Actions**: View contacts, search, add new, select for sending.
    - **Navigation**: To AddContactScreen, ContactDetailScreen.

### Send UI Features
79. **EvmStep4VerifyAndSendScreen** - `features/send_ui/src/commonMain/kotlin/com/mangala/wallet/features/send/presentation/step4/EvmStep4VerifyAndSendScreen.kt`
    - **Description**: Final verification step for EVM token transfers.
    - **User Actions**: Review details, adjust gas, confirm send.
    - **Navigation**: To success screen or error handling.

### Send Pro Features
80. **BitcoinStep4VerifyAndSendScreen** - `features/send_pro/src/commonMain/kotlin/com/mangala/wallet/features/send/presentation/step4/bitcoin/BitcoinStep4VerifyAndSendScreen.kt`
    - **Description**: Final verification for Bitcoin transactions.
    - **User Actions**: Review UTXOs, set fee rate, confirm transaction.
    - **Navigation**: To success screen or error handling.

81. **AntelopeStep4VerifyAndSendScreen** - `features/send_pro/src/commonMain/kotlin/com/mangala/wallet/features/send/presentation/step4/antelope/AntelopeStep4VerifyAndSendScreen.kt`
    - **Description**: Final verification for Antelope transactions.
    - **User Actions**: Review actions, set resources, confirm transaction.
    - **Navigation**: To success screen or error handling.

### Send Base Features
82. **TransactionFeeScreen** - `features/send_base/src/commonMain/kotlin/com/mangala/wallet/features/send_base/transactionfee/TransactionFeeScreen.kt`
    - **Description**: Configure transaction fee settings and priority.
    - **User Actions**: Select fee tier, set custom gas price, view estimates.
    - **Navigation**: Back to send flow with fee configured.

83. **Step5SendSuccessScreen** - `features/send_base/src/commonMain/kotlin/com/mangala/wallet/features/send_base/step5/Step5SendSuccessScreen.kt`
    - **Description**: Success screen after transaction is sent.
    - **User Actions**: View transaction hash, share details, return to wallet.
    - **Navigation**: To transaction details or home screen.

84. **Step3SelectAmountScreen** - `features/send_base/src/commonMain/kotlin/com/mangala/wallet/features/send_base/step3/Step3SelectAmountScreen.kt`
    - **Description**: Amount input step in send flow.
    - **User Actions**: Enter amount, use max button, view balance.
    - **Navigation**: To next step in send flow.

85. **Step2SelectNetworkScreen** - `features/send_base/src/commonMain/kotlin/com/mangala/wallet/features/send_base/step2/Step2SelectNetworkScreen.kt`
    - **Description**: Network selection step in multi-chain send.
    - **User Actions**: Choose blockchain network, view balances per network.
    - **Navigation**: To amount selection step.

86. **SelectRecipientTypeScreen** - `features/send_base/src/commonMain/kotlin/com/mangala/wallet/features/send_base/selectrecipienttype/SelectRecipientTypeScreen.kt`
    - **Description**: Choose recipient input method.
    - **User Actions**: Select address input, QR scan, or contacts.
    - **Navigation**: To appropriate recipient selection screen.

87. **ReceiveTokenPickAccountScreen** - `features/send_base/src/commonMain/kotlin/com/mangala/wallet/features/send_base/pickaccount/ReceiveTokenPickAccountScreen.kt`
    - **Description**: Select account to receive tokens.
    - **User Actions**: Choose from available accounts, view addresses.
    - **Navigation**: To receive QR screen with selected account.

### Wallet Cold Features
88. **SyncAccountScreen** - `features/wallet_cold/src/commonMain/kotlin/com/mangala/wallet/features/wallet/presentation/syncaccount/SyncAccountScreen.kt`
    - **Description**: Cold wallet sync screen for offline account management.
    - **User Actions**: Display sync QR, scan sync data, confirm sync.
    - **Navigation**: To QR display or scanner screens.

89. **SignedTransactionQrScreen** - `features/wallet_cold/src/commonMain/kotlin/com/mangala/wallet/features/wallet/presentation/signedtransactionqr/SignedTransactionQrScreen.kt`
    - **Description**: Display signed transaction as QR for broadcasting.
    - **User Actions**: Show QR code, adjust brightness, share QR.
    - **Navigation**: Back to cold wallet main screen.

90. **ConfirmQrScreen** - `features/wallet_cold/src/commonMain/kotlin/com/mangala/wallet/features/wallet/presentation/confirmqr/ConfirmQrScreen.kt`
    - **Description**: Confirm scanned QR transaction data.
    - **User Actions**: Review transaction, sign offline, reject.
    - **Navigation**: To SignedTransactionQrScreen after signing.

### QR Code Features
91. **TransactionQrScreen** - `features/transactionqr_ui/src/commonMain/kotlin/com/mangala/wallet/features/transactionqr/presentation/TransactionQrScreen.kt`
    - **Description**: Display transaction data as QR for cold wallet signing.
    - **User Actions**: Show QR, adjust display settings, scan response.
    - **Navigation**: To transaction completion or error screens.

92. **TransactionQrScreen** - `features/swap_ui/src/commonMain/kotlin/com/mangala/wallet/features/swap/presentation/qr/TransactionQrScreen.kt`
    - **Description**: Swap-specific transaction QR display.
    - **User Actions**: Display swap transaction for signing, scan result.
    - **Navigation**: Back to swap flow with signed transaction.

93. **TransactionQrScreen** - `features/browser_bridge_ui/src/commonMain/kotlin/com/mangala/browser/presentation/qr/TransactionQrScreen.kt`
    - **Description**: Browser bridge transaction QR display.
    - **User Actions**: Show dApp transaction for cold signing.
    - **Navigation**: Back to browser with signed result.

### Core PIN Features
94. **UnlockPinScreen** - `core/pin/src/commonMain/kotlin/com/mangala/wallet/pin/presentation/unlock/UnlockPinScreen.kt`
    - **Description**: PIN entry screen for unlocking wallet features.
    - **User Actions**: Enter PIN, use biometrics if enabled.
    - **Navigation**: To requested feature after successful unlock.

95. **ConfirmPinScreen** - `core/pin/src/commonMain/kotlin/com/mangala/wallet/pin/presentation/confirm/ConfirmPinScreen.kt`
    - **Description**: PIN confirmation during setup or change.
    - **User Actions**: Re-enter PIN to confirm, correct mismatches.
    - **Navigation**: To completion or back to PIN setup.

96. **SetupPinScreen** - `core/pin/src/commonMain/kotlin/com/mangala/wallet/pin/presentation/setup/SetupPinScreen.kt`
    - **Description**: Initial PIN creation screen.
    - **User Actions**: Create new PIN, view security tips.
    - **Navigation**: To ConfirmPinScreen for verification.

### Core Wallet Features (Additional)
97. **CreateWalletScreen** - `core/wallet/src/commonMain/kotlin/com/mangala/wallet/wallet/presentation/create/CreateWalletScreen.kt`
    - **Description**: Main wallet creation screen.
    - **User Actions**: Generate new wallet, view seed phrase.
    - **Navigation**: To backup flow, PIN setup.

98. **ImportWalletGuideScreen** - `core/wallet/src/commonMain/kotlin/com/mangala/wallet/wallet/presentation/import/ImportWalletGuideScreen.kt`
    - **Description**: Guide for importing existing wallets.
    - **User Actions**: Choose import method, understand process.
    - **Navigation**: To specific import screens.

99. **RestoreRecoveryPhraseScreen** - `core/wallet/src/commonMain/kotlin/com/mangala/wallet/wallet/presentation/restore/RestoreRecoveryPhraseScreen.kt`
    - **Description**: Restore wallet from recovery phrase.
    - **User Actions**: Enter recovery words, validate phrase.
    - **Navigation**: To wallet setup after successful restore.

### Receive Features
100. **ReceiveTokenScreen** - `features/receive/src/commonMain/kotlin/com/mangala/wallet/features/receive/presentation/ReceiveTokenScreen.kt`
    - **Description**: Main receive screen showing address and QR code.
    - **User Actions**: Display QR, copy address, set amount, share.
    - **Navigation**: To AddAmountToReceiveQrScreen, share functionality.

101. **AddAmountToReceiveQrScreen** - `features/receive/src/commonMain/kotlin/com/mangala/wallet/features/receive/presentation/AddAmountToReceiveQrScreen.kt`
    - **Description**: Add specific amount to receive request.
    - **User Actions**: Enter amount, generate QR with amount.
    - **Navigation**: Back to ReceiveTokenScreen with amount.

### EVM Snap Features
102. **ImportEOSAccountViaEVMScreen** - `features/evm_snap/src/commonMain/kotlin/com/mangala/wallet/features/evm_snap/presentation/import/ImportEOSAccountViaEVMScreen.kt`
    - **Description**: Import EOS accounts using EVM snap functionality.
    - **User Actions**: Connect EVM wallet, import EOS accounts.
    - **Navigation**: To ChooseImportedEosAccountScreen.

103. **ChooseImportedEosAccountScreen** - `features/evm_snap/src/commonMain/kotlin/com/mangala/wallet/features/evm_snap/presentation/import/ChooseImportedEosAccountScreen.kt`
    - **Description**: Select from discovered EOS accounts.
    - **User Actions**: Choose accounts to import, configure settings.
    - **Navigation**: To account list after import.

104. **CreateEosAccountViaEVMScreen** - `features/evm_snap/src/commonMain/kotlin/com/mangala/wallet/features/evm_snap/presentation/create/CreateEosAccountViaEVMScreen.kt`
    - **Description**: Create new EOS account via EVM bridge.
    - **User Actions**: Set account name, configure resources.
    - **Navigation**: To success screen after creation.

### Antelope RAM Features
105. **RamTransferScreen** - `features/chains/antelope_ram/src/commonMain/kotlin/com/mangala/wallet/features/chains/antelope/ram/presentation/transfer/RamTransferScreen.kt`
    - **Description**: Transfer RAM between Antelope accounts.
    - **User Actions**: Enter recipient, set amount, confirm transfer.
    - **Navigation**: To transaction confirmation.

106. **RamDetailScreen** - `features/chains/antelope_ram/src/commonMain/kotlin/com/mangala/wallet/features/chains/antelope/ram/presentation/details/RamDetailScreen.kt`
    - **Description**: Detailed RAM holdings and market information.
    - **User Actions**: View RAM balance, market price, usage stats.
    - **Navigation**: To BuySellRamScreen, RamTransferScreen.

107. **BuySellRamScreen** - `features/chains/antelope_ram/src/commonMain/kotlin/com/mangala/wallet/features/chains/antelope/ram/presentation/buysell/BuySellRamScreen.kt`
    - **Description**: Buy or sell RAM on Antelope network.
    - **User Actions**: Toggle buy/sell, enter amount, execute trade.
    - **Navigation**: To transaction confirmation.

108. **ChartRamScreen** - `features/chains/antelope_ram/src/commonMain/kotlin/com/mangala/wallet/features/chains/antelope/ram/presentation/details/bottomSheet/ChartRamScreen.kt`
    - **Description**: RAM price chart and market analytics.
    - **User Actions**: View price history, analyze trends.
    - **Navigation**: Back to RAM details.

### Antelope Create Account Features (Additional)
109. **Step2SelectAccountNameScreen** - `features/chains/antelope_create_account/src/commonMain/kotlin/com/mangala/wallet/features/chains/antelope/create_account/presentation/step2/Step2SelectAccountNameScreen.kt`
    - **Description**: Account name selection in creation flow.
    - **User Actions**: Enter account name, check availability.
    - **Navigation**: To payment options screen.

110. **CreateByFriendBottomSheetScreen** - `features/chains/antelope_create_account/src/commonMain/kotlin/com/mangala/wallet/features/chains/antelope/create_account/presentation/step3/createbyfriend/CreateByFriendBottomSheetScreen.kt`
    - **Description**: Instructions for friend-assisted account creation.
    - **User Actions**: View instructions, copy details, share.
    - **Navigation**: Closes sheet with action taken.

111. **SelectAccountNameBottomSheetScreen** - `features/chains/antelope_create_account/src/commonMain/kotlin/com/mangala/wallet/features/chains/antelope/create_account/presentation/step3/selectaccountname/SelectAccountNameBottomSheetScreen.kt`
    - **Description**: Bottom sheet for quick name selection.
    - **User Actions**: Choose from suggested names, enter custom.
    - **Navigation**: Returns selected name to parent.

112. **SelectPaymentAccountScreen** - `features/chains/antelope_create_account/src/commonMain/kotlin/com/mangala/wallet/features/chains/antelope/create_account/presentation/step3/selectpaymentaccount/SelectPaymentAccountScreen.kt`
    - **Description**: Choose account to pay for creation.
    - **User Actions**: Select payment account, view balances.
    - **Navigation**: To payment confirmation.

113. **Step3CreateAccountPaymentScreen** - `features/chains/antelope_create_account/src/commonMain/kotlin/com/mangala/wallet/features/chains/antelope/create_account/presentation/step3/Step3CreateAccountPaymentScreen.kt`
    - **Description**: Payment step for account creation.
    - **User Actions**: Review costs, select payment method.
    - **Navigation**: To purchase confirmation or IAP.

114. **SelectAccountTypeScreen** - `features/chains/antelope_create_account/src/commonMain/kotlin/com/mangala/wallet/features/chains/antelope/create_account/presentation/selectaccounttype/SelectAccountTypeScreen.kt`
    - **Description**: Choose between account creation methods.
    - **User Actions**: Select creation type, view requirements.
    - **Navigation**: To appropriate creation flow.

115. **IapCreateAccountScreen** - `features/chains/antelope_create_account/src/commonMain/kotlin/com/mangala/wallet/features/chains/antelope/create_account/presentation/iap/IapCreateAccountScreen.kt`
    - **Description**: In-app purchase for account creation.
    - **User Actions**: Purchase account, complete IAP flow.
    - **Navigation**: To success screen after purchase.

116. **CreateAccountForFriendScreen** - `features/chains/antelope_create_account/src/commonMain/kotlin/com/mangala/wallet/features/chains/antelope/create_account/presentation/forfriend/CreateAccountForFriendScreen.kt`
    - **Description**: Create account on behalf of others.
    - **User Actions**: Enter friend's details, pay for account.
    - **Navigation**: To confirmation after creation.

### Antelope Pro Features (Additional)
102. **NetAndCpuScreen** - `features/chains/antelope_pro/src/commonMain/kotlin/com/mangala/wallet/features/chains/antelope/presentation/netAndCpu/NetAndCpuScreen.kt`
    - **Description**: Manage NET and CPU resources for Antelope accounts.
    - **User Actions**: View resource usage, stake/unstake, rent resources.
    - **Navigation**: To StakeForResourceScreen, RentViaRexScreen.

103. **BackupAntelopePrivateKeyScreen** - `features/chains/antelope_pro/src/commonMain/kotlin/com/mangala/wallet/features/chains/antelope/presentation/backupaccount/backupPrivateKey/BackupAntelopePrivateKeyScreen.kt`
    - **Description**: Backup private keys for Antelope accounts.
    - **User Actions**: Display private key, copy to clipboard, export securely.
    - **Navigation**: To backup completion, security warnings.

104. **Step1ImportAccountPrivateKeyScreen** - `features/chains/antelope_pro/src/commonMain/kotlin/com/mangala/wallet/features/chains/antelope/presentation/importaccount/Step1ImportAccountPrivateKeyScreen.kt`
    - **Description**: First step in importing Antelope account by private key.
    - **User Actions**: Enter or paste private key, validate format.
    - **Navigation**: To account selection screen.

105. **GiftRamScreen** - `features/chains/antelope_pro/src/commonMain/kotlin/com/mangala/wallet/features/chains/antelope/presentation/giftram/GiftRamScreen.kt`
    - **Description**: Gift RAM to other Antelope accounts.
    - **User Actions**: Enter recipient, specify RAM amount, send gift.
    - **Navigation**: To transaction confirmation.

106. **MyProposalDetailScreen** - `features/chains/antelope_pro/src/commonMain/kotlin/com/mangala/wallet/features/chains/antelope/presentation/proposal/myProposal/detail/MyProposalDetailScreen.kt`
    - **Description**: Detailed view of user's own multisig proposals.
    - **User Actions**: View status, manage approvals, cancel if needed.
    - **Navigation**: To approval management, transaction screens.

107. **ExpiredProposalDetailScreen** - `features/chains/antelope_pro/src/commonMain/kotlin/com/mangala/wallet/features/chains/antelope/presentation/proposal/expiredProposal/detail/ExpiredProposalDetailScreen.kt`
    - **Description**: Details of expired multisig proposals.
    - **User Actions**: View expired proposal info, clean up old proposals.
    - **Navigation**: Back to proposal list.

108. **ApprovalProposalDetailScreen** - `features/chains/antelope_pro/src/commonMain/kotlin/com/mangala/wallet/features/chains/antelope/presentation/proposal/approvals/detail/ApprovalProposalDetailScreen.kt`
    - **Description**: Proposals awaiting user's approval.
    - **User Actions**: Review proposal, approve or reject, view signers.
    - **Navigation**: To transaction confirmation after action.

109. **PowerUpScreen** - `features/chains/antelope_pro/src/commonMain/kotlin/com/mangala/wallet/features/chains/antelope/presentation/powerup/PowerUpScreen.kt`
    - **Description**: PowerUp resources using REX system.
    - **User Actions**: Select resource type, set amount, execute power up.
    - **Navigation**: To transaction confirmation.

110. **UnLinkAuthScreen** - `features/chains/antelope_pro/src/commonMain/kotlin/com/mangala/wallet/features/chains/antelope/presentation/permission/unlinkauth/UnLinkAuthScreen.kt`
    - **Description**: Remove authorization links between permissions and actions.
    - **User Actions**: Select linked auths, confirm removal.
    - **Navigation**: To transaction confirmation.

111. **PermissionListScreen** - `features/chains/antelope_pro/src/commonMain/kotlin/com/mangala/wallet/features/chains/antelope/presentation/permission/list/PermissionListScreen.kt`
    - **Description**: List all permissions for an Antelope account.
    - **User Actions**: View permissions, navigate to details, create new.
    - **Navigation**: To PermissionDetailScreen, CreatePermissionScreen.

112. **LinkAuthScreen** - `features/chains/antelope_pro/src/commonMain/kotlin/com/mangala/wallet/features/chains/antelope/presentation/permission/linkauth/LinkAuthScreen.kt`
    - **Description**: Link specific actions to permissions.
    - **User Actions**: Select action, choose permission, create link.
    - **Navigation**: To transaction confirmation.

113. **PermissionDetailScreen** - `features/chains/antelope_pro/src/commonMain/kotlin/com/mangala/wallet/features/chains/antelope/presentation/permission/detail/PermissionDetailScreen.kt`
    - **Description**: Detailed view of a specific permission.
    - **User Actions**: View authorities, threshold, linked actions.
    - **Navigation**: To update permission, link/unlink auth screens.

114. **CreatePermissionScreen** - `features/chains/antelope_pro/src/commonMain/kotlin/com/mangala/wallet/features/chains/antelope/presentation/permission/createcustom/CreatePermissionScreen.kt`
    - **Description**: Create custom permissions for Antelope accounts.
    - **User Actions**: Set permission name, threshold, add authorities.
    - **Navigation**: To transaction confirmation.

115. **PermissionScreen** - `features/chains/antelope_pro/src/commonMain/kotlin/com/mangala/wallet/features/chains/antelope/presentation/permission/PermissionScreen.kt`
    - **Description**: Main permission management screen.
    - **User Actions**: Manage all permission-related features.
    - **Navigation**: To various permission management screens.

116. **StakeForResourceScreen** - `features/chains/antelope_pro/src/commonMain/kotlin/com/mangala/wallet/features/chains/antelope/presentation/netAndCpu/stakeforresource/StakeForResourceScreen.kt`
    - **Description**: Stake system tokens for NET/CPU resources.
    - **User Actions**: Choose resource type, enter amount, stake tokens.
    - **Navigation**: To transaction confirmation.

117. **RentViaRexScreen** - `features/chains/antelope_pro/src/commonMain/kotlin/com/mangala/wallet/features/chains/antelope/presentation/netAndCpu/rentviarex/RentViaRexScreen.kt`
    - **Description**: Rent resources via REX system.
    - **User Actions**: Select rental duration, amount, execute rental.
    - **Navigation**: To transaction confirmation.

118. **ProposalTableScreen** - `features/chains/antelope_pro/src/commonMain/kotlin/com/mangala/wallet/features/chains/antelope/presentation/multisig/proposal/list/ProposalTableScreen.kt`
    - **Description**: Table view of multisig proposals.
    - **User Actions**: Sort, filter, search proposals in table format.
    - **Navigation**: To individual proposal details.

119. **ProposalsByProposerScreen** - `features/chains/antelope_pro/src/commonMain/kotlin/com/mangala/wallet/features/chains/antelope/presentation/multisig/proposal/list/ProposalsByProposerScreen.kt`
    - **Description**: Proposals grouped by proposer.
    - **User Actions**: View proposals by creator, filter by proposer.
    - **Navigation**: To proposal details.

120. **ProposalDetailScreen** - `features/chains/antelope_pro/src/commonMain/kotlin/com/mangala/wallet/features/chains/antelope/presentation/multisig/proposal/detail/ProposalDetailScreen.kt`
    - **Description**: Comprehensive detail view of multisig proposals.
    - **User Actions**: Review all proposal data, take actions based on role.
    - **Navigation**: To approval/rejection flows.

121. **MultisigProposalApproverScreen** - `features/chains/antelope_pro/src/commonMain/kotlin/com/mangala/wallet/features/chains/antelope/presentation/multisig/proposal/create/approver/MultisigProposalApproverScreen.kt`
    - **Description**: Select approvers for new multisig proposal.
    - **User Actions**: Add/remove approvers, set approval requirements.
    - **Navigation**: Back to proposal creation.

122. **MultisigProposalActionScreen** - `features/chains/antelope_pro/src/commonMain/kotlin/com/mangala/wallet/features/chains/antelope/presentation/multisig/proposal/create/action/MultisigProposalActionScreen.kt`
    - **Description**: Define actions for multisig proposal.
    - **User Actions**: Create transaction actions, set parameters.
    - **Navigation**: Back to proposal creation.

123. **ImportAccountByKeyCertScreen** - `features/chains/antelope_pro/src/commonMain/kotlin/com/mangala/wallet/features/chains/antelope/presentation/importaccount/keycert/ImportAccountByKeyCertScreen.kt`
    - **Description**: Import Antelope account using key certificate.
    - **User Actions**: Upload/paste certificate, decrypt if needed.
    - **Navigation**: To account import confirmation.

124. **Step2ImportAccountSelectAccountScreen** - `features/chains/antelope_pro/src/commonMain/kotlin/com/mangala/wallet/features/chains/antelope/presentation/importaccount/step2/Step2ImportAccountSelectAccountScreen.kt`
    - **Description**: Select account after key validation.
    - **User Actions**: Choose from available accounts for key.
    - **Navigation**: To import completion.

125. **SelectPermissionToBackupScreen** - `features/chains/antelope_pro/src/commonMain/kotlin/com/mangala/wallet/features/chains/antelope/presentation/backupaccount/selectPermissionToBackup/SelectPermissionToBackupScreen.kt`
    - **Description**: Choose which permissions to backup.
    - **User Actions**: Select permissions, choose backup method.
    - **Navigation**: To backup execution screen.

126. **EsrScreen** - `features/chains/antelope_pro/src/commonMain/kotlin/com/mangala/wallet/features/chains/antelope/presentation/esr/EsrScreen.kt`
    - **Description**: Handle EOSIO Signing Requests (ESR).
    - **User Actions**: Parse ESR, review actions, sign request.
    - **Navigation**: To transaction confirmation.

127. **GuideBackupAccountScreen** - `features/chains/antelope_pro/src/commonMain/kotlin/com/mangala/wallet/features/chains/antelope/presentation/backupaccount/guideBackupAccount/GuideBackupAccountScreen.kt`
    - **Description**: Guide for backing up Antelope accounts.
    - **User Actions**: Read backup instructions, choose backup method.
    - **Navigation**: To specific backup screens.

128. **BackupAntelopeAccountScreen** - `features/chains/antelope_pro/src/commonMain/kotlin/com/mangala/wallet/features/chains/antelope/presentation/backupaccount/BackupAntelopeAccountScreen.kt`
    - **Description**: Main backup screen for Antelope accounts.
    - **User Actions**: Select backup type, initiate backup process.
    - **Navigation**: To specific backup method screens.

### Antelope UI Features (Additional)
129. **CreateAccountScreen** - `features/chains/antelope_ui/src/commonMain/kotlin/com/mangala/wallet/features/chains/antelope/presentation/createaccount/CreateAccountScreen.kt`
    - **Description**: Create new Antelope account in UI wallet.
    - **User Actions**: Enter account details, configure resources.
    - **Navigation**: To account creation flow completion.

### Portfolio Features
130. **PortfolioScreen** - `features/portfolio/src/commonMain/kotlin/com/mangala/wallet/features/portfolio/presentation/PortfolioScreen.kt`
    - **Description**: Portfolio overview showing all assets and values.
    - **User Actions**: View total portfolio value, asset allocation, performance.
    - **Navigation**: To specific asset details, charts.

### Crypto Payment Features
131. **PayWithCryptoScreen** - `features/crypto_payment/src/commonMain/kotlin/com/mangala/features/crypto_payment/presentation/PayWithCryptoScreen.kt`
    - **Description**: Main crypto payment interface for merchant integration.
    - **User Actions**: Select payment method, review amount, confirm payment.
    - **Navigation**: To payment confirmation, error screens.

132. **CryptoPaymentErrorScreen** - `features/crypto_payment/src/commonMain/kotlin/com/mangala/features/crypto_payment/presentation/CryptoPaymentErrorScreen.kt`
    - **Description**: Error screen for failed crypto payments.
    - **User Actions**: View error details, retry payment, contact support.
    - **Navigation**: Back to payment or exit flow.

133. **ChangeNetworkForPaymentScreen** - `features/crypto_payment/src/commonMain/kotlin/com/mangala/features/crypto_payment/presentation/ChangeNetworkForPaymentScreen.kt`
    - **Description**: Switch networks for crypto payment compatibility.
    - **User Actions**: Select compatible network, confirm switch.
    - **Navigation**: Back to payment with new network.

134. **AllowanceScreen** - `features/crypto_payment/src/commonMain/kotlin/com/mangala/features/crypto_payment/presentation/AllowanceScreen.kt`
    - **Description**: Set token allowance for payment contracts.
    - **User Actions**: Review required allowance, approve spending limit.
    - **Navigation**: To payment flow after approval.

135. **SelectWalletBottomSheetScreen** - `features/crypto_payment/src/commonMain/kotlin/com/mangala/features/crypto_payment/presentation/bottom_sheets/SelectWalletBottomSheetScreen.kt`
    - **Description**: Choose wallet for payment.
    - **User Actions**: Select from available wallets, add new wallet.
    - **Navigation**: Returns selected wallet to payment flow.

136. **SelectPaymentMethodScreen** - `features/crypto_payment/src/commonMain/kotlin/com/mangala/features/crypto_payment/presentation/SelectPaymentMethodScreen.kt`
    - **Description**: Choose crypto payment method.
    - **User Actions**: Select cryptocurrency, view rates, confirm choice.
    - **Navigation**: To payment detail screen.

137. **SelectNetworkBottomSheetScreen** - `features/crypto_payment/src/commonMain/kotlin/com/mangala/features/crypto_payment/presentation/bottom_sheets/SelectNetworkBottomSheetScreen.kt`
    - **Description**: Network selection for payment.
    - **User Actions**: Choose blockchain network, view fees.
    - **Navigation**: Returns selected network to parent.

138. **SelectAccountBottomSheetScreen** - `features/crypto_payment/src/commonMain/kotlin/com/mangala/features/crypto_payment/presentation/bottom_sheets/SelectAccountBottomSheetScreen.kt`
    - **Description**: Account selection for payment source.
    - **User Actions**: Choose payment account, view balances.
    - **Navigation**: Returns selected account to parent.

139. **PaymentDetailScreen** - `features/crypto_payment/src/commonMain/kotlin/com/mangala/features/crypto_payment/presentation/PaymentDetailScreen.kt`
    - **Description**: Payment details and confirmation.
    - **User Actions**: Review payment details, confirm transaction.
    - **Navigation**: To success or error screens.

## Other Screen-related Implementations

### SharedScreen
The project also uses `SharedScreen` (`common/ui/src/commonMain/kotlin/com/mangala/wallet/ui/SharedScreen.kt`) as a sealed class with many data objects and data classes for screen navigation.

### Tab Navigation
- **ScanTab** - `features/wallet_cold/src/commonMain/kotlin/com/mangala/wallet/features/wallet/presentation/ScanTab.kt` (implements `DestinationTab` which extends `Tab`)

## Notes
- Many features use ScreenModel pattern alongside the Screen/BaseScreen pattern
- `BaseScreen` is the custom base class that extends `cafe.adriel.voyager.core.screen.Screen` and adds additional functionality like screen models and scaffolding
- Some screens have duplicate implementations for different build variants (cold, pro, ui)