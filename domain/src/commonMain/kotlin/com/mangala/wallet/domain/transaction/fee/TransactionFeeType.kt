package com.mangala.wallet.domain.transaction.fee

enum class TransactionFeeType(val multiplier: Double, val estimatedProcessingTimeMinutes: Int) {
    ECONOMY(0.8, 7),
    REGULAR(1.0, 3),
    FAST(1.2, 1)
}
