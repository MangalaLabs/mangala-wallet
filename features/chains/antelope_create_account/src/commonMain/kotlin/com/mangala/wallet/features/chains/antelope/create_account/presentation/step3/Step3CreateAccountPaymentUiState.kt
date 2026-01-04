package com.mangala.wallet.features.chains.antelope.create_account.presentation.step3

import com.mangala.antelope.base.domain.model.FeeBreakdown
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.model.account.domain.eos.AccountNameType
import com.mangala.wallet.model.blockchain.BlockchainType
import com.wallet.iap.purchases.IapProduct
import com.wallet.iap.purchases.PaymentInfo
import dev.icerock.moko.resources.StringResource

sealed interface Step3CreateAccountPaymentUiState {
    data object Loading : Step3CreateAccountPaymentUiState
    data class Ready(
        private val accountName: String,
        val accountNameSuffix: String,
        val accountNameType: AccountNameType,
        val eosOwnerPrivateKey: String?,
        val eosActivePrivateKey: String?,
        val accountNameError: Boolean,
        val selectedAccountIndex: Int?,
        val accounts: List<AntelopeAccount>,
        val selectedPaymentOption: PaymentOption?,
        val availablePaymentOptions: List<PaymentOption>,
        val blockchainType: BlockchainType,
        val isPinSetup: Boolean,
        val createAccountState: CreateAccountState = CreateAccountState.NotCreated,
        val promptConfirmTransaction: Boolean,
        val resourceRequiredBreakdown: FeeBreakdown? = null,
        val resourceRequiredTotal: String? = null,
        val isLoading: Boolean = false,
        val iapProduct: IapProduct? = null,
        val iapProductAlreadyOwnedDialog: IapProductAlreadyOwnedDialog? = null,
        val paymentInfo: PaymentInfo? = null
    ) : Step3CreateAccountPaymentUiState {
        val accountNameWithSuffix = "$accountName$accountNameSuffix"
        val accountNameWithoutSuffix = accountName
        val selectedAccount = selectedAccountIndex?.let { accounts.getOrNull(it) }
        val payWithExistingAccountEnabled = accounts.isNotEmpty()
        val iapPaymentEnabled = iapProduct != null
        val showRetryIapButtonText =
            selectedPaymentOption == PaymentOption.IN_APP_PURCHASE && paymentInfo != null
    }

    val confirmButtonEnabled get() = this is Ready && this.selectedPaymentOption != null && this.isLoading.not() && !this.accountNameError && (this.createAccountState == CreateAccountState.NotCreated || this.createAccountState is CreateAccountState.Error)
}

sealed interface CreateAccountState {
    data object NotCreated : CreateAccountState
    data object Creating : CreateAccountState
    data class Error(val errorMessageString: StringResource) : CreateAccountState
    data object Created : CreateAccountState
}

data class IapProductAlreadyOwnedDialog(
    val accountName: String
)

enum class PaymentOption {
    IN_APP_PURCHASE,
    EXISTING_IMPORTED_ACCOUNT,
    ASK_A_FRIEND_TO_CREATE,
    FREE,
    PAY_WITH_CRYPTO
}