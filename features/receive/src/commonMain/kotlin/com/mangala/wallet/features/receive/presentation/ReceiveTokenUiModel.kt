package com.mangala.wallet.features.receive.presentation

import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.model.account.domain.AccountModel
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.token.TokenEntity

sealed interface AccountUiModelReceiveTokenUiState {
    val key: String
    val selectedNetwork: BlockchainNetworkData
    val qrCodeData: String
    val nativeCoin: TokenEntity
    val amount: String?
        get() = null

    data object Initial : AccountUiModelReceiveTokenUiState {
        override val nativeCoin: TokenEntity = TokenEntity(0, "", "", "", null, null)
        override val key = ""
        override val selectedNetwork: BlockchainNetworkData =
            BlockchainNetworkData.getAllBlockchainNetworkSupported(false).first()
        override val qrCodeData = ""
    }

    data class Evm(
        val address: String?,
        override val nativeCoin: TokenEntity,
        override val selectedNetwork: BlockchainNetworkData,
        override val amount: String? = null
    ) : AccountUiModelReceiveTokenUiState {
        override val key = address ?: ""
        override val qrCodeData
            get() = address?.let { addr ->
                try {
                    val prefix =
                        if (selectedNetwork.blockchainType == BlockchainType.BinanceSmartChain) "smartchain" else selectedNetwork.blockChainUid
                    
                    // Validate the address is a valid hex string before creating Address object
                    val cleanAddress = addr.trim()
                    if (cleanAddress.isBlank()) {
                        return@let "$prefix:$cleanAddress"
                    }
                    
                    // Create Address object with proper error handling
                    val evmAddress = try {
                        Address(cleanAddress)
                    } catch (e: NumberFormatException) {
                        // Return the address without EIP55 formatting if invalid
                        return@let "$prefix:$cleanAddress" + amount?.let { "?amount=$it" }.orEmpty()
                    }
                    
                    "$prefix:${evmAddress.eip55}" + amount?.let { "?amount=$it" }.orEmpty()
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Fallback to raw address
                    val prefix =
                        if (selectedNetwork.blockchainType == BlockchainType.BinanceSmartChain) "smartchain" else selectedNetwork.blockChainUid
                    "$prefix:$addr" + amount?.let { "?amount=$it" }.orEmpty()
                }
            }.orEmpty()
    }

    data class Antelope(
        val address: String?,
        override val nativeCoin: TokenEntity,
        override val selectedNetwork: BlockchainNetworkData,
        override val amount: String? = null
    ) : AccountUiModelReceiveTokenUiState {
        override val key: String = address.orEmpty()
        override val qrCodeData
            get() = address?.let { addr ->
                "${selectedNetwork.blockChainUid}:$addr" +
                        amount?.let { "?amount=$it" }.orEmpty()
            }.orEmpty()
    }
}