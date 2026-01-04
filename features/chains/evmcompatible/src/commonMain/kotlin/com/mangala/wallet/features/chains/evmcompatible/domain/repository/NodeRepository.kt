package com.mangala.wallet.features.chains.evmcompatible.domain.repository

import com.mangala.wallet.features.chains.evmcompatible.data.model.jsonrpc.EstimateGasJsonRpc
import com.mangala.wallet.features.chains.evmcompatible.data.model.jsonrpc.models.RpcBlock
import com.mangala.wallet.features.chains.evmcompatible.data.model.jsonrpc.models.RpcLatestBlock
import com.mangala.wallet.features.chains.evmcompatible.data.model.jsonrpc.models.RpcTransaction
import com.mangala.wallet.features.chains.evmcompatible.data.model.jsonrpc.models.RpcTransactionReceipt
import com.mangala.wallet.model.provider.JsonRpcNodeResponse
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.features.chains.evmcompatible.model.DefaultBlockParameter
import com.mangala.wallet.features.chains.evmcompatible.model.TransactionLog

interface NodeRepository {
    suspend fun getBalance(url: String, address: String): JsonRpcNodeResponse
    suspend fun estimateGas(url: String, id: Int, estimateGasJsonRpc: EstimateGasJsonRpc, isContract: Boolean): JsonRpcNodeResponse
    suspend fun getNonce(url: String, id: Int, address: Address, defaultBlockParameter: DefaultBlockParameter): String
    suspend fun getNonceNodeResponse(url: String, id: Int, address: Address, defaultBlockParameter: DefaultBlockParameter): JsonRpcNodeResponse
    suspend fun getGasPrice(url: String, id: Int): String
    suspend fun feeHistory(url: String, id: Int): String
    suspend fun sendRawTransaction(url: String, id: Int, signedTransaction: ByteArray): String
    suspend fun getTransactionReceipt(url: String, id: Int, transactionHashes: List<String>): List<RpcTransactionReceipt>
    suspend fun getTransactionReceipt(url: String, id: Int, transactionHash: String): RpcTransactionReceipt?
    suspend fun getTransaction(url: String, id: Int, transactionHash: ByteArray): RpcTransaction
    suspend fun getLatestBlock(url: String, id: Int): RpcLatestBlock?
    suspend fun getBlock(url: String, id: Int, blockNumber: Long): RpcBlock
    suspend fun getBlockNumber(url: String, id: Int): Long?
    suspend fun getLogs(url: String, id: Int, address: Address?, topics: List<ByteArray?>, fromBlock: Long, toBlock: Long, pullTimestamps: Boolean): List<TransactionLog>
    suspend fun call(url: String, id: Int, contractAddress: Address, data: ByteArray, defaultBlockParameter: DefaultBlockParameter): ByteArray?

    /**
     * Function call get data from external state in smart contract.
     * from: default is address of zero (Address(0))
     */
    suspend fun call(url: String, id: Int, contractAddress: Address, from: Address, data: ByteArray, defaultBlockParameter: DefaultBlockParameter): ByteArray?
}