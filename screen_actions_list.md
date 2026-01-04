# Mangala Wallet Screen Actions

This document lists all the actions available on key screens in the Mangala Wallet application.

## Authentication Screens

### LockScreen
- Enter PIN to unlock
- Use biometric authentication (if enabled)
- Access forgot PIN option

### ForgotPinScreen
- Initiate PIN recovery process
- Navigate back to previous screen

### SetupPinScreen
- Create new PIN
- View security tips

### ConfirmPinScreen
- Re-enter PIN to confirm
- Correct mismatches

### UnlockPinScreen
- Enter PIN
- Use biometrics if enabled

### BiometryScreen
- Enable biometric authentication
- Skip biometric setup
- Navigate back

## Wallet Management

### HomeScreen
- View portfolio overview
- Navigate to main features
- Manage wallets
- Access settings

### WalletMainScreen
- View balances
- Access features
- Manage accounts
- Refresh data

### CreateWalletScreen
- Generate new wallet
- View seed phrase

### BackupWalletAlertScreen
- Choose to backup now
- Skip backup at own risk

### BackupWalletGuideScreen
- Navigate through backup guide pages
- Start backup process from page 2
- Navigate back to previous screen

### ShowRecoveryPhraseScreen
- View recovery phrase
- Copy words
- Confirm written down

### VerifyRecoveryPhraseScreen
- Enter specific words from phrase
- Confirm backup

### BackupWalletDoneScreen
- Confirm backup completion
- Understand backup importance

### RestoreWalletGuideScreen
- Learn restore process
- Choose restore method

### RestoreRecoveryPhraseScreen
- Enter recovery words
- Validate phrase

### ImportWalletGuideScreen
- Choose import method
- Understand process

### ResetWalletScreen
- Confirm reset
- Understand data loss warning
- Execute reset

## Account Management

### ManageAccountsScreen
- View account list
- Add new accounts
- Remove accounts
- Switch active account

### AccountDetailsScreen
- View account information
- Copy account address
- Access account settings
- Navigate to transaction history

### AddAccountScreen (EVM)
- Create new account
- Import private key
- Derive from HD wallet

### BitcoinCreateAccountScreen
- Generate Bitcoin address
- Configure account type
- Set label

### CreateAccountNotificationScreen
- View account creation suggestions
- Dismiss or proceed

### SyncAccountScreen
- Generate sync code
- Scan sync QR
- Confirm sync

## Send & Receive

### SendScreen
- Initiate transfer
- Select recipient/token/amount
- Review transaction details

### SelectRecipientTypeScreen
- Select address input
- QR scan option
- Contacts selection

### Step2SelectNetworkScreen
- Choose blockchain network
- View balances per network

### Step3SelectAmountScreen
- Enter amount
- Use max button
- View balance

### TransactionFeeScreen
- Select fee tier
- Set custom gas price
- View estimates

### EvmStep4VerifyAndSendScreen
- Review details
- Adjust gas
- Confirm send

### BitcoinStep4VerifyAndSendScreen
- Review UTXOs
- Set fee rate
- Confirm transaction

### AntelopeStep4VerifyAndSendScreen
- Review actions
- Set resources
- Confirm transaction

### Step5SendSuccessScreen
- View transaction hash
- Share details
- Return to wallet

### ReceiveTokenScreen
- Display QR
- Copy address
- Set amount
- Share

### EditReceiveAmountScreen
- Enter desired receive amount
- Confirm amount selection
- Clear/modify amount

### ReceiveTokenPickAccountScreen
- Choose from available accounts
- View addresses

## Antelope Features

### ManageAntelopeAccountScreen
- View account resources
- Manage permissions
- Stake/unstake resources
- Access account tools

### NetAndCpuScreen
- View resource usage
- Stake/unstake resources
- Rent resources

### PermissionScreen
- Manage all permission-related features
- Navigate to permission management screens

### PermissionListScreen
- View permissions
- Navigate to details
- Create new permissions

### PermissionDetailScreen
- View authorities
- View threshold
- View linked actions

### UpdatePermissionScreen
- Modify permission settings
- Update authority keys
- Add/remove permissions
- Save permission changes

### CreatePermissionScreen
- Set permission name
- Set threshold
- Add authorities

### LinkAuthScreen
- Select action
- Choose permission
- Create link

### UnLinkAuthScreen
- Select linked auths
- Confirm removal

### StakeForResourceScreen
- Choose resource type
- Enter amount
- Stake tokens

### RentViaRexScreen
- Select rental duration
- Set amount
- Execute rental

### PowerUpScreen
- Select resource type
- Set amount
- Execute power up

### ImportAccountScreen (Cold)
- Enter private key
- Scan QR code
- Import account
- Select account type

### ImportAccountScreen (UI)
- Enter private key
- Import via QR code
- Select network
- Verify account details

### CreateKeyPairScreen
- Generate new key pair
- Copy keys to clipboard
- Show/hide private key
- Save keys securely

### BackupAntelopeAccountScreen
- Select backup type
- Initiate backup process

### BackupWithKeyCertScreen
- Generate key certificate
- Export/save certificate
- Copy certificate data
- Share certificate securely

### BackupAntelopePrivateKeyScreen
- Display private key
- Copy to clipboard
- Export securely

### Step1ImportAccountPrivateKeyScreen
- Enter or paste private key
- Validate format

### Step2ImportAccountSelectAccountScreen
- Choose from available accounts for key

### SelectPermissionToBackupScreen
- Select permissions
- Choose backup method

### GuideBackupAccountScreen
- Read backup instructions
- Choose backup method

### ImportAccountByKeyCertScreen
- Upload/paste certificate
- Decrypt if needed

## Antelope Multisig

### AntelopeMultisigScreen
- View proposal list
- Create new proposal
- Approve/reject proposals
- Filter proposals by status

### MsigScreen
- View proposals by category
- Search proposals
- Filter by status
- Navigate to details

### CreateNewProposalScreen
- Add/remove actions
- Select approvers
- Set expiration time
- Submit proposal

### MultisigProposalActionScreen
- Create transaction actions
- Set parameters

### MultisigProposalApproverScreen
- Add/remove approvers
- Set approval requirements

### SelectAccountPermissionScreen
- Select permission level
- View permission details
- Confirm selection

### ProposalDetailScreen
- Review all proposal data
- Take actions based on role

### ExpiredProposalScreen
- View expired proposals
- Delete expired proposals
- View proposal details

### ExpiredProposalDetailScreen
- View expired proposal info
- Clean up old proposals

### MyProposalDetailScreen
- View status
- Manage approvals
- Cancel if needed

### ApprovalProposalDetailScreen
- Review proposal
- Approve or reject
- View signers

## Antelope RAM Management

### RamDetailScreen
- View RAM balance
- View market price
- View usage stats

### BuySellRamScreen
- Toggle buy/sell
- Enter amount
- Execute trade

### RamTransferScreen
- Enter recipient
- Set amount
- Confirm transfer

### ChartRamScreen
- View price history
- Analyze trends

### GiftRamScreen
- Enter recipient
- Specify RAM amount
- Send gift

## Account Creation

### Step1SelectAccountTypeScreen
- Select account type
- View account type benefits
- Continue to next step

### Step2SelectAccountNameScreen
- Enter account name
- Check availability

### Step3CreateAccountPaymentScreen
- Review costs
- Select payment method

### SelectPaymentAccountScreen
- Select payment account
- View balances

### IapCreateAccountScreen
- Purchase account
- Complete IAP flow

### CreateAccountForFriendScreen
- Enter friend's details
- Pay for account

### SelectAccountTypeScreen
- Select creation type
- View requirements

### CreateByFriendBottomSheetScreen
- View instructions
- Copy details
- Share

### SelectAccountNameBottomSheetScreen
- Choose from suggested names
- Enter custom name

## NFT Management

### NftMainScreen
- View NFT collection
- Filter by chain
- Search NFTs
- Import new NFTs

### NftDetailsScreen
- View NFT properties
- Transfer NFT
- View on explorer

### ImportNftScreen
- Enter NFT details
- Select network
- Confirm import

### SendNftScreen
- Enter recipient address
- Confirm transfer details
- Sign transaction

### SendNftConfirmationScreen
- Review transfer details
- Confirm or cancel
- Sign transaction

## Web3 & dApp Integration

### BrowserTabScreen
- Browse web3 sites
- Interact with dApps
- Manage bookmarks
- Navigate back/forward

### ConfirmTransactionScreen
- Confirm transaction
- Decline transaction
- View transaction details

### SignPersonalMessageScreen
- Sign the message
- Decline signing request
- Close screen via clear button

### SwitchChainScreen
- Confirm chain switch
- Decline chain switch request
- Close screen

### WalletConnectScreen
- Scan QR codes
- Manage connections
- Approve requests

### EsrScreen
- Parse ESR
- Review actions
- Sign request

## Cold Wallet Features

### ConfirmQrScreen
- Review transaction
- Sign offline
- Reject

### SignedTransactionQrScreen
- Show QR code
- Adjust brightness
- Share QR

### TransactionQrScreen (variants)
- Show QR
- Adjust display settings
- Scan response
- Display swap/dApp transaction for signing

## Token Swap

### SwapTokenScreen
- Select tokens to swap
- Enter amounts
- View rates
- Initiate swap

### SelectTokenScreen
- Search tokens
- Select from list
- View balances

### PreviewSwapTokenScreen
- Review rates
- Review fees
- Review slippage
- Confirm or cancel swap

## Transaction History

### TransactionHistoryScreen (EVM)
- View transactions
- Filter
- Search
- Pull to refresh

### TransactionInfoScreen (EVM)
- View transaction details
- Copy hashes
- View on explorer

### TransactionHistoryBitcoinScreen
- View transactions
- Check confirmations
- Refresh

### TransactionInfoBitcoinScreen
- View inputs/outputs
- View confirmations
- View fees

### TransactionHistoryAntelopeScreen
- View actions
- Filter by type
- Search transactions

### TransactionHistoryFilterBottomSheetScreen
- Filter by type
- Filter by date
- Filter by status
- Filter by amount

### TransactionHistoryFilterAntelopeBottomSheetScreen
- Filter by action type
- Filter by contract
- Filter by date

## Settings

### BaseMenuScreen
- Navigate to different settings sections
- Logout
- Manage preferences

### SecurityScreen
- Change PIN
- Enable biometrics
- Manage security preferences

### ThemeScreen
- Switch between light/dark themes
- Set auto theme
- Preview themes

### LanguageScreen
- Select preferred language
- Search languages
- Apply language change

### CurrencyScreen
- Choose fiat currency
- Search currencies
- Set default

### NetworkScreen
- Add custom networks
- Edit RPC URLs
- Switch networks

### NetworkBottomSheetScreen
- Select network
- View network status
- Add custom network

### NotificationsScreen
- Toggle notification types
- Set alert preferences
- Manage push notifications

### WalletScreen (variants)
- Manage wallets
- View backup status
- Configure wallet settings

### AddWalletScreen (variants)
- Create new wallet
- Import existing wallet
- Configure options

### WalletDetailsScreen (Cold)
- View wallet info
- Export public keys
- Manage cold storage

### PreferencesScreen (Pro)
- Configure advanced settings
- Manage defaults
- Set preferences

### MenuScreen (Pro)
- Access all settings
- View account info
- Manage app

### AboutUsScreen
- View app information
- Access terms of service
- Access privacy policy
- Access licenses

### ConnectWithUsScreen
- Access social media links
- Email support
- Visit website

### HelpCenterScreen
- Browse help topics
- Search FAQs
- Contact support

### IconsInAppScreen
- View icon meanings
- Search icons
- Understand symbology

### ShareAppScreen
- Share app link
- Send referral code
- Access social sharing

### DevMenuScreen
- Access debug tools
- View logs
- Toggle test features

## Contacts Management

### ContactsScreen
- View contacts
- Search contacts
- Add new contacts
- Select for sending

### ContactDetailScreen
- Edit contact info
- Delete contact
- Copy addresses

### AddContactScreen
- Enter name
- Add addresses for different chains
- Save contact

## EVM Snap Features

### ImportEOSAccountViaEVMScreen
- Connect EVM wallet
- Import EOS accounts

### ChooseImportedEosAccountScreen
- Choose accounts to import
- Configure settings

### CreateEosAccountViaEVMScreen
- Set account name
- Configure resources

## Crypto Payment

### PayWithCryptoScreen
- Select payment method
- Review amount
- Confirm payment

### CryptoPaymentErrorScreen
- View error details
- Retry payment
- Contact support

### ChangeNetworkForPaymentScreen
- Select compatible network
- Confirm switch

### AllowanceScreen
- Review required allowance
- Approve spending limit

### SelectWalletBottomSheetScreen
- Select from available wallets
- Add new wallet

### SelectPaymentMethodScreen
- Select cryptocurrency
- View rates
- Confirm choice

### SelectNetworkBottomSheetScreen
- Choose blockchain network
- View fees

### SelectAccountBottomSheetScreen
- Choose payment account
- View balances

### PaymentDetailScreen
- Review payment details
- Confirm transaction