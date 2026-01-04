package com.mangala.wallet.features.chains.antelope.create_account.presentation.step2

import androidx.compose.ui.text.input.TextFieldValue
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.AccountCharacterValidationResult
import com.mangala.wallet.model.account.domain.eos.AccountNameType
import com.mangala.wallet.model.blockchain.BlockchainType

sealed interface Step2SelectAccountNameUiState {
    data object Loading : Step2SelectAccountNameUiState
    data class Ready(
        val accountName: TextFieldValue = TextFieldValue(""),
        val accountType: AccountNameType,
        val validationResult: AccountCharacterValidationResult,
        val isCheckingAccountExistence: Boolean = false,
        val isAccountNotTaken: Boolean? = null,
        val blockchainType: BlockchainType? = null
    ) : Step2SelectAccountNameUiState {
        val accountNameSuffix =
            if (accountType == AccountNameType.Premium && blockchainType != null) AntelopeAccount.getPremiumAccountSuffix(
                blockchainType
            ) else ""
        val accountNameLengthWithSuffix = accountName.text.length + accountNameSuffix.length
        val isAccountNameValid: Boolean
            get() = validationResult.isValid && isAccountNotTaken == true
    }
}