package com.mangala.wallet.ui.utils.navigation.args

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.model.blockchain.BlockchainType

data class SignTransactionRequestArgs(
    val requestId: String,
    val walletId: String,
    val accountId: String,
    val fromAddress: String,
    val nonce: Long,
    val blockchainType: BlockchainType,
    val transactionTo: String,
    val transactionValue: BigInteger,
    val transactionInput: ByteArray,
    val legacyGasPrice: Long?,
    val maxFeePerGas: Long?,
    val maxPriorityFeePerGas: Long?,
    val baseFee: Long?,
    val gasLimit: Long,
    val gasFiatValue: String,
    val transactionType: SignTransactionTypeArg,
    val contactName: String?,
    val contactAddress: String?
)

sealed class SignTransactionTypeArg {
    data class SendCoinOrErc20Token(val amount: String, val symbol: String, val fiatValue: String): SignTransactionTypeArg()
    data class SendErc721Or1155Token(val collectionName: String, val tokenId: String): SignTransactionTypeArg()
    data class Swap(val fromToken: String, val toToken: String, val fromAmount: String, val toAmount: String): SignTransactionTypeArg()
    data class Erc20Approve(val token: String, val amount: String): SignTransactionTypeArg()
    data class SignWeb3(val url: String, val payload: String): SignTransactionTypeArg()
}