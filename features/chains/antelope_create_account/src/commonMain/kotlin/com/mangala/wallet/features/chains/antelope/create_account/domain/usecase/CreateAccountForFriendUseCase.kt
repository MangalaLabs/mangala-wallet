package com.mangala.wallet.features.chains.antelope.create_account.domain.usecase

import com.linh.antelope_qr.domain.model.CreateAccountForFriendRequest
import com.mangala.antelope.base.domain.model.ResourceProviderResponse
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccountPermission
import com.mangala.wallet.features.chains.antelope_base.domain.model.CreateAccountRamOption
import com.mangala.wallet.features.chains.antelope_base.domain.model.transaction.SignTransactionRequest
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.AccountBalanceRefresher
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountPermissionsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.BaseTransactUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.ResourceProviderRequestTransactionUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.SignAndComputeTransactionUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.SignAndPushResourceProvidedTransactionUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.SignAndPushTransactionUseCase
import com.mangala.wallet.model.blockchain.BlockchainType

class CreateAccountForFriendUseCase(
    private val getAccountPermissionsUseCase: GetAccountPermissionsUseCase,
    private val generateCreateAccountSignRequestUseCase: GenerateCreateAccountSignRequestUseCase,
    private val signAndPushTransactionUseCase: SignAndPushTransactionUseCase,
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
        request: CreateAccountForFriendRequest,
        account: AntelopeAccount,
        createAccountRamOption: CreateAccountRamOption,
    ): Result<ResourceProviderResponse> {
        val blockchainType = BlockchainType.fromUid(request.blockchainUid)

        return constructAndRequestTransaction(
            blockchainType,
            account.accountName,
            constructSignRequest = { permission ->
                constructCreateAccountRequest(
                    blockchainType,
                    account,
                    permission,
                    request,
                    createAccountRamOption
                )
            }
        )
    }

    suspend fun pushCreateAccount(
        request: CreateAccountForFriendRequest,
        account: AntelopeAccount,
        createAccountRamOption: CreateAccountRamOption,
    ): Result<String> {
        val blockchainType = BlockchainType.fromUid(request.blockchainUid)

        return constructAndPushTransaction(
            blockchainType,
            account.accountName,
            constructSignRequest = { permission ->
                constructCreateAccountRequest(
                    blockchainType,
                    account,
                    permission,
                    request,
                    createAccountRamOption
                )
            }
        )
    }

    private suspend fun constructCreateAccountRequest(
        blockchainType: BlockchainType,
        account: AntelopeAccount,
        permission: AntelopeAccountPermission,
        request: CreateAccountForFriendRequest,
        ramOption: CreateAccountRamOption
    ): SignTransactionRequest? {
        return generateCreateAccountSignRequestUseCase(
            blockchainType = blockchainType,
            creatorAccountName = account.accountName,
            signingPermissionName = permission.permissionType.permissionName,
            accountName = request.accountName,
            newAccountOwnerPublicKey = request.ownerPublicKey,
            newAccountActivePublicKey = request.activePublicKey,
            createAccountRamOption = ramOption
        )
    }
}