package com.mangala.wallet.websocket.chat.network

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.net.InetAddress

class DesktopNetworkMonitor : NetworkMonitor {
    
    private val _networkState = MutableStateFlow(NetworkState(
        isConnected = true,
        type = NetworkType.ETHERNET, // Desktop typically uses Ethernet
        quality = NetworkQuality.EXCELLENT,
        isRoaming = false,
        isMetered = false,
        signalStrength = -1,
        bandwidth = NetworkBandwidth.HIGH,
        hasInternetAccess = true,
        captivePortalDetected = false
    ))
    
    override fun observeNetworkState(): Flow<NetworkState> = _networkState.asStateFlow()
    
    override suspend fun getCurrentNetworkState(): NetworkState = _networkState.value
    
    override fun isNetworkSuitable(): Boolean {
        // Check if we can reach a known host
        return try {
            InetAddress.getByName("8.8.8.8").isReachable(1000)
        } catch (e: Exception) {
            false
        }
    }
    
    override fun startMonitoring() {
        // Desktop doesn't need active network monitoring
        // Could implement periodic connectivity checks if needed
    }
    
    override fun stopMonitoring() {
        // No active monitoring to stop on desktop
    }
}