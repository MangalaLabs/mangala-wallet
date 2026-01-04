package com.mangala.wallet.domain.dapp.usecase

import com.mangala.wallet.domain.base.UseCase
import com.mangala.wallet.domain.dapp.repository.DAppRepository
import com.mangala.wallet.model.category_dapp.domain.DAppModel

class GetDAppUseCase(private val dAppRepository: DAppRepository) {
    suspend operator fun invoke(id: String): DAppModel? {
        return dAppRepository.getDAppDetails(id)
    }
}