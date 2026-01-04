package com.mangala.wallet.features.addressbook.presentation.qr

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.mangala.wallet.features.addressbook.domain.qr.QrDataType
import com.mangala.wallet.features.addressbook.presentation.qr.ShowQrScreenContent
import org.koin.core.parameter.parametersOf

/**
 * Unified QR screen for all data types
 */
class ShowQrScreen(
    private val dataType: QrDataType,
    private val onBackClick: () -> Unit = {}
) : Screen {
    
    @Composable
    override fun Content() {
        com.mangala.wallet.ui.LocalBottomNavigationVisibility.current.value = false
        
        val screenModel = getScreenModel<QrScreenModel> { parametersOf(dataType) }
        val uiState by screenModel.uiState.collectAsState()
        
        ShowQrScreenContent(
            isLoading = uiState.isLoading,
            qrDisplayData = uiState.displayData,
            error = uiState.error,
            qrCodeImage = uiState.qrImage,
            onRetry = { screenModel.retry() },
            onBackClick = onBackClick,
            generateQrContent = { displayData ->
                screenModel.getQrContent() ?: ""
            }
        )
    }
}

/**
 * Convenience factory functions for different QR types
 */
object QrScreenFactory {
    
    fun contactQr(contactId: String, onBackClick: () -> Unit = {}) = 
        ShowQrScreen(QrDataType.Contact(contactId), onBackClick)
    
    fun groupQr(groupId: String, onBackClick: () -> Unit = {}) = 
        ShowQrScreen(QrDataType.Group(groupId), onBackClick)
    
    fun tagQr(tagId: String, onBackClick: () -> Unit = {}) = 
        ShowQrScreen(QrDataType.Tag(tagId), onBackClick)
    
    fun addressQr(addressId: String, onBackClick: () -> Unit = {}) = 
        ShowQrScreen(QrDataType.Address(addressId), onBackClick)
}