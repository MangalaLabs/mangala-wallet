package com.mangala.wallet.features.chains.antelope_base.domain.usecase.token

import com.mangala.wallet.domain.token.price.usecases.GetNativeCoinUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.account.token.AntelopeTokenBalance
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountWithBalanceInfoUseCase
import com.mangala.wallet.model.blockchain.BlockchainType
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class GetAntelopeAccountCryptoBalanceUseCase(
    private val getAntelopeAccountTokenBalanceUseCase: GetAntelopeAccountTokenBalanceUseCase,
    private val getAccountWithBalanceInfoUseCase: GetAccountWithBalanceInfoUseCase,
    private val getNativeCoinUseCase: GetNativeCoinUseCase
) {
    suspend operator fun invoke(
        accountName: String,
        blockchainType: BlockchainType,
        forceRefresh: Boolean
    ): Result<List<AntelopeTokenBalance>> = coroutineScope {
        val accountAsync =
            async { getAccountWithBalanceInfoUseCase(accountName, forceRefresh = forceRefresh) }
        val nativeCoinAsync = async { getNativeCoinUseCase(blockchainType.uid) }
        val tokenBalanceAsync = async {
            getAntelopeAccountTokenBalanceUseCase.invoke(
                accountName,
                blockchainType,
                forceRefresh = forceRefresh
            )
        }

        val nativeCoin = nativeCoinAsync.await()
        val account = accountAsync.await().getOrNull()
        val tokenBalance = tokenBalanceAsync.await().getOrNull() ?: emptyList()

        val nativeBalanceValue = account?.safeCoreBalance
        val nativeBalanceItem = nativeBalanceValue?.let {
            AntelopeTokenBalance(
                key = it.symbol,
                symbol = it.symbol,
                amount = it.amount,
                contract = "",
                decimals = nativeCoin.decimals?.toInt() ?: 0,
                metadata = AntelopeTokenBalance.AntelopeTokenMetadata(
                    name = it.symbol,
                    logo = "",
                    localImage = null,
                    website = "",
                    createdAt = ""
                ),
                exchanges = listOf(
                    AntelopeTokenBalance.AntelopeTokenExchangeData(
                        name = "",
                        price = 1.0,
                    )
                )
            )
        }

        val allBalances = mutableListOf(nativeBalanceItem)
        allBalances.addAll(tokenBalance)

        return@coroutineScope Result.success(allBalances.filterNotNull())
    }
}