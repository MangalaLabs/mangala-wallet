package com.mangala.wallet.features.addressbook.presentation.qr

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.features.addressbook.domain.qr.*
import com.mangala.wallet.scanqr.QRCodeGenerator
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Simplified, single-responsibility QR screen model
 */
class QrScreenModel(
    private val dataType: QrDataType
) : ScreenModel, KoinComponent {
    
    private val qrService: QrService by inject()
    private val qrGenerator: QRCodeGenerator by inject()
    
    // UI State
    private val _uiState = MutableStateFlow(QrUiState())
    val uiState: StateFlow<QrUiState> = _uiState.asStateFlow()
    
    init {
        loadQrData()
    }
    
    /**
     * Load QR data for the specified type
     */
    fun loadQrData() {
        screenModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            when (val result = qrService.loadQrData(dataType)) {
                is QrLoadResult.Success -> {
                    val displayData = result.data
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        displayData = displayData,
                        error = null
                    )
                    generateQrImage(displayData)
                }
                is QrLoadResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                QrLoadResult.Loading -> {
                    // Loading state already set above
                }
            }
        }
    }
    
    /**
     * Generate QR code image
     */
    private fun generateQrImage(displayData: QrDisplayData) {
        screenModelScope.launch {
            try {
                when (val contentResult = qrService.generateQrContent(displayData)) {
                    is QrContentResult.Success -> {
                        val qrImage = qrGenerator.generateQRCode(contentResult.content)
                        _uiState.value = _uiState.value.copy(
                            qrImage = qrImage,
                            qrContent = contentResult.content
                        )
                    }
                    is QrContentResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            error = contentResult.message
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to generate QR code: ${e.message}"
                )
            }
        }
    }
    
    /**
     * Retry loading data
     */
    fun retry() {
        loadQrData()
    }
    
    /**
     * Get QR content for sharing/copying
     */
    fun getQrContent(): String? {
        return _uiState.value.qrContent
    }
    
    /**
     * Clear error state
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

/**
 * UI state for QR screen
 */
data class QrUiState(
    val isLoading: Boolean = false,
    val displayData: QrDisplayData? = null,
    val qrImage: Any? = null,
    val qrContent: String? = null,
    val error: String? = null
) {
    val hasData: Boolean get() = displayData != null && qrImage != null
    val hasError: Boolean get() = error != null
}