package com.mangala.wallet.features.addressbook.presentation.mapper

import com.mangala.wallet.features.addressbook.data.model.ContactModel
import com.mangala.wallet.features.addressbook.data.model.blockchain.WalletAddressEntity
import com.mangala.wallet.model.blockchain.NetworkType

/**
 * Mapper to convert AddressBook entities to ReceiveTokenScreen parameters
 */
object AddressBookToReceiveMapper {
    
    /**
     * Maps blockchain type string to NetworkType enum
     * Based on BlockchainType definitions in the codebase
     */
    fun mapToNetworkType(blockchainType: String?): NetworkType {
        
        return when (blockchainType?.uppercase()) {
            // EVM-based networks (Based on AddressBook database)
            "ETHEREUM", "ETH",
            "POLYGON", "MATIC", 
            "BINANCE SMART CHAIN", "BNB",
            "VAULTA EVM", // EOS EVM in AddressBook
            "AVALANCHE", "AVAX",
            "FANTOM", "FTM",
            "OPTIMISM",
            "ARBITRUM ONE",
            "GNOSIS", "XDAI",
            // Test networks
            "ETHEREUM GOERLI",
            "ETHEREUM SEPOLIA",
            "ETHEREUM HOLESKY",
            "POLYGON MUMBAI",
            "BINANCE SMART CHAIN TESTNET" -> {
                NetworkType.EVM
            }
            
            // Antelope-based networks
            "VAULTA", "A", // Vaulta is EOS in AddressBook
            "VAULTA TESTNET", // Vaulta testnet
            "VAULTA JUNGLE TESTNET", // Name from database
            "EOS JUNGLE TESTNET" -> {
                NetworkType.ANTELOPE
            }
            
            // Other blockchain types that are not supported by ReceiveTokenScreen
            "BITCOIN", "BTC",
            "SOLANA", "SOL" -> throw IllegalArgumentException("$blockchainType is not supported for QR display. Only EVM and Antelope blockchains are supported.")
            
            // Note: ReceiveTokenScreen currently only supports EVM and ANTELOPE
            // Other network types will throw exception to be caught and handled with toast
            else -> {
                // Try to infer from common patterns
                when {
                    blockchainType?.contains("EVM", ignoreCase = true) == true -> {
                        NetworkType.EVM
                    }
                    blockchainType?.contains("ANTELOPE", ignoreCase = true) == true -> {
                        NetworkType.ANTELOPE
                    }
                    blockchainType?.contains("ETHEREUM", ignoreCase = true) == true -> {
                        NetworkType.EVM
                    }
                    blockchainType?.contains("POLYGON", ignoreCase = true) == true -> {
                        NetworkType.EVM
                    }
                    blockchainType?.contains("BSC", ignoreCase = true) == true -> {
                        NetworkType.EVM
                    }
                    blockchainType?.contains("EOS", ignoreCase = true) == true -> {
                        NetworkType.ANTELOPE
                    }
                    blockchainType?.contains("WAX", ignoreCase = true) == true -> {
                        NetworkType.ANTELOPE
                    }
                    blockchainType?.contains("VAULTA", ignoreCase = true) == true -> {
                        NetworkType.ANTELOPE
                    }
                    // For unsupported types, throw exception
                    else -> {
                        throw IllegalArgumentException("Blockchain type '$blockchainType' is not supported for QR display")
                    }
                }
            }
        }
    }
    
    /**
     * Maps blockchain name to blockchain UID
     * Based on the AddressBook database blockchain_types table
     * Returns null if not found - the ReceiveTokenScreen will handle this
     */
    fun mapToBlockchainUid(blockchainName: String?): String? {
        return when (blockchainName?.uppercase()) {
            "ETHEREUM", "ETH" -> "ethereum"
            "POLYGON", "MATIC" -> "polygon-pos"
            "BINANCE SMART CHAIN", "BNB" -> "binance-smart-chain"
            "VAULTA", "A" -> "eos"  // Vaulta is EOS in AddressBook
            "VAULTA TESTNET" -> "eos-testnet"  // Vaulta testnet
            "VAULTA JUNGLE TESTNET" -> "eos-jungle-testnet"  // Vaulta Jungle Testnet from database
            "VAULTA EVM", "EOS" -> "eos-evm"
            "BITCOIN", "BTC" -> "bitcoin"
            "SOLANA", "SOL" -> "solana"
            "AVALANCHE", "AVAX" -> "avalanche"
            "FANTOM", "FTM" -> "fantom"
            "OPTIMISM" -> "optimistic-ethereum"
            "ARBITRUM ONE" -> "arbitrum-one"
            "GNOSIS", "XDAI" -> "gnosis"
            // Test networks
            "ETHEREUM GOERLI" -> "ethereum-goerli"
            "ETHEREUM SEPOLIA" -> "ethereum-sepolia"
            "POLYGON MUMBAI" -> "polygon-mumbai"
            else -> null
        }
    }
    
    /**
     * Creates parameters for ReceiveTokenScreen from a ContactModel
     */
    fun fromContactModel(contact: ContactModel): ReceiveParams {
        return ReceiveParams(
            accountId = null,
            address = contact.walletAddress,
            networkType = mapToNetworkType(contact.blockchainName),
            initialBlockchainUid = mapToBlockchainUid(contact.blockchainName)
        )
    }
    
    /**
     * Creates parameters for ReceiveTokenScreen from a WalletAddressEntity
     */
    fun fromWalletAddress(walletAddress: WalletAddressEntity, blockchainInfo: BlockchainInfo? = null): ReceiveParams {
        val networkType = blockchainInfo?.let { 
            mapToNetworkType(it.type)
        } ?: mapToNetworkType(walletAddress.walletType)
        
        return ReceiveParams(
            accountId = null,
            address = walletAddress.address,
            networkType = networkType,
            initialBlockchainUid = blockchainInfo?.uid
        )
    }
    
    /**
     * Data class to hold parameters for ReceiveTokenScreen
     */
    data class ReceiveParams(
        val accountId: String?,
        val address: String?,
        val networkType: NetworkType,
        val initialBlockchainUid: String?
    )
    
    /**
     * Helper data class for blockchain information
     */
    data class BlockchainInfo(
        val uid: String,
        val type: String,
        val name: String
    )
}