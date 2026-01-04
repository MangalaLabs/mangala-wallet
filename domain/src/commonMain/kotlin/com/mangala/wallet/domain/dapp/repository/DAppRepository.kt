package com.mangala.wallet.domain.dapp.repository

import com.mangala.wallet.model.category_dapp.domain.DAppModel
import com.mangala.wallet.model.category_dapp.remote.BaseResponse
import com.mangala.wallet.model.category_dapp.remote.Category
import com.mangala.wallet.model.category_dapp.remote.CategoryDApp
import com.mangala.wallet.model.category_dapp.remote.DAppRemote
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.network.CustomError
import kotlinx.coroutines.flow.Flow

interface DAppRepository {

    // Fetching List of Categories
    suspend fun getListOfCategories(): ApiResponse<BaseResponse<List<Category>>, CustomError>

    // Fetching DApps by Categories
    suspend fun getDAppsByCategories(listCategoryID: List<String>): ApiResponse<BaseResponse<List<CategoryDApp>>, CustomError>

    // Fetching DApps by a Single Category
    suspend fun getDAppsBySingleCategory(categoryID: String): ApiResponse<BaseResponse<List<DAppRemote>>, CustomError>

    // Fetching a Single DApp Details
    suspend fun getDAppDetails(id: String): DAppModel?

    // Save a DApp
    suspend fun saveDApp(dApp: DAppRemote)

    // Delete a DApp by its ID
    suspend fun deleteDApp(id: String)

    // Get List of all DApps
    suspend fun getListDApp(): List<DAppModel>

    fun getDAppFlow(id: String): Flow<DAppModel?>

    fun getListDAppFlow(): Flow<List<DAppModel>>

    suspend fun getDappsJson(): ApiResponse<String, CustomError>

}
