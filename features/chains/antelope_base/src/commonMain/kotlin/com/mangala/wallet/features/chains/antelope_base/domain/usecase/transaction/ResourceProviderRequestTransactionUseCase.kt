package com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction

import com.mangala.antelope.base.domain.model.ResourceProviderResponse
import com.mangala.antelope.base.domain.model.Transaction
import com.mangala.antelope.base.domain.repository.AntelopeRepository
import com.mangala.antelope.base.model.SystemContracts
import com.mangala.wallet.antelope_balance.BalanceFormatter
import com.mangala.wallet.features.chains.antelope_base.domain.model.transaction.SignTransactionRequest
import com.mangala.wallet.features.chains.antelope_base.domain.model.transaction.toPackedTrx
import com.mangala.wallet.model.blockchain.BlockchainType
import com.memtrip.eos.chain.actions.transaction.decoder.AbiBinaryTransactionReader

class ResourceProviderRequestTransactionUseCase(private val antelopeRepository: AntelopeRepository) {

    suspend operator fun invoke(
        signTransactionRequest: SignTransactionRequest,
        actor: String,
        permissionName: String,
        blockchainType: BlockchainType,
        maxFees: Double? = null // Max required fee for transaction, -1 for no fee limit
    ): Result<ResourceProviderResponse> {
        val packedTrx = signTransactionRequest.toTransactionAbi().toPackedTrx()
        val response = antelopeRepository.requestTransaction(
            blockchainType,
            packedTrx,
            actor,
            permissionName
        )

        val resourceProviderResponse = response.getOrNull()

        if (resourceProviderResponse != null) {
            val modifiedTransactionValid =
                checkModifiedTransactionValid(signTransactionRequest, resourceProviderResponse)

            if (!modifiedTransactionValid) return Result.failure(Exception("Resource provider response does not contain original actions"))

            val addedActions = getNewActions(signTransactionRequest, resourceProviderResponse)

            if (checkNewActionsNotInWhitelist(addedActions)) {
                return Result.failure(
                    Exception("New actions added to the transaction are not in the whitelist")
                )
            }

            if (maxFees == null) return response

            if (checkTransactionFeeNotExceedMaxFee(addedActions, maxFees)) {
                return Result.failure(
                    Exception("Transaction fees exceed the maximum allowed")
                )
            }
        }

        return response
    }

    private fun checkTransactionFeeNotExceedMaxFee(
        addedActions: List<Transaction.Action>,
        maxFees: Double
    ): Boolean {
        // Find any transfer actions that were added to the transaction, which we assume are fees
        val addedFees = addedActions.filter { action ->
            action.account == SystemContracts.TOKEN && action.name == "transfer"
        }.map { action ->
            val transferArgs = AbiBinaryTransactionReader(action.data).readTransferArgs()

            BalanceFormatter.deserialize(transferArgs.quantity).amount
        }.reduce { acc, d ->
            acc + d
        }

        if (addedFees > maxFees) {
            return true
        }
        return false
    }

    private fun checkNewActionsNotInWhitelist(addedActions: List<Transaction.Action>): Boolean {
        return addedActions.any { action ->
            ACTIONS_WHITELIST.any { (account, actionName) ->
                action.account == account && action.name == actionName
            }.not()
        }
    }

    private fun checkModifiedTransactionValid(
        original: SignTransactionRequest,
        resourceProviderResponse: ResourceProviderResponse
    ): Boolean {
        val modifiedTransaction = getModifiedTransaction(resourceProviderResponse) ?: return true

        return original.actions.all { originalAction ->
            modifiedTransaction.actions.any { modifiedAction ->
                isSameTransaction(originalAction, modifiedAction)
            }
        } && modifiedTransaction.signatures.isNotEmpty()
    }

    private fun getNewActions(
        original: SignTransactionRequest,
        modified: ResourceProviderResponse
    ): List<Transaction.Action> {
        val modifiedTransaction = getModifiedTransaction(modified) ?: return emptyList()

        return modifiedTransaction.actions.filter { modifiedAction ->
            original.actions.any { originalAction ->
                isSameTransaction(originalAction, modifiedAction).not()
            }
        }
    }

    private fun getModifiedTransaction(resourceProviderResponse: ResourceProviderResponse): Transaction? {
        val modifiedTransaction = when (resourceProviderResponse) {
            is ResourceProviderResponse.FeeRequired -> resourceProviderResponse.newTransaction
            is ResourceProviderResponse.ResourcePaidForFree -> resourceProviderResponse.newTransaction
            else -> null
        }
        return modifiedTransaction
    }

    private fun isSameTransaction(
        originalAction: SignTransactionRequest.Action,
        modifiedAction: Transaction.Action
    ): Boolean {
        // Ensure the original contract account matches
        val matchesOriginalContractAccount = originalAction.account == modifiedAction.account

        // Ensure the original contract action matches
        val matchesOriginalContractAction = originalAction.name == modifiedAction.name

        // Ensure original authorization is intact
        val matchesOriginalAuthorization =
            originalAction.authorization.size == modifiedAction.authorization.size &&
                    originalAction.authorization.getOrNull(0)?.actor == modifiedAction.authorization.getOrNull(
                0
            )?.actor

        // Ensure the original action data matches
        val matchesOriginalActionData = originalAction.getActionData() == modifiedAction.data

        return matchesOriginalContractAccount && matchesOriginalContractAction && matchesOriginalAuthorization && matchesOriginalActionData
    }

    companion object {
        private val ACTIONS_WHITELIST = listOf(
            SystemContracts.TOKEN to "transfer",
            "greymassnoop" to "noop",
            "eosio" to "buyrambytes"
        ) // Pair of account & action name
    }
}