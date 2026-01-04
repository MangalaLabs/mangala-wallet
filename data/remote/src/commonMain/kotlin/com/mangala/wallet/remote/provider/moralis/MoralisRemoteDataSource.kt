package com.mangala.wallet.remote.provider.moralis

import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.provider.BaseBalanceResponse
import com.mangala.wallet.model.provider.BaseGetPaginatedTransactionsForAddressResponse
import com.mangala.wallet.model.provider.covalenthq.GetNftsForAddressResponse
import com.mangala.wallet.model.provider.moralis.MoralisBalance
import com.mangala.wallet.model.provider.moralis.MoralisBalanceResponse
import com.mangala.wallet.model.provider.moralis.MoralisWalletHistoryResponse
import com.mangala.wallet.remote.BuildKonfig
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.di.safeApiCall
import com.mangala.wallet.remote.network.CustomError
import com.mangala.wallet.remote.provider.BaseBlockExplorerRemoteDataSource
import com.mangala.wallet.remote.provider.eosEVM.EosEvmRemoteDataSource.Companion.TRANSACTION_FETCH_PAGE_SIZE

class MoralisRemoteDataSource(
    private val api: MoralisApi
): BaseBlockExplorerRemoteDataSource {
    suspend fun getWalletHistory(
        chainName: BlockchainType,
        walletAddress: String,
        page: Int?,
        cursor: String?
    ): ApiResponse<MoralisWalletHistoryResponse, CustomError> {
        return safeApiCall {
            api.getWalletHistory(
                chain = chainName.getMoralisChainName(),
                address = walletAddress,
                page = page,
                pageSize = TRANSACTION_FETCH_PAGE_SIZE,
                apiKey = BuildKonfig.MORALIS_API_KEY,
                cursor = cursor
            )
        }
    }

    override suspend fun getBalanceByNetWorkAndAddress(
        chainNetWork: String,
        address: String
    ): ApiResponse<MoralisBalance, CustomError> {
        val response = safeApiCall<MoralisBalanceResponse, CustomError> {
            api.getNativeAndErc20Balance(
                chain = BlockchainType.fromUid(chainNetWork).getMoralisChainName(),
                address = address,
                pageSize = TRANSACTION_FETCH_PAGE_SIZE,
                apiKey = BuildKonfig.MORALIS_API_KEY
            )
        }

        return response.map {
            MoralisBalance(
                items = it.result?.map {
                    BaseBalanceResponse.Item(
                        contractDecimals = it?.decimals,
                        contractName = it?.name,
                        contractTickerSymbol = it?.symbol,
                        contractAddress = it?.tokenAddress,
                        logoUrl = it?.logo,
                        balance = it?.balance,
                        balance24h = null,
                        nativeToken = it?.nativeToken
                    )
                } ?: emptyList()
            )
        }
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

    private fun BlockchainType.getMoralisChainName(): String {
        return when (this) {
            BlockchainType.ArbitrumOne -> "arbitrum"
            BlockchainType.Avalanche -> "avalanche"
            BlockchainType.BinanceSmartChain -> "bsc"
            BlockchainType.BinanceSmartChainTestNet -> "0x61"
            BlockchainType.Bitcoin,
            BlockchainType.BitcoinCash,
            BlockchainType.Dash,
            BlockchainType.ECash,
            BlockchainType.Eos,
            BlockchainType.EosEvm,
            BlockchainType.EosJungleTestnet,
            BlockchainType.EthereumGoerli,
            BlockchainType.Litecoin,
            is BlockchainType.Unsupported,
            BlockchainType.Solana,
            BlockchainType.Zcash -> throw UnsupportedOperationException("${this.name} is not supported by Moralis")
            BlockchainType.Ethereum -> "eth"
            BlockchainType.EthereumHolesky -> "holesky"
            BlockchainType.EthereumSepolia -> "sepolia"
            BlockchainType.Fantom -> "fantom"
            BlockchainType.Gnosis -> "gnosis"
            BlockchainType.Optimism -> "optimism"
            BlockchainType.Polygon -> "polygon"
            BlockchainType.PolygonMumbai -> "0x13882"
            BlockchainType.BitcoinTestnet4 -> TODO()
        }
    }

    companion object {
        const val TAG = "MoralisRemoteDataSource"
    }
}