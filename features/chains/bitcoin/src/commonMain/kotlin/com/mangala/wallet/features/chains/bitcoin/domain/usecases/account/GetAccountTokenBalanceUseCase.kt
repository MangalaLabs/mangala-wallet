package com.mangala.wallet.features.chains.bitcoin.domain.usecases.account

import com.mangala.wallet.domain.token.price.usecases.GetNativeCoinUseCase
import com.mangala.wallet.domain.token.usecases.FetchTokenPriceUseCase
import com.mangala.wallet.features.chains.bitcoin.domain.model.account.BitcoinAccount
import com.mangala.wallet.features.chains.bitcoin.domain.model.account.BitcoinAccountWithBalance
import com.mangala.wallet.features.chains.bitcoin.domain.repository.balance.BitcoinBalanceRepository
import com.mangala.wallet.features.chains.bitcoin.domain.usecases.electrum.GetElectrumBalanceUseCase
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.model.token.domain.TokenBalanceModel
import com.mangala.wallet.model.util.Resource
import com.mangala.wallet.utils.ext.orZero
import com.mangala.wallet.utils.toBigDecimalOrNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine

class GetAccountTokenBalanceUseCase(
    private val getElectrumBalanceUseCase: GetElectrumBalanceUseCase,
    private val getNativeCoinUseCase: GetNativeCoinUseCase,
    private val fetchTokenPriceUseCase: FetchTokenPriceUseCase,
    private val balanceRepository: BitcoinBalanceRepository
) {
    operator fun invoke(
        forceReload: Boolean,
        blockchainNetworkData: BlockchainNetworkData,
        account: BitcoinAccount
    ): Flow<BitcoinAccountWithBalance> = channelFlow {
        val nativeCoin = getNativeCoinUseCase(blockchainNetworkData.blockchainType.uid)

        val tokenPriceFlow = fetchTokenPriceUseCase.getTokenPriceWithSparkline(
            forceReload = forceReload,
            tokenUids = listOf(nativeCoin.coinUid)
        )

        val balanceFlow = balanceRepository.getBalance(
            forceRefresh = forceReload,
            accountId = account.accountId,
            address = account.bip84Address,
            blockchainType = blockchainNetworkData.blockchainType
        )
//        val balanceFlow = getElectrumBalanceUseCase.invoke(
//            forceRefresh = forceReload,
//            accountId = account.accountId,
//            bip84Address = account.bip84Address,
//            blockchainType = blockchainNetworkData.blockchainType
//        )

        tokenPriceFlow.combine(balanceFlow) { tokenPrice, balance ->
            tokenPrice to balance
        }.collectLatest { (tokenPriceResource, balanceResource) ->
            val tokenPrice = tokenPriceResource.data?.firstOrNull()
            val balance = balanceResource.data?.confirmedSats

            val btcPrice = tokenPrice?.currentPrice?.toBigDecimalOrNull()
            val priceChange24h = tokenPrice?.priceChangePercentage24h?.toBigDecimalOrNull()
            val sparklineData = tokenPrice?.sparklineIn7d?.price

            val totalAmount =
                balance?.toDouble()?.times(tokenPrice?.currentPrice?.toDoubleOrNull().orZero())

            val tokenBalanceModel = if (balance == null) null else TokenBalanceModel(
                tokenId = 0,
                accountId = account.accountId,
                totalAmount = totalAmount.orZero(),
                balance24h = "",
                balance = balance.toString() ?: "0",
                balanceLocked = "",
                orderNumber = 0,
                contractDecimals = 8,
                contractName = blockchainNetworkData.blockchainType.getNativeTokenName(),
                contractSymbol = blockchainNetworkData.blockchainType.getNativeTokenSymbol(),
                contractAddress = "",
                logoUrl = "",
                localImage = blockchainNetworkData.localImage,
                coinUid = nativeCoin.coinUid,
                currencyCode = tokenPrice?.currencyCode.orEmpty(),
                currentPrice = btcPrice?.toString(),
                marketCap = tokenPrice?.marketCap,
                marketCapRank = tokenPrice?.marketCapRank,
                totalVolume = tokenPrice?.totalVolume,
                high24h = tokenPrice?.high24h,
                low24h = tokenPrice?.low24h,
                priceChange24h = tokenPrice?.priceChange24h,
                priceChangePercentage24h = tokenPrice?.priceChangePercentage24h,
                priceChangePercentage7d = tokenPrice?.priceChangePercentage7dInCurrency,
                marketCapChange24h = tokenPrice?.marketCapChange24h,
                marketCapChangePercentage24h = tokenPrice?.marketCapChangePercentage24h,
                sparklineIn7d = if (sparklineData != null) {
                    TokenBalanceModel.Sparkline(sparklineData.toMutableList())
                } else null,
            )

            val resource = if (tokenPriceResource.isLoading() || balanceResource.isLoading()) {
                Resource.Loading(tokenBalanceModel)
            } else if (tokenPriceResource.isError()) {
                Resource.Error(Exception(), tokenBalanceModel)
            } else {
                Resource.Success(tokenBalanceModel)
            }

            send(BitcoinAccountWithBalance(account, resource))
        }
    }
}