package com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction

import com.mangala.antelope.base.domain.model.ResourceProviderResponse
import com.mangala.antelope.base.domain.model.Transaction
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccountPermission
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopePermissionType
import com.mangala.wallet.features.chains.antelope_base.domain.model.transaction.SignTransactionRequest
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.AccountBalanceRefresher
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountPermissionsUseCase
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.utils.analytics.MangalaAnalytics

abstract class BaseTransactUseCase(
    private val signAndPushTransactionUseCase: SignAndPushTransactionUseCase,
    private val signAndComputeTransactionUseCase: SignAndComputeTransactionUseCase,
    private val getAccountPermissionsUseCase: GetAccountPermissionsUseCase,
    private val resourceProviderRequestTransactionUseCase: ResourceProviderRequestTransactionUseCase,
    private val signAndPushResourceProvidedTransactionUseCase: SignAndPushResourceProvidedTransactionUseCase,
    private val accountBalanceRefresher: AccountBalanceRefresher
) {
    abstract val shouldRefreshTokenBalanceAfterTransaction: Boolean

    suspend fun pushResourceProvidedTransaction(
        blockchainType: BlockchainType,
        senderAccountName: String,
        transaction: Transaction
    ): Result<String> {
        val activeOrOwnerPermission = getActiveOrOwnerPermission(senderAccountName)
        val permissionName = activeOrOwnerPermission?.permissionType?.permissionName ?: return Result.failure(
            IllegalArgumentException("No active private key found for account")
        )

        return signAndPushResourceProvidedTransactionUseCase(
            transaction,
            senderAccountName,
            permissionName,
            blockchainType
        ).onSuccess {
            MangalaAnalytics.trackEvent(MangalaAnalytics.EventName.TRANSACTION_SUBMITTED)
            accountBalanceRefresher.refresh(
                senderAccountName,
                blockchainType,
                shouldRefreshTokenBalanceAfterTransaction
            )
        }.onFailure {
            MangalaAnalytics.trackEvent(MangalaAnalytics.EventName.TRANSACTION_FAILED)
        }
    }

    protected suspend fun constructAndPushTransaction(
        blockchainType: BlockchainType,
        senderAccountName: String,
        constructSignRequest: suspend (permission: AntelopeAccountPermission) -> SignTransactionRequest?,
    ): Result<String> {
        return constructAndPushTransaction(
            blockchainType,
            senderAccountName,
            { getActiveOrOwnerPermission(senderAccountName) },
            constructSignRequest
        ).onSuccess {
            accountBalanceRefresher.refresh(
                senderAccountName,
                blockchainType,
                shouldRefreshTokenBalanceAfterTransaction
            )
        }
    }

    protected suspend fun constructAndComputeTransaction(
        blockchainType: BlockchainType,
        senderAccountName: String,
        constructSignRequest: suspend (permission: AntelopeAccountPermission) -> SignTransactionRequest?,
    ): Result<String> {
        return constructAndComputeTransaction(
            blockchainType,
            senderAccountName,
            { getActiveOrOwnerPermission(senderAccountName) },
            constructSignRequest
        )
    }

    protected suspend fun constructAndRequestTransaction(
        blockchainType: BlockchainType,
        senderAccountName: String,
        constructSignRequest: suspend (permission: AntelopeAccountPermission) -> SignTransactionRequest?,
    ): Result<ResourceProviderResponse> {
        return constructAndRequestTransaction(
            blockchainType,
            senderAccountName,
            { getActiveOrOwnerPermission(senderAccountName) },
            constructSignRequest
        )
    }

    private suspend fun getActiveOrOwnerPermission(accountName: String): AntelopeAccountPermission? {
        val permissions = getAccountPermissionsUseCase.invoke(accountName)
        return permissions.firstOrNull { it.permissionType is AntelopePermissionType.Active || it.permissionType is AntelopePermissionType.Owner }
    }

    private suspend fun constructAndPushTransaction(
        blockchainType: BlockchainType,
        senderAccountName: String,
        getPermission: suspend () -> AntelopeAccountPermission?,
        constructSignRequest: suspend (permission: AntelopeAccountPermission) -> SignTransactionRequest?,
    ): Result<String> {
        val permission = getPermission()
            ?: return Result.failure(IllegalArgumentException("No active private key found for account")) // TODO: Allow users to choose permission to sign
        val signRequest = constructSignRequest(permission)

        return signRequest?.let {
            signAndPushTransactionUseCase(
                it,
                senderAccountName,
                permission.permissionType.permissionName,
                blockchainType
            )
        } ?: Result.failure(Exception("Failed to generate sign request"))
    }

    private suspend fun constructAndComputeTransaction(
        blockchainType: BlockchainType,
        senderAccountName: String,
        getPermission: suspend () -> AntelopeAccountPermission?,
        constructSignRequest: suspend (permission: AntelopeAccountPermission) -> SignTransactionRequest?,
    ): Result<String> {
        val permission = getPermission()
            ?: return Result.failure(IllegalArgumentException("No active private key found for account")) // TODO: Allow users to choose permission to sign
        val signRequest = constructSignRequest(permission)

        return signRequest?.let {
            signAndComputeTransactionUseCase(
                it,
                senderAccountName,
                permission.permissionType.permissionName,
                blockchainType
            )
        } ?: Result.failure(Exception("Failed to generate sign request"))
    }

    private suspend fun constructAndRequestTransaction(
        blockchainType: BlockchainType,
        senderAccountName: String,
        getPermission: suspend () -> AntelopeAccountPermission?,
        constructSignRequest: suspend (permission: AntelopeAccountPermission) -> SignTransactionRequest?,
    ): Result<ResourceProviderResponse> {
        MangalaAnalytics.trackEvent(MangalaAnalytics.EventName.TRANSACTION_INITIATED)
        val permission = getPermission()
            ?: return Result.failure(IllegalArgumentException("No active private key found for account")) // TODO: Allow users to choose permission to sign
        val signRequest = constructSignRequest(permission)

        return signRequest?.let {
            resourceProviderRequestTransactionUseCase(
                it,
                senderAccountName,
                permission.permissionType.permissionName,
                blockchainType
            )
        } ?: run {
            MangalaAnalytics.trackEvent(MangalaAnalytics.EventName.TRANSACTION_FAILED)
            Result.failure(Exception("Failed to generate sign request"))
        }
    }
}