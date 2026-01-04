package com.mangala.wallet.features.chains.antelope.ram.domain.usecase

import com.mangala.antelope.base.domain.model.ResourceProviderResponse
import com.mangala.wallet.antelope_balance.Balance
import com.mangala.wallet.antelope_balance.BalanceFormatter
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccountPermission
import com.mangala.wallet.features.chains.antelope_base.domain.model.transaction.SignTransactionRequest
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.AccountBalanceRefresher
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountPermissionsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.BaseTransactUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.ResourceProviderRequestTransactionUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.SignAndComputeTransactionUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.SignAndPushResourceProvidedTransactionUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.SignAndPushTransactionUseCase
import com.mangala.wallet.model.blockchain.BlockchainType

class BuySellRamUseCase(
    private val generateBuyRamSignRequestUseCase: GenerateBuyRamSignRequestUseCase,
    private val generateSellRamSignRequestUseCase: GenerateSellRamSignRequestUseCase,
    getAccountPermissionsUseCase: GetAccountPermissionsUseCase,
    signAndPushTransactionUseCase: SignAndPushTransactionUseCase,
    signAndComputeTransactionUseCase: SignAndComputeTransactionUseCase,
    resourceProviderRequestTransactionUseCase: ResourceProviderRequestTransactionUseCase,
    signAndPushResourceProvidedTransactionUseCase: SignAndPushResourceProvidedTransactionUseCase,
    accountBalanceRefresher: AccountBalanceRefresher
) : BaseTransactUseCase(
    signAndPushTransactionUseCase,
    signAndComputeTransactionUseCase,
    getAccountPermissionsUseCase,
    resourceProviderRequestTransactionUseCase,
    signAndPushResourceProvidedTransactionUseCase,
    accountBalanceRefresher
) {

    override val shouldRefreshTokenBalanceAfterTransaction: Boolean = false

    suspend fun buyRam(
        blockchainType: BlockchainType,
        senderAccountName: String,
        receiver: String,
        coinQuantity: Balance
    ): Result<String> {
        return constructAndPushTransaction(
            blockchainType,
            senderAccountName,
            constructSignRequest = { permission ->
                constructBuyRamRequest(
                    blockchainType,
                    senderAccountName,
                    permission,
                    receiver,
                    coinQuantity
                )
            }
        )
    }

    suspend fun computeBuyRam(
        blockchainType: BlockchainType,
        senderAccountName: String,
        receiver: String,
        coinQuantity: Balance
    ): Result<String> {
        return constructAndComputeTransaction(
            blockchainType,
            senderAccountName,
            constructSignRequest = { permission ->
                constructBuyRamRequest(
                    blockchainType,
                    senderAccountName,
                    permission,
                    receiver,
                    coinQuantity
                )
            }
        )
    }

    suspend fun requestBuyRamTransaction(
        blockchainType: BlockchainType,
        senderAccountName: String,
        receiver: String,
        coinQuantity: Balance
    ): Result<ResourceProviderResponse> {
        return constructAndRequestTransaction(
            blockchainType,
            senderAccountName,
            constructSignRequest = { permission ->
                constructBuyRamRequest(
                    blockchainType,
                    senderAccountName,
                    permission,
                    receiver,
                    coinQuantity
                )
            }
        )
    }

    suspend fun buyRamBytes(
        blockchainType: BlockchainType,
        senderAccountName: String,
        receiver: String,
        quantityInBytes: Long
    ): Result<String> {
        return constructAndPushTransaction(
            blockchainType,
            senderAccountName,
            constructSignRequest = { permission ->
                constructBuyRamBytesRequest(
                    blockchainType,
                    senderAccountName,
                    permission,
                    receiver,
                    quantityInBytes
                )
            }
        )
    }

    suspend fun computeBuyRamBytes(
        blockchainType: BlockchainType,
        senderAccountName: String,
        receiver: String,
        quantityInBytes: Long
    ): Result<String> {
        return constructAndComputeTransaction(
            blockchainType,
            senderAccountName,
            constructSignRequest = { permission ->
                constructBuyRamBytesRequest(
                    blockchainType,
                    senderAccountName,
                    permission,
                    receiver,
                    quantityInBytes
                )
            }
        )
    }

    suspend fun requestBuyRamBytesTransaction(
        blockchainType: BlockchainType,
        senderAccountName: String,
        receiver: String,
        quantityInBytes: Long
    ): Result<ResourceProviderResponse> {
        return constructAndRequestTransaction(
            blockchainType,
            senderAccountName,
            constructSignRequest = { permission ->
                constructBuyRamBytesRequest(
                    blockchainType,
                    senderAccountName,
                    permission,
                    receiver,
                    quantityInBytes
                )
            }
        )
    }

    suspend fun sellRam(
        blockchainType: BlockchainType,
        accountName: String,
        quantityInBytes: Long
    ): Result<String> {
        return constructAndPushTransaction(
            blockchainType,
            accountName,
            constructSignRequest = { permission ->
                constructSellRamRequest(
                    blockchainType,
                    accountName,
                    permission,
                    quantityInBytes
                )
            }
        )
    }

    suspend fun computeSellRam(
        blockchainType: BlockchainType,
        accountName: String,
        quantityInBytes: Long
    ): Result<String> {
        return constructAndComputeTransaction(
            blockchainType,
            accountName,
            constructSignRequest = { permission ->
                constructSellRamRequest(
                    blockchainType,
                    accountName,
                    permission,
                    quantityInBytes
                )
            }
        )
    }

    suspend fun requestSellRamTransaction(
        blockchainType: BlockchainType,
        accountName: String,
        quantityInBytes: Long
    ): Result<ResourceProviderResponse> {
        return constructAndRequestTransaction(
            blockchainType,
            accountName,
            constructSignRequest = { permission ->
                constructSellRamRequest(
                    blockchainType,
                    accountName,
                    permission,
                    quantityInBytes
                )
            }
        )
    }

    private suspend fun constructBuyRamRequest(
        blockchainType: BlockchainType,
        senderAccountName: String,
        permission: AntelopeAccountPermission,
        receiver: String,
        coinQuantity: Balance
    ) = generateBuyRamSignRequestUseCase.generateBuyRamRequest(
        blockchainType = blockchainType,
        senderAccountName = senderAccountName.trim(),
        signingPermissionName = permission.permissionType.permissionName,
        receiver = receiver,
        quantity = BalanceFormatter.formatEosBalance(coinQuantity, ignoreLocale = true)
    )

    private suspend fun constructBuyRamBytesRequest(
        blockchainType: BlockchainType,
        senderAccountName: String,
        permission: AntelopeAccountPermission,
        receiver: String,
        quantityInBytes: Long
    ) = generateBuyRamSignRequestUseCase.generateBuyRamBytesRequest(
        blockchainType = blockchainType,
        senderAccountName = senderAccountName.trim(),
        signingPermissionName = permission.permissionType.permissionName,
        receiver = receiver,
        bytes = quantityInBytes
    )

    private suspend fun constructSellRamRequest(
        blockchainType: BlockchainType,
        accountName: String,
        permission: AntelopeAccountPermission,
        quantityInBytes: Long
    ): SignTransactionRequest? = generateSellRamSignRequestUseCase(
        blockchainType = blockchainType,
        senderAccountName = accountName,
        signingPermissionName = permission.permissionType.permissionName,
        bytes = quantityInBytes
    )
}