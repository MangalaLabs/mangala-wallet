package com.mangala.wallet.menu_base.presentation.wallet

import com.mangala.wallet.model.wallet.domain.WalletModel

data class WalletScreenModelUiModel(
    val items: List<WalletScreenModelItemUiModel> = emptyList()
)

data class WalletScreenModelItemUiModel(
    val wallet: WalletModel
)