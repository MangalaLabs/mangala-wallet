package com.mangala.wallet.features.menu.presentation.wallet

import com.mangala.wallet.model.wallet.domain.WalletModel

data class WalletScreenModelUiModel(
    val items: List<WalletScreenModelItemUiModel> = emptyList()
)

data class WalletScreenModelItemUiModel(
    val wallet: WalletModel
)