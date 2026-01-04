package com.mangala.wallet.domain.dapp.usecase

import com.mangala.wallet.domain.dapp.repository.DAppRepository

class DeleteDAppUseCase(private val dAppRepository: DAppRepository) {
    suspend operator fun invoke(id: String) {
        dAppRepository.deleteDApp(id)
    }
}