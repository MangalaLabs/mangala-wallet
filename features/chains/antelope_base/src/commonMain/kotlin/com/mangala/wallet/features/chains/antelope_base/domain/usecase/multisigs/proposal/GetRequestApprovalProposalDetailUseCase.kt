package com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal

import com.mangala.antelope.base.api.model.GetMultisigProposalTableRowResponse
import com.mangala.antelope.base.api.model.GetTableRowsMultisigsRequest
import com.mangala.wallet.features.chains.antelope_base.domain.model.account.AccountName.MULTISIG
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.GetTableRowsMultisigsProposalsUseCase
import com.mangala.wallet.model.blockchain.BlockchainType

class GetRequestApprovalProposalDetailUseCase(
    private val getTableRowsMultisigsProposalsUseCase: GetTableRowsMultisigsProposalsUseCase,
) {

    suspend operator fun invoke(
        proposerAccountName: String,
        proposalName: String,
        blockchainType: BlockchainType,
    ): GetMultisigProposalTableRowResponse? {
        return getTableRowsMultisigsProposalsUseCase.invoke(
            request = GetTableRowsMultisigsRequest(
                code = MULTISIG,
                table = "approvals2",
                lowerBound = proposalName,
                upperBound = proposalName,
                encodeType = "",
                keyType = "",
                indexPosition = "1",
                limit = 100,
                scope = proposerAccountName,
                json = true,
                reverse = false,
                showPayer = false
            ),
            blockchainType = blockchainType
        )
    }
}