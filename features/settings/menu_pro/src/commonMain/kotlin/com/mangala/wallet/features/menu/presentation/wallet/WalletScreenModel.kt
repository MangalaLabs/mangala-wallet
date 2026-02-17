package com.mangala.wallet.features.menu.presentation.wallet

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.biometry.presentation.IBiometryScreenModel
import com.mangala.wallet.domain.wallet.usecases.DeletedWalletUseCase
import com.mangala.wallet.domain.wallet.usecases.GetAllWalletsUseCase
import com.mangala.wallet.domain.wallet.usecases.GetSelectedWalletUseCase
import com.mangala.wallet.domain.wallet.usecases.SelectWalletUseCase
import com.mangala.wallet.local.securestorage.SecureStorageWrapper
import com.mangala.wallet.local.securestorage.SecureStorageWrapperConstants.PIN_KEY
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject

class WalletScreenModel(
    private val getSelectedWalletUseCase: GetSelectedWalletUseCase,
    private val selectedWalletUseCase: SelectWalletUseCase,
    private val getAllWalletsUseCase: GetAllWalletsUseCase,
    private val deletedWalletUseCase: DeletedWalletUseCase,
    private val biometryScreenModel: IBiometryScreenModel,
    private val secureStorageWrapper: SecureStorageWrapper
) : BaseScreenModel() {

    private val _uiModel = MutableStateFlow(WalletScreenModelUiModel())
    val uiModel: StateFlow<WalletScreenModelUiModel> get() = _uiModel

    init {
        screenModelScope.launch {
            collectSelectedWallet()
        }
    }

    fun onClickSelectWallet(item: WalletScreenModelItemUiModel) {
        screenModelScope.launch {
            if (item.wallet.isSelected) return@launch
            selectedWalletUseCase(item.wallet.id)

        }
    }

    fun onClickDeletedWallet(item: WalletScreenModelItemUiModel) {
        screenModelScope.launch {
            deletedWalletUseCase(item.wallet.id)
            checkWalletAndResetPin()
        }
    }

    //check if no wallet is selected, should reset biometric and pin
    fun checkWalletAndResetPin() {
        screenModelScope.launch {
            getSelectedWalletUseCase.invokeFlow().collect {
                val wallets = getAllWalletsUseCase()
                if(wallets.isEmpty()){
                    //reset biometric and pin
                    biometryScreenModel.enableBiometric(false)
                    secureStorageWrapper.saveValue(PIN_KEY, "")
                }
            }
        }
    }

    private suspend fun collectSelectedWallet() {
        getSelectedWalletUseCase.invokeFlow().collect {
            val wallets = getAllWalletsUseCase()
            val items = wallets.map {
                WalletScreenModelItemUiModel(it)
            }
            _uiModel.update {
                it.copy(
                    isLoading = false,
                    items = items
                )
            }
        }
    }

}
