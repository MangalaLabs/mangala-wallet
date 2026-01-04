package com.mangala.wallet.remote.provider.quicknode

import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.provider.BaseBalanceResponse
import com.mangala.wallet.model.provider.BaseGetPaginatedTransactionsForAddressResponse
import com.mangala.wallet.model.provider.covalenthq.GetNftsForAddressResponse
import com.mangala.wallet.model.provider.quicknode.QuickNodeTokenBalanceRequest
import com.mangala.wallet.model.provider.quicknode.QuickNodeTokenBalanceResponse
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.network.CustomError
import com.mangala.wallet.remote.provider.BaseBlockExplorerRemoteDataSource

class QuickNodeRemoteDataSource(private val api: QuickNodeApi): BaseBlockExplorerRemoteDataSource {
    override suspend fun getBalanceByNetWorkAndAddress(
        chainNetWork: String,
        address: String
    ): ApiResponse<BaseBalanceResponse, CustomError> {
        TODO()
    }

    override suspend fun getLatestTransactionsForAddress(
        chainName: BlockchainType,
        walletAddress: String
    ): ApiResponse<BaseGetPaginatedTransactionsForAddressResponse, CustomError> {
        TODO("Not yet implemented")
    }

    override suspend fun getPaginatedTransactionsForAddress(
        chainName: BlockchainType,
        walletAddress: String,
        page: Int
    ): ApiResponse<BaseGetPaginatedTransactionsForAddressResponse, CustomError> {
        TODO("Not yet implemented")
    }

    override suspend fun getNftsForAddress(
        chainName: BlockchainType,
        walletAddress: String,
        withUncached: Boolean
    ): ApiResponse<GetNftsForAddressResponse, CustomError> {
        TODO("Not yet implemented")
    }
}