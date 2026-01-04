package com.mangala.wallet.features.chains.antelope_base.domain.usecase.actions

import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.actions.abis.AntelopeActionAbi
import com.mangala.wallet.features.chains.antelope_base.domain.repository.actions.ActionsRepository
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountInfoUseCase
import com.mangala.wallet.utils.ext.parseUtcDateTimeToInstantOrNull

class GetActionsByContractUseCase(
    private val getAccountInfoUseCase: GetAccountInfoUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val actionsRepository: ActionsRepository
) {

    suspend operator fun invoke(
        accountName: String,
        actionName: String,
        forceRefresh: Boolean
    ): Result<List<AntelopeActionAbi>> {
        val networkSelected = getSelectedNetworkUseCase()
        val blockchainType = networkSelected.blockchainType
        val accountInfo = getAccountInfoUseCase(blockchainType, accountName)
            ?: return Result.success(emptyList())

        return actionsRepository.getActionName(
            accountName = accountName,
            actionName = actionName,
            lastAccountCodeUpdatedTimestamp = accountInfo
                .lastCodeUpdate.toString().parseUtcDateTimeToInstantOrNull()
                ?.toEpochMilliseconds() ?: 0,
            blockchainType = blockchainType,
            forceRefresh = forceRefresh
        )
    }

}