package com.mangala.wallet.wallet.presentation.backup

import com.mangala.wallet.domain.wallet.usecases.CreateWalletUseCase
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel

class BackupWalletAlertScreenModel(
    private val createWalletUseCase: CreateWalletUseCase,
): BaseScreenModel() {

}