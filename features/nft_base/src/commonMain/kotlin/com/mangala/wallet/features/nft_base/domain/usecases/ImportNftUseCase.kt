package com.mangala.wallet.features.nft_base.domain.usecases

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.chains.erc1155.contract.Eip1155BalanceOfMethod
import com.mangala.wallet.features.chains.erc1155.contract.Eip1155UriMethod
import com.mangala.wallet.features.chains.erc20.contract.NameMethod
import com.mangala.wallet.features.chains.erc20.contract.SymbolMethod
import com.mangala.wallet.features.chains.erc721.contract.Eip721OwnerOfMethod
import com.mangala.wallet.features.chains.evmcompatible.core.toBigInteger
import com.mangala.wallet.features.chains.evmcompatible.domain.repository.NodeRepository
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.features.chains.evmcompatible.model.DefaultBlockParameter
import com.mangala.wallet.features.nft_base.domain.model.NftCollection
import com.mangala.wallet.features.nft_base.domain.model.NftType
import com.mangala.wallet.features.nft_base.domain.repository.NftMetadataRepository
import com.mangala.wallet.features.nft_base.domain.repository.NftRepository
import com.mangala.wallet.model.blockchain.BlockchainType
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class ImportNftUseCase(
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val nftMetadataRepository: NftMetadataRepository,
    private val nftRepository: NftRepository,
    private val nodeRepository: NodeRepository
) {

    suspend operator fun invoke(
        accountId: String,
        contractAddress: String,
        txId: Int,
        tokenId: BigInteger,
        ownerAddress: String,
        defaultBlockParameter: DefaultBlockParameter
    ): Result<Boolean> = coroutineScope {
        val selectedNetwork = getSelectedNetworkUseCase()
        val rpcUrl = BlockchainType.fromUid(selectedNetwork.blockChainUid).getRpcUrl().first()

        val erc720OwnerResult = getErc720OwnerOf(
            rpcUrl,
            contractAddress,
            txId,
            tokenId,
            defaultBlockParameter
        )

        if (erc720OwnerResult.isSuccess) {
            val tokenOwnerAddress = erc720OwnerResult.getOrNull().orEmpty()

            return@coroutineScope if (ownerAddress.lowercase() == tokenOwnerAddress.lowercase()) {
                val metadataDeferred = async {
                    getErc721TokenUri(
                        rpcUrl,
                        contractAddress,
                        txId,
                        tokenId,
                        defaultBlockParameter
                    )
                }
                val nameDeferred = async {
                    getName(
                        rpcUrl,
                        contractAddress,
                        txId,
                        defaultBlockParameter
                    )
                }
                val symbolDeferred = async {
                    getSymbol(
                        rpcUrl,
                        contractAddress,
                        txId,
                        defaultBlockParameter
                    )
                }
                val metadata = metadataDeferred.await()
                val name = nameDeferred.await()
                val symbol = symbolDeferred.await()

                nftRepository.insertNft(
                    accountId,
                    selectedNetwork.blockChainUid,
                    NftCollection(
                        contractName = name,
                        contractTickerSymbol = symbol,
                        contractAddress = contractAddress,
                        nft = metadata?.let { listOf(it) } ?: listOf(
                            NftCollection.Nft(
                                tokenId = tokenId.toString(),
                                tokenUrl = "", // TODO: Pass in tokenUrl from metadata call
                                name = "$name#$tokenId",
                                description = "",
                                image = "",
                                attributes = emptyList()
                            )
                        ),
                        type = NftType.ERC721
                    )
                )

                Result.success(true)
            } else {
                Result.failure(UserNotNftOwnerException())
            }
        } else {
            return@coroutineScope when(erc720OwnerResult.exceptionOrNull()) {
                is NotErc721ContractException -> {
                    val erc1155OwnerResult = getErc1155BalanceOf(
                        rpcUrl,
                        contractAddress,
                        txId,
                        tokenId,
                        ownerAddress,
                        defaultBlockParameter
                    )

                    if (erc1155OwnerResult.isSuccess) {
                        val balance = erc1155OwnerResult.getOrNull() ?: BigInteger.ZERO
                        if (balance == BigInteger.ONE) {
                            val metadataDeferred = async {
                                getErc1155Uri(
                                    rpcUrl,
                                    contractAddress,
                                    txId,
                                    tokenId,
                                    defaultBlockParameter
                                )
                            }
                            val nameDeferred = async {
                                getName(
                                    rpcUrl,
                                    contractAddress,
                                    txId,
                                    defaultBlockParameter
                                )
                            }
                            val symbolDeferred = async {
                                getSymbol(
                                    rpcUrl,
                                    contractAddress,
                                    txId,
                                    defaultBlockParameter
                                )
                            }
                            val metadata = metadataDeferred.await()
                            val name = nameDeferred.await()
                            val symbol = symbolDeferred.await()

                            nftRepository.insertNft(
                                accountId,
                                selectedNetwork.blockChainUid,
                                NftCollection(
                                    contractName = name,
                                    contractTickerSymbol = symbol,
                                    contractAddress = contractAddress,
                                    nft = metadata?.let { listOf(it) } ?: listOf(
                                        NftCollection.Nft(
                                            tokenId = tokenId.toString(),
                                            tokenUrl = "", // TODO: Pass in tokenUrl from metadata call, maybe return default value for Nft.Data instead of nullable NFt.Data?
                                            name = "$name#$tokenId",
                                            description = "",
                                            image = "",
                                            attributes = emptyList()
                                        )
                                    ),
                                    type = NftType.ERC1155
                                )
                            )
                            Result.success(true)
                        } else {
                            return@coroutineScope Result.failure(UserNotNftOwnerException())
                        }
                    } else {
                        return@coroutineScope Result.failure(Throwable())
                    }
                }

                else -> erc720OwnerResult.map { false }
            }
        }
    }

    private suspend fun getErc720OwnerOf(
        rpcUrl: String,
        contractAddress: String,
        txId: Int,
        tokenId: BigInteger,
        defaultBlockParameter: DefaultBlockParameter
    ): Result<String> {
        return try {
            val result = nodeRepository.call(
                rpcUrl,
                txId,
                Address(contractAddress),
                Eip721OwnerOfMethod(tokenId).encodedABI(),
                defaultBlockParameter
            )

            val ownerOf = result?.takeLast(20)?.toByteArray()


            ownerOf?.let {
                if (it.isNotEmpty()) {
                    Result.success(Address(ownerOf).hex)
                } else {
                    Result.failure(NotErc721ContractException())
                }
            } ?: kotlin.run {
                Result.failure(NotErc721ContractException())
            }
        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    private suspend fun getErc1155BalanceOf(
        rpcUrl: String,
        contractAddress: String,
        txId: Int,
        tokenId: BigInteger,
        ownerAddress: String,
        defaultBlockParameter: DefaultBlockParameter
    ): Result<BigInteger> {
        return try {
            val result = nodeRepository.call(
                rpcUrl,
                txId,
                Address(contractAddress),
                Eip1155BalanceOfMethod(Address(ownerAddress), tokenId).encodedABI(),
                defaultBlockParameter
            )

            result?.let {
                if (it.isNotEmpty()) {
                    Result.success(result.toBigInteger())
                } else {
                    Result.failure(Exception())
                }
            } ?: kotlin.run {
                Result.failure(Exception())
            }
        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    private suspend fun getErc1155Uri(
        rpcUrl: String,
        contractAddress: String,
        txId: Int,
        tokenId: BigInteger,
        defaultBlockParameter: DefaultBlockParameter
    ): NftCollection.Nft? {
        val result = nodeRepository.call(
            rpcUrl,
            txId,
            Address(contractAddress),
            Eip1155UriMethod(tokenId).encodedABI(),
            defaultBlockParameter
        ) ?: throw Exception()

        val uri = result.slice(IntRange(64, result.lastIndex))
            .filterNot { it in 0..31 || it in 127..159 } // remove control characters
            .toByteArray()
            .decodeToString()

        return if (uri.isNotEmpty()) {
            nftMetadataRepository.getNftMetadata(tokenId.toString(), uri)
        } else {
            null
        }
    }

    private suspend fun getErc721TokenUri(
        rpcUrl: String,
        contractAddress: String,
        txId: Int,
        tokenId: BigInteger,
        defaultBlockParameter: DefaultBlockParameter
    ): NftCollection.Nft? {
        val result = nodeRepository.call(
            rpcUrl,
            txId,
            Address(contractAddress),
            Eip721OwnerOfMethod(tokenId).encodedABI(),
            defaultBlockParameter
        ) ?: throw Exception()

        val uri = result.slice(IntRange(64, result.lastIndex))
            .filterNot { it in 0..31 || it in 127..159 } // remove control characters
            .toByteArray()
            .decodeToString()

        return if (uri.isNotEmpty()) {

            nftMetadataRepository.getNftMetadata(tokenId.toString(), uri)
        } else {
            null
        }
    }

    private suspend fun getName(
        rpcUrl: String,
        contractAddress: String,
        txId: Int,
        defaultBlockParameter: DefaultBlockParameter
    ): String {
        val rawResult = nodeRepository.call(
            rpcUrl,
            txId,
            Address(contractAddress),
            NameMethod().encodedABI(),
            defaultBlockParameter
        ) ?: throw Exception()

        return rawResult.slice(IntRange(32, rawResult.lastIndex))
            .filterNot { it in 0..31 || it in 127..159 } // remove control characters
            .toByteArray()
            .decodeToString()
    }

    private suspend fun getSymbol(
        rpcUrl: String,
        contractAddress: String,
        txId: Int,
        defaultBlockParameter: DefaultBlockParameter
    ): String {
        val rawResult = nodeRepository.call(
            rpcUrl,
            txId,
            Address(contractAddress),
            SymbolMethod().encodedABI(),
            defaultBlockParameter
        ) ?: throw Exception()

        return rawResult.slice(IntRange(32, rawResult.lastIndex))
            .filterNot { it in 0..31 || it in 127..159 } // remove control characters
            .toByteArray()
            .decodeToString()
    }
}

class UserNotNftOwnerException : Exception()
class NotErc721ContractException : Exception()