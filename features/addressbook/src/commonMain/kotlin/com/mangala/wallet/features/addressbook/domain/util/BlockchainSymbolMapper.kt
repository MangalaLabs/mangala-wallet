package com.mangala.wallet.features.addressbook.domain.util

/**
 * Utility object to map between blockchain UIDs and symbols.
 * This allows the code to work with readable symbols while maintaining
 * backward compatibility with UIDs in the database.
 */
object BlockchainSymbolMapper {
    
    // Mapping from UID to Symbol
    private val uidToSymbolMap = mapOf(
        // Bitcoin networks
        "bitcoin" to "BTC",
        "bitcoin-testnet4" to "BTC_TESTNET",
        
        // Ethereum networks
        "ethereum" to "ETH",
        "ethereum-goerli" to "ETH_GOERLI",
        "ethereum-sepolia" to "ETH_SEPOLIA",
        
        // EVM compatible chains
        "binance-smart-chain" to "BSC",
        "binance-smart-chain-testnet" to "BSC_TESTNET",
        "polygon-pos" to "POLYGON",
        "polygon-mumbai" to "POLYGON_MUMBAI",
        "avalanche" to "AVAX",
        "avalanche-fuji" to "AVAX_FUJI",
        "fantom" to "FTM",
        "fantom-testnet" to "FTM_TESTNET",
        "arbitrum-one" to "ARB",
        "arbitrum-sepolia" to "ARB_SEPOLIA",
        "optimistic-ethereum" to "OP",
        "optimism-sepolia" to "OP_SEPOLIA",
        
        // Antelope chains
        "eos" to "VAULTA",
        "eos-testnet" to "VAULTA_TESTNET",
        "eos-jungle-testnet" to "VAULTA_TESTNET",
        "eos-evm" to "VAULTA_EVM",
        "eos-evm-testnet" to "VAULTA_EVM_TESTNET",
        "telos" to "TELOS",
        "telos-testnet" to "TELOS_TESTNET",
        "wax" to "WAX",
        "wax-testnet" to "WAX_TESTNET",
        "fio" to "FIO",
        "fio-testnet" to "FIO_TESTNET",
        
        // Other chains
        "binancecoin" to "BNB",
        "solana" to "SOL",
        "solana-devnet" to "SOL_DEVNET",
        "tron" to "TRX",
        "tron-nile" to "TRX_NILE",
        "cardano" to "ADA",
        "polkadot" to "DOT",
        "cosmos" to "ATOM"
    )
    
    // Reverse mapping from Symbol to UID
    private val symbolToUidMap = uidToSymbolMap.entries.associate { (uid, symbol) -> symbol to uid }
    
    /**
     * Convert a blockchain UID to its symbol representation.
     * Returns the UID itself if no mapping is found.
     */
    fun uidToSymbol(uid: String): String {
        return uidToSymbolMap[uid.lowercase()] ?: uid.uppercase()
    }
    
    /**
     * Convert a blockchain symbol to its UID representation.
     * Returns the symbol itself in lowercase if no mapping is found.
     */
    fun symbolToUid(symbol: String): String {
        return symbolToUidMap[symbol.uppercase()] ?: symbol.lowercase()
    }
    
    /**
     * Normalize a blockchain identifier to UID.
     * This function accepts either a UID or a symbol and returns the UID.
     */
    fun normalizeToUid(blockchainIdentifier: String): String {
        val identifier = blockchainIdentifier.trim()
        
        // First check if it's already a UID
        if (uidToSymbolMap.containsKey(identifier.lowercase())) {
            return identifier.lowercase()
        }
        
        // Then check if it's a symbol
        return symbolToUidMap[identifier.uppercase()] ?: identifier.lowercase()
    }
    
    /**
     * Normalize a blockchain identifier to symbol.
     * This function accepts either a UID or a symbol and returns the symbol.
     */
    fun normalizeToSymbol(blockchainIdentifier: String): String {
        val identifier = blockchainIdentifier.trim()
        
        // First check if it's already a symbol
        if (symbolToUidMap.containsKey(identifier.uppercase())) {
            return identifier.uppercase()
        }
        
        // Then check if it's a UID
        return uidToSymbolMap[identifier.lowercase()] ?: identifier.uppercase()
    }
    
    /**
     * Check if a given identifier is a valid blockchain (either UID or symbol).
     */
    fun isValidBlockchain(identifier: String): Boolean {
        val normalized = identifier.trim()
        return uidToSymbolMap.containsKey(normalized.lowercase()) || 
               symbolToUidMap.containsKey(normalized.uppercase())
    }
    
    /**
     * Get all supported blockchain symbols.
     */
    fun getAllSymbols(): Set<String> = symbolToUidMap.keys
    
    /**
     * Get all supported blockchain UIDs.
     */
    fun getAllUids(): Set<String> = uidToSymbolMap.keys
}