package com.mangala.wallet.features.transactionqr.presentation

import com.mangala.wallet.features.chains.evmcompatible.model.SignTransactionRequest
import com.mangala.wallet.features.chains.evmcompatible.model.SignTransactionType
import com.mangala.wallet.ui.utils.navigation.args.SignTransactionRequestArgs
import com.mangala.wallet.ui.utils.navigation.args.SignTransactionTypeArg

// TODO: Find a way to share this
fun SignTransactionRequestArgs.toSignTransactionRequest() = SignTransactionRequest(
    requestId = requestId,
    walletId = walletId,
    accountId = accountId,
    fromAddress = fromAddress,
    nonce = nonce,
    blockchainType = blockchainType,
    transactionTo = transactionTo,
    transactionValue = transactionValue,
    transactionInput = transactionInput,
    legacyGasPrice = legacyGasPrice,
    maxFeePerGas = maxFeePerGas,
    maxPriorityFeePerGas = maxPriorityFeePerGas,
    baseFee = baseFee,
    gasLimit = gasLimit,
    gasFiatValue = gasFiatValue,
    transactionType = transactionType.toSignTransactionTypeArg(),
    contactName = contactName,
    contactAddress = contactAddress
)

fun SignTransactionTypeArg.toSignTransactionTypeArg() = when (this) {
    is SignTransactionTypeArg.SendCoinOrErc20Token -> SignTransactionType.SendCoinOrErc20Token(amount, symbol, fiatValue)
    is SignTransactionTypeArg.SendErc721Or1155Token -> SignTransactionType.SendErc721Or1155Token(collectionName, tokenId)
    is SignTransactionTypeArg.Swap -> SignTransactionType.Swap(fromToken, toToken, fromAmount, toAmount)
    is SignTransactionTypeArg.Erc20Approve -> SignTransactionType.Erc20Approve(token, amount)
    is SignTransactionTypeArg.SignWeb3 -> SignTransactionType.SignWeb3(url, payload)
}