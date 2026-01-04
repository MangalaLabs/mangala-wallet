package com.mangala.wallet.local.token.exchangerate

import commangalawalletdatabase.TokenExchangeRateMetadataEntity

interface TokenExchangeRateMetadataLocalDataSource {

    suspend fun insertTokenExchangeRateMetadata(metadata: TokenExchangeRateMetadataEntity)
    suspend fun getLastUpdatedTimestamp(coinUid: String): Long?

}