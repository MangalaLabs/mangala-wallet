package com.mangala.wallet.model.provider.covalenthq

import com.mangala.wallet.model.provider.BaseBalanceResponse

data class CovalenthqBalance(
    val data: Data?,
    val error: Boolean?,
    val errorMessage: String?,
    val errorCode: Int?
): BaseBalanceResponse(
    items = data?.items?.map {
        Item(
            contractDecimals = it.contractDecimals,
            contractName = it.contractName,
            contractTickerSymbol = it.contractTickerSymbol,
            contractAddress = it.contractAddress,
            logoUrl = it.logoUrl,
            balance = it.balance,
            balance24h = it.balance24h,
            nativeToken = it.nativeToken
        )
    }
){
    data class Data(
        val address: String?,
        val updatedAt: String?,
        val nextUpdateAt: String?,
        val quoteCurrency: String?,
        val chainId: Int?,
        val chainName: String?,
        val items: List<Item>?,
//    val pagination: Any?
    ){
        data class Item(
            val contractDecimals: Long?,
            val contractName: String?,
            val contractTickerSymbol: String?,
            val contractAddress: String?,
            val supportsErc: List<String>?,
            val logoUrl: String?,
            val lastTransferredAt: String?,
            val nativeToken: Boolean?,
            val type: String?,
            val balance: String?,
            val balance24h: String?,
            val quoteRate: Double?,
            val quoteRate24h: Double?,
            val quote: Double?,
            val quote24h: Double?,
//    val nftData: Any?
        )
    }
}