package com.mangala.wallet.utils

/**
 * Utility for formatting EOS symbols to display symbols (e.g., "EOS" to "A").
 * This is in common.utils so it can be used by modules that don't have access to BlockchainType.
 * Uses reflection to check if a blockchain is an EOS network.
 */
object EosSymbolFormatter {
    
    /**
     * Formats a currency symbol according to the blockchain type.
     * For EOS blockchains, converts "EOS" to "A".
     * 
     * @param symbol The original symbol (e.g., "EOS")
     * @param blockchainType The blockchain type (as Any? since we don't have BlockchainType here)
     * @return The formatted symbol (e.g., "A" for EOS on EOS blockchain)
     */
    fun formatSymbol(symbol: String, blockchainType: Any?): String {
        return if (symbol == "EOS" && isEosBlockchain(blockchainType)) {
            "A"
        } else {
            symbol
        }
    }
    
    /**
     * Formats text containing currency amounts with EOS symbols.
     * Replaces " EOS" with " A" for EOS blockchains.
     * 
     * @param text The text containing currency amounts (e.g., "100 EOS")
     * @param blockchainType The blockchain type (as Any? since we don't have BlockchainType here)
     * @return The formatted text (e.g., "100 A" for EOS blockchain)
     */
    fun formatTextWithSymbol(text: String, blockchainType: Any?): String {
        return if (text.contains(" EOS") && isEosBlockchain(blockchainType)) {
            text.replace(" EOS", " A")
        } else {
            text
        }
    }
    
    /**
     * Checks if the given blockchain type represents an EOS blockchain.
     * Uses class name checking for cross-platform compatibility.
     * 
     * @param blockchainType The blockchain type to check
     * @return true if this is an EOS blockchain, false otherwise
     */
    private fun isEosBlockchain(blockchainType: Any?): Boolean {
        return blockchainType?.let { blockchain ->
            // Check class name - works across all platforms
            val className = blockchain::class.simpleName
            className == "Eos" || className == "EosJungleTestnet"
        } ?: false
    }
}