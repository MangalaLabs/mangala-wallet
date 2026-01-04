package com.mangala.wallet.domain.dapp.usecase

import com.mangala.wallet.domain.dapp.repository.DAppRepository
import com.mangala.wallet.model.category_dapp.remote.BaseResponse
import com.mangala.wallet.model.category_dapp.remote.Category
import com.mangala.wallet.remote.di.ApiResponse

class GetListOfCategoriesUseCase(private val dAppRepository: DAppRepository) {
    suspend operator fun invoke(): List<Category> {
        val result = dAppRepository.getListOfCategories()
        val response = mutableListOf<Category>()
        if (result is ApiResponse.Success){
            response.addAll(result.body.data)
        }
        return response;
    }
}
