package com.mangala.wallet.features.chains.antelope.create_account.presentation.step1

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountsUseCase
import com.mangala.wallet.model.account.domain.eos.AccountNameType
import com.mangala.wallet.qrcode.domain.model.QrCodeData
import com.mangala.wallet.qrcode.domain.usecase.ParseQRCodeResultUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import com.mangala.wallet.utils.isQrCodeScanningSupported
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class Step1SelectAccountTypeScreenModel(
    private val getAntelopeAccountsUseCase: GetAccountsUseCase,
    private val parseQrCodeResultUseCase: ParseQRCodeResultUseCase
): BaseScreenModel() {

    private val _uiState: MutableStateFlow<Step1SelectAccountTypeUiState> = MutableStateFlow(
        Step1SelectAccountTypeUiState.Loading()
    )
    val uiState: StateFlow<Step1SelectAccountTypeUiState> = _uiState.asStateFlow()

    init {
        screenModelScope.launch {
            val accounts = getAntelopeAccountsUseCase()
            _uiState.value = Step1SelectAccountTypeUiState.Success(
                selectedAccountType = _uiState.value.selectedAccountType,
                isCreateForFriendAccountAvailable = accounts.isNotEmpty() && isQrCodeScanningSupported()
            )
        }
    }

    fun setSelectedAccountType(accountType: AccountNameType) {
        _uiState.update {
            when (val currentState = _uiState.value) {
                is Step1SelectAccountTypeUiState.Success -> currentState.copy(selectedAccountType = accountType)
                is Step1SelectAccountTypeUiState.Loading -> currentState.copy(selectedAccountType = accountType)
            }
        }
    }

    fun onScanQrCodeResult(qrCodeData: String): QrCodeData? {
        val qrCodeResult = parseQrCodeResultUseCase(qrCodeData)

//        if (qrCodeResult is QrCodeData.AntelopeCreateAccountForFriend) {
//            return qrCodeResult
//        }

        return null
    }
}