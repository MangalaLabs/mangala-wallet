package com.mangala.wallet.remote.provider

import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.provider.BaseBalanceResponse
import com.mangala.wallet.model.provider.BaseGetPaginatedTransactionsForAddressResponse
import com.mangala.wallet.model.provider.covalenthq.GetNftsForAddressResponse
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.network.CustomError

interface BaseBlockExplorerRemoteDataSource {
    suspend fun getBalanceByNetWorkAndAddress(
        chainNetWork: String,
        address: String
    ): ApiResponse<BaseBalanceResponse, CustomError>

    suspend fun getLatestTransactionsForAddress(
        chainName: BlockchainType,
        walletAddress: String,
    ): ApiResponse<BaseGetPaginatedTransactionsForAddressResponse, CustomError>

    suspend fun getPaginatedTransactionsForAddress(
        chainName: BlockchainType,
        walletAddress: String,
        page: Int
    ): ApiResponse<BaseGetPaginatedTransactionsForAddressResponse, CustomError>

    suspend fun getNftsForAddress(
        chainName: BlockchainType,
        walletAddress: String,
        withUncached: Boolean = true
    ): ApiResponse<GetNftsForAddressResponse, CustomError>
}