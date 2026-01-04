package com.mangala.wallet.features.chains.bitcoin.domain.model.fee

import fr.acinq.bitcoin.Satoshi
import kotlin.jvm.JvmInline

/**
 * Represents a fee rate in satoshis per virtual byte (sats/vB)
 * This is commonly used in block explorers and wallets
 */
@JvmInline
value class FeeratePerVbyte(val sat: Long) {
    /**
     * Calculate fee for a given virtual size
     * @param vsize Transaction virtual size in virtual bytes
     * @return Fee in satoshis
     */
    fun toFeeSatoshi(vsize: Long): Satoshi {
        // Calculate raw fee
        val rawFee = sat * vsize
        // Ensure minimum fee of 1 satoshi
        return Satoshi(maxOf(1L, rawFee))
    }

    companion object {
        // Default fee rates - updated to meet minimum relay fee requirements
        val MINIMUM = FeeratePerVbyte(2)  // 2 sat/vB
        val ECONOMIC = FeeratePerVbyte(5) // 5 sat/vB
        val NORMAL = FeeratePerVbyte(10)  // 10 sat/vB
        val PRIORITY = FeeratePerVbyte(20) // 20 sat/vB
    }
}