package com.mangala.wallet.domain.token.historicalprice.usecases

import com.mangala.wallet.domain.coin.usecases.GetCoinByUidUseCase
import com.mangala.wallet.domain.provider.coingecko.repository.CoingeckoRepository
import com.mangala.wallet.domain.token.historicalprice.repository.TokenHistoricalPriceRepository
import com.mangala.wallet.domain.token.usecases.GetTokenByReferenceUseCase
import com.mangala.wallet.model.provider.coingecko.CoingeckoPriceHistoryResponse
import com.mangala.wallet.model.token.TokenHistoricalPrice
import com.mangala.wallet.model.util.Resource
import com.mangala.wallet.remote.di.ApiResponse
import commangalawalletdatabase.TokenHistoricalPriceEnitity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class FetchHistoricalTokenPriceUseCase(
    private val getTokenByReferenceUseCase: GetTokenByReferenceUseCase,
    private val getCoinByIdUseCase: GetCoinByUidUseCase,
    private val tokenHistoricalPriceRepository: TokenHistoricalPriceRepository
) {

    suspend operator fun invoke(
        tokenRef: String,
        date: Instant,
    ): Result<TokenHistoricalPrice> {
        val token = getTokenByReferenceUseCase(tokenRef).firstOrNull() ?: return Result.failure(Exception("Token not found"))
        val coin = getCoinByIdUseCase(token.coinUid).firstOrNull()?.coinGeckoId ?: return Result.failure(Exception("Coin not found"))

        val localDate = date.toLocalDateTime(TimeZone.currentSystemDefault())
        val dateString = "${localDate.dayOfMonth.toString().padStart(2, '0')}-${localDate.monthNumber.toString().padStart(2, '0')}-${localDate.year}"

        return tokenHistoricalPriceRepository.fetchHistoricalTokenPrice(coin, dateString)
    }

    suspend fun invokeFlow(
        tokenRef: String,
        date: Instant,
        forceRefresh: Boolean
    ): Flow<Resource<TokenHistoricalPrice?>> {
        val token = getTokenByReferenceUseCase(tokenRef).firstOrNull() ?: return flowOf(
            Resource.Error(Exception("Token not found"))
        )
        val coin = getCoinByIdUseCase(token.coinUid).firstOrNull()?.coinGeckoId ?: return flowOf(
            Resource.Error(Exception("Coin not found"))
        )

        val localDate = date.toLocalDateTime(TimeZone.currentSystemDefault())
        val dateString = "${localDate.dayOfMonth.toString().padStart(2, '0')}-${
            localDate.monthNumber.toString().padStart(2, '0')
        }-${localDate.year}"

        return tokenHistoricalPriceRepository.fetchHistoricalTokenPriceFlow(coin, dateString, forceRefresh)
    }
}