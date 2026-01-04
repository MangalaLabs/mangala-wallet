package com.mangala.features.wallet.presentationv2.core.base

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.qrcode.domain.model.QrCodeData
import com.mangala.wallet.scanqr.ScanQRCode
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.getValue

/**
 * Base abstract class for all wallet screens in V2 architecture.
 * Each blockchain implementation should extend this class.
 */
abstract class BaseWalletScreenV2<T : BaseScreenModel> : BaseScreen<T>(), KoinComponent {

    val scanQRCode: ScanQRCode by inject()
    
    /**
     * The network type this wallet screen represents
     */
    abstract val networkType: NetworkType
    
    /**
     * Navigate to send transaction screen
     */
    abstract fun onNavigateToSend()
    
    /**
     * Navigate to receive screen
     */
    abstract fun onNavigateToReceive()
    
    /**
     * Navigate to transaction history
     */
    abstract fun onNavigateToHistory()
    
    /**
     * Common wallet actions that all chains support
     */
    interface WalletActions {
        fun onRefresh()
        fun onCopyAddress()
        fun onShareAddress()
    }

    abstract fun onHandleQrCodeResult(
        result: QrCodeData?,
        navigator: Navigator,
        globalNavigator: Navigator,
        screenModel: T
    )
}