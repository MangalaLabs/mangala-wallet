package com.mangala.wallet.features.chains.evmcompatible.domain.repository

import com.mangala.wallet.features.chains.evmcompatible.data.model.jsonrpc.EstimateGasJsonRpc
import com.mangala.wallet.features.chains.evmcompatible.core.amountToPush
import com.mangala.wallet.features.chains.evmcompatible.core.hexStringToByteArrayOrNull
import com.mangala.wallet.features.chains.evmcompatible.core.hexStringToLongOrNull
import com.mangala.wallet.features.chains.evmcompatible.core.toHexString
import com.mangala.wallet.features.chains.evmcompatible.data.model.jsonrpc.models.RpcBlock
import com.mangala.wallet.features.chains.evmcompatible.data.model.jsonrpc.models.RpcLatestBlock
import com.mangala.wallet.features.chains.evmcompatible.data.model.jsonrpc.models.RpcLatestBlockResponseWithoutTransactionDetail
import com.mangala.wallet.features.chains.evmcompatible.data.model.jsonrpc.models.RpcTransaction
import com.mangala.wallet.features.chains.evmcompatible.data.model.jsonrpc.models.RpcTransactionReceipt
import com.mangala.wallet.features.chains.evmcompatible.data.model.jsonrpc.models.RpcTransactionReceiptResponse
import com.mangala.wallet.features.chains.evmcompatible.data.model.provider.infura.NodeRequest
import com.mangala.wallet.model.provider.JsonRpcNodeResponse
import com.mangala.wallet.features.chains.evmcompatible.data.model.provider.infura.Param
import com.mangala.wallet.features.chains.evmcompatible.data.remote.provider.infura.NodeRemoteDataSource
import com.mangala.wallet.features.chains.evmcompatible.domain.mapper.toRpcLatestBlock
import com.mangala.wallet.features.chains.evmcompatible.domain.mapper.toRpcTransactionReceipt
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.features.chains.evmcompatible.model.DefaultBlockParameter
import com.mangala.wallet.features.chains.evmcompatible.model.GasPrice
import com.mangala.wallet.features.chains.evmcompatible.model.TransactionLog
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class NodeRepositoryImpl(
    private val network: NodeRemoteDataSource,
    private val parsingJson: Json,
    private val serializingJson: Json
) : NodeRepository {

    override suspend fun getBalance(url: String, address: String): JsonRpcNodeResponse {
        return network.getData(
            url,
            NodeRequest(
                "2.0",
                "eth_getBalance",
                listOf(Param.StringParam(address), Param.StringParam("latest")),
                1
            )
        )
    }

    override suspend fun estimateGas(
        url: String,
        id: Int,
        estimateGasJsonRpc: EstimateGasJsonRpc,
        isContract: Boolean
    ): JsonRpcNodeResponse {
        val map = mutableMapOf<String, String?>()

        when (estimateGasJsonRpc.gasPrice) {
            is GasPrice.Eip1559 -> {
                map["from"] = estimateGasJsonRpc.from.hex
                if (!isContract) {
                    map["to"] = estimateGasJsonRpc.to?.hex
                    map["value"] = estimateGasJsonRpc.amount?.amountToPush()
                    map["gas"] = estimateGasJsonRpc.gasLimit?.toHexString()
                    map["maxFeePerGas"] = estimateGasJsonRpc.gasPrice.maxFeePerGas?.toHexString()
                    map["maxPriorityFeePerGas"] =
                        estimateGasJsonRpc.gasPrice.maxPriorityFeePerGas?.toHexString()
                }
                map["data"] = estimateGasJsonRpc.data?.toHexString()
            }

            is GasPrice.Legacy -> {
                map["from"] = estimateGasJsonRpc.from.hex
                if (!isContract) {
                    map["to"] = estimateGasJsonRpc.to?.hex
                    map["value"] = estimateGasJsonRpc.amount?.amountToPush()
                    map["gas"] = estimateGasJsonRpc.gasLimit?.toHexString()
                    map["gasPrice"] = estimateGasJsonRpc.gasPrice?.legacyGasPrice?.toHexString()
                }
//                map["to"] = estimateGasJsonRpc.to?.hex
                map["data"] = estimateGasJsonRpc.data?.toHexString()
            }
        }
//        val inputData = InfuraEstimateGasRequest(
//            "2.0",
//            estimateGasJsonRpc.method,
//            listOf(map),
//            id
//        )
//        val input = serializingJson.encodeToString(inputData)

        val nodeRequest = NodeRequest(
            "2.0",
            estimateGasJsonRpc.method,
            listOf(Param.MapParam(map)),
            id
        )

        return network.getData(url, nodeRequest)
    }

    override suspend fun getNonce(
        url: String,
        id: Int,
        address: Address,
        defaultBlockParameter: DefaultBlockParameter
    ): String {
        println("leonard start node getNonce")
        val inputData = NodeRequest(
            "2.0",
            "eth_getTransactionCount",
            listOf(Param.StringParam(address.hex), Param.StringParam(defaultBlockParameter.raw)),
            id
        )
        val nodeResponse = network.getData(url, inputData)
        println("nonce response: $nodeResponse")
        return parsingJson.encodeToString(nodeResponse) // TODO - LEONARD: migrate using node response after all
    }

    override suspend fun getNonceNodeResponse(
        url: String,
        id: Int,
        address: Address,
        defaultBlockParameter: DefaultBlockParameter
    ): JsonRpcNodeResponse {
        val inputData = NodeRequest(
            "2.0",
            "eth_getTransactionCount",
            listOf(Param.StringParam(address.hex), Param.StringParam(defaultBlockParameter.raw)),
            id
        )
        return network.getData(url, inputData)
    }

    override suspend fun getGasPrice(url: String, id: Int): String {
        val inputData = NodeRequest(
            "2.0",
            "eth_gasPrice",
            listOf(),
            id
        )
        val nodeResponse = network.getData(url, inputData)
        return parsingJson.encodeToString(nodeResponse) // TODO - LEONARD: migrate using node response after all
    }

    override suspend fun feeHistory(url: String, id: Int): String {
        val inputData =
            "{\"id\":$id,\"method\":\"eth_feeHistory\",\"params\":[\"0x4\",\"latest\",[50]],\"jsonrpc\":\"2.0\"}"
        return network.getDataByString(url, inputData)
    }

    override suspend fun sendRawTransaction(
        url: String,
        id: Int,
        signedTransaction: ByteArray
    ): String {
        val inputData = NodeRequest(
            "2.0",
            "eth_sendRawTransaction",
            listOf(Param.StringParam(signedTransaction.toHexString())),
            id
        )
        val nodeResponse = network.getData(url, inputData)
        return parsingJson.encodeToString(nodeResponse) // TODO - LEONARD: migrate using node response after all
    }

    override suspend fun getTransactionReceipt(
        url: String,
        id: Int,
        transactionHashes: List<String>
    ): List<RpcTransactionReceipt> {
        val requests = transactionHashes.map {
            NodeRequest(
                "2.0",
                "eth_getTransactionReceipt",
                listOf(Param.StringParam(it)),
                id
            )
        }
        val input = serializingJson.encodeToString(requests)
        val transactionReceiptResponse: List<RpcTransactionReceiptResponse> =
            parsingJson.decodeFromString(network.getDataByString(url, input))

        return transactionReceiptResponse.mapNotNull { it.toRpcTransactionReceipt() }
    }

    override suspend fun getTransactionReceipt(
        url: String,
        id: Int,
        transactionHash: String
    ): RpcTransactionReceipt? {
        val inputData = NodeRequest(
            "2.0",
            "eth_getTransactionReceipt",
            listOf(Param.StringParam(transactionHash)),
            id
        )
        val input = serializingJson.encodeToString(inputData)
        val transactionReceiptResponse: RpcTransactionReceiptResponse =
            parsingJson.decodeFromString(network.getDataByString(url, input))

        return transactionReceiptResponse.toRpcTransactionReceipt()
    }

    override suspend fun getTransaction(
        url: String,
        id: Int,
        transactionHash: ByteArray
    ): RpcTransaction {
        TODO("Not yet implemented")
    }

    override suspend fun getBlock(url: String, id: Int, blockNumber: Long): RpcBlock {
        TODO("Not yet implemented")
    }
    
    override suspend fun getLatestBlock(url: String, id: Int): RpcLatestBlock? {
        val inputData = NodeRequest(
            "2.0",
            "eth_getBlockByNumber",
            listOf(Param.StringParam("latest"), Param.BooleanParam(false)),
            id
            
        )
        val input = serializingJson.encodeToString(inputData) 
        val latestBlockResponse: RpcLatestBlockResponseWithoutTransactionDetail =
            parsingJson.decodeFromString(network.getDataByString(url, input))

        return latestBlockResponse.toRpcLatestBlock()
    }

    override suspend fun getBlockNumber(url: String, id: Int): Long? {
        val inputData = NodeRequest(
            "2.0",
            "eth_blockNumber",
            listOf(),
            id
        )
        val data = network.getData(url, inputData)
        return data.result?.hexStringToLongOrNull()
    }

    override suspend fun getLogs(
        url: String,
        id: Int,
        address: Address?,
        topics: List<ByteArray?>,
        fromBlock: Long,
        toBlock: Long,
        pullTimestamps: Boolean
    ): List<TransactionLog> {
        TODO("Not yet implemented")
    }

    override suspend fun call(
        url: String,
        id: Int,
        contractAddress: Address,
        data: ByteArray,
        defaultBlockParameter: DefaultBlockParameter
    ): ByteArray? {
        val map = mutableMapOf<String, String>()
        map["to"] = contractAddress.hex
        map["data"] = data.toHexString()
        val nodeRequest = NodeRequest(
            "2.0",
            "eth_call",
            listOf(Param.MapParam(map), Param.StringParam(defaultBlockParameter.raw)),
            id
        )

        val nodeResponse = network.getData(url, nodeRequest)
        println("data response call node: $data")
        return try {
            nodeResponse.result?.hexStringToByteArrayOrNull()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun call(
        url: String,
        id: Int,
        contractAddress: Address,
        from: Address,
        data: ByteArray,
        defaultBlockParameter: DefaultBlockParameter
    ): ByteArray? {
//        val map = mutableMapOf<String, String?>()
//        map["to"] = contractAddress.hex
//        map["data"] = data.toHexString()
//
//        val inputData = NodeAnyRequest(
//            "2.0",
//            "eth_call",
//            listOf(map, defaultBlockParameter.raw),
//            id
//        )

        val input =
            "{\"jsonrpc\":\"2.0\",\"method\":\"eth_call\",\"params\": [{\"from\": \"${from.hex}\", \"to\": \"${contractAddress.hex}\",\"data\": \"${data.toHexString()}\"}, \"${defaultBlockParameter.raw}\"],\"id\":${id.toString()}}"
        val data = network.getDataByString(url, input)
        try {
            val jsonRpcNodeResponse =
                parsingJson.decodeFromString(JsonRpcNodeResponse.serializer(), data)
            return jsonRpcNodeResponse.result?.hexStringToByteArrayOrNull()
        } catch (e: Exception) {
            return null
        }
    }

}