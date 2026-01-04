package com.mangala.wallet.remote.category_dapps

import com.mangala.wallet.model.category_dapp.remote.Category
import com.mangala.wallet.model.category_dapp.remote.DAppRemote
import com.mangala.wallet.model.category_dapp.remote.BaseResponse
import com.mangala.wallet.model.category_dapp.remote.CategoryDApp
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.di.safeApiCall
import com.mangala.wallet.remote.network.CustomError

class CategoryDAppRemoteDataSource(private val categoryDAppsApi: CategoryDAppApi) {
    suspend fun getListCategory(): ApiResponse<BaseResponse<List<Category>>, CustomError> {
        return safeApiCall{ categoryDAppsApi.getCategories()}
    }

    suspend fun getListDApps(listCategoryID: List<String>): ApiResponse<BaseResponse<List<CategoryDApp>>, CustomError> {
        return safeApiCall { categoryDAppsApi.getDappsByCategories(listCategoryID) }
    }

    suspend fun getListDApps(categoryID: String): ApiResponse<BaseResponse<List<DAppRemote>>, CustomError> {
        return safeApiCall {categoryDAppsApi.getDappsByCategory(categoryID) }
    }

    suspend fun getDappsJson():  ApiResponse<String, CustomError> {
        return safeApiCall {categoryDAppsApi.getDappsJson() }
    }

}