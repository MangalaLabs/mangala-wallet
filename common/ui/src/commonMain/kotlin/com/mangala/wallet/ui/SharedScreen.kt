package com.mangala.wallet.ui

import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.registry.ScreenProvider
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.model.account.domain.AccountBlockchainModel
import com.mangala.wallet.model.account.domain.eos.AccountNameType
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.model.qr.SyncAccountRequest
import com.mangala.wallet.model.token.domain.TokenBalanceModel
import com.mangala.wallet.model.wallet.domain.WalletModel
import com.mangala.wallet.ui.utils.navigation.args.PreviewSwapTokenScreenArgs
import com.mangala.wallet.ui.utils.navigation.args.SignTransactionRequestArgs

sealed class SharedScreen : ScreenProvider {
    data class HomeScreen(
        val initialTab: InitialTab = InitialTab.WALLET
    ) : SharedScreen() {
        enum class InitialTab {
            WALLET, CONVERSATION_UI
        }
    }
    data class UnlockPinScreen(
        val unlockPinCase: Int,
        val antelopeAccountName: String?,
        val onUnlockSuccess: () -> Unit = {},
        val unlockPinCallback: ((Boolean) -> Unit)? = null
    ) : SharedScreen() {
        companion object {
            const val OPEN_APP = 1
            const val CHANGE_PIN = 2
            const val SHOW_WORDS_PHRASE = 3
            const val ADD_ACCOUNT = 4
            const val ADD_ACCOUNT_BITCOIN = 5
            const val CONFIRM_DAPP = 6
            const val ENABLE_BIOMETRY = 7
            const val VERIFY_SEND_TRANSACTION = 8
            const val BACKUP_ANTELOPE_ACCOUNT = 9
        }
    }

    data object LockScreen : SharedScreen()
    data class SetupPinScreen(
        val blockchainUid: String? = null,
        val antelopeAccountName: String? = null,
        val listString: List<String>? = null,
        val name: String? = null,
        val onPinSetupSuccess: (() -> Unit)? = null,
        val onPinSetupCancel: (() -> Unit)? = null,
        val pinCase: String
    ) : SharedScreen() {
        enum class SetupPinScreenCase {
            CREATE_NEW_WALLET, // CreateNewWallet(val blockchainUid: String, val antelopeAccountName: String?)
            CREATE_NEW_PIN, // CreateNewPin(val blockchainUid: String, val antelopeAccountName: String?)
            CHANGE_PIN, // ChangePin
            CREATE_NEW_PIN_AND_BACKUP_ANTELOPE, // CreateNewPinAndBackupAntelope(val accountName: String, val blockchainUid: String?)
            RESTORE_WALLET, // data class RestoreWallet(val listString : List<String>, val name : String)
            CREATE_NEW_PIN_AND_CONTINUE,
            CREATE_NEW_PIN_AND_CONTINUE_HOME_SCREEN
        }
    }

    data class BuySellRamScreen(
        val accountName: String,
        val isBuyRam: Boolean
    ) : SharedScreen()

    data class RamTransferScreen(
        val accountName: String
    ) : SharedScreen()

    data class SignSellRamScreen(
//        val ramPrice: String,
        val amountRamSell: String,
//        val quotaUsed: String,
//        val timeSellRam: String,
        val accountName: String,
        val isBuyRam: Boolean,
        val amountEOSBuy: String,
    ) : SharedScreen()

    data class ConfirmPinScreen(
        val pin: String,
        val blockchainUid: String? = null,
        val antelopeAccountName: String? = null,
        val listString: List<String>? = null,
        val name: String? = null,
        val onPinSetupSuccess: (() -> Unit)? = null,
        val pinCase: String
    ) : SharedScreen()
    data object ForgotPinScreen : SharedScreen()
    data object ResetWalletScreen : SharedScreen()
    data object RestoreWalletScreen : SharedScreen()
    data class RestoreRecoveryPhraseScreen(
        val nextScreen: ScreenType = ScreenType.HOME_SCREEN
    ) : SharedScreen()
    data object CreateWalletGuideScreen : SharedScreen()
    data class CreateWalletScreen(
        val blockchainUid: String? = null,
        val antelopeAccountName: String? = null,
        val listString: List<String>? = null,
        val name: String? = null,
        val createWalletCase: CreateWalletScreenCase
    ) : SharedScreen() {
        enum class CreateWalletScreenCase {
            CREATE_NEW_WALLET, // CreateNewWallet(val blockchainUid: String?, val antelopeAccountName: String?)
            IMPORT_NEW_ACCOUNT, // ImportNewAccount(val blockchainUid: String?, val antelopeAccountName: String?)
            IMPORT_WALLET // ImportWallet(val listString: List<String>, val name: String)
        }
    }

    data object CreateWalletScreenTest : SharedScreen()
    data object ShowRecoveryPhraseScreen : SharedScreen()
    data object ShowRecoveryPhraseScreenV2 : SharedScreen()
    data class BackupWalletGuideScreen(val page: Int, val blockchainUid: String, val antelopeAccountName: String?) : SharedScreen()
    data class BackupWalletAlertScreen(val blockchainUid: String, val antelopeAccountName: String?) : SharedScreen()
    data object VerifyRecoveryPhraseScreen : SharedScreen()
    data object VerifyRecoveryPhraseScreenV2 : SharedScreen()
    data object BackupWalletDoneScreen : SharedScreen()
    data object BackupWalletDoneScreenV2 : SharedScreen()
    data class ImportWalletGuideScreen(
        val nextScreen: ScreenType = ScreenType.HOME_SCREEN
    ) : SharedScreen()
    data class BiometryScreen(
        val blockchainUid: String? = null,
        val antelopeAccountName: String? = null,
        val pinCase: String? = null,
        val listString: List<String>? = null,
        val name: String? = null,
        val onBiometryCallback: ((Boolean) -> Unit)? = null,
        val onCancel: () -> Unit = {}
    ) : SharedScreen()
    data class WalletConnectScreen(val uri: String) : SharedScreen()
    data class PortfolioScreen(val accountId: String, val address: String, val networkType: NetworkType, val accountName: String) : SharedScreen()
    data class RamDetailScreen(val accountName: String) : SharedScreen()
    data class ChartRamScreen(
        val isLoading: Boolean,
        val ramPrice: String,
        val ramCurrency: String,
        val pnlPercent: String,
        val pnlColor: Color
    ) : SharedScreen()

    data class SelectRecipientTypeScreen(val accountId: String, val networkType: String) : SharedScreen()
    data class Step2SelectNetwork(
        val accountId: String,
        val networkType: String,
        val address: String? // Address to forward to next screen (from QR code flow)
    ) : SharedScreen()

    data class Step3SelectAmountScreen(
        val accountId: String,
        val contactId: Long?,
        val address: String?,
        val blockchainUid: String?,
        val amount: String?
    ) : SharedScreen()

    data class SelectContactAddressScreen(
        val contactId: String,
        val accountId: String
    ) : SharedScreen()

    data class SendContactListScreen(
        val accountId: String
    ) : SharedScreen()

    data class Step4EvmVerifyAndSendScreen(
        val contactId: Long?,
        val address: String?,
        val blockchainUid: String?,
        val tokenId: String,
        val amount: String,
        val accountId: String,
    ) : SharedScreen()
    data class Step4AntelopeVerifyAndSendScreen(
        val contactId: Long?,
        val senderAccount: String,
        val toAccount: String,
        val blockchainUid: String?,
        val tokenSymbol: String,
        val amount: String,
        val memo: String
    ) : SharedScreen()
    data class Step4BitcoinVerifyAndSendScreen(
        val contactId: Long?,
        val address: String?,
        val blockchainUid: String?,
        val tokenId: String,
        val amount: String,
        val accountId: String
    ) : SharedScreen()

    data class Step5SendSuccessScreen(val txHash: String, val blockchainUid: String) : SharedScreen()
    data class ReceiveTokenScreen(val accountId: String?, val address: String?, val networkType : NetworkType, val initialBlockchainUid: String?) : SharedScreen()
    class ReceiveTokenPickAccountScreen(val onClickAccountInfo: (accountId: String) -> Unit,val networkType: NetworkType) : SharedScreen()
    data class EvmCreateAccountScreen(
        val isPinVerified: Boolean = false
    ) : SharedScreen()
    data class BitcoinCreateAccountScreen(
        val isPinVerified: Boolean = false
    ): SharedScreen()

    data class ConfirmQrScreen(
        val qrCode: String = ""
    ) : SharedScreen()
    data object ManageAccountsScreen : SharedScreen()
    data class AccountDetailsScreen(val accountId: String) : SharedScreen()

    data object SwapTokenScreen : SharedScreen()
    data class PreviewSwapTokenScreen(
        val args: PreviewSwapTokenScreenArgs
    ) : SharedScreen()

    data object WalletMainScreen: SharedScreen()

    data object SendTokenScreen: SharedScreen()
    data object NetworkScreen: SharedScreen()
    data class NetworkBottomSheetScreen(
        val selectedNetwork: BlockchainNetworkData?,
        val onItemSelected: (BlockchainNetworkData) -> Unit
    ): SharedScreen()
    data object ThemeScreen: SharedScreen()
    data object LanguageScreen: SharedScreen()
    data object CurrencyScreen: SharedScreen()
    data object NotificationsScreen: SharedScreen()
    data class ContactsScreen(
        val blockchainUid: String? = null,
        val onSelectContact: ((id: Long) -> Unit)? = null
    ): SharedScreen()
    data class AddContactScreen(
        val id: Long = 0,
        val name: String = "",
        val blockchainUid: String = "",
        val address: String = "",
        val isEdit: Boolean = false
    ): SharedScreen()
    data class ContactDetailScreen(val contactId: Long): SharedScreen()
    data object SecurityScreen: SharedScreen()
    data object MenuScreen: SharedScreen()
    data object HelpCenterScreen: SharedScreen()
    data object TermsAndPolicyScreen: SharedScreen()
    data object WalletScreen: SharedScreen()
    data class WalletDetailsScreen(val walletId: String) : SharedScreen()
    data object AddWalletScreen: SharedScreen()
    data object ViewStepTutorialAddWalletScreen: SharedScreen()
    data object PreferencesScreen: SharedScreen()
    data class TransactionHistoryScreen(val accountId: String): SharedScreen()
    data class TransactionHistoryBitcoinScreen(val bitcoinAddress: String, val blockchainUid: String): SharedScreen()
    data class TransactionInfoScreen(val accountId: String, val txHash: String): SharedScreen()
    data class TransactionInfoBitcoinScreen(val bitcoinAddress: String, val txHash: String): SharedScreen()
    data class TransactionHistoryAntelopeScreen(val accountName: String): SharedScreen()
    data object ConnectWithUsScreen: SharedScreen()
    data object ShareAppScreen: SharedScreen()
    data object AboutUsScreen: SharedScreen()
    data object IconsInAppScreen: SharedScreen()
    data object NftScreen: SharedScreen()
    data object ImportNftScreen: SharedScreen()
    data class StakeForResourceScreen(val accountName: String, val isStakeRex: Boolean, val isCpu: Boolean): SharedScreen()
    data class RentViaRexScreen(val accountName: String, val isCpu: Boolean): SharedScreen()
    data class NftDetailsScreen(val accountId: String, val tokenId: String, val collectionContractAddress: String): SharedScreen()
    data class PowerUpScreen(val accountName: String, val isCpu: Boolean) : SharedScreen()
    data class NetAndCpuScreen(val accountName: String, val isCpu: Boolean) : SharedScreen()

    data class SendNftConfirmationScreen(
        val blockchainUid: String,
        val contactId: Long?,
        val accountId: String,
        val recipientAddress: String,
        val collectionContractAddress: String,
        val tokenId: String
    ): SharedScreen()
    data class BrowserConfirmTransactionScreen(
        val url: String,
        val accountId: String,
        val coinDecimals: Long,
        val chainId: Long,
        val callbackId: Long,
        val value: String,
        val recipient: String,
        val payload: String,
        val nonce: Long,
        val isLegacyTransaction: Boolean,
        val onSignMessageFail: () -> Unit,
        val onSignMessageSuccessful: (callbackId: Long, signHex: String) -> Unit,
        val onConfirm: (isOpenPin: Boolean) -> Unit,
        val onDecline: () -> Unit
    ): SharedScreen()

    // Specific to Cold Wallet app
    data class ColdWalletSyncAccountScreen(val accountId: String): SharedScreen()
    data class SignedTransactionQrScreen(
        val requestId: String,
        val walletId: String,
        val accountId: String,
        val nonce: Long,
        val blockchainUid: String,
        val fromAddress: String,
        val toAddress: String,
        val value: BigInteger,
        val input: ByteArray,
        val legacyGasPrice: Long?,
        val maxFeePerGas: Long?,
        val maxPriorityFeePerGas: Long?,
        val baseFee: Long?,
        val gasLimit: Long,
        val gasFiatValue: String,
        val transactionType: String,
        val contactName: String?,
        val contactAddress: String?
    ): SharedScreen()

    // Specific to UI Wallet app
    data class UiWalletSyncAccountScreen(val syncAccountRequest: SyncAccountRequest): SharedScreen()
    data class SendSignedTransactionScreen(
        val walletId: String,
        val accountId: String,
        val nonce: Long,
        val fromAddress: String,
        val blockchainUid: String,
        val toAddress: String,
        val value: BigInteger,
        val input: ByteArray,
        val legacyGasPrice: Long?,
        val maxFeePerGas: Long?,
        val maxPriorityFeePerGas: Long?,
        val baseFee: Long?,
        val gasLimit: Long,
        val gasFiatValue: String,
        val transactionType: String,
        val contactName: String?,
        val contactAddress: String?,
        val v: Int,
        val r: ByteArray,
        val s: ByteArray
    ): SharedScreen()
    data class TransactionQrScreen(
        val signTransactionRequestArgs: SignTransactionRequestArgs,
        val onScannedSignedTransaction: (v: Int, r: ByteArray, s: ByteArray) -> Unit,
        val onDispose: () -> Unit
    ): SharedScreen()

    data class AntelopeImportAccountScreen(val privateKey : String?) : SharedScreen()
    data object Step1ImportAccountScreen : SharedScreen()
    data object AntelopeCreateAccountStep1Screen: SharedScreen()
    data object AntelopeCreateAccountV2Screen : SharedScreen()

    // Antelope
    data class SelectAccountTypeScreen(val accountType: AccountNameType, val accountTypeSelected: (accountType: AccountNameType) -> Unit = {}) : SharedScreen()
    data class SelectPaymentAccountScreen(val initialAccountName: String, val onSelectAccount: (String) -> Unit): SharedScreen()
    data class SelectAccountNameBottomSheetScreen(val initialAccountName: String, val accountNameSuffix: String?, val accountType: AccountNameType, val onSelectAccountName: (String) -> Unit): SharedScreen()
    data object Step2SelectAccountNameScreenV2: SharedScreen()
    data class CreateByFriendBottomSheetScreen(
        val accountName: String,
        val eosOwnerPrivateKey: String? = null,
        val eosActivePrivateKey: String? = null,
        val onAccountCreated: () -> Unit
    ): SharedScreen()
    data class CreateAccountForFriendScreen(
        val accountName: String,
        val activePublicKey: String,
        val ownerPublicKey: String,
        val blockchainUid: String
    ) : SharedScreen()
    data class BackupAntelopeAccountScreen(val accountName: String, val blockchainUid: String?) :
        SharedScreen()
    data class GuideBackupAntelopeAccountScreen(val accountName: String) : SharedScreen()
    data class SelectPermissionToBackupScreen(val accountName: String): SharedScreen()
    data class BackupAntelopePrivateKeyScreen(val accountName: String, val permissionName: String) : SharedScreen()
    data object ManageAntelopeAccountScreen: SharedScreen()
    data class EsrScreen(val esrUri: String) : SharedScreen()
    data object AntelopeMulitsigScreen : SharedScreen()
    data object BitcoinTestScreen : SharedScreen()
    data object DevMenuScreen : SharedScreen()
    data class ImportAccountByKeyCertScreen(val keyCert: String) : SharedScreen()
    data object SelectAccountPermissionScreen : SharedScreen()
    data class PayWithCryptoScreen(
        val accountName: String,
        val paidAccountId: String,
        val accountBlockchainTypeUid: String,
        val accountNameType: AccountNameType,
        val eosOwnerPrivateKey: String? = null,
        val eosActivePrivateKey: String? = null
    ): SharedScreen()
    data class ChangeNetworkForPaymentScreen(
        val accountName: String,
        val accountNameType: AccountNameType,
        val eosOwnerPrivateKey: String? = null,
        val eosActivePrivateKey: String? = null
    ): SharedScreen()
    data class SelectNetworkBottomSheetScreen (
        val onContinue: (BlockchainType?) -> Unit,
        val networks: List<BlockchainType>,
        val selectedNetwork: BlockchainType?,
        val onDismiss: () -> Unit
    ): SharedScreen()
    data class SelectWalletBottomSheetScreen (
        val onContinue: (WalletModel?) -> Unit,
        val networks: List<WalletModel>,
        val selectedNetwork: WalletModel?,
        val onDismiss: () -> Unit
    ): SharedScreen()
    data class SelectAccountBottomSheetScreen (
        val onContinue: (AccountBlockchainModel?) -> Unit,
        val accounts: List<AccountBlockchainModel>,
        val selectedAccount: AccountBlockchainModel?,
        val onDismiss: () -> Unit
    ): SharedScreen()
    data class PaymentDetailScreen(
        val cpu: BigDecimal,
        val net: BigDecimal,
        val ram: BigDecimal,
        val serviceFee: BigDecimal,
        val totalEos: BigDecimal,
        val onDismiss: () -> Unit,
        val coinUid: String
    ): SharedScreen()

    class CryptoPaymentErrorScreen(
        val error: String,
        val errorDescription: String? = null,
        val blockchainTypeUid: String? = null
    ) : SharedScreen()

    data class IapCreateAccountScreen(
        val accountNameWithSuffix: String,
        val accountNameType: String,
        val skipToCreateAccountStep: Boolean = false, // for when payment already confirmed/ existing payment
        val retryCreateAccountName: Boolean = false,
        val purchaseToken: String? = null,
        val purchaseId: String? = null
    ): SharedScreen()

    data class AllowanceScreen(
        val paidAccountId: String,
        val minimumAllowance: BigDecimal,
        val token: TokenBalanceModel,
        val onCallback: () -> Unit,
        val onDismiss: () -> Unit
    ): SharedScreen()

    data class SelectPaymentMethodScreen(
        val paymentMethods: List<TokenBalanceModel>,
        val onDismiss: () -> Unit,
        val onSelectedPaymentMethod: (TokenBalanceModel) -> Unit
    ): SharedScreen()

    data class CreateAccountNotificationScreen(
        val accountName: String = "",
        val chainId: String = "",
        val isSuccess: Boolean,
        val errorMessage: String = "",
        val onDismiss:() -> Unit
    ): SharedScreen()

    data object ImportEOSAccountViaEVMScreen: SharedScreen()

    data class ChooseImportedEosAccountScreen(
        val eosOwnerPrivateKey: String,
        val eosActivePrivateKey: String
    ): SharedScreen()

    // Antelope MSIG
    data class MyProposalDetailScreen(
        val proposalName: String,
        val submitter: String,
        val chainId: String? = null
    ): SharedScreen()

    data class CreateEosAccountViaEVMScreen(
        val accountName: String,
        val accountNameSuffix: String?,
        val accountNameType: AccountNameType
    ): SharedScreen()

    data class Step3CreateAccountPaymentScreen(
        val initialAccountName: String,
        val initialAccountSuffix: String?,
        val initialAccountType: AccountNameType,
        val eosOwnerPrivateKey: String? = null,
        val eosActivePrivateKey: String? = null
    ): SharedScreen()

    data class GiftRamScreen(
        val accountName: String
    ): SharedScreen()

    enum class ScreenType{
        HOME_SCREEN,
        IMPORT_EOS_VIA_EVM
    }

    data class TwoFactorAuthenticationSetupScreen(
        val onSuccess: () -> Unit
    ): SharedScreen()

    data class Unlock2FaScreen(
        val onUnlockSuccess: () -> Unit,
        val onUnlockCancelled: () -> Unit,
    ): SharedScreen()

    data object Setting2FaScreen: SharedScreen()

    data class TwoFactorSetupRequiredScreen(
        val onSetup2Fa: () -> Unit,
        val onCancel: () -> Unit,
        val onFallbackToPin: () -> Unit
    ): SharedScreen()

    data class AddressSelectionScreen(
        val tagId: String? = null,
        val initialSelectedContactIds: List<String> = emptyList(),
        val onApplySelections: (List<String>) -> Unit
    ): SharedScreen()

    data class CreateTagBottomSheet(
        val onTagCreated: (Any) -> Unit // Using Any instead of specific TagEntity to avoid circular dependency
    ): SharedScreen()

    data class ConversationUiScreen(val sessionId: String? = null): SharedScreen()
    data object ConversationSessionListScreen: SharedScreen()
    data object ContactListScreen: SharedScreen()
    
    // Unified Contact Screen - supports both create and edit modes
    data class ContactScreen(
        val contactId: String? = null, // null = create mode, value = edit mode
        val prefilledName: String = "",
        val prefilledAddress: String = "",
        val prefilledBlockchain: String = "",
        val onBackClick: () -> Unit = {},
        val onSaveSuccess: (String) -> Unit = {}
    ): SharedScreen()
    data class SignInScreen(val showTokenExpiredMessage: Boolean = false): SharedScreen()
    data object OnboardingScreen: SharedScreen()
    data object ConversationUiEntryPointScreen: SharedScreen()
    data object ImportPrivateKeyScreen: SharedScreen()
    data class Step4CreatingAccountScreen(
        val accountName: String,
        val accountSuffix: String,
        val operationType: AccountOperationType = AccountOperationType.CREATE
    ): SharedScreen() {
        enum class AccountOperationType {
            CREATE,
            IMPORT
        }
    }
    data object AuthDemoScreen: SharedScreen()
}