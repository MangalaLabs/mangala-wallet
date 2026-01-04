package com.mangala.wallet.model.provider.eosEVM

import com.mangala.wallet.model.provider.BaseBalanceResponse

data class EosEvmBalance(
    val message: String?,
    val result: List<Result>?,
    val status: String?
) : BaseBalanceResponse(
    items = result?.map {
        Item(
            contractAddress = it.contractAddress,
            balance = it.balance,
            contractDecimals = it.decimals,
            contractName = it.name,
            contractTickerSymbol = it.symbol,
            nativeToken = it.nativeToken,
            logoUrl = "",
            balance24h = it.balance
        )
    }
) {
    data class Result(
        val balance: String?,
        val contractAddress: String?,
        val decimals: Long?,
        val name: String?,
        val symbol: String?,
        val nativeToken: Boolean?
    )
}
