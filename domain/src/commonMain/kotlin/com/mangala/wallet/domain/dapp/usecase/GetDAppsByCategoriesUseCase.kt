package com.mangala.wallet.domain.dapp.usecase

import com.mangala.wallet.domain.dapp.repository.DAppRepository
import com.mangala.wallet.model.category_dapp.remote.BaseResponse
import com.mangala.wallet.model.category_dapp.remote.Category
import com.mangala.wallet.model.category_dapp.remote.CategoryDApp
import com.mangala.wallet.remote.di.ApiResponse

class GetDAppsByCategoriesUseCase(private val dAppRepository: DAppRepository) {
    suspend operator fun invoke(listCategoryID: List<String>): List<CategoryDApp> {
        val result = dAppRepository.getDAppsByCategories(listCategoryID)
        val response = mutableListOf<CategoryDApp>()
        if (result is ApiResponse.Success){
            response.addAll(result.body.data)
        }
        return response;
    }
}