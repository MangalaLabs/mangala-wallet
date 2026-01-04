package com.mangala.wallet.websocket.chat.network

import kotlinx.coroutines.flow.Flow

/**
 * Monitors network connectivity and quality across platforms
 */
interface NetworkMonitor {
    /**
     * Observable network state changes
     */
    fun observeNetworkState(): Flow<NetworkState>
    
    /**
     * Get current network state synchronously
     */
    suspend fun getCurrentNetworkState(): NetworkState
    
    /**
     * Check if network is available for WebSocket connection
     */
    fun isNetworkSuitable(): Boolean
    
    /**
     * Register for network change callbacks
     */
    fun startMonitoring()
    
    /**
     * Unregister network callbacks
     */
    fun stopMonitoring()
}

/**
 * Represents current network state
 */
data class NetworkState(
    val isConnected: Boolean,
    val type: NetworkType,
    val quality: NetworkQuality,
    val isRoaming: Boolean = false,
    val isMetered: Boolean = false,
    val signalStrength: Int = -1, // dBm
    val bandwidth: NetworkBandwidth = NetworkBandwidth.UNKNOWN,
    val hasInternetAccess: Boolean = false, // Can reach internet
    val captivePortalDetected: Boolean = false
)

/**
 * Network connection type
 */
enum class NetworkType {
    WIFI,
    CELLULAR_5G,
    CELLULAR_4G,
    CELLULAR_3G,
    CELLULAR_2G,
    ETHERNET,
    VPN,
    NONE,
    UNKNOWN
}

/**
 * Network quality assessment
 */
enum class NetworkQuality {
    EXCELLENT,  // Low latency, high bandwidth
    GOOD,       // Suitable for real-time
    FAIR,       // May experience delays
    POOR,       // Expect disconnections
    UNUSABLE    // Don't attempt connection
}

/**
 * Network bandwidth estimation
 */
enum class NetworkBandwidth {
    HIGH,       // > 10 Mbps
    MEDIUM,     // 1-10 Mbps
    LOW,        // < 1 Mbps
    UNKNOWN
}

/**
 * Network change events
 */
sealed class NetworkEvent {
    object Connected : NetworkEvent()
    object Disconnected : NetworkEvent()
    data class TypeChanged(val from: NetworkType, val to: NetworkType) : NetworkEvent()
    data class QualityChanged(val from: NetworkQuality, val to: NetworkQuality) : NetworkEvent()
    object RoamingStateChanged : NetworkEvent()
    object CaptivePortalDetected : NetworkEvent()
}