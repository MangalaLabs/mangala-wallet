package com.mangala.wallet.features.chains.antelope_base.data.local.proposal

import app.cash.paging.PagingSource
import app.cash.sqldelight.paging3.QueryPagingSource
import com.mangala.wallet.features.chains.antelope_base.data.local.AntelopeDatabaseWrapper
import com.mangala.wallet.features.chains.antelope_base.domain.model.proposal.ProposalData
import com.mangala.wallet.features.chains.antelopebase.AntelopeMsigProposalDraftEntity
import com.mangala.wallet.features.chains.antelopebase.AntelopeProposalEntity
import com.mangala.wallet.features.chains.antelopebase.SelectMsigProposalDraftByAccountName
import com.mangala.wallet.model.blockchain.BlockchainType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

internal class ProposalLocalDataSourceImpl(
    databaseWrapper: AntelopeDatabaseWrapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ProposalLocalDataSource {

    private val database = databaseWrapper.instance
    private val dbQuery = database.antelopeDatabaseQueries

    override fun getProposalPagingSource(
        accountName: String,
        blockchainType: BlockchainType,
        type: ProposalData.Type,
    ): PagingSource<Int, AntelopeProposalEntity> = QueryPagingSource(
        countQuery = when (type) {
            ProposalData.Type.PROPOSAL -> dbQuery.countProposals(
                accountName = accountName,
                blockchain_uid = blockchainType.uid,
            )

            ProposalData.Type.APPROVAL -> dbQuery.countApprovals(
                accountName = accountName,
                blockchain_uid = blockchainType.uid,
            )
        },
        transacter = dbQuery,
        context = Dispatchers.IO,
        queryProvider = { limit, offset ->
            when (type) {
                ProposalData.Type.PROPOSAL -> dbQuery.getAntelopeProposalsByAccountNamePaged(
                    accountName = accountName,
                    blockchain_uid = blockchainType.uid,
                    limit = limit,
                    offset = offset
                )

                ProposalData.Type.APPROVAL -> dbQuery.getAntelopeApprovalsByAccountNamePaged(
                    accountName = accountName,
                    blockchain_uid = blockchainType.uid,
                    limit = limit,
                    offset = offset
                )
            }
        }
    )

    override suspend fun upsertProposal(proposalEntity: AntelopeProposalEntity) = withContext(ioDispatcher) {
        upsertProposalWithoutSuspend(proposalEntity)
    }


    override suspend fun upsertProposals(proposalEntities: List<AntelopeProposalEntity>) = withContext(ioDispatcher) {
        proposalEntities.forEach { proposalEntity ->
            upsertProposalWithoutSuspend(proposalEntity)
        }
    }

    override suspend fun insertProposal(proposalEntity: AntelopeProposalEntity) = withContext(ioDispatcher) {
        insertProposalWithoutSuspend(proposalEntity)
    }

    override suspend fun insertProposals(proposalEntities: List<AntelopeProposalEntity>) = withContext(ioDispatcher) {
        proposalEntities.forEach { proposalEntity ->
            insertProposalWithoutSuspend(proposalEntity)
        }
    }

    override suspend fun insertDraftProposal(
        proposalId: Long?,
        proposalEntity: AntelopeProposalEntity,
        draftEntity: AntelopeMsigProposalDraftEntity
    ) = withContext(ioDispatcher) {
        dbQuery.transaction {
            insertProposalWithoutSuspend(proposalEntity)
            dbQuery.insertMsigProposalDraft(
                id = proposalId,
                proposal_name = draftEntity.proposal_name,
                blockchain_uid = draftEntity.blockchain_uid,
                account_name = draftEntity.account_name,
                proposer_permission_name = draftEntity.proposer_permission_name,
                actions_detail_json = draftEntity.actions_detail_json,
                approvers_detail_json = draftEntity.approvers_detail_json
            )
        }
    }

    override suspend fun getDraftProposal(
        blockchainType: BlockchainType,
        proposalName: String,
        proposerAccountName: String
    ): SelectMsigProposalDraftByAccountName? = withContext(ioDispatcher) {
        return@withContext dbQuery.selectMsigProposalDraftByAccountName(
            blockchain_uid = blockchainType.uid,
            proposal_name = proposalName,
            account_name = proposerAccountName
        ).executeAsOneOrNull()
    }

    override fun getDraftProposalExists(
        blockchainType: BlockchainType,
        proposalName: String,
        proposerAccountName: String
    ): Boolean {
        return dbQuery.getDraftProposalExists(
            blockchain_uid = blockchainType.uid,
            proposal_name = proposalName,
            account_name = proposerAccountName
        ).executeAsOneOrNull() == 1L
    }

    override suspend fun deleteProposal(
        blockchainType: BlockchainType,
        proposalName: String,
        proposerAccountName: String
    ) = withContext(ioDispatcher) {
        // Since we've got ON DELETE CASCADE on the proposal draft table, can just delete this and draft will follow
        dbQuery.deleteProposal(
            blockchain_uid = blockchainType.uid,
            proposal_name = proposalName,
            account_name = proposerAccountName
        )
    }

    override suspend fun deleteDraftProposal(
        blockchainType: BlockchainType,
        proposalName: String,
        proposerAccountName: String
    ) = withContext(ioDispatcher) {
        dbQuery.deleteProposalDraft(
            blockchain_uid = blockchainType.uid,
            proposal_name = proposalName,
            account_name = proposerAccountName
        )
    }

    override suspend fun invalidateProposalCache(
        accountName: String,
        blockchainType: BlockchainType,
        type: ProposalData.Type
    ) = withContext(ioDispatcher) {
        when (type) {
            ProposalData.Type.PROPOSAL -> dbQuery.invalidateCachedProposals(
                accountName = accountName, blockchainUid = blockchainType.uid
            )

            ProposalData.Type.APPROVAL -> dbQuery.invalidateCachedApprovals(
                accountName = accountName, blockchainUid = blockchainType.uid
            )
        }
    }

    private fun upsertProposalWithoutSuspend(proposalEntity: AntelopeProposalEntity) {
        dbQuery.upsertProposal(
            proposer = proposalEntity.proposer,
            proposal_name = proposalEntity.proposal_name,
            blockchain_uid = proposalEntity.blockchain_uid,
            created_at = proposalEntity.created_at,
            expires_at = proposalEntity.expires_at,
            actionsName = proposalEntity.actionsName,
            accountName = proposalEntity.account_name,
            isDraft = proposalEntity.is_draft
        )
    }

    private fun insertProposalWithoutSuspend(proposalEntity: AntelopeProposalEntity) {
        dbQuery.insertOrReplaceProposal(
            proposer = proposalEntity.proposer,
            proposal_name = proposalEntity.proposal_name,
            blockchain_uid = proposalEntity.blockchain_uid,
            created_at = proposalEntity.created_at,
            expires_at = proposalEntity.expires_at,
            actionsName = proposalEntity.actionsName,
            account_name = proposalEntity.account_name,
            is_draft = proposalEntity.is_draft,
            requested_approvals = proposalEntity.requested_approvals,
        )
    }
}
