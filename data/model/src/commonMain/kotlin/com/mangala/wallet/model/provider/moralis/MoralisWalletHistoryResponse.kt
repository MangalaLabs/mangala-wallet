package com.mangala.wallet.model.provider.moralis

import com.mangala.wallet.model.provider.BaseGetPaginatedTransactionsForAddressResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MoralisWalletHistoryResponse(
    val page: String? = null,
    @SerialName("page_size") val pageSize: String? = null,
    val cursor: String? = "",
    val result: List<TransactionResult> = emptyList()
): BaseGetPaginatedTransactionsForAddressResponse()

@Serializable
data class TransactionResult(
    val hash: String? = null,
    val nonce: String? = null,
    @SerialName("transaction_index") val transactionIndex: String? = null,
    @SerialName("from_address_entity") val fromAddressEntity: String? = null,
    @SerialName("from_address_entity_logo") val fromAddressEntityLogo: String? = null,
    @SerialName("from_address") val fromAddress: String? = null,
    @SerialName("from_address_label") val fromAddressLabel: String? = null,
    @SerialName("to_address_entity") val toAddressEntity: String? = null,
    @SerialName("to_address_entity_logo") val toAddressEntityLogo: String? = null,
    @SerialName("to_address") val toAddress: String? = null,
    @SerialName("to_address_label") val toAddressLabel: String? = null,
    val value: String? = null,
    val gas: String? = null,
    @SerialName("gas_price") val gasPrice: String? = null,
    @SerialName("receipt_cumulative_gas_used") val receiptCumulativeGasUsed: String? = null,
    @SerialName("receipt_gas_used") val receiptGasUsed: String? = null,
    @SerialName("receipt_contract_address") val receiptContractAddress: String? = null,
    @SerialName("receipt_root") val receiptRoot: String? = null,
    @SerialName("receipt_status") val receiptStatus: String? = null,
    @SerialName("block_timestamp") val blockTimestamp: String? = null,
    @SerialName("block_number") val blockNumber: String? = null,
    @SerialName("block_hash") val blockHash: String? = null,
    @SerialName("internal_transactions") val internalTransactions: List<InternalTransaction>? = emptyList(),
    @SerialName("nft_transfers") val nftTransfers: List<NftTransfer>? = emptyList(),
    @SerialName("erc20_transfers") val erc20Transfers: List<Erc20Transfer>? = emptyList(),
    @SerialName("native_transfers") val nativeTransfers: List<NativeTransfer>? = emptyList(),
    @SerialName("transaction_fee") val transactionFee: String? = null,
    @SerialName("category") val category: String? = null
)

@Serializable
data class InternalTransaction(
    @SerialName("transaction_hash") val transactionHash: String? = null,
    @SerialName("block_number") val blockNumber: String? = null,
    @SerialName("block_hash") val blockHash: String? = null,
    val type: String? = null,
    val from: String? = null,
    val to: String? = null,
    val value: String? = null,
    val gas: String? = null,
    @SerialName("gas_used") val gasUsed: String? = null,
    val input: String? = null,
    val output: String? = null
)

@Serializable
data class NftTransfer(
    @SerialName("token_address") val tokenAddress: String? = null,
    @SerialName("token_id") val tokenId: String? = null,
    @SerialName("from_address_entity") val fromAddressEntity: String? = null,
    @SerialName("from_address_entity_logo") val fromAddressEntityLogo: String? = null,
    @SerialName("from_address") val fromAddress: String? = null,
    @SerialName("from_address_label") val fromAddressLabel: String? = null,
    @SerialName("to_address_entity") val toAddressEntity: String? = null,
    @SerialName("to_address_entity_logo") val toAddressEntityLogo: String? = null,
    @SerialName("to_address") val toAddress: String? = null,
    @SerialName("to_address_label") val toAddressLabel: String? = null,
    val value: String? = null,
    val amount: String? = null,
    @SerialName("contract_type") val contractType: String? = null,
    @SerialName("block_number") val blockNumber: String? = null,
    @SerialName("block_timestamp") val blockTimestamp: String? = null,
    @SerialName("block_hash") val blockHash: String? = null,
    @SerialName("transaction_hash") val transactionHash: String? = null,
    @SerialName("transaction_type") val transactionType: String? = null,
    @SerialName("transaction_index") val transactionIndex: Int? = null,
    @SerialName("log_index") val logIndex: Int? = null,
    val operator: String? = null,
    @SerialName("possible_spam") val possibleSpam: String? = null,
    @SerialName("verified_collection") val verifiedCollection: String? = null,
    @SerialName("direction") val direction: String? = null
)

@Serializable
data class Erc20Transfer(
    @SerialName("token_name") val tokenName: String? = null,
    @SerialName("token_symbol") val tokenSymbol: String? = null,
    @SerialName("token_logo") val tokenLogo: String? = null,
    @SerialName("token_decimals") val tokenDecimals: String? = null,
    @SerialName("transaction_hash") val transactionHash: String? = null,
    val address: String? = null,
    @SerialName("block_timestamp") val blockTimestamp: String? = null,
    @SerialName("block_number") val blockNumber: String? = null,
    @SerialName("block_hash") val blockHash: String? = null,
    @SerialName("to_address_entity") val toAddressEntity: String? = null,
    @SerialName("to_address_entity_logo") val toAddressEntityLogo: String? = null,
    @SerialName("to_address") val toAddress: String? = null,
    @SerialName("to_address_label") val toAddressLabel: String? = null,
    @SerialName("from_address_entity") val fromAddressEntity: String? = null,
    @SerialName("from_address_entity_logo") val fromAddressEntityLogo: String? = null,
    @SerialName("from_address") val fromAddress: String? = null,
    @SerialName("from_address_label") val fromAddressLabel: String? = null,
    val value: String? = null,
    @SerialName("value_formatted") val valueFormatted: String? = null,
    @SerialName("transaction_index") val transactionIndex: Int? = null,
    @SerialName("log_index") val logIndex: Int? = null,
    @SerialName("possible_spam") val possibleSpam: String? = null,
    @SerialName("verified_contract") val verifiedContract: String? = null,
    @SerialName("direction") val direction: String? = null
)

@Serializable
data class NativeTransfer(
    @SerialName("from_address_entity") val fromAddressEntity: String? = null,
    @SerialName("from_address_entity_logo") val fromAddressEntityLogo: String? = null,
    @SerialName("from_address") val fromAddress: String? = null,
    @SerialName("from_address_label") val fromAddressLabel: String? = null,
    @SerialName("to_address_entity") val toAddressEntity: String? = null,
    @SerialName("to_address_entity_logo") val toAddressEntityLogo: String? = null,
    @SerialName("to_address") val toAddress: String? = null,
    @SerialName("to_address_label") val toAddressLabel: String? = null,
    val value: String? = null,
    @SerialName("value_formatted") val valueFormatted: String? = null,
    @SerialName("internal_transaction") val internalTransaction: String? = null,
    @SerialName("token_symbol") val tokenSymbol: String? = null,
    @SerialName("token_logo") val tokenLogo: String? = null,
    @SerialName("direction") val direction: String? = null
)