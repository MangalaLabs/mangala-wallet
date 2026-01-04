package com.mangala.wallet.features.chains.bitcoin.domain.model.fee

enum class FeeRatePriority {
    FASTEST,  // Usually targets inclusion within 1-2 blocks
    MEDIUM,   // Usually targets inclusion within 3-6 blocks
    SLOW,     // Usually targets inclusion within 6+ blocks
    ECONOMY,  // Usually targets inclusion within 24+ hours
    MINIMUM   // The absolute minimum fee a node will accept
}