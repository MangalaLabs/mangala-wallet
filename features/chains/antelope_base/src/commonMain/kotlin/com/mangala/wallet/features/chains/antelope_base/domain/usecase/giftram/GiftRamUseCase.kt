package com.mangala.wallet.features.chains.antelope_base.domain.usecase.giftram

import com.mangala.antelope.base.domain.model.ResourceProviderResponse
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccountPermission
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.AccountBalanceRefresher
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountPermissionsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.BaseTransactUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.ResourceProviderRequestTransactionUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.SignAndComputeTransactionUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.SignAndPushResourceProvidedTransactionUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.SignAndPushTransactionUseCase
import com.mangala.wallet.model.blockchain.BlockchainType

class GiftRamUseCase(
    private val generateGiftRamSignRequestUseCase: GenerateGiftRamSignRequestUseCase,
    getAccountPermissionsUseCase: GetAccountPermissionsUseCase,
    signAndPushTransactionUseCase: SignAndPushTransactionUseCase,
    signAndComputeTransactionUseCase: SignAndComputeTransactionUseCase,
    resourceProviderRequestTransactionUseCase: ResourceProviderRequestTransactionUseCase,
    signAndPushResourceProvidedTransactionUseCase: SignAndPushResourceProvidedTransactionUseCase,
    accountBalanceRefresher: AccountBalanceRefresher
): BaseTransactUseCase(
    signAndPushTransactionUseCase,
    signAndComputeTransactionUseCase,
    getAccountPermissionsUseCase,
    resourceProviderRequestTransactionUseCase,
    signAndPushResourceProvidedTransactionUseCase,
    accountBalanceRefresher
) {
    override val shouldRefreshTokenBalanceAfterTransaction: Boolean = false

    suspend fun giftRam(
        blockchainType: BlockchainType,
        senderAccountName: String,
        recipientAccountName: String,
        quantityInBytes: Long,
        memo: String
    ): Result<String> {
        return constructAndPushTransaction(
            blockchainType,
            senderAccountName,
            constructSignRequest = { permission ->
                constructGiftRamRequest(
                    blockchainType = blockchainType,
                    senderAccountName = senderAccountName,
                    permission = permission,
                    quantityInBytes = quantityInBytes,
                    recipientAccountName = recipientAccountName,
                    memo = memo
                )
            }
        )
    }

    suspend fun requestGiftRamTransaction(
        blockchainType: BlockchainType,
        senderAccountName: String,
        recipientAccountName: String,
        quantityInBytes: Long,
        memo: String
    ): Result<ResourceProviderResponse> {
        return constructAndRequestTransaction(
            blockchainType,
            senderAccountName,
            constructSignRequest = { permission ->
                constructGiftRamRequest(
                    blockchainType = blockchainType,
                    senderAccountName = senderAccountName,
                    permission = permission,
                    quantityInBytes = quantityInBytes,
                    recipientAccountName = recipientAccountName,
                    memo = memo
                )
            }
        )
    }

    private suspend fun constructGiftRamRequest(
        blockchainType: BlockchainType,
        senderAccountName: String,
        permission: AntelopeAccountPermission,
        quantityInBytes: Long,
        memo: String,
        recipientAccountName: String
    ) = generateGiftRamSignRequestUseCase(
        blockchainType = blockchainType,
        senderAccountName = senderAccountName.trim(),
        signingPermissionName = permission.permissionType.permissionName,
        bytes = quantityInBytes,
        memo = memo,
        recipientAccountName = recipientAccountName
    )
}