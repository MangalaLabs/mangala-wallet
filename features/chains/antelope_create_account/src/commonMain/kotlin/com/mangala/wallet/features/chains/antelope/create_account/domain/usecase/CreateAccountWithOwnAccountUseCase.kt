package com.mangala.wallet.features.chains.antelope.create_account.domain.usecase

import com.mangala.antelope.base.domain.model.ResourceProviderResponse
import com.mangala.wallet.antelope_key_manager.domain.model.AccountKeyPairs
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccountPermission
import com.mangala.wallet.features.chains.antelope_base.domain.model.CreateAccountRamOption
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.AccountBalanceRefresher
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountPermissionsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.BaseTransactUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.ResourceProviderRequestTransactionUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.SignAndComputeTransactionUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.SignAndPushResourceProvidedTransactionUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.SignAndPushTransactionUseCase
import com.mangala.wallet.model.blockchain.BlockchainType

class CreateAccountWithOwnAccountUseCase(
    private val generateCreateAccountSignRequestUseCase: GenerateCreateAccountSignRequestUseCase,
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

    suspend fun requestCreateAccount(
        newAccountName: String,
        account: AntelopeAccount,
        createAccountRamOption: CreateAccountRamOption,
        blockchainType: BlockchainType,
        keyPairs: AccountKeyPairs
    ): Result<ResourceProviderResponse> {
        return constructAndRequestTransaction(
            blockchainType,
            account.accountName
        ) { permission ->
            generateTransactionRequest(
                blockchainType,
                account,
                permission,
                newAccountName,
                keyPairs,
                createAccountRamOption
            )
        }
    }

    suspend fun pushCreateAccount(
        newAccountName: String,
        account: AntelopeAccount,
        createAccountRamOption: CreateAccountRamOption,
        blockchainType: BlockchainType,
        keyPairs: AccountKeyPairs
    ): Result<String> {
        val response = constructAndPushTransaction(
            blockchainType,
            account.accountName
        ) { permission ->
            generateTransactionRequest(
                blockchainType,
                account,
                permission,
                newAccountName,
                keyPairs,
                createAccountRamOption
            )
        }

        if (response.isFailure) {
            return response
        }

        return response
    }

    private suspend fun generateTransactionRequest(
        blockchainType: BlockchainType,
        account: AntelopeAccount,
        permission: AntelopeAccountPermission,
        newAccountName: String,
        keyPairs: AccountKeyPairs,
        createAccountRamOption: CreateAccountRamOption
    ) = generateCreateAccountSignRequestUseCase(
        blockchainType = blockchainType,
        creatorAccountName = account.accountName,
        signingPermissionName = permission.permissionType.permissionName,
        accountName = newAccountName,
        newAccountOwnerPublicKey = keyPairs.ownerKeyPair.publicKey.toString(),
        newAccountActivePublicKey = keyPairs.activeKeyPair.publicKey.toString(),
        createAccountRamOption = createAccountRamOption
    )
}