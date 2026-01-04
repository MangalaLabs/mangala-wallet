package com.mangala.wallet.features.chains.antelope_base.domain.model.account.token

import dev.icerock.moko.resources.ImageResource

data class AntelopeTokenBalance(
    val key: String,
    val symbol: String,
    val amount: Double,
    val contract: String,
    val decimals: Int,
    val metadata: AntelopeTokenMetadata,
    val exchanges: List<AntelopeTokenExchangeData>
) {
    data class AntelopeTokenMetadata(
        val name: String,
        val logo: String,
        val localImage: ImageResource?,
        val website: String,
        val createdAt: String
    )
    data class AntelopeTokenExchangeData(
        val name: String,
        val price: Double,
    )
}