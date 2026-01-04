package com.mangala.wallet.features.chains.antelope_base.domain.usecase.rentViaRex

import com.mangala.antelope.base.domain.model.ResourceProviderResponse
import com.mangala.antelope.base.domain.model.Transaction
import com.mangala.wallet.antelope_balance.Balance
import com.mangala.wallet.antelope_balance.BalanceFormatter
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccountPermission
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.AccountBalanceRefresher
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountPermissionsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.RefreshAccountBalanceUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.BaseTransactUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.ResourceProviderRequestTransactionUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.SignAndComputeTransactionUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.SignAndPushResourceProvidedTransactionUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.SignAndPushTransactionUseCase
import com.mangala.wallet.model.blockchain.BlockchainType

class RentViaRexUseCase(
    private val generateRentViaRexUseCase: GenerateRentViaRexUseCase,
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

    suspend fun pushRentViaRexTransaction(
        blockchainType: BlockchainType,
        senderAccountName: String,
        receiver: String,
        loadAmount: Balance,
        loadFund: Balance,
        isCpu: Boolean
    ): Result<String> {
        return constructAndPushTransaction(
            blockchainType,
            senderAccountName,
            constructSignRequest = { permission ->
                constructRentViaRexRequest(
                    blockchainType,
                    senderAccountName,
                    permission,
                    receiver,
                    loadFund,
                    loadAmount,
                    isCpu = isCpu
                )
            }
        )
    }

    suspend fun requestRentViaRexTransaction(
        blockchainType: BlockchainType,
        senderAccountName: String,
        receiver: String,
        loadAmount: Balance,
        loadFund: Balance,
        isCpu: Boolean
    ): Result<ResourceProviderResponse> {
        return constructAndRequestTransaction(
            blockchainType,
            senderAccountName,
            constructSignRequest = { permission ->
                constructRentViaRexRequest(
                    blockchainType,
                    senderAccountName,
                    permission,
                    receiver,
                    loadFund,
                    loadAmount,
                    isCpu = isCpu
                )
            }
        )
    }

    private suspend fun constructRentViaRexRequest(
        blockchainType: BlockchainType,
        senderAccountName: String,
        permission: AntelopeAccountPermission,
        receiver: String,
        loadFund: Balance,
        loadAmount: Balance,
        isCpu: Boolean
    ) = generateRentViaRexUseCase.invoke(
        blockchainType = blockchainType,
        senderAccountName = senderAccountName.trim(),
        signingPermissionName = permission.permissionType.permissionName,
        receiver = receiver,
        loadFund = BalanceFormatter.formatEosBalance(loadFund, ignoreLocale = true),
        loadAmount = BalanceFormatter.formatEosBalance(loadAmount, ignoreLocale = true),
        isCpu = isCpu
    )
}