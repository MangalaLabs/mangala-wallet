package com.mangala.wallet.domain.dapp.usecase

import com.mangala.wallet.domain.dapp.repository.DAppRepository
import com.mangala.wallet.model.category_dapp.remote.BaseResponse
import com.mangala.wallet.model.category_dapp.remote.DAppRemote
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.network.CustomError

class GetDAppsBySingleCategoryUseCase(private val dAppRepository: DAppRepository) {
    suspend operator fun invoke(categoryID: String): ApiResponse<BaseResponse<List<DAppRemote>>, CustomError> {
        return dAppRepository.getDAppsBySingleCategory(categoryID)
    }
}
