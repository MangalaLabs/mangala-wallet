package com.mangala.wallet.features.send_base.step4

import com.mangala.wallet.features.chains.evmcompatible.model.SignTransactionRequest
import com.mangala.wallet.features.chains.evmcompatible.model.SignTransactionType
import com.mangala.wallet.ui.utils.navigation.args.SignTransactionRequestArgs
import com.mangala.wallet.ui.utils.navigation.args.SignTransactionTypeArg

fun SignTransactionRequest.toSignTransactionRequestArgs() = SignTransactionRequestArgs(
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

fun SignTransactionType.toSignTransactionTypeArg() = when (this) {
    is SignTransactionType.SendCoinOrErc20Token -> SignTransactionTypeArg.SendCoinOrErc20Token(amount, symbol, fiatValue)
    is SignTransactionType.SendErc721Or1155Token -> SignTransactionTypeArg.SendErc721Or1155Token(collectionName, tokenId)
    is SignTransactionType.Swap -> SignTransactionTypeArg.Swap(fromToken, toToken, fromAmount, toAmount)
    is SignTransactionType.Erc20Approve -> SignTransactionTypeArg.Erc20Approve(token, amount)
    is SignTransactionType.SignWeb3 -> SignTransactionTypeArg.SignWeb3(url, payload)
}