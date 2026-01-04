package com.mangala.wallet.remote.provider.alchemy

import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.provider.BaseBalanceResponse
import com.mangala.wallet.model.provider.BaseGetPaginatedTransactionsForAddressResponse
import com.mangala.wallet.model.provider.JsonRpcNodeResponse
import com.mangala.wallet.model.provider.alchemy.AlchemyBalance
import com.mangala.wallet.model.provider.alchemy.AlchemyNativeCoinBalanceResponse
import com.mangala.wallet.model.provider.alchemy.AlchemyRequest
import com.mangala.wallet.model.provider.alchemy.AlchemyTokenBalanceResponse
import com.mangala.wallet.model.provider.alchemy.AlchemyTokenMetadataByContractResponse
import com.mangala.wallet.model.provider.covalenthq.GetNftsForAddressResponse
import com.mangala.wallet.remote.BuildKonfig
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.di.safeApiCall
import com.mangala.wallet.remote.network.CustomError
import com.mangala.wallet.remote.provider.BaseBlockExplorerRemoteDataSource
import com.mangala.wallet.utils.hexStringToLongOrNull
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class AlchemyRemoteDataSource(
    private val alchemyApi: AlchemyApi,
) : BaseBlockExplorerRemoteDataSource {

    override suspend fun getBalanceByNetWorkAndAddress(
        chainNetWork: String,
        address: String
    ): ApiResponse<BaseBalanceResponse, CustomError> {
        val blockchainType = BlockchainType.fromUid(chainNetWork)

        val result = coroutineScope {
            val url = getAlchemyUrl(blockchainType)
            val tokenBalanceAsync = async {
                safeApiCall<AlchemyTokenBalanceResponse, JsonRpcNodeResponse> {
                    getTokenBalancesByWallet(
                        url,
                        AlchemyRequest(
                            id = 1,
                            method = ALCHEMY_GET_TOKEN_BALANCES_METHOD,
                            params = listOf(address)
                        )
                    )
                }
            }
            val nativeTokenBalanceAsync = async {
                safeApiCall<AlchemyNativeCoinBalanceResponse, JsonRpcNodeResponse> {
                    alchemyApi.getNativeCoinBalance(
                        url,
                        AlchemyRequest(
                            id = 2,
                            method = NODE_GET_NATIVE_COIN_BALANCE_METHOD,
                            params = listOf(address, "latest")
                        )
                    )
                }
            }

            val tokenBalanceResult = tokenBalanceAsync.await()
            val nativeTokenBalanceResult = nativeTokenBalanceAsync.await()

            if (tokenBalanceResult is ApiResponse.Error) return@coroutineScope tokenBalanceResult
            if (nativeTokenBalanceResult is ApiResponse.Error) return@coroutineScope nativeTokenBalanceResult

            val nativeTokenWithMetadata = BaseBalanceResponse.Item(
                contractDecimals = 18,
                contractName = blockchainType.getNativeTokenName(),
                contractTickerSymbol = blockchainType.getNativeTokenSymbol(),
                contractAddress = "0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee",
                logoUrl = null,
                balance = (nativeTokenBalanceResult as? ApiResponse.Success)?.body?.result?.hexStringToLongOrNull()?.toString() ?: "0",
                balance24h = null,
                nativeToken = true
            )

            val tokenBalanceWithMetadata =
                (tokenBalanceResult as? ApiResponse.Success)?.body?.result?.tokenBalances?.mapIndexed { index, tokenBalance ->
                    async {
                        val tokenMetadata = tokenBalance.contractAddress?.let {
                            getTokenMetadataByContract(
                                url,
                                AlchemyRequest(
                                    id = index,
                                    method = ALCHEMY_GET_TOKEN_METADATA_METHOD,
                                    params = listOf(it)
                                )
                            )
                        } ?: return@async null

                        BaseBalanceResponse.Item(
                            contractDecimals = tokenMetadata.result?.decimals?.toLong(),
                            contractName = tokenMetadata.result?.name,
                            contractTickerSymbol = tokenMetadata.result?.symbol,
                            contractAddress = tokenBalance.contractAddress,
                            logoUrl = tokenMetadata.result?.logo,
                            balance = tokenBalance.tokenBalance?.hexStringToLongOrNull()?.toString()
                                ?: "0",
                            balance24h = null,
                            nativeToken = false
                        )
                    }
                }?.awaitAll()

            return@coroutineScope ApiResponse.Success(
                AlchemyBalance(
                    listOf(nativeTokenWithMetadata) + tokenBalanceWithMetadata?.filterNotNull().orEmpty()
                )
            )
        }

        return result.mapWithErrorType(
            transformData = {
                it
            },
            transformErrorType = {
                CustomError(it?.error?.message ?: "Unknown error")
            }
        )
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

    private suspend fun getTokenBalancesByWallet(
        url: String,
        request: AlchemyRequest
    ): AlchemyTokenBalanceResponse {
        // TODO: Use safeApiCall
        return alchemyApi.getTokenBalancesByWallet(url, request)
    }

    private suspend fun getTokenMetadataByContract(
        url: String,
        request: AlchemyRequest
    ): AlchemyTokenMetadataByContractResponse {
        return alchemyApi.getTokenMetadataByContract(url, request)
    }

    private fun getAlchemyUrl(blockchainType: BlockchainType): String {
        val alchemyApiKey = BuildKonfig.ALCHEMY_API_KEY

        return "https://${blockchainType.getAlchemyNetworkName()}.g.alchemy.com/v2/$alchemyApiKey"
    }

    private fun BlockchainType.getAlchemyNetworkName(): String {
        return when (this) {
            BlockchainType.ArbitrumOne -> "arb-mainnet"
            BlockchainType.Avalanche -> "avax-mainnet"
            BlockchainType.BinanceSmartChain -> "bnb-mainnet"
            BlockchainType.BinanceSmartChainTestNet -> "bnb-testnet"
            BlockchainType.Bitcoin, BlockchainType.BitcoinCash -> throw UnsupportedOperationException(
                "Bitcoin and Bitcoin Cash are not supported by Alchemy"
            )
            BlockchainType.Dash -> throw UnsupportedOperationException("Dash is not supported by Alchemy")
            BlockchainType.ECash -> throw UnsupportedOperationException("ECash is not supported by Alchemy")
            BlockchainType.Eos, BlockchainType.EosEvm, BlockchainType.EosJungleTestnet -> throw UnsupportedOperationException(
                "EOS is not supported by Alchemy"
            )
            BlockchainType.Ethereum -> "eth-mainnet"
            BlockchainType.EthereumGoerli -> throw UnsupportedOperationException("Goerli is not supported by Alchemy")
            BlockchainType.EthereumHolesky -> "eth-holesky"
            BlockchainType.EthereumSepolia -> "eth-sepolia"
            BlockchainType.Fantom -> TODO()
            BlockchainType.Gnosis -> TODO()
            BlockchainType.Litecoin -> TODO()
            BlockchainType.Optimism -> TODO()
            BlockchainType.Polygon -> TODO()
            BlockchainType.PolygonMumbai -> TODO()
            BlockchainType.Solana -> TODO()
            is BlockchainType.Unsupported -> TODO()
            BlockchainType.Zcash -> TODO()
            BlockchainType.BitcoinTestnet4 -> TODO()
        }
    }

    companion object {
        const val TAG = "AlchemyRemoteDataSource"

        private const val ALCHEMY_GET_TOKEN_METADATA_METHOD = "alchemy_getTokenMetadata"
        private const val ALCHEMY_GET_TOKEN_BALANCES_METHOD = "alchemy_getTokenBalances"

        private const val NODE_GET_NATIVE_COIN_BALANCE_METHOD = "eth_getBalance"
    }
}