package com.mangala.wallet.features.chains.antelope_base.domain.usecase

import com.mangala.antelope.base.api.model.GetInfoResponse
import com.mangala.antelope.base.domain.usecase.GetInfoUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.transaction.SignTransactionRequest
import com.mangala.wallet.features.chains.antelope_base.domain.model.transaction.SignTransactionType
import com.mangala.wallet.model.blockchain.BlockchainType
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.DurationUnit
import kotlin.time.toDuration

abstract class BaseGenerateSignRequestUseCase(val getInfoUseCase: GetInfoUseCase) {

    private suspend fun getInfo(blockchainType: BlockchainType): GetInfoResponse? {
        return getInfoUseCase(blockchainType)
    }

    protected fun constructAuthorization(senderAccountName: String, signingPermissionName: String): List<SignTransactionRequest.Authorization> {
        return listOf(
            SignTransactionRequest.Authorization(
                actor = senderAccountName,
                permission = signingPermissionName
            )
        )
    }

    private fun transactionDefaultExpiry(): Instant = with(Clock.System.now()) {
        plus(COLD_WALLET_TRANSACTION_EXPIRY_MINUTES.toDuration(DurationUnit.MINUTES))
    }

    protected suspend fun constructSignRequest(
        blockchainType: BlockchainType,
        constructAuthorization: () -> List<SignTransactionRequest.Authorization>,
        constructActions: (authorization: List<SignTransactionRequest.Authorization>) -> List<SignTransactionRequest.Action>,
        signTransactionType: SignTransactionType,
        expiryTime: Long = transactionDefaultExpiry().toEpochMilliseconds()
    ): SignTransactionRequest? {
        return getInfo(blockchainType)?.let {
            val authorization = constructAuthorization()

            val actions = constructActions(authorization)

            SignTransactionRequest(
                signTransactionType = signTransactionType,
                chainId = it.chainId.orEmpty(),
                expiryTimestamp = expiryTime,
                authorization = authorization,
                headBlockId = it.headBlockId.orEmpty(),
                actions = actions
            )
        }
    }

    companion object {
        private const val COLD_WALLET_TRANSACTION_EXPIRY_MINUTES =
            5 // Needs a longer time than the default value so that users have time to scan QR
    }
}