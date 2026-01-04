package com.mangala.wallet.features.chains.antelope_base.domain.usecase.esr

import com.benasher44.uuid.uuid4
import com.mangala.antelope.base.api.model.PushTransactionRequest
import com.mangala.antelope.base.domain.usecase.GetInfoUseCase
import com.mangala.antelope.base.domain.usecase.PushTransactionUseCase
import com.mangala.wallet.antelope_key_manager.EosKeyManager
import com.mangala.wallet.features.chains.antelope_base.domain.model.esr.EsrCallbackData
import com.mangala.wallet.features.chains.antelope_base.domain.model.esr.anchorlink.AnchorLinkSession
import com.mangala.wallet.features.chains.antelope_base.domain.model.transaction.SignTransactionRequest
import com.mangala.wallet.features.chains.antelope_base.domain.model.transaction.SignTransactionType
import com.mangala.wallet.features.chains.antelope_base.domain.model.transaction.toPackedTrx
import com.mangala.wallet.features.chains.antelope_base.domain.repository.esr.EsrRepository
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.esr.anchorlink.SaveAndConnectAnchorLinkSessionUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.SignTransactionUseCase
import com.mangala.wallet.model.blockchain.BlockchainType
import com.memtrip.eos.chain.actions.transaction.abi.TransactionAbi
import com.memtrip.eos.chain.actions.transaction.esr.EsrSigningRequestArgs
import com.memtrip.eos.core.block.BlockIdDetails
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.minutes

class ResolveEsrUseCase(
    private val signTransactionUseCase: SignTransactionUseCase,
    private val pushTransactionUseCase: PushTransactionUseCase,
    private val getInfoUseCase: GetInfoUseCase,
    private val esrRepository: EsrRepository,
    private val saveAndConnectAnchorLinkSessionUseCase: SaveAndConnectAnchorLinkSessionUseCase,
    private val eosKeyManager: EosKeyManager
) {

    suspend operator fun invoke(
        esrUri: String,
        esrSigningRequestArgs: EsrSigningRequestArgs,
        actor: String,
        permissionName: String,
        blockchainType: BlockchainType? = null
    ): Result<String> {
        // TODO: Handle multichain sign request

        val resolvedBlockchainType = resolveBlockchainType(
            blockchainType,
            esrSigningRequestArgs
        )
        val resolvedBlockchainUid = resolvedBlockchainType.uid

        val resolvedTransactionAbi = if (esrSigningRequestArgs.isIdentityRequest) {
            if (esrSigningRequestArgs.shouldBroadcast) return Result.failure(
                UnsupportedOperationException("Identity request should not be broadcasted")
            )

            resolveIdentityRequestTransaction(esrSigningRequestArgs, actor, permissionName)
        } else {
            val transaction = esrSigningRequestArgs.transaction
                ?: return Result.failure(Exception("No transaction provided"))

            resolveTransactionAbiHeaders(
                transaction,
                resolvedBlockchainType
            )
        }

        val signature = signTransactionUseCase(
            chainId = esrSigningRequestArgs.resolvedChainId,
            transactionAbi = resolvedTransactionAbi, // TODO: Support if it's a list of transaction and not txAbi
            actor = actor,
            permissionName = permissionName,
            blockchainUid = resolvedBlockchainUid
        )

        if (esrSigningRequestArgs.shouldBroadcast.not() && esrSigningRequestArgs.callback.isNotBlank()) {
            val isIdentityRequest = esrSigningRequestArgs.isIdentityRequest

            if (isIdentityRequest) {
                return resolveIdentityRequest(
                    esrSigningRequestArgs,
                    signature,
                    actor,
                    permissionName,
                    resolvedTransactionAbi,
                    esrUri
                )
            } else {
                return performCallback(
                    esrSigningRequestArgs,
                    signature,
                    "",
                    actor,
                    permissionName,
                    resolvedTransactionAbi,
                    esrUri,
                )
            }
        }

        val pushResult = pushTransactionUseCase.withResult(
            BlockchainType.fromUid(resolvedBlockchainUid),
            PushTransactionRequest(
                signatures = listOf(signature), // TODO: Support multiple signatures (resource provided tx)
                compression = "none",
                packedContextFreeData = "",
                packedTrx = resolvedTransactionAbi.toPackedTrx()
            )
        )

        val txHash =
            pushResult.getOrNull() ?: return Result.failure(Exception("Transaction failed to push"))

        return performCallback(
            esrSigningRequestArgs,
            signature,
            txHash,
            actor,
            permissionName,
            resolvedTransactionAbi,
            esrUri,
            anchorLinkChannel = null,
            anchorLinkReceivePublicKey = null,
            anchorLinkName = null
        ).onSuccess {
            return pushResult
        }
    }

    private fun resolveBlockchainType(
        specifiedBlockchainType: BlockchainType?,
        esrSigningRequestArgs: EsrSigningRequestArgs
    ): BlockchainType {
        val esrResolvedChainId = esrSigningRequestArgs.resolvedChainId
        val esrResolvedBlockchainType = BlockchainType.fromChainId(esrResolvedChainId.orEmpty())

        return if (specifiedBlockchainType != null && esrResolvedBlockchainType != specifiedBlockchainType) {
            throw IllegalArgumentException("Mismatch in specified blockchainUid and resolvedChainId")
        } else if (specifiedBlockchainType == null && esrResolvedBlockchainType is BlockchainType.Unsupported) {
            throw IllegalArgumentException("No blockchainUid specified or resolved")
        } else if (specifiedBlockchainType != null && esrResolvedBlockchainType is BlockchainType.Unsupported) {
            specifiedBlockchainType
        } else {
            esrResolvedBlockchainType
        }
    }

    private suspend fun resolveTransactionAbiHeaders(
        transactionAbi: TransactionAbi,
        blockchainType: BlockchainType
    ): TransactionAbi {
        val shouldFillExpiration = transactionAbi.expiration.toEpochMilliseconds() == 0L
        val shouldFillRefBlock =
            transactionAbi.ref_block_num == 0 && transactionAbi.ref_block_prefix == 0L

        val (resolvedRefBlockNum, resolvedRefBlockPrefix) = if (shouldFillRefBlock) {
            val info = getInfoUseCase(blockchainType)

            val blockIdDetails = BlockIdDetails(info?.headBlockId.orEmpty())

            blockIdDetails.blockNum to blockIdDetails.blockPrefix
        } else {
            transactionAbi.ref_block_num to transactionAbi.ref_block_prefix
        }

        val resolvedExpiration = if (shouldFillExpiration) {
            Clock.System.now().plus(TRANSACTION_EXPIRY_MINUTES.minutes)
        } else {
            transactionAbi.expiration
        }

        return transactionAbi.copy(
            expiration = resolvedExpiration,
            ref_block_num = resolvedRefBlockNum,
            ref_block_prefix = resolvedRefBlockPrefix
        )
    }

    private suspend fun resolveIdentityRequestTransaction(
        esrSigningRequestArgs: EsrSigningRequestArgs,
        actor: String,
        permissionName: String,
    ): TransactionAbi {
        val chainId = esrSigningRequestArgs.resolvedChainId.orEmpty()
        val blockchainType = BlockchainType.fromChainId(chainId)

        val info = getInfoUseCase(blockchainType)

        return SignTransactionRequest(
            authorization = listOf(
                SignTransactionRequest.Authorization(
                    actor = actor,
                    permission = permissionName
                )
            ),
            chainId = chainId,
            expiryTimestamp = Clock.System.now().plus(TRANSACTION_EXPIRY_MINUTES.minutes)
                .toEpochMilliseconds(),
            headBlockId = info?.headBlockId.orEmpty(),
            actions = listOf(
                SignTransactionRequest.Action.IdentityProof(
                    scope = "identity",
                    authorization = listOf(
                        SignTransactionRequest.Authorization(
                            actor = actor,
                            permission = permissionName
                        )
                    ),
                    authorizingAccountName = actor,
                    authorizingPermission = permissionName,
                )
            ),
            signTransactionType = SignTransactionType.ESR_IDENTITY
        ).toTransactionAbi()
    }

    private suspend fun resolveIdentityRequest(
        esrSigningRequestArgs: EsrSigningRequestArgs,
        signature: String,
        actor: String,
        permissionName: String,
        resolvedTransactionAbi: TransactionAbi,
        esrUri: String
    ): Result<String> {
        val anchorLinkChannel = "$ANCHOR_LINK_CHANNEL_URL/${uuid4()}"
        val receiveKey = eosKeyManager.createEosPrivateKey()
        eosKeyManager.importPrivateKey(receiveKey)

        val anchorLinkReceivePublicKey = receiveKey.publicKey.toString()
        val anchorLinkName = "Mangala Wallet"

        return performCallback(
            esrSigningRequestArgs,
            signature,
            "",
            actor,
            permissionName,
            resolvedTransactionAbi,
            esrUri,
            anchorLinkChannel = anchorLinkChannel,
            anchorLinkReceivePublicKey = anchorLinkReceivePublicKey,
            anchorLinkName = anchorLinkName
        ).onSuccess {
            saveAndConnectAnchorLinkSessionUseCase(
                AnchorLinkSession(
                    accountName = actor,
                    requestKey = esrSigningRequestArgs.link?.requestKey?.toString().orEmpty(),
                    receiveKey = receiveKey.publicKey.toString(),
                    url = anchorLinkChannel,
                    name = esrSigningRequestArgs.link?.sessionName.orEmpty()
                ),
                BlockchainType.fromChainId(esrSigningRequestArgs.resolvedChainId.orEmpty())
            )
        }
    }

    private suspend fun performCallback(
        esrSigningRequestArgs: EsrSigningRequestArgs,
        signature: String,
        txHash: String,
        actor: String,
        permissionName: String,
        resolvedTransactionAbi: TransactionAbi,
        esrUri: String,
        anchorLinkChannel: String? = null,
        anchorLinkReceivePublicKey: String? = null,
        anchorLinkName: String? = null
    ): Result<String> {
        return if (esrSigningRequestArgs.shouldPerformBackgroundCallback) {
            esrRepository.postCallback(
                esrSigningRequestArgs.callback,
                EsrCallbackData(
                    signatures = listOf(signature), // TODO: Support multiple signatures (resource provided tx)
                    transactionId = txHash,
                    blockNumber = null, // Since tx not broadcasted
                    signerAccount = actor,
                    signerPermission = permissionName,
                    referenceBlockNum = resolvedTransactionAbi.ref_block_num.toString(),
                    referenceBlockId = resolvedTransactionAbi.ref_block_prefix.toString(),
                    request = esrUri.replace("esr:", "esr://"),
                    expirationTime = resolvedTransactionAbi.expiration.toString()
                        .removeSuffix("Z"),
                    resolvedChainId = esrSigningRequestArgs.resolvedChainId,
                    anchorLinkChannel = anchorLinkChannel,
                    anchorLinkReceivePublicKey = anchorLinkReceivePublicKey,
                    anchorLinkName = anchorLinkName
                )
            )
            Result.success("")
        } else {
            Result.failure(UnsupportedOperationException("Foreground callback not supported"))
        }
    }

    companion object {
        private const val TRANSACTION_EXPIRY_MINUTES = 2
        private const val ANCHOR_LINK_CHANNEL_URL = "https://cb.anchor.link"
    }
}