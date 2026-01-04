package com.mangala.wallet.features.chains.antelope_base.presentation.utils

import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.utils.EosSymbolFormatter

/**
 * Utility object for handling Antelope/EOS symbol formatting.
 * This wraps the common EosSymbolFormatter and provides type-safe overloads for BlockchainType.
 */
object AntelopeSymbolUtils {

    /**
     * Formats a currency symbol according to the blockchain type.
     * For EOS blockchains, converts "EOS" to "A".
     * Type-safe version for BlockchainType.
     */
    fun formatSymbol(symbol: String, blockchainType: BlockchainType?): String {
        // For BlockchainType, we can check directly
        return if (symbol == "EOS" && blockchainType?.isEosNetwork() == true) {
            "A"
        } else {
            symbol
        }
    }

    /**
     * Formats a currency symbol according to the blockchain type.
     * For EOS blockchains, converts "EOS" to "A".
     * Delegates to common utility for non-BlockchainType parameters.
     */
    fun formatSymbol(symbol: String, blockchainType: Any?): String {
        return when (blockchainType) {
            is BlockchainType -> formatSymbol(symbol, blockchainType)
            else -> EosSymbolFormatter.formatSymbol(symbol, blockchainType)
        }
    }

    /**
     * Formats text containing currency amounts with EOS symbols.
     * Replaces " EOS" with " A" for EOS blockchains.
     * Type-safe version for BlockchainType.
     */
    fun formatTextWithSymbol(text: String, blockchainType: BlockchainType?): String {
        return if (blockchainType?.isEosNetwork() == true && text.contains(" EOS")) {
            text.replace(" EOS", " A")
        } else {
            text
        }
    }

    /**
     * Formats text containing currency amounts with EOS symbols.
     * Replaces " EOS" with " A" for EOS blockchains.
     * Delegates to common utility for non-BlockchainType parameters.
     */
    fun formatTextWithSymbol(text: String, blockchainType: Any?): String {
        return when (blockchainType) {
            is BlockchainType -> formatTextWithSymbol(text, blockchainType)
            else -> EosSymbolFormatter.formatTextWithSymbol(text, blockchainType)
        }
    }
}