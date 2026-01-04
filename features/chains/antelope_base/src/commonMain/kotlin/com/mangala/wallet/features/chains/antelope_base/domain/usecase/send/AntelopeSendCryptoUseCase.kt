package com.mangala.wallet.features.chains.antelope_base.domain.usecase.send

import com.mangala.antelope.base.domain.model.ResourceProviderResponse
import com.mangala.wallet.antelope_balance.Balance
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

class AntelopeSendCryptoUseCase(
    private val generateSendAssetSignRequestUseCase: GenerateSendAssetSignRequestUseCase,
    signAndPushTransactionUseCase: SignAndPushTransactionUseCase,
    signAndComputeTransactionUseCase: SignAndComputeTransactionUseCase,
    getAccountPermissionsUseCase: GetAccountPermissionsUseCase,
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

    private var shouldRefreshTokenBalance = false

    override val shouldRefreshTokenBalanceAfterTransaction: Boolean get() = shouldRefreshTokenBalance

    suspend fun sendToken(
        blockchainType: BlockchainType,
        senderAccountName: String,
        recipientAccountName: String,
        quantity: Balance,
        memo: String,
        contract: String
    ): Result<String> {
        return constructAndPushTransaction(
            blockchainType,
            senderAccountName,
            constructSignRequest = { permission ->
                constructSignRequest(
                    blockchainType = blockchainType,
                    senderAccountName = senderAccountName,
                    activePermission = permission,
                    recipientAccountName = recipientAccountName,
                    quantity = quantity,
                    memo = memo,
                    contract = contract
                )
            }
        )
    }

    suspend fun computeSendToken(
        blockchainType: BlockchainType,
        senderAccountName: String,
        recipientAccountName: String,
        quantity: Balance,
        memo: String,
        contract: String
    ): Result<String> {
        return constructAndComputeTransaction(
            blockchainType,
            senderAccountName,
            constructSignRequest = { permission ->
                constructSignRequest(
                    blockchainType = blockchainType,
                    senderAccountName = senderAccountName,
                    activePermission = permission,
                    recipientAccountName = recipientAccountName,
                    quantity = quantity,
                    memo = memo,
                    contract = contract
                )
            }
        )
    }

    suspend fun requestSendTransaction(
        blockchainType: BlockchainType,
        senderAccountName: String,
        recipientAccountName: String,
        quantity: Balance,
        memo: String,
        contract: String
    ): Result<ResourceProviderResponse> {
        shouldRefreshTokenBalance = getShouldRefreshTokenBalance(quantity)

        return constructAndRequestTransaction(
            blockchainType,
            senderAccountName,
            constructSignRequest = { permission ->
                constructSignRequest(
                    blockchainType = blockchainType,
                    senderAccountName = senderAccountName,
                    activePermission = permission,
                    recipientAccountName = recipientAccountName,
                    quantity = quantity,
                    memo = memo,
                    contract = contract
                )
            }
        )
    }

    private suspend fun constructSignRequest(
        blockchainType: BlockchainType,
        senderAccountName: String,
        activePermission: AntelopeAccountPermission,
        recipientAccountName: String,
        quantity: Balance,
        memo: String,
        contract: String
    ): SignTransactionRequest? {
        val signRequest = generateSendAssetSignRequestUseCase(
            blockchainType,
            senderAccountName.trim(),
            activePermission.permissionType.permissionName,
            recipientAccountName.trim(),
            quantity,
            memo,
            contract
        )
        return signRequest
    }

    private fun getShouldRefreshTokenBalance(quantity: Balance): Boolean {
        return quantity.symbol !in ("EOS")
    }
}