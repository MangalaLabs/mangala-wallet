package com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal

import com.mangala.antelope.base.domain.usecase.GetInfoUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.transaction.SignTransactionRequest
import com.mangala.wallet.features.chains.antelope_base.domain.model.transaction.SignTransactionType
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.BaseGenerateSignRequestUseCase
import com.mangala.wallet.model.blockchain.BlockchainType
import com.memtrip.eos.core.block.BlockIdDetails

class GenerateExecuteProposalUseCase(
    getInfoUseCase: GetInfoUseCase
) : BaseGenerateSignRequestUseCase(getInfoUseCase) {

    suspend operator fun invoke(
        blockchainType: BlockchainType,
        proposerAccountName: String,
        proposalName: String,
        accountPermissionExecuted: String,
        accountNameExecuted: String,
    ): SignTransactionRequest? {
        val chainInfo = getInfoUseCase(blockchainType)

        val blockDetails = chainInfo?.let {
            BlockIdDetails(
                it.headBlockId.orEmpty()
            )
        }
        return constructSignRequest(
            blockchainType,
            constructAuthorization = {
                constructAuthorization(accountNameExecuted, accountPermissionExecuted)
            },
            constructActions = { authorization ->
                val transferAction = SignTransactionRequest.Action.ExecuteProposal(
                    authorization = authorization,
                    proposerAccountName = proposerAccountName,
                    proposalName = proposalName,
                    accountNameExecuted = accountNameExecuted,
                    accountPermissionExecuted = accountPermissionExecuted,
                    blockNum = blockDetails?.blockNum ?: 0,
                    blockPrefix = blockDetails?.blockPrefix ?: 0
                )
                listOf(transferAction)
            },
            signTransactionType = SignTransactionType.EXECUTE_PROPOSAL
        )
    }
}

