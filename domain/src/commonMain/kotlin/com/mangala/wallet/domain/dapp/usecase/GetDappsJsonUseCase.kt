package com.mangala.wallet.domain.dapp.usecase

import com.mangala.wallet.domain.dapp.repository.DAppRepository
import com.mangala.wallet.model.category_dapp.remote.CategoryDApp
import com.mangala.wallet.remote.di.ApiResponse
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

class GetDappsJsonUseCase(private val dAppRepository: DAppRepository) {
    suspend operator fun invoke(): List<CategoryDApp> {
        val result = dAppRepository.getDappsJson()
        val response = mutableListOf<CategoryDApp>()
        if (result is ApiResponse.Success) {
            val categoryDApps = parseCategoryDApps(result.body)
            response.addAll(categoryDApps)
        }
        return response
    }

    fun parseCategoryDApps(json: String): List<CategoryDApp> {
        val jsonParser = Json { ignoreUnknownKeys = true }
        return jsonParser.decodeFromString(ListSerializer(CategoryDApp.serializer()), json)
    }

}