package com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal

import com.mangala.antelope.base.domain.model.ResourceProviderResponse
import com.mangala.antelope.base.domain.model.Transaction
import com.mangala.wallet.features.chains.antelope_base.domain.model.multisig.MultisigAction
import com.mangala.wallet.features.chains.antelope_base.domain.model.actions.abis.AntelopeActionAbi
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.AccountBalanceRefresher
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountPermissionsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.BaseTransactUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.ResourceProviderRequestTransactionUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.SignAndComputeTransactionUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.SignAndPushResourceProvidedTransactionUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.SignAndPushTransactionUseCase
import com.mangala.wallet.model.blockchain.BlockchainType
import com.memtrip.eos.chain.actions.transaction.abi.ActionAbi
import com.memtrip.eos.chain.actions.transaction.abi.TransactionAuthorizationAbi

class CreateProposeTransactionUseCase(
    private val generateCreateProposalUseCase: GenerateCreateProposalUseCase,
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

    suspend fun pushCreateProposal(
        blockchainType: BlockchainType,
        senderAccountName: String,
        proposalName: String,
        requestedPermissions: List<TransactionAuthorizationAbi>,
        accountPermissionExecuted: String,
        actions: List<MultisigAction>,
        expiryTimestamp: Long
    ): Result<String> {
        val nestedActionAbiMap = rebuildActionsList(actions)

        return try {
            constructAndPushTransaction(
                blockchainType,
                senderAccountName
            ) {
                generateCreateProposalUseCase(
                    blockchainType = blockchainType,
                    proposerAccountName = senderAccountName.trim(),
                    proposalName = proposalName.trim(),
                    requestedPermissions = requestedPermissions,
                    accountPermissionExecuted = accountPermissionExecuted,
                    actions = nestedActionAbiMap,
                    expiryTimestamp = expiryTimestamp
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun requestCreateProposal(
        blockchainType: BlockchainType,
        senderAccountName: String,
        proposalName: String,
        requestedPermissions: List<TransactionAuthorizationAbi>,
        accountPermissionExecuted: String,
        actions: List<MultisigAction>,
        expiryTimestamp: Long
    ): Result<ResourceProviderResponse> {
        val nestedActionAbiMap = rebuildActionsList(actions)

        return try {
            constructAndRequestTransaction(
                blockchainType,
                senderAccountName
            ) {
                generateCreateProposalUseCase(
                    blockchainType = blockchainType,
                    proposerAccountName = senderAccountName.trim(),
                    proposalName = proposalName.trim(),
                    requestedPermissions = requestedPermissions,
                    accountPermissionExecuted = accountPermissionExecuted,
                    actions = nestedActionAbiMap,
                    expiryTimestamp = expiryTimestamp
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun rebuildActionsList(
        actions: List<MultisigAction>,
    ): List<MultisigAction> {
        return actions.map {
            val indexMap = it.fields.actionMapIndex
            val actionsList = it.fields.actionList

            // Record the number of elements an array has. Index = element index in antelopeActionAbiList, Value = number of elements in array
            val elementsInArray = IntArray(indexMap.size) { 0 }
            indexMap.forEach { parentIndex ->
                if (parentIndex != -1) {
                    elementsInArray[parentIndex]++
                }
            }

            val actionsListWithArraySize = actionsList.mapIndexed { index, antelopeActionAbi ->
                antelopeActionAbi.copy(arraySize = elementsInArray[index])
            }

            it.copy(
                fields = it.fields.copy(
                    actionList = actionsListWithArraySize
                )
            )
        }
    }
}