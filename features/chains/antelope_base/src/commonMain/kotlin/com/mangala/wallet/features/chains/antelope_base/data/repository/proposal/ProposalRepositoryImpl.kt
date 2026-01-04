package com.mangala.wallet.features.chains.antelope_base.data.repository.proposal

import androidx.paging.ExperimentalPagingApi
import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import app.cash.paging.map
import com.mangala.antelope.base.api.remote.EosRemoteDataSource
import com.mangala.wallet.features.chains.antelope_base.data.local.cache.AntelopeRemoteKeyLocalDataSource
import com.mangala.wallet.features.chains.antelope_base.data.local.proposal.ProposalLocalDataSource
import com.mangala.wallet.features.chains.antelope_base.data.repository.proposal.mapper.toMultisigProposal
import com.mangala.wallet.features.chains.antelope_base.domain.model.multisig.MultisigAction
import com.mangala.wallet.features.chains.antelope_base.domain.model.multisig.MultisigActionAuthorization
import com.mangala.wallet.features.chains.antelope_base.domain.model.multisig.MultisigProposal
import com.mangala.wallet.features.chains.antelope_base.domain.model.proposal.ProposalData
import com.mangala.wallet.features.chains.antelope_base.domain.repository.proposal.ProposalRepository
import com.mangala.wallet.features.chains.antelopebase.AntelopeMsigProposalDraftEntity
import com.mangala.wallet.features.chains.antelopebase.AntelopeProposalEntity
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.utils.ext.toLong
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ProposalRepositoryImpl(
    private val serializingJson: Json,
    private val eosRemoteDataSource: EosRemoteDataSource,
    private val proposalLocalDataSource: ProposalLocalDataSource,
    private val antelopeRemoteKeyLocalDataSource: AntelopeRemoteKeyLocalDataSource,
) : ProposalRepository {

    @ExperimentalPagingApi
    override fun getPaginatedProposalForAccount(
        accountName: String,
        blockchainType: BlockchainType,
        type: ProposalData.Type,
        forceRefresh: Boolean
    ): Flow<PagingData<ProposalData>> = Pager(
        config = PagingConfig(
            pageSize = PAGINATED_PROPOSAL_PAGE_SIZE,
            prefetchDistance = PAGINATED_PROPOSAL_PAGE_SIZE / 2
        ),
        remoteMediator = AntelopeProposalRemoteMediator(
            accountName = accountName,
            blockchainType = blockchainType,
            remoteDataSource = eosRemoteDataSource,
            localDataSource = proposalLocalDataSource,
            remoteKeyDataSource = antelopeRemoteKeyLocalDataSource,
            limit = PAGINATED_PROPOSAL_PAGE_SIZE,
            type = type
        )
    ) {
        proposalLocalDataSource.getProposalPagingSource(
            accountName = accountName,
            blockchainType = blockchainType,
            type = type
        )
    }.flow.map { pagingData ->
        pagingData.map {
            it.toProposalData()
        }
    }

    override suspend fun saveProposalDraft(
        proposalId: Long?,
        blockchainType: BlockchainType,
        proposalName: String,
        expirationTimestamp: Long,
        proposerAccountName: String,
        proposerAccountPermission: String,
        actions: List<MultisigAction>,
        approvers: Map<MultisigActionAuthorization, List<MultisigActionAuthorization>>
    ) {
        proposalLocalDataSource.insertDraftProposal(
            proposalId,
            proposalEntity = AntelopeProposalEntity(
                proposal_name = proposalName,
                proposer = proposerAccountName,
                requested_approvals = approvers.flatMap {
                    entry -> entry.value.map { it.authorizationName }
                }, // Flatten map to a list of names
                blockchain_uid = blockchainType.uid,
                created_at = Clock.System.now().toEpochMilliseconds(),
                expires_at = expirationTimestamp,
                actionsName = actions.map { it.actionName },
                account_name = proposerAccountName,
                is_draft = true.toLong()
            ),
            draftEntity = AntelopeMsigProposalDraftEntity(
                id = proposalId ?: 0,
                proposal_name = proposalName,
                account_name = proposerAccountName,
                blockchain_uid = blockchainType.uid,
                actions_detail_json = serializingJson.encodeToString(actions),
                approvers_detail_json = serializingJson.encodeToString(approvers),
                proposer_permission_name = proposerAccountPermission
            )
        )
    }

    override suspend fun getProposalDraft(
        blockchainType: BlockchainType,
        proposalName: String,
        proposerAccountName: String
    ): MultisigProposal? {
        return proposalLocalDataSource.getDraftProposal(
            blockchainType = blockchainType,
            proposalName = proposalName,
            proposerAccountName = proposerAccountName
        )?.toMultisigProposal(json = serializingJson)
    }

    override fun isProposalDraftExists(
        blockchainType: BlockchainType,
        proposalName: String,
        proposerAccountName: String
    ): Boolean {
        return proposalLocalDataSource.getDraftProposalExists(
            blockchainType = blockchainType,
            proposalName = proposalName,
            proposerAccountName = proposerAccountName
        )
    }

    override suspend fun updateProposal(proposalData: ProposalData) {
        proposalLocalDataSource.upsertProposal(
            proposalData.toAntelopeProposalEntity(proposalData.proposer)
        )
    }

    override suspend fun deleteProposal(
        blockchainType: BlockchainType,
        proposalName: String,
        proposerAccountName: String
    ) {
        proposalLocalDataSource.deleteProposal(
            blockchainType = blockchainType,
            proposalName = proposalName,
            proposerAccountName = proposerAccountName
        )
    }

    override suspend fun deleteDraftProposal(
        blockchainType: BlockchainType,
        proposalName: String,
        proposerAccountName: String
    ) {
        proposalLocalDataSource.deleteDraftProposal(
            blockchainType = blockchainType,
            proposalName = proposalName,
            proposerAccountName = proposerAccountName
        )
    }

    companion object {
//        The response approximate size is 30kb if page size is 20, 15kb if page size is 10
        private const val PAGINATED_PROPOSAL_PAGE_SIZE = 20
    }
}