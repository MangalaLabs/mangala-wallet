package com.mangala.wallet.utils.analytics

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.analytics.analytics
import dev.gitlive.firebase.analytics.logEvent

object MangalaAnalytics {
    fun trackScreenView(screenName: String, screenClass: String) {
        if (isAnalyticsEnabled().not()) return

        val analytics = Firebase.analytics

        analytics.logEvent(EventName.SCREEN_VIEW) {
            param(EventParam.SCREEN_NAME, screenName)
            param(EventParam.SCREEN_CLASS, screenClass)
        }
    }

    fun trackEvent(
        eventName: String,
        params: Map<String, String> = emptyMap()
    ) {
        if (isAnalyticsEnabled().not()) return

        val analytics = Firebase.analytics

        analytics.logEvent(eventName) {
            params.forEach { (key, value) ->
                param(key, value)
            }
        }
    }

    object EventName {
        internal const val SCREEN_VIEW = "screen_view"

        const val ONBOARDING_INITIATED = "onboarding_initiated"
        const val ONBOARDING_COMPLETED = "onboarding_completed"

        const val TRANSACTION_INITIATED = "transaction_initiated"
        const val TRANSACTION_SUBMITTED = "transaction_submitted"
        const val TRANSACTION_FAILED = "transaction_failed"

        const val SIGN_UP_STARTED = "sign_up_started"
        const val SIGN_UP_COMPLETED = "sign_up_completed"
        const val SIGN_UP_ERROR = "sign_up_error"

        const val SIGN_IN_STARTED = "sign_in_started"
        const val SIGN_IN_COMPLETED = "sign_in_completed"
        const val SIGN_IN_ERROR = "sign_in_error"

        const val CONVERSATION_CREATED = "conversation_created"
        const val CONVERSATION_OPENED = "conversation_opened"
        const val CONVERSATION_MESSAGE_SENT = "conversation_message_sent"
        const val CONVERSATION_MESSAGE_RECEIVED = "conversation_message_received"

        const val CONTACT_CREATE_COMPLETED = "contact_create_completed"

        const val QR_SCANNER_OPENED = "qr_scanner_opened"
        const val QR_SCANNER_RESULT_PARSED = "qr_scanner_result_parsed"
    }

    object EventParam {
        internal const val SCREEN_NAME = "screen_name"
        internal const val SCREEN_CLASS = "screen_class"
        const val ONBOARDING_STEP_NAME = "step_name"
        const val ERROR_TYPE = "error_type"
        const val QR_RESULT_TYPE = "qr_result_type"
    }

    object EventParamValue {
        const val ONBOARDING_STEP_CONVERSATION_UI = "conversation_ui"
        const val ONBOARDING_STEP_CREATE_WALLET = "create_wallet"
        const val ONBOARDING_STEP_IMPORT_WALLET = "import_wallet"

        const val SIGN_IN_ERROR_PASSKEY_NOT_SUPPORTED = "passkey_not_supported"
        const val SIGN_IN_ERROR_CREDENTIAL_NOT_FOUND = "credential_not_found"
        const val SIGN_IN_ERROR_OTHER_ERROR = "other_error"

        const val QR_RESULT_TYPE_ADDRESS = "address"
        const val QR_RESULT_TYPE_TRANSACTION = "transaction"
        const val QR_RESULT_TYPE_PAYMENT = "payment"
    }

    object Screens {
        const val MANAGE_ACCOUNTS = "ManageAccountsScreen"

        const val BROWSER_TAB = "BrowserTabScreen"
        const val BROWSER_SWITCH_CHAIN = "BrowserSwitchChainScreen"
        const val BROWSER_CONFIRM_TRANSACTION = "BrowserConfirmTransactionScreen"
        const val BROWSER_SIGN_PERSONAL_MESSAGE = "BrowserSignPersonalMessageScreen"

        const val NETWORK = "NetworkScreen"
        const val NETWORK_BOTTOM_SHEET = "NetworkBottomSheetScreen"

        const val CONFIRM_PIN = "ConfirmPinScreen"
        const val SETUP_PIN = "SetupPinScreen"

        const val BACKUP_WALLET_ALERT = "BackupWalletAlertScreen"
        const val BACKUP_WALLET_DONE = "BackupWalletDoneScreen"
        const val BACKUP_WALLET_GUIDE = "BackupWalletGuideScreen"

        const val EVM_SWAP_TOKEN = "EvmSwapTokenScreen"
        const val EVM_SWAP_SELECT_TOKEN = "EvmSwapSelectTokenScreen"
        const val EVM_SWAP_PREVIEW = "EvmSwapPreviewScreen"

        const val MENU = "MenuScreen"
        const val MENU_ADD_WALLET = "MenuAddWalletScreen"
        const val PREFERENCES = "PreferencesScreen"
        const val WALLET = "WalletScreen"
        const val THEME = "ThemeScreen"
        const val SHARE_APP = "ShareAppScreen"
        const val SECURITY = "SecurityScreen"
        const val NOTIFICATIONS = "NotificationsScreen"
        const val LANGUAGE = "LanguageScreen"

        const val CONTACTS = "ContactsScreen"
        const val ADD_CONTACTS = "AddContactsScreen"

        const val ANTELOPE_MULTISIG_PROPOSAL_ACTION = "AntelopeMultisigProposalActionScreen"

        const val TRANSACTION_HISTORY = "TransactionHistoryScreen"
        const val TRANSACTION_HISTORY_FILTER = "TransactionHistoryFilterScreen"
        const val EVM_TRANSACTION_INFO = "EvmTransactionInfoScreen"
        const val BITCOIN_TRANSACTION_INFO = "BitcoinTransactionInfoScreen"

        const val DEV_MENU = "DevMenuScreen" // Shouldn't be in production but we'll have to override it anyways

        const val WALLET_MAIN = "WalletMainScreen"

        const val HELP_CENTER = "HelpCenterScreen"
        const val CONNECT_WITH_US = "ConnectWithUsScreen"
        const val ABOUT_US = "AboutUsScreen"
        const val CURRENCY = "CurrencyScreen"
        const val CONTACT_DETAILS = "ContactDetailsScreen"

        const val RECEIVE_TOKEN = "ReceiveTokenScreen"
        const val RECEIVE_TOKEN_PICK_ACCOUNT = "ReceiveTokenPickAccountScreen"
        const val ADD_AMOUNT_TO_RECEIVE_QR = "AddAmountToReceiveQrScreen"

        const val TRANSACTION_QR = "TransactionQrScreen"

        const val SEND_SELECT_RECIPIENT_TYPE = "SendSelectRecipientTypeScreen"
        const val SEND_TOKEN_SELECT_NETWORK = "SendTokenSelectNetworkScreen"
        const val SEND_TOKEN_SELECT_AMOUNT = "SendTokenSelectAmountScreen"
        const val SEND_TOKEN_VERIFY_AND_SEND_EVM = "SendTokenVerifyAndSendEvmScreen"
        const val SEND_TOKEN_VERIFY_AND_SEND_ANTELOPE = "SendTokenVerifyAndSendAntelopeScreen"
        const val SEND_TOKEN_SUCCESS = "SendTokenSuccessScreen"

        const val ETICKET_BOOKING = "ETicketBookingScreen"
        const val ETICKET_CONFIRMATION = "ETicketConfirmationScreen"
        const val ETICKET_EVENT_DETAILS = "ETicketEventDetailsScreen"
        const val ETICKET_EVENTS_LIST = "ETicketEventsListScreen"
        const val ETICKET_USER_EVENT_FAVORITE = "ETicketUserEventFavoriteScreen"
        const val ETICKET_HOME = "ETicketHomeScreen"
        const val ETICKET_CATEGORY = "ETicketCategoryScreen"

        const val EVM_SHOW_RECOVERY_PHRASE = "EvmShowRecoveryPhraseScreen"
        const val EVM_VERIFY_RECOVERY_PHRASE = "EvmVerifyRecoveryPhraseScreen"
        const val EVM_CREATE_WALLET_GUIDE = "EvmCreateWalletGuideScreen"
        const val EVM_CREATE_WALLET = "EvmCreateWalletScreen"
        const val EVM_RESTORE_RECOVERY_PHRASE = "EvmRestoreRecoveryPhraseScreen"
        const val EVM_IMPORT_WALLET_SUCCESS = "EvmImportWalletSuccessScreen"
        const val EVM_WALLET_DETAILS = "EvmWalletDetailsScreen"
        const val EVM_ADD_ACCOUNT = "EvmAddAccountScreen"
        const val EVM_TRANSACTION_FEE = "EvmTransactionFeeScreen"
        const val EVM_RESET_WALLET = "EvmResetWalletScreen"

        const val EVM_NFT_MAIN = "EvmNftMainScreen"
        const val EVM_NFT_DETAILS = "EvmNftDetailsScreen"
        const val EVM_SEND_NFT_CONFIRMATION = "EvmSendNftConfirmationScreen"
        const val EVM_SEND_NFT = "EvmSendNftScreen"
        const val EVM_IMPORT_NFT = "EvmImportNftScreen"

        const val EVM_WALLET_CONNECT = "EvmWalletConnectScreen"

        const val EVM_SEND_SIGNED_TRANSACTION_SCREEN = "EvmSendSignedTransactionScreen"
        const val ANTELOPE_SIGN_TRANSACTION_SCREEN = "AntelopeSignTransactionScreen"

        const val ANTELOPE_BUY_RAM = "AntelopeBuyRamScreen"
        const val ANTELOPE_SELL_RAM = "AntelopeSellRamScreen"
        const val ANTELOPE_RAM_DETAILS = "AntelopeRamDetailsScreen"
        const val ANTELOPE_RAM_CHARTS = "AntelopeRamChartsScreen"
        const val ANTELOPE_RAM_TRANSFER = "AntelopeRamTransferScreen"

        const val ANTELOPE_CREATE_ACCOUNT_FOR_FRIEND = "AntelopeCreateAccountForFriendScreen"
        const val ANTELOPE_IAP_CREATE_ACCOUNT = "AntelopeIapCreateAccountScreen"
        const val ANTELOPE_CREATE_ACCOUNT_SELECT_ACCOUNT_TYPE = "AntelopeCreateAccountSelectAccountTypeScreen"
        const val ANTELOPE_CREATE_ACCOUNT_SELECT_ACCOUNT_NAME = "AntelopeCreateAccountSelectAccountNameScreen"
        const val ANTELOPE_CREATE_ACCOUNT_READY_TO_CLAIM = "AntelopeCreateAccountReadyToClaimScreen"
        const val ANTELOPE_CREATE_ACCOUNT_CREATING = "AntelopeCreateAccountCreatingScreen"
        const val ANTELOPE_CREATE_ACCOUNT_BACKUP_OPTIONS = "AntelopeCreateAccountBackupOptionsScreen"
        const val ANTELOPE_CREATE_ACCOUNT_PAYMENT = "AntelopeCreateAccountPaymentScreen"
        const val ANTELOPE_CREATE_ACCOUNT_UI = "AntelopeCreateAccountUiScreen"
        const val ANTELOPE_BACKUP_ACCOUNT = "AntelopeBackupAccountScreen"
        const val ANTELOPE_BACKUP_PRIVATE_KEY = "AntelopeBackupPrivateKeyScreen"
        const val ANTELOPE_BACKUP_ACCOUNT_GUIDE = "AntelopeGuideBackupAccountScreen"
        const val ANTELOPE_BACKUP_WITH_KEYCERT = "AntelopeBackupWithKeyCertScreen"
        const val ANTELOPE_BACKUP_ACCOUNT_SELECT_PERMISSION = "AntelopeBackupAccountSelectPermissionScreen"
        const val ANTELOPE_ESR = "AntelopeEsrScreen"
        const val ANTELOPE_IMPORT_ACCOUNT_PRIVATE_KEY = "AntelopeImportAccountPrivateKeyScreen"
        const val ANTELOPE_IMPORT_ACCOUNT_SELECT_ACCOUNT = "AntelopeImportAccountSelectAccountScreen"
        const val ANTELOPE_IMPORT_ACCOUNT_KEYCERT = "AntelopeImportAccountKeyCertScreen"
        const val ANTELOPE_IMPORT_ACCOUNT_UI = "AntelopeImportAccountUiScreen"
        const val ANTELOPE_IMPORT_ACCOUNT_COLD = "AntelopeImportAccountColdScreen"
        const val ANTELOPE_CREATE_KEY_PAIR_COLD = "AntelopeCreateKeyPairColdScreen"
        const val ANTELOPE_MANAGE_ACCOUNT = "AntelopeManageAccountScreen"
        const val ANTELOPE_MULTISIG_CREATE_NEW_PROPOSAL_ROOT = "AntelopeMultisigCreateNewProposalRootScreen"
        const val ANTELOPE_MULTISIG_CREATE_NEW_PROPOSAL = "AntelopeMultisigCreateNewProposalScreen"
        const val ANTELOPE_MULTISIG_PROPOSAL_APPROVER = "AntelopeMultisigProposalApproverScreen"
        const val ANTELOPE_MULTISIG_PROPOSAL_PERMISSION = "AntelopeMultisigProposalPermissionScreen"
        const val ANTELOPE_MULTISIG_PROPOSAL_DETAILS = "AntelopeMultisigProposalDetailsScreen"
        const val ANTELOPE_MULTISIG_MY_PROPOSAL = "AntelopeMultisigMyProposalScreen"
        const val ANTELOPE_MULTISIG_APPROVALS = "AntelopeMultisigApprovalsScreen"
        const val ANTELOPE_MULTISIG_APPROVAL_PROPOSAL_DETAIL = "AntelopeMultisigApprovalProposalDetailScreen"
        const val ANTELOPE_MULTISIG_EXPIRED_PROPOSAL = "AntelopeMultisigExpiredProposalScreen"
        const val ANTELOPE_MULTISIG_EXPIRED_PROPOSAL_DETAIL = "AntelopeMultisigExpiredProposalDetailScreen"
        const val ANTELOPE_POWER_UP = "AntelopePowerUpScreen"
        const val ANTELOPE_UNLINK_AUTH = "AntelopeUnlinkAuthScreen"
        const val ANTELOPE_PERMISSIONS_LIST = "AntelopePermissionsListScreen"
        const val ANTELOPE_LINK_AUTH = "AntelopeLinkAuthScreen"
        const val ANTELOPE_CREATE_PERMISSION = "AntelopeCreatePermissionScreen"
        const val ANTELOPE_PERMISSIONS = "AntelopePermissionsScreen"
        const val ANTELOPE_PERMISSION_DETAIL = "AntelopePermissionDetailScreen"
        const val ANTELOPE_STAKE_FOR_RESOURCES_NET = "AntelopeStakeForResourcesNetScreen"
        const val ANTELOPE_STAKE_FOR_RESOURCES_CPU = "AntelopeStakeForResourcesCpuScreen"
        const val ANTELOPE_RENT_VIA_REX_CPU = "AntelopeRentViaRexCpuScreen"
        const val ANTELOPE_RENT_VIA_REX_NET = "AntelopeRentViaRexNetScreen"
        const val ANTELOPE_RESOURCE_NET = "AntelopeResourceNetScreen"
        const val ANTELOPE_RESOURCE_CPU = "AntelopeResourceCpuScreen"
        const val ANTELOPE_PROPOSALS_BY_PROPOSER = "AntelopeProposalsByProposerScreen"
        const val ANTELOPE_PROPOSAL_TABLE = "AntelopeProposalTableScreen"
        const val ANTELOPE_CREATE_ACCOUNT_VIA_EVM = "AntelopeCreateAccountViaEvmScreen"
        const val ANTELOPE_CREATE_ACCOUNT_VIA_EVM_CHOOSE_IMPORTED = "AntelopeCreateAccountViaEvmChooseImportedScreen"
        const val ANTELOPE_IMPORT_ACCOUNT_VIA_EVM = "AntelopeImportAccountViaEvmScreen"
        const val ANTELOPE_CREATE_ACCOUNT_WITH_EXISTING_SELECT_PAYMENT_ACCOUNT = "AntelopeCreateAccountWithExistingSelectPaymentAccountScreen"
        const val ANTELOPE_CREATE_ACCOUNT_SELECT_ACCOUNT_NAME_BOTTOM_SHEET = "AntelopeCreateAccountSelectAccountNameBottomSheetScreen"
        const val ANTELOPE_CREATE_ACCOUNT_SELECT_ACCOUNT_TYPE_BOTTOM_SHEET = "AntelopeCreateAccountSelectAccountTypeBottomSheetScreen"
        const val ANTELOPE_CREATE_ACCOUNT_BY_FRIEND_BOTTOM_SHEET = "AntelopeCreateAccountByFriendBottomSheetScreen"

        const val ANTELOPE_UI = "AntelopeUiScreen"
        const val ANTELOPE_COLD = "AntelopeColdScreen"

        const val BITCOIN_ADD_ACCOUNT = "BitcoinAddAccountScreen"

        const val ANTELOPE_PAY_WITH_CRYPTO_CREATE_ACCOUNT_NOTIFICATION = "AntelopePayWithCryptoCreateAccountNotificationScreen"
        const val PAY_WITH_CRYPTO = "PayWithCryptoScreen"
        const val PAY_WITH_CRYPTO_CHANGE_NETWORK_FOR_PAYMENT = "PayWithCryptoChangeNetworkForPaymentScreen"
        const val PAY_WITH_CRYPTO_ERROR = "PayWithCryptoErrorScreen"
        const val PAY_WITH_CRYPTO_SELECT_WALLET = "PayWithCryptoSelectWalletScreen"
        const val PAY_WITH_CRYPTO_SELECT_PAYMENT_METHOD = "PayWithCryptoSelectPaymentMethodScreen"
        const val PAY_WITH_CRYPTO_SELECT_NETWORK = "PayWithCryptoSelectNetworkScreen"
        const val PAY_WITH_CRYPTO_SELECT_ACCOUNT = "PayWithCryptoSelectAccountScreen"
        const val PAY_WITH_CRYPTO_PAYMENT_DETAIL = "PayWithCryptoPaymentDetailScreen"
        const val PAY_WITH_CRYPTO_ALLOWANCE = "PayWithCryptoAllowanceScreen"

        const val UNLOCK_PIN = "UnlockPinScreen"
        const val FORGOT_PIN = "ForgotPinScreen"
        const val LOCK = "LockScreen"
        const val BIOMETRY = "BiometryScreen"

        const val PORTFOLIO = "PortfolioScreen"
        const val GIFT_RAM = "GiftRamScreen"

        const val SYNC_ACCOUNT = "SyncAccountScreen"

        const val SIGNED_TRANSACTION_QR = "SignedTransactionQrScreen"

        const val ADDRESS_SELECTION = "AddressSelectionScreen"
        const val ADD_TAG = "AddTagScreen"
        const val CREATE_TAG = "CreateTagScreen"

        const val RECENT_TRANSACTION_DETAILS = "RecentTransactionDetailsScreen"
        const val CONVERSATION_UI = "ConversationUiScreen"

        const val LOGIN = "LoginScreen"
        const val REGISTER = "RegisterScreen"
        const val TRY_WITH_AI = "TryWithAIScreen"

        const val ONBOARDING = "Onboarding"

        const val RESTORE_WALLET_GUIDE = "RestoreWalletGuideScreen"
        const val TERMS_OF_SERVICE = "TermsOfServiceScreen"
    }
}

