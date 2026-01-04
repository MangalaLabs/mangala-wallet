package com.mangala.wallet.features.addressbook.presentation.contact.scan

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class ScanToAddContactScreenModel : ScreenModel, KoinComponent {
    
    private val _isScanning = MutableStateFlow(true)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()
    
    private val _scanResult = MutableStateFlow<String?>(null)
    val scanResult: StateFlow<String?> = _scanResult.asStateFlow()
    
    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState.asStateFlow()
    
    fun onQrCodeScanned(qrData: String) {
        screenModelScope.launch {
            _scanResult.value = qrData
            _isScanning.value = false
            _errorState.value = null
        }
    }
    
    fun onScanError(error: String) {
        screenModelScope.launch {
            _errorState.value = error
            _isScanning.value = false
        }
    }
    
    fun restartScanning() {
        screenModelScope.launch {
            _isScanning.value = true
            _scanResult.value = null
            _errorState.value = null
        }
    }
    
    fun clearError() {
        screenModelScope.launch {
            _errorState.value = null
        }
    }
}