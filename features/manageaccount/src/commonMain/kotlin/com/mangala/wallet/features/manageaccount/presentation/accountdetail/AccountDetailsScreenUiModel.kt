package com.mangala.wallet.features.manageaccount.presentation.accountdetail

import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.model.account.domain.AccountBlockchainModel
import com.mangala.wallet.model.account.domain.AccountModel

sealed class AccountDetailsScreenUiState() {
    object Loading : AccountDetailsScreenUiState()
    data class Success(val accountBlockchainModel: AccountModel) :
        AccountDetailsScreenUiState()
}

fun AccountBlockchainModel.formattedBip44Address() =
    Address(bip44Address).eip55.take(12) + "..." + Address(bip44Address).eip55.takeLast(12)

