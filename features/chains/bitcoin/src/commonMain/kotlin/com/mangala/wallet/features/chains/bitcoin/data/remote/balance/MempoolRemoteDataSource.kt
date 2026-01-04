package com.mangala.wallet.features.chains.bitcoin.data.remote.balance

import com.mangala.wallet.features.chains.bitcoin.data.remote.balance.response.MempoolGetBalanceResponse
import com.mangala.wallet.features.chains.bitcoin.data.remote.balance.response.MempoolUtxoResponseItem
import com.mangala.wallet.features.chains.bitcoin.data.remote.fee.response.MempoolRecommendedFeesResponse
import com.mangala.wallet.features.chains.bitcoin.data.remote.transaction.response.BitcoinTransactionResponse
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.di.safeApiCall
import com.mangala.wallet.remote.network.CustomError

class MempoolRemoteDataSource(
    private val mempoolApi: MempoolApi
) {

    suspend fun getBalance(
        blockchainType: BlockchainType,
        address: String
    ): ApiResponse<MempoolGetBalanceResponse, CustomError> {
        return safeApiCall {
            mempoolApi.getBalance(blockchainType.getMempoolNetworkName(), address)
        }
    }

    suspend fun getUtxo(
        blockchainType: BlockchainType,
        address: String
    ): ApiResponse<List<MempoolUtxoResponseItem>, CustomError> {
        return safeApiCall {
            mempoolApi.getUtxo(blockchainType.getMempoolNetworkName(), address)
        }
    }

    suspend fun getTransaction(
        blockchainType: BlockchainType,
        txId: String
    ): ApiResponse<BitcoinTransactionResponse, CustomError> {
        return safeApiCall {
            mempoolApi.getTransaction(blockchainType.getMempoolNetworkName(), txId)
        }
    }
    
    suspend fun getTransactionsByAddress(
        blockchainType: BlockchainType,
        address: String
    ): ApiResponse<List<BitcoinTransactionResponse>, CustomError> {
        return safeApiCall {
            mempoolApi.getTransactionsByAddress(blockchainType.getMempoolNetworkName(), address)
        }
    }
    
    suspend fun getTransactionsByAddressAfterTxid(
        blockchainType: BlockchainType,
        address: String,
        afterTxid: String
    ): ApiResponse<List<BitcoinTransactionResponse>, CustomError> {
        return safeApiCall {
            mempoolApi.getTransactionsByAddressAfterTxid(
                blockchainType.getMempoolNetworkName(), 
                address,
                afterTxid
            )
        }
    }

    suspend fun sendTransaction(
        blockchainType: BlockchainType,
        transactionHex: String
    ): ApiResponse<String, CustomError> {
        return safeApiCall {
            mempoolApi.sendTransaction(blockchainType.getMempoolNetworkName(), transactionHex)
        }
    }

    suspend fun getRecommendedFees(): ApiResponse<MempoolRecommendedFeesResponse, CustomError> {
        return safeApiCall {
            mempoolApi.getRecommendedFees() // Getting mainnet fees, even on testnets
        }
    }

    private fun BlockchainType.getMempoolNetworkName(): String {
        return when (this) {
            BlockchainType.Bitcoin -> ""
            BlockchainType.BitcoinTestnet4 -> "testnet4/" // Because mainnet doesn't need a network name, will need to add it to path of testnets
            else -> throw IllegalArgumentException("Unsupported blockchain type: $this")
        }
    }
}