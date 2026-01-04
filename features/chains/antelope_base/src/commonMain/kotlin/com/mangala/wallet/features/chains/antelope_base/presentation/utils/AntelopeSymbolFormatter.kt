package com.mangala.wallet.features.chains.antelope_base.presentation.utils

import com.mangala.wallet.model.blockchain.BlockchainType

/**
 * Extension functions for BlockchainType to handle EOS symbol formatting.
 * These provide a fluent API for formatting symbols when working with BlockchainType instances.
 */

/**
 * Formats a currency symbol according to this blockchain type.
 * For EOS blockchains, converts "EOS" to "A".
 *
 * Usage: blockchainType.formatSymbol("EOS") // Returns "A" for EOS chains
 */
fun BlockchainType?.formatSymbol(symbol: String): String {
    return AntelopeSymbolUtils.formatSymbol(symbol, this)
}

/**
 * Formats text containing currency amounts with EOS symbols.
 * Replaces " EOS" with " A" for EOS blockchains.
 *
 * Usage: blockchainType.formatTextWithSymbol("100 EOS") // Returns "100 A" for EOS chains
 */
fun BlockchainType?.formatTextWithSymbol(text: String): String {
    return AntelopeSymbolUtils.formatTextWithSymbol(text, this)
}

/**
 * Object providing static methods for symbol formatting.
 * Use this when you need to format symbols but don't have a BlockchainType instance
 * to use the extension functions.
 */
object AntelopeSymbolFormatter {
    /**
     * Formats a symbol according to the blockchain type.
     * Delegates to AntelopeSymbolUtils for the actual logic.
     */
    fun formatSymbol(symbol: String, blockchainType: Any?): String {
        return AntelopeSymbolUtils.formatSymbol(symbol, blockchainType)
    }

    /**
     * Formats text containing currency amounts according to the blockchain type.
     * Delegates to AntelopeSymbolUtils for the actual logic.
     */
    fun formatTextWithSymbol(text: String, blockchainType: Any?): String {
        return AntelopeSymbolUtils.formatTextWithSymbol(text, blockchainType)
    }
}