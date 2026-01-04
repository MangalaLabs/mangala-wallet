package com.mangala.wallet.model.provider

import com.mangala.wallet.model.provider.covalenthq.CovalenthqBalance
import com.mangala.wallet.model.provider.covalenthq.CovalenthqResponse
import com.mangala.wallet.model.provider.eosEVM.EosEvmBalance
import com.mangala.wallet.model.provider.eosEVM.EosEvmTokenBalanceResponse

fun CovalenthqResponse.toCovalenthqBalance() = CovalenthqBalance(
    data = data?.toDomainData(),
    error = error,
    errorMessage = errorMessage,
    errorCode = errorCode
)

private fun CovalenthqResponse.Data.toDomainData() = CovalenthqBalance.Data(
    address = address,
    updatedAt = updatedAt,
    nextUpdateAt = nextUpdateAt,
    quoteCurrency = quoteCurrency,
    chainId = chainId,
    chainName = chainName,
    items = items?.map { it.toDomainItem() }
)

private fun CovalenthqResponse.Data.Item.toDomainItem() = CovalenthqBalance.Data.Item(
    contractDecimals = contractDecimals,
    contractName = contractName,
    contractTickerSymbol = contractTickerSymbol,
    contractAddress = contractAddress,
    supportsErc = supportsErc,
    logoUrl = logoUrl,
    lastTransferredAt = lastTransferredAt,
    nativeToken = nativeToken,
    type = type,
    balance = balance,
    balance24h = balance24h,
    quoteRate = quoteRate,
    quoteRate24h = quoteRate24h,
    quote = quote,
    quote24h = quote24h
)

fun EosEvmTokenBalanceResponse.toEosEvmTokenBalance() = EosEvmBalance(
    message = message,
    result = result?.map { it.toDomainResult() },
    status = status
)

private fun EosEvmTokenBalanceResponse.Result.toDomainResult() = EosEvmBalance.Result(
    balance = balance,
    contractAddress = contractAddress,
    decimals = decimals?.toLongOrNull(),
    name = name,
    symbol = symbol,
    nativeToken = false
)