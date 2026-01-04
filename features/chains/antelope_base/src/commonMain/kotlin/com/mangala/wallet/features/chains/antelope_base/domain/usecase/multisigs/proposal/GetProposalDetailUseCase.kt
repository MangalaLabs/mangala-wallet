package com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal

import com.mangala.antelope.base.api.model.GetTableRowsMultisigsRequest
import com.mangala.antelope.base.api.model.GetTableRowsMultisigsResponse
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.GetTableRowsDataProposalUseCase
import com.mangala.wallet.model.blockchain.BlockchainType

class GetProposalDetailUseCase(
    private val getTableRowsDataProposalUseCase: GetTableRowsDataProposalUseCase,
) {

    suspend operator fun invoke(
        blockchainType: BlockchainType,
        proposerAccountName: String,
        proposalName: String
    ): GetTableRowsMultisigsResponse? {
        val result = getTableRowsDataProposalUseCase(
            blockchainType,
            createTableRowsRequest(proposerAccountName, proposalName)
        )
        return result
    }


    private fun createTableRowsRequest(proposerAccountName: String, proposalName: String) =
        GetTableRowsMultisigsRequest(
            code = "eosio.msig",
            scope = proposerAccountName,
            table = "proposal",
            lowerBound = proposalName,
            encodeType = "",
            upperBound = "",
            limit = 1,
            showPayer = false,
            json = true,
            keyType = "",
            indexPosition = "",
            reverse = false
        )
}