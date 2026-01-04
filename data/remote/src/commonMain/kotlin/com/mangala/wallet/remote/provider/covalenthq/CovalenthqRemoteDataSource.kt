package com.mangala.wallet.remote.provider.covalenthq

import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.provider.covalenthq.CovalenthqBalance
import com.mangala.wallet.model.provider.covalenthq.CovalenthqResponse
import com.mangala.wallet.model.provider.covalenthq.GetNftsForAddressResponse
import com.mangala.wallet.model.provider.covalenthq.GetPaginatedCovalenthqTransactionsForAddressResponse
import com.mangala.wallet.model.provider.toCovalenthqBalance
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.di.safeApiCall
import com.mangala.wallet.remote.network.CustomError
import com.mangala.wallet.remote.provider.BaseBlockExplorerRemoteDataSource

class CovalenthqRemoteDataSource(private val api: CovalenthqApi): BaseBlockExplorerRemoteDataSource {
    companion object {
        const val TAG = "CovalenthqRemoteDataSource"
    }

    override suspend fun getBalanceByNetWorkAndAddress(
        chainNetWork: String,
        address: String
    ): ApiResponse<CovalenthqBalance, CustomError> {
        return safeApiCall<CovalenthqResponse, CustomError> {
            api.getBalanceByNetWorkAndAddress(chainNetWork, address)
        }.map { it.toCovalenthqBalance() }
    }

    override suspend fun getLatestTransactionsForAddress(
        chainName: BlockchainType,
        walletAddress: String,
    ): ApiResponse<GetPaginatedCovalenthqTransactionsForAddressResponse, CustomError> {
        return safeApiCall {
            api.getLatestTransactionsForAddress(chainName.endPointCovalenthq, walletAddress)
        }
    }

    override suspend fun getPaginatedTransactionsForAddress(
        chainName: BlockchainType,
        walletAddress: String,
        page: Int
    ): ApiResponse<GetPaginatedCovalenthqTransactionsForAddressResponse, CustomError> {
        return safeApiCall {
            api.getPaginatedTransactionsForAddress(chainName.endPointCovalenthq, walletAddress, page)
        }
    }

    override suspend fun getNftsForAddress(
        chainName: BlockchainType,
        walletAddress: String,
        withUncached: Boolean
    ): ApiResponse<GetNftsForAddressResponse, CustomError> {
        return safeApiCall {
            api.getNftsForAddress(chainName.endPointCovalenthq, walletAddress, withUncached)
        }
    }
}