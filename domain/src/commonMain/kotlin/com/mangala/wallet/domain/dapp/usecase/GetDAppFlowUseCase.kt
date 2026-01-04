package com.mangala.wallet.domain.dapp.usecase

import com.mangala.wallet.domain.dapp.repository.DAppRepository
import com.mangala.wallet.model.category_dapp.domain.DAppModel
import kotlinx.coroutines.flow.Flow

class GetDAppFlowUseCase(private val dAppRepository: DAppRepository) {
    operator fun invoke(id: String): Flow<DAppModel?> {
        return dAppRepository.getDAppFlow(id)
    }
}
