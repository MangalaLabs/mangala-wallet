package com.mangala.wallet.remote.provider.eosEVM

import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.provider.covalenthq.GetNftsForAddressResponse
import com.mangala.wallet.model.provider.eosEVM.EosEvmBalance
import com.mangala.wallet.model.provider.eosEVM.EosEvmNativeCoinBalanceResponse
import com.mangala.wallet.model.provider.eosEVM.EosEvmTokenBalanceResponse
import com.mangala.wallet.model.provider.eosEVM.GetPaginatedEosEvmTokenTransferForAddressResponse
import com.mangala.wallet.model.provider.eosEVM.GetPaginatedEosEvmTransactionsForAddressResponse
import com.mangala.wallet.model.provider.toEosEvmTokenBalance
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.di.safeApiCall
import com.mangala.wallet.remote.network.CustomError
import com.mangala.wallet.remote.provider.BaseBlockExplorerRemoteDataSource
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class EosEvmRemoteDataSource(private val api: EosEvmApi) : BaseBlockExplorerRemoteDataSource {
    companion object {
        const val MAINNET = "EosEvmRemoteDataSource_Mainnet"
        const val TESTNET = "EosEvmRemoteDataSource_Testnet"
        const val TRANSACTION_FETCH_PAGE_SIZE = 10
    }

    override suspend fun getBalanceByNetWorkAndAddress(
        chainNetWork: String,
        address: String
    ): ApiResponse<EosEvmBalance, CustomError> = coroutineScope {
        val asyncResponse = async {
            safeApiCall<EosEvmTokenBalanceResponse, CustomError> {
                api.getBalanceByAddress(address)
            }
        }

        val asyncNativeCoinResponse = async {
            safeApiCall<EosEvmNativeCoinBalanceResponse, CustomError> {
                api.getNativeCoinBalanceByAddress(address)
            }
        }

        val nativeCoinResponse = asyncNativeCoinResponse.await()
        val resultList = mutableListOf(
            if (nativeCoinResponse is ApiResponse.Success)
                EosEvmBalance.Result(
                    balance = nativeCoinResponse.body.result ?: "0",
                    contractAddress = "0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee",
                    decimals = 18,
                    name = "EOS",
                    symbol = "EOS",
                    nativeToken = true
                )
            else EosEvmBalance.Result(
                balance = "0",
                contractAddress = "0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee",
                decimals = 18,
                name = "EOS",
                symbol = "EOS",
                nativeToken = true
            )
        )

        val response = asyncResponse.await()
        val tokenBalance = if (response is ApiResponse.Success) {
            val domainResponse = response.body.toEosEvmTokenBalance()
            if (domainResponse.status == "1") {
                resultList.addAll(domainResponse.result ?: emptyList())
                ApiResponse.Success(
                    domainResponse.copy(
                        result = resultList.toList()
                    )
                )
            } else {
                ApiResponse.Success(
                    domainResponse.copy(
                        result =  resultList.toList()
                    )
                )
            }
        } else response.map { it.toEosEvmTokenBalance() }

        return@coroutineScope tokenBalance
    }

    override suspend fun getLatestTransactionsForAddress(
        chainName: BlockchainType,
        walletAddress: String
    ): ApiResponse<GetPaginatedEosEvmTransactionsForAddressResponse, CustomError> {
        throw Exception("UnsupportedOperationException")
    }

    override suspend fun getPaginatedTransactionsForAddress(
        chainName: BlockchainType,
        walletAddress: String,
        page: Int
    ): ApiResponse<GetPaginatedEosEvmTransactionsForAddressResponse, CustomError> {
        return safeApiCall {
            api.getPaginatedTransactionsForAddress(walletAddress, page, TRANSACTION_FETCH_PAGE_SIZE)
        }
    }

    suspend fun getPaginatedTokenTransferForAddress(
        walletAddress: String,
        page: Int
    ): ApiResponse<GetPaginatedEosEvmTokenTransferForAddressResponse, CustomError> {
        return safeApiCall {
            api.getPaginatedTokenTransferForAddress(walletAddress, page, TRANSACTION_FETCH_PAGE_SIZE)
        }
    }

    override suspend fun getNftsForAddress(
        chainName: BlockchainType,
        walletAddress: String,
        withUncached: Boolean
    ): ApiResponse<GetNftsForAddressResponse, CustomError> {
        TODO("Not yet implemented")
    }
}