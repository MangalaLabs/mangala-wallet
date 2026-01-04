package com.mangala.features.browser

import com.mangala.wallet.model.account.domain.AccountBlockchainModel

data class BrowserAccountsUiModel(
    val accounts: List<AccountBlockchainModel> = emptyList(),
    val chainId: Long = 1L,
    val rpcUrl: String = ""
)