package com.mangala.wallet.domain.provider.moralis.mapper

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.mangala.wallet.domain.transaction.history.Transaction
import com.mangala.wallet.domain.transaction.history.TransactionStatus
import com.mangala.wallet.domain.transaction.history.TransactionType
import com.mangala.wallet.model.provider.moralis.Erc20Transfer
import com.mangala.wallet.model.provider.moralis.NativeTransfer
import com.mangala.wallet.model.provider.moralis.NftTransfer
import com.mangala.wallet.model.provider.moralis.TransactionResult
import com.mangala.wallet.utils.ext.ethToWei
import com.mangala.wallet.utils.ext.parseUtcDateTimeToInstantOrNull
import com.mangala.wallet.utils.ext.toLong
import commangalawalletdatabase.TransactionsEntity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// https://goldrush.dev/docs/api-reference/transactions/get-recent-transactions-for-address-v3#response-items-gas-offered
fun TransactionResult.toTransactionEntity(
    json: Json,
    accountId: String,
    blockchainUid: String
) = TransactionsEntity(
    blockHeight = blockNumber?.toLong() ?: 0L,
    blockSignedAt = blockTimestamp?.parseUtcDateTimeToInstantOrNull()?.toEpochMilliseconds() ?: 0,
    feesPaid = transactionFee?.let { BigDecimal.parseString(it).ethToWei(18).toStringExpanded() } ?: "0",
    fromAddress = fromAddress.orEmpty(),
    fromAddressLabel = fromAddressLabel.orEmpty(),
    gasContractAddress = null,
    gasContractDecimals = null,
    gasContractName = null,
    gasContractTickerSymbol = null,
    gasLogoUrl = null,
    gasOffered = gas?.toLongOrNull() ?: 0,
    gasPrice = gasPrice.orEmpty(),
    gasQuote = "", // The gas spent in quote-currency denomination.
    gasQuoteRate = "", // The native gas exchange rate for the requested quote-currency.
    gasSpent = receiptGasUsed.orEmpty(), // The gas spent for this tx.
    minerAddress = "",
    prettyGasQuote = "", // A prettier version of the quote for rendering purposes.
    prettyValueQuote = "", // A prettier version of the quote for rendering purposes.
    status = if (receiptStatus == "1") TransactionStatus.SUCCESS.name else TransactionStatus.FAILED.name,
    toAddress = toAddress.orEmpty(),
    toAddressLabel = toAddressLabel.orEmpty(),
    transactionHash = hash.orEmpty(),
    txOffset = transactionIndex?.toLong() ?: 0, // The offset is the position of the tx in the block.
    value_ = value.orEmpty(),
    valueQuote = "", // The value attached in quote-currency to this tx.
    accountId = accountId,
    transactionType = category?.mapTransactionCategoryToTransactionType()?.name ?: TransactionType.CONTRACT_CALL.name,
    logEvents = null, // TODO: Put in
    blockchain_uid = blockchainUid,
    is_nft_transaction = nftTransfers?.isNotEmpty()?.toLong() ?: 0,
    nft_transfers = json.encodeToString(nftTransfers),
    erc20_transfers = json.encodeToString(erc20Transfers),
    native_transfers = json.encodeToString(nativeTransfers),
)

fun Erc20Transfer.toErc20Transfer() = Transaction.Erc20Transfer(
    tokenName = tokenName.orEmpty(),
    tokenSymbol = tokenSymbol.orEmpty(),
    direction = direction.orEmpty(),
    amountFormatted = valueFormatted.orEmpty(),
    assetAddress = address.orEmpty(),
)

fun NftTransfer.toNftTransfer() = Transaction.NftTransfer(
    tokenSymbol = tokenId.orEmpty(),
    amountFormatted = amount.orEmpty(),
    direction = direction.orEmpty(),
    assetAddress = tokenAddress.orEmpty(),
)

fun NativeTransfer.toNativeTransfer() = Transaction.NativeTransfer(
    tokenSymbol = tokenSymbol.orEmpty(),
    direction = direction.orEmpty(),
    amountFormatted = valueFormatted.orEmpty()
)

// https://docs.moralis.com/web3-data-api/evm/wallet-history
private fun String.mapTransactionCategoryToTransactionType(): TransactionType {
    return when(this.lowercase()) {
        "send" -> TransactionType.SEND
        "receive" -> TransactionType.RECEIVE
        "nft send" -> TransactionType.SEND
        "nft receive" -> TransactionType.RECEIVE
        "token send" -> TransactionType.SEND
        "token receive" -> TransactionType.RECEIVE
        "deposit" -> TransactionType.CONTRACT_CALL
        "withdraw" -> TransactionType.CONTRACT_CALL
        "token swap" -> TransactionType.SWAP
        "airdrop" -> TransactionType.RECEIVE
        "mint" -> TransactionType.CONTRACT_CALL
        "burn" -> TransactionType.CONTRACT_CALL
        "nft purchase" -> TransactionType.RECEIVE
        "nft sale" -> TransactionType.SEND
        "borrow" -> TransactionType.CONTRACT_CALL
        "approve" -> TransactionType.CONTRACT_CALL
        "revoke" -> TransactionType.CONTRACT_CALL
        "contract interaction" -> TransactionType.CONTRACT_CALL
        else -> TransactionType.CONTRACT_CALL
    }
}