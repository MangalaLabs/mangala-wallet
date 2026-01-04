package com.mangala.wallet.model.provider

abstract class BaseBalanceResponse(
    val items: List<Item>?
){
    data class Item(
        val contractDecimals: Long?,
        val contractName: String?,
        val contractTickerSymbol: String?,
        val contractAddress: String?,
        val logoUrl: String?,
        val balance: String?,
        val balance24h: String?,
        val nativeToken: Boolean?
    )
}