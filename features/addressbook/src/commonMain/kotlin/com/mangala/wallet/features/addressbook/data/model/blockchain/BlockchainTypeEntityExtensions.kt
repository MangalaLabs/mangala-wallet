package com.mangala.wallet.features.addressbook.data.model.blockchain

import com.mangala.wallet.features.addressbook.domain.util.BlockchainSymbolMapper

/**
 * Extension functions for BlockchainTypeEntity to work with both UIDs and symbols.
 */

/**
 * Get the display symbol for this blockchain (e.g., "BTC", "ETH", "VAULTA").
 * This converts the UID stored in the database to a user-friendly symbol.
 */
val BlockchainTypeEntity.displaySymbol: String
    get() = BlockchainSymbolMapper.uidToSymbol(this.id)

/**
 * Get the blockchain UID for validation and database operations.
 * This returns the actual UID stored in the database.
 */
val BlockchainTypeEntity.uid: String
    get() = this.id

/**
 * Check if this blockchain is an Antelope-based chain.
 */
val BlockchainTypeEntity.isAntelopeChain: Boolean
    get() = BlockchainSymbolMapper.normalizeToSymbol(this.id) in setOf(
        "VAULTA", "VAULTA_TESTNET", "TELOS", "TELOS_TESTNET",
        "WAX", "WAX_TESTNET", "FIO", "FIO_TESTNET"
    )

/**
 * Check if this blockchain is an EVM-compatible chain.
 */
val BlockchainTypeEntity.isEvmChain: Boolean
    get() = BlockchainSymbolMapper.normalizeToSymbol(this.id) in setOf(
        "ETH", "ETH_GOERLI", "ETH_SEPOLIA",
        "BSC", "BSC_TESTNET",
        "POLYGON", "POLYGON_MUMBAI",
        "AVAX", "AVAX_FUJI",
        "FTM", "FTM_TESTNET",
        "ARB", "ARB_SEPOLIA",
        "OP", "OP_SEPOLIA",
        "VAULTA_EVM", "VAULTA_EVM_TESTNET"
    )

/**
 * Check if this blockchain is a testnet.
 */
val BlockchainTypeEntity.isTestnet: Boolean
    get() = this.networkType == BlockchainTypeEntity.NETWORK_TESTNET ||
            this.displaySymbol.endsWith("_TESTNET")

/**
 * Get a user-friendly display name for this blockchain.
 */
val BlockchainTypeEntity.displayName: String
    get() = when (val symbol = this.displaySymbol) {
        "BTC" -> "Bitcoin"
        "BTC_TESTNET" -> "Bitcoin Testnet"
        "ETH" -> "Ethereum"
        "ETH_GOERLI" -> "Ethereum Goerli"
        "ETH_SEPOLIA" -> "Ethereum Sepolia"
        "BSC" -> "BNB Smart Chain"
        "BSC_TESTNET" -> "BNB Smart Chain Testnet"
        "POLYGON" -> "Polygon"
        "POLYGON_MUMBAI" -> "Polygon Mumbai"
        "AVAX" -> "Avalanche"
        "AVAX_FUJI" -> "Avalanche Fuji"
        "FTM" -> "Fantom"
        "FTM_TESTNET" -> "Fantom Testnet"
        "ARB" -> "Arbitrum"
        "ARB_SEPOLIA" -> "Arbitrum Sepolia"
        "OP" -> "Optimism"
        "OP_SEPOLIA" -> "Optimism Sepolia"
        "VAULTA" -> "Vaulta"
        "VAULTA_TESTNET" -> "Vaulta Testnet"
        "VAULTA_EVM" -> "Vaulta EVM"
        "VAULTA_EVM_TESTNET" -> "Vaulta EVM Testnet"
        "TELOS" -> "Telos"
        "TELOS_TESTNET" -> "Telos Testnet"
        "WAX" -> "WAX"
        "WAX_TESTNET" -> "WAX Testnet"
        "FIO" -> "FIO"
        "FIO_TESTNET" -> "FIO Testnet"
        "BNB" -> "Binance Coin"
        "SOL" -> "Solana"
        "SOL_DEVNET" -> "Solana Devnet"
        "TRX" -> "Tron"
        "TRX_NILE" -> "Tron Nile"
        "ADA" -> "Cardano"
        "DOT" -> "Polkadot"
        "ATOM" -> "Cosmos"
        else -> this.name // Fallback to the entity's name field
    }