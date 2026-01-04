package com.mangala.features.wallet.presentation

import com.mangala.wallet.qrcode.domain.model.QrCodeData
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseWalletMainScreenModel: BaseScreenModel() {
    protected val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    protected var isInitialLoad: Boolean = true

    abstract fun onScanQrCodeResult(qrCodeData: String): QrCodeData?
    abstract fun getCurrentAccountId(): String
}