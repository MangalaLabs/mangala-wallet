package com.mangala.features.wallet.presentationv2.core.base

import cafe.adriel.voyager.navigator.Navigator
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.qrcode.domain.model.QrCodeData
import com.mangala.wallet.scanqr.ScanQRCode
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.KoinComponent

/**
 * Base view model for wallet screens with common functionality
 */
abstract class BaseWalletViewModel : BaseScreenModel(), KoinComponent {

    protected val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    protected var isInitialLoad: Boolean = true
    
    abstract val networkType: NetworkType
    
    /**
     * Refresh wallet data
     */
    open fun onRefresh() {
        // Override in implementations
    }
    
    /**
     * Copy address to clipboard
     */
    abstract fun onCopyAddress()
    
    /**
     * Share address
     */
    abstract fun onShareAddress()
}