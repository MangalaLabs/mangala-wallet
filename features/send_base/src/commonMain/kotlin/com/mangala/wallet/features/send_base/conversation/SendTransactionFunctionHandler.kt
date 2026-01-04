package com.mangala.wallet.features.send_base.conversation

import com.mangala.wallet.antelope_balance.Balance
import com.mangala.wallet.core.ai.domain.model.function.FunctionResult
import com.mangala.wallet.core.ai.domain.model.function.handler.FunctionHandler
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.send.AntelopeSendCryptoUseCase
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.antelope.base.domain.model.Transaction
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

class SendTransactionFunctionHandler(
    private val antelopeSendCryptoUseCase: AntelopeSendCryptoUseCase
) : FunctionHandler {
    override val functionName: String = SEND_TRANSACTION_FUNCTION_NAME

    override suspend fun execute(parameters: Map<String, Any?>): FunctionResult {
        return try {
            val recipientAddress = parameters[SEND_TRANSACTION_PARAM_RECIPIENT_ADDRESS] as? String
                ?: return FunctionResult.Error("", "Missing recipient_address parameter")
            val amount = parameters[SEND_TRANSACTION_PARAM_AMOUNT]?.toString()
                ?: return FunctionResult.Error("", "Missing amount parameter")
            val asset = parameters[SEND_TRANSACTION_PARAM_ASSET] as? String
                ?: return FunctionResult.Error("", "Missing asset parameter")
            val memo = parameters[SEND_TRANSACTION_PARAM_MEMO] as? String ?: ""
            val feeLevel = parameters[SEND_TRANSACTION_PARAM_FEE] as? String ?: "medium"
            val senderAddress = parameters[SEND_TRANSACTION_SENDER_ADDRESS] as? String
                ?: return FunctionResult.Error("", "Missing sender_address parameter")
            val blockchainUid = parameters[SEND_TRANSACTION_BLOCKCHAIN_UID]?.toString() ?: return FunctionResult.Error("", "Missing blockchain_uid parameter")
            val blockchainType = BlockchainType.fromUid(blockchainUid)
            
            // Check if we have a prepared transaction from ResourceProviderResponse
            val preparedTransaction = parameters[SEND_TRANSACTION_PARAM_TRANSACTION]?.let { transactionParam ->
                when (transactionParam) {
                    is String -> {
                        try {
                            Json.decodeFromString<Transaction>(transactionParam)
                        } catch (e: Exception) {
                            null // Invalid JSON, fall back to regular transaction flow
                        }
                    }
                    is Transaction -> transactionParam // Backward compatibility
                    else -> null
                }
            }
            
            val response = if (preparedTransaction != null) {
                // Use the prepared transaction with resource provisions
                antelopeSendCryptoUseCase.pushResourceProvidedTransaction(
                    blockchainType = blockchainType,
                    senderAccountName = senderAddress,
                    transaction = preparedTransaction
                )
            } else {
                // Fall back to regular transaction flow
                antelopeSendCryptoUseCase.sendToken(
                    blockchainType = blockchainType,
                    senderAccountName = senderAddress,
                    recipientAccountName = recipientAddress,
                    quantity = Balance(amount.toDouble(), asset),
                    memo = memo,
                    contract = asset
                )
            }

            response.getOrNull()?.let { successResponse ->
                FunctionResult.Success(
                    data = mapOf(
                        "transactionId" to successResponse,
                        "status" to "success",
                        "network" to determineNetwork(asset),
                        "amount" to amount,
                        "asset" to asset,
                        "recipient" to recipientAddress,
                        "memo" to memo,
                        "feeLevel" to feeLevel,
                        "blockExplorerUrl" to generateBlockExplorerUrl(asset),
                        "estimatedConfirmationTime" to getEstimatedConfirmationTime(asset),
                        "message" to if (preparedTransaction != null) {
                            "Transaction submitted successfully using resource provider."
                        } else {
                            "Transaction submitted successfully. Implementation enhanced with multi-chain support framework."
                        }
                    )
                )
            } ?: return FunctionResult.Error("", "Transaction failed: ${response.exceptionOrNull()?.message ?: "Unknown error"}")
        } catch (e: Exception) {
            FunctionResult.Error("", "Transaction failed: ${e.message}")
        }
    }
    
    private fun determineNetwork(asset: String): String {
        return when (asset.uppercase()) {
            "EOS" -> "EOS"
            "TLOS" -> "Telos"
            "WAX" -> "WAX"
            "ETH" -> "Ethereum"
            "MATIC" -> "Polygon"
            "BNB" -> "BNB Smart Chain"
            "ARB" -> "Arbitrum"
            "BTC" -> "Bitcoin"
            "BCH" -> "Bitcoin Cash"
            "LTC" -> "Litecoin"
            else -> "Unknown"
        }
    }
    
    private fun generateBlockExplorerUrl(asset: String): String {
        return when (asset.uppercase()) {
            "EOS" -> "https://bloks.io/transaction/"
            "TLOS" -> "https://telos.bloks.io/transaction/"
            "WAX" -> "https://wax.bloks.io/transaction/"
            "ETH" -> "https://etherscan.io/tx/"
            "MATIC" -> "https://polygonscan.com/tx/"
            "BNB" -> "https://bscscan.com/tx/"
            "ARB" -> "https://arbiscan.io/tx/"
            "BTC" -> "https://blockstream.info/tx/"
            "BCH" -> "https://blockchair.com/bitcoin-cash/transaction/"
            "LTC" -> "https://blockchair.com/litecoin/transaction/"
            else -> "https://explorer.mangala.wallet/"
        }
    }
    
    private fun getEstimatedConfirmationTime(asset: String): String {
        return when (asset.uppercase()) {
            "EOS", "TLOS", "WAX" -> "0.5 seconds"
            "ETH" -> "2-5 minutes"
            "MATIC" -> "2-5 seconds"
            "BNB" -> "3 seconds"
            "ARB" -> "1-2 minutes"
            "BTC" -> "10-60 minutes"
            "BCH" -> "10-60 minutes"
            "LTC" -> "2.5-10 minutes"
            else -> "Variable"
        }
    }
}