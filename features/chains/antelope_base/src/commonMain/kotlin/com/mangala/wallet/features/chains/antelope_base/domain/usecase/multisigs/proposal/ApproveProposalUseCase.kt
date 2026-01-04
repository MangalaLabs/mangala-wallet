package com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal

import com.mangala.antelope.base.domain.model.ResourceProviderResponse
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.AccountBalanceRefresher
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountPermissionsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.BaseTransactUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.ResourceProviderRequestTransactionUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.SignAndComputeTransactionUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.SignAndPushResourceProvidedTransactionUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.SignAndPushTransactionUseCase
import com.mangala.wallet.model.blockchain.BlockchainType

class ApproveProposalUseCase(
    private val generateApproveProposalUseCase: GenerateApproveProposalUseCase,
    signAndPushTransactionUseCase: SignAndPushTransactionUseCase,
    signAndComputeTransactionUseCase: SignAndComputeTransactionUseCase,
    getAccountPermissionsUseCase: GetAccountPermissionsUseCase,
    resourceProviderRequestTransactionUseCase: ResourceProviderRequestTransactionUseCase,
    signAndPushResourceProvidedTransactionUseCase: SignAndPushResourceProvidedTransactionUseCase,
    accountBalanceRefresher: AccountBalanceRefresher,
) : BaseTransactUseCase(
    signAndPushTransactionUseCase,
    signAndComputeTransactionUseCase,
    getAccountPermissionsUseCase,
    resourceProviderRequestTransactionUseCase,
    signAndPushResourceProvidedTransactionUseCase,
    accountBalanceRefresher
) {
    override val shouldRefreshTokenBalanceAfterTransaction: Boolean = false

    suspend fun pushApproveProposal(
        blockchainType: BlockchainType,
        proposerAccountName: String,
        proposalName: String,
        accountPermissionExecuted: String,
        accountNameExecuted: String,
    ): Result<String> {
        return constructAndPushTransaction(
            blockchainType,
            accountNameExecuted
        ) {
            generateApproveProposalUseCase(
                blockchainType = blockchainType,
                proposerAccountName = proposerAccountName.trim(),
                proposalName = proposalName.trim(),
                accountPermissionExecuted = accountPermissionExecuted,
                accountNameExecuted = accountNameExecuted
            )
        }
    }

    suspend fun requestApproveProposal(
        blockchainType: BlockchainType,
        proposerAccountName: String,
        proposalName: String,
        accountPermissionExecuted: String,
        accountNameExecuted: String,
    ): Result<ResourceProviderResponse> {
        return constructAndRequestTransaction(
            blockchainType,
            accountNameExecuted
        ) {
            generateApproveProposalUseCase(
                blockchainType = blockchainType,
                proposerAccountName = proposerAccountName.trim(),
                proposalName = proposalName.trim(),
                accountPermissionExecuted = accountPermissionExecuted,
                accountNameExecuted = accountNameExecuted
            )
        }
    }

}