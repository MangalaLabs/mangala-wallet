package com.mangala.wallet.websocket.chat.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.telephony.TelephonyManager
import io.github.aakira.napier.Napier
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow

class AndroidNetworkMonitor(
    private val context: Context
) : NetworkMonitor {
    
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
    
    private val _currentState = MutableStateFlow(NetworkState(
        isConnected = false,
        type = NetworkType.NONE,
        quality = NetworkQuality.UNUSABLE,
        isRoaming = false,
        isMetered = false
    ))
    
    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    
    override fun observeNetworkState(): Flow<NetworkState> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                updateNetworkState()
                trySend(getCurrentNetworkStateSync())
            }
            
            override fun onLost(network: Network) {
                updateNetworkState()
                trySend(getCurrentNetworkStateSync())
            }
            
            override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
                updateNetworkState()
                trySend(getCurrentNetworkStateSync())
            }
        }
        
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
            
        connectivityManager.registerNetworkCallback(request, callback)
        
        // Emit current state immediately
        trySend(getCurrentNetworkStateSync())
        
        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }
    
    override suspend fun getCurrentNetworkState(): NetworkState = getCurrentNetworkStateSync()
    
    private fun getCurrentNetworkStateSync(): NetworkState {
        val network = connectivityManager.activeNetwork
        val capabilities = network?.let { connectivityManager.getNetworkCapabilities(it) }
        
        if (network == null || capabilities == null) {
            return NetworkState(
                isConnected = false,
                type = NetworkType.NONE,
                quality = NetworkQuality.UNUSABLE,
                isRoaming = false,
                isMetered = false
            )
        }
        
        val type = getNetworkType(capabilities)
        val quality = calculateNetworkQuality(capabilities, type)
        val isMetered = !capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
        val hasInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        val captivePortal = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_CAPTIVE_PORTAL)
        
        // Get roaming state for cellular
        val isRoaming = if (type.name.startsWith("CELLULAR")) {
            telephonyManager?.isNetworkRoaming ?: false
        } else {
            false
        }
        
        // Get signal strength
        val signalStrength = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            capabilities.signalStrength
        } else {
            -1
        }
        
        return NetworkState(
            isConnected = true,
            type = type,
            quality = quality,
            isRoaming = isRoaming,
            isMetered = isMetered,
            signalStrength = signalStrength,
            bandwidth = estimateBandwidth(capabilities),
            hasInternetAccess = hasInternet,
            captivePortalDetected = captivePortal
        )
    }
    
    private fun getNetworkType(capabilities: NetworkCapabilities): NetworkType {
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkType.WIFI
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                getCellularNetworkType()
            }
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> NetworkType.ETHERNET
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> NetworkType.VPN
            else -> NetworkType.UNKNOWN
        }
    }
    
    private fun getCellularNetworkType(): NetworkType {
        return when (telephonyManager?.dataNetworkType) {
            TelephonyManager.NETWORK_TYPE_NR -> NetworkType.CELLULAR_5G
            TelephonyManager.NETWORK_TYPE_LTE -> NetworkType.CELLULAR_4G
            TelephonyManager.NETWORK_TYPE_HSDPA,
            TelephonyManager.NETWORK_TYPE_HSUPA,
            TelephonyManager.NETWORK_TYPE_HSPA,
            TelephonyManager.NETWORK_TYPE_EVDO_0,
            TelephonyManager.NETWORK_TYPE_EVDO_A,
            TelephonyManager.NETWORK_TYPE_EVDO_B,
            TelephonyManager.NETWORK_TYPE_UMTS -> NetworkType.CELLULAR_3G
            TelephonyManager.NETWORK_TYPE_EDGE,
            TelephonyManager.NETWORK_TYPE_GPRS -> NetworkType.CELLULAR_2G
            else -> NetworkType.CELLULAR_4G // Default assumption
        }
    }
    
    private fun calculateNetworkQuality(capabilities: NetworkCapabilities, type: NetworkType): NetworkQuality {
        val bandwidth = capabilities.linkDownstreamBandwidthKbps
        val signalStrength = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            capabilities.signalStrength
        } else {
            0
        }
        
        return when (type) {
            NetworkType.WIFI -> {
                when {
                    bandwidth > 10_000 && signalStrength > -50 -> NetworkQuality.EXCELLENT
                    bandwidth > 5_000 && signalStrength > -60 -> NetworkQuality.GOOD
                    bandwidth > 1_000 && signalStrength > -70 -> NetworkQuality.FAIR
                    bandwidth > 0 -> NetworkQuality.POOR
                    else -> NetworkQuality.UNUSABLE
                }
            }
            NetworkType.CELLULAR_5G -> NetworkQuality.EXCELLENT
            NetworkType.CELLULAR_4G -> {
                when {
                    bandwidth > 10_000 -> NetworkQuality.EXCELLENT
                    bandwidth > 5_000 -> NetworkQuality.GOOD
                    bandwidth > 1_000 -> NetworkQuality.FAIR
                    else -> NetworkQuality.POOR
                }
            }
            NetworkType.CELLULAR_3G -> {
                when {
                    bandwidth > 2_000 -> NetworkQuality.GOOD
                    bandwidth > 500 -> NetworkQuality.FAIR
                    else -> NetworkQuality.POOR
                }
            }
            NetworkType.CELLULAR_2G -> NetworkQuality.POOR
            NetworkType.ETHERNET -> NetworkQuality.EXCELLENT
            else -> NetworkQuality.FAIR
        }
    }
    
    private fun estimateBandwidth(capabilities: NetworkCapabilities): NetworkBandwidth {
        val kbps = capabilities.linkDownstreamBandwidthKbps
        return when {
            kbps > 10_000 -> NetworkBandwidth.HIGH
            kbps > 1_000 -> NetworkBandwidth.MEDIUM
            kbps > 0 -> NetworkBandwidth.LOW
            else -> NetworkBandwidth.UNKNOWN
        }
    }
    
    override fun isNetworkSuitable(): Boolean {
        val state = getCurrentNetworkStateSync()
        return state.isConnected && 
               state.quality != NetworkQuality.UNUSABLE &&
               state.hasInternetAccess &&
               !state.captivePortalDetected
    }
    
    override fun startMonitoring() {
        if (networkCallback != null) return
        
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                Napier.d("Network available", tag = "AndroidNetworkMonitor")
                updateNetworkState()
            }
            
            override fun onLost(network: Network) {
                Napier.d("Network lost", tag = "AndroidNetworkMonitor")
                updateNetworkState()
            }
            
            override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
                Napier.d("Network capabilities changed", tag = "AndroidNetworkMonitor")
                updateNetworkState()
            }
        }
        
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
            
        connectivityManager.registerNetworkCallback(request, networkCallback!!)
    }
    
    override fun stopMonitoring() {
        networkCallback?.let {
            connectivityManager.unregisterNetworkCallback(it)
            networkCallback = null
        }
    }
    
    private fun updateNetworkState() {
        _currentState.value = getCurrentNetworkStateSync()
    }
}