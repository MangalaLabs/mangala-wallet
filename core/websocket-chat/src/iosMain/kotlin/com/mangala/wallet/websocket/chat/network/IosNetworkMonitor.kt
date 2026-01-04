package com.mangala.wallet.websocket.chat.network

import io.github.aakira.napier.Napier
import kotlinx.cinterop.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import platform.CoreTelephony.*
import platform.Foundation.*
import platform.Network.*
import platform.darwin.dispatch_get_global_queue
import platform.darwin.DISPATCH_QUEUE_PRIORITY_DEFAULT

@OptIn(ExperimentalForeignApi::class)
class IosNetworkMonitor : NetworkMonitor {
    
    private val monitor = nw_path_monitor_create()
    private val queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT.toLong(), 0u)
    
    private val _networkState = MutableStateFlow(NetworkState(
        isConnected = false,
        type = NetworkType.NONE,
        quality = NetworkQuality.UNUSABLE
    ))
    
    init {
        setupPathMonitor()
    }
    
    private fun setupPathMonitor() {
        nw_path_monitor_set_update_handler(monitor) { path ->
            val state = createNetworkState(path)
            _networkState.value = state
            
            Napier.d("Network state updated: $state", tag = "IosNetworkMonitor")
        }
        
        nw_path_monitor_set_queue(monitor, queue)
        nw_path_monitor_start(monitor)
    }
    
    override fun observeNetworkState(): Flow<NetworkState> = _networkState.asStateFlow()
    
    override suspend fun getCurrentNetworkState(): NetworkState = _networkState.value
    
    override fun isNetworkSuitable(): Boolean {
        val state = _networkState.value
        return state.isConnected && 
               state.quality != NetworkQuality.UNUSABLE &&
               state.hasInternetAccess
    }
    
    override fun startMonitoring() {
        // Already started in init
    }
    
    override fun stopMonitoring() {
        nw_path_monitor_cancel(monitor)
    }
    
    private fun createNetworkState(path: nw_path_t?): NetworkState {
        if (path == null) {
            return NetworkState(
                isConnected = false,
                type = NetworkType.NONE,
                quality = NetworkQuality.UNUSABLE
            )
        }
        
        val status = nw_path_get_status(path)
        val isConnected = status == nw_path_status_satisfied || status == nw_path_status_satisfiable
        
        if (!isConnected) {
            return NetworkState(
                isConnected = false,
                type = NetworkType.NONE,
                quality = NetworkQuality.UNUSABLE
            )
        }
        
        val type = determineNetworkType(path)
        val quality = calculateNetworkQuality(path, type)
        val isExpensive = nw_path_is_expensive(path)
        val isConstrained = nw_path_is_constrained(path)
        
        // Check for cellular info
        val cellularInfo = getCellularInfo()
        
        return NetworkState(
            isConnected = true,
            type = type,
            quality = quality,
            isRoaming = cellularInfo.isRoaming,
            isMetered = isExpensive,
            signalStrength = cellularInfo.signalStrength,
            bandwidth = if (isConstrained) NetworkBandwidth.LOW else NetworkBandwidth.UNKNOWN,
            hasInternetAccess = status == nw_path_status_satisfied,
            captivePortalDetected = false // iOS doesn't expose this directly
        )
    }
    
    private fun determineNetworkType(path: nw_path_t): NetworkType {
        return when {
            nw_path_uses_interface_type(path, nw_interface_type_wifi) -> NetworkType.WIFI
            nw_path_uses_interface_type(path, nw_interface_type_cellular) -> {
                determineCellularType()
            }
            nw_path_uses_interface_type(path, nw_interface_type_wired) -> NetworkType.ETHERNET
            else -> NetworkType.UNKNOWN
        }
    }
    
    private fun determineCellularType(): NetworkType {
        val cellularInfo = getCellularInfo()
        return when (cellularInfo.technology) {
            "5G", "NR" -> NetworkType.CELLULAR_5G
            "LTE", "4G" -> NetworkType.CELLULAR_4G
            "3G", "WCDMA", "HSDPA", "HSUPA" -> NetworkType.CELLULAR_3G
            "EDGE", "GPRS", "2G" -> NetworkType.CELLULAR_2G
            else -> NetworkType.CELLULAR_4G // Default assumption
        }
    }
    
    private fun calculateNetworkQuality(path: nw_path_t, type: NetworkType): NetworkQuality {
        val isConstrained = nw_path_is_constrained(path)
        
        if (isConstrained) {
            return NetworkQuality.POOR
        }
        
        return when (type) {
            NetworkType.WIFI -> {
                // iOS doesn't provide signal strength directly
                // Assume good quality for WiFi
                NetworkQuality.GOOD
            }
            NetworkType.CELLULAR_5G -> NetworkQuality.EXCELLENT
            NetworkType.CELLULAR_4G -> NetworkQuality.GOOD
            NetworkType.CELLULAR_3G -> NetworkQuality.FAIR
            NetworkType.CELLULAR_2G -> NetworkQuality.POOR
            NetworkType.ETHERNET -> NetworkQuality.EXCELLENT
            else -> NetworkQuality.FAIR
        }
    }
    
    private data class CellularInfo(
        val technology: String = "",
        val isRoaming: Boolean = false,
        val signalStrength: Int = -1
    )
    
    private fun getCellularInfo(): CellularInfo {
        // Note: Accessing cellular info requires entitlements
        // This is a simplified version
        val telephonyInfo = CTTelephonyNetworkInfo()
        val carrier = telephonyInfo.subscriberCellularProvider
        
        return CellularInfo(
            technology = telephonyInfo.currentRadioAccessTechnology?.substringAfterLast("CTRadioAccessTechnology") ?: "",
            isRoaming = carrier?.isRoaming ?: false,
            signalStrength = -1 // Not easily accessible on iOS
        )
    }
    
    @Suppress("CAST_NEVER_SUCCEEDS")
    private fun nw_path_uses_interface_type(path: nw_path_t, type: nw_interface_type_t): Boolean {
        var result = false
        nw_path_enumerate_interfaces(path) { intf ->
            if (nw_interface_get_type(intf) == type) {
                result = true
                return@nw_path_enumerate_interfaces false
            }
            true
        }
        return result
    }
}

// Extension to safely check if CTCarrier is roaming
private val CTCarrier.isRoaming: Boolean
    get() = this.carrierName != this.mobileNetworkCode