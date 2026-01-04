package com.mangala.wallet.domain.dapp.usecase

import com.mangala.wallet.domain.dapp.repository.DAppRepository
import com.mangala.wallet.model.category_dapp.domain.DAppModel

class GetListDAppUseCase(private val dAppRepository: DAppRepository) {
    suspend operator fun invoke(): List<DAppModel> {
        return dAppRepository.getListDApp()
    }
}
