package com.mangala.wallet.wallet.presentation.backup

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.wallet.usecases.CreateWalletUseCase
import com.mangala.wallet.domain.wallet.usecases.GetAllWalletsUseCase
import com.mangala.wallet.model.wallet.domain.WalletModel
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ShowRecoveryPhraseScreenModel(
    private val getAllWalletsUseCase: GetAllWalletsUseCase
): BaseScreenModel() {


    private val _wallets = MutableStateFlow<List<WalletModel>>(emptyList())
    val wallets: StateFlow<List<WalletModel>> get() = _wallets

    init {
        getAllWallet()
    }

    private fun getAllWallet() {
        screenModelScope.launch {
            val wallets = getAllWalletsUseCase()
            _wallets.value = wallets
        }
    }

}