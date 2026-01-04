package com.mangala.wallet.domain.dapp.usecase

import com.mangala.wallet.domain.dapp.repository.DAppRepository
import com.mangala.wallet.model.category_dapp.remote.DAppRemote

class SaveDAppUseCase(private val dAppRepository: DAppRepository) {
    suspend operator fun invoke(dApp: DAppRemote) {
        dAppRepository.saveDApp(dApp)
    }
}
