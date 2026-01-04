package com.mangala.wallet.domain.portfolio.mapper

import com.mangala.wallet.model.blockchain.NetworkType

/**
 * Maps between NetworkType and Portfolio API network IDs
 */
object PortfolioNetworkMapper {
    
    /**
     * Maps NetworkType to Portfolio API network ID
     */
    fun mapNetworkTypeToNetworkId(networkType: NetworkType): Int = when (networkType) {
        NetworkType.ANTELOPE -> 1
        NetworkType.EVM -> 2
        NetworkType.BITCOIN -> 3
        NetworkType.OTHER -> TODO()
        NetworkType.UNSUPPORTED -> TODO()
    }
    
    /**
     * Maps Portfolio API network ID to NetworkType
     */
    fun mapNetworkIdToNetworkType(networkId: Int): NetworkType = when (networkId) {
        1 -> NetworkType.ANTELOPE
        2 -> NetworkType.EVM
        3 -> NetworkType.BITCOIN
        else -> throw IllegalArgumentException("Unknown networkId: $networkId")
    }
    
    /**
     * Gets all supported network types
     */
    fun getSupportedNetworkTypes(): List<NetworkType> = listOf(
        NetworkType.ANTELOPE,
        NetworkType.EVM,
        NetworkType.BITCOIN,
    )
    
    /**
     * Gets all supported network IDs
     */
    fun getSupportedNetworkIds(): List<Int> = listOf(1, 2, 3, 4)
    
    /**
     * Checks if a network type is supported by the Portfolio API
     */
    fun isNetworkTypeSupported(networkType: NetworkType): Boolean = 
        networkType in getSupportedNetworkTypes()
    
    /**
     * Checks if a network ID is supported by the Portfolio API
     */
    fun isNetworkIdSupported(networkId: Int): Boolean = 
        networkId in getSupportedNetworkIds()
}