package com.mangala.wallet.domain.provider.coingecko.usecases

import com.mangala.wallet.domain.base.UseCase
import com.mangala.wallet.domain.provider.coingecko.repository.CoingeckoRepository
import com.mangala.wallet.model.provider.coingecko.CoinGeckoCoinResponse

class GetMarketTickerUseCase(private val coingeckoRepository: CoingeckoRepository) : UseCase<CoinGeckoCoinResponse>(){
    override suspend fun run(params: Map<String, Any?>): CoinGeckoCoinResponse {
        val coinGeckoId = params["coinGeckoId"] as String
        return coingeckoRepository.getMarketTicker(coinGeckoId = coinGeckoId)
    }

}
