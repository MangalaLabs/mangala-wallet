package com.mangala.wallet.domain.dapp.usecase

import com.mangala.wallet.domain.dapp.repository.DAppRepository
import com.mangala.wallet.model.category_dapp.domain.DAppModel
import kotlinx.coroutines.flow.Flow

class GetListDAppFlowUseCase(private val dAppRepository: DAppRepository) {
    operator fun invoke(): Flow<List<DAppModel>> {
        return dAppRepository.getListDAppFlow()
    }
}