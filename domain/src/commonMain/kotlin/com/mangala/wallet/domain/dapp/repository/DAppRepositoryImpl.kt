package com.mangala.wallet.domain.dapp.repository

import com.mangala.wallet.local.dapp.DAppLocalDataSource
import com.mangala.wallet.model.category_dapp.domain.DAppModel
import com.mangala.wallet.model.category_dapp.local.DAppEntity
import com.mangala.wallet.model.category_dapp.remote.BaseResponse
import com.mangala.wallet.model.category_dapp.remote.Category
import com.mangala.wallet.model.category_dapp.remote.CategoryDApp
import com.mangala.wallet.model.category_dapp.remote.DAppRemote
import com.mangala.wallet.remote.category_dapps.CategoryDAppRemoteDataSource
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.network.CustomError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DAppRepositoryImpl(
    private val categoryDAppRemoteDataSource: CategoryDAppRemoteDataSource,
    private val dAppLocalDataSource: DAppLocalDataSource
) : DAppRepository {

    override suspend fun getListOfCategories(): ApiResponse<BaseResponse<List<Category>>, CustomError> {
        return categoryDAppRemoteDataSource.getListCategory()
    }

    override suspend fun getDAppsByCategories(listCategoryID: List<String>): ApiResponse<BaseResponse<List<CategoryDApp>>, CustomError> {
        return categoryDAppRemoteDataSource.getListDApps(listCategoryID)
    }

    override suspend fun getDAppsBySingleCategory(categoryID: String): ApiResponse<BaseResponse<List<DAppRemote>>, CustomError> {
        return categoryDAppRemoteDataSource.getListDApps(categoryID)
    }

    override suspend fun getDAppDetails(id: String): DAppModel? {
        val entity = dAppLocalDataSource.getDApp(id)
        return entity?.mapToDomainModel() as DAppModel?
    }

    override suspend fun saveDApp(dApp: DAppRemote) {
        dAppLocalDataSource.saveDApp(dApp)
    }

    override suspend fun deleteDApp(id: String) {
        dAppLocalDataSource.deleteDApp(id)
    }

    override suspend fun getListDApp(): List<DAppModel> {
        return dAppLocalDataSource.getListDApp().map { it.mapToDomainModel() as DAppModel }
    }

    override fun getDAppFlow(id: String): Flow<DAppModel?> {
        return dAppLocalDataSource.getDAppFlow(id).map { entity ->
            entity?.toModel()
        }
    }

    override fun getListDAppFlow(): Flow<List<DAppModel>> {
        return dAppLocalDataSource.getListDAppFlow().map { entities ->
            entities.map { it.toModel() }
        }
    }

    override suspend fun getDappsJson(): ApiResponse<String, CustomError> {
        return categoryDAppRemoteDataSource.getDappsJson()
    }

    private fun DAppEntity.toModel(): DAppModel {
        return DAppModel(
            uuid = this.uuid,
            title = this.title ?: "",
            description = this.description ?: "",
            iconUrl = this.iconUrl ?: "",
            bannerUrl = this.bannerUrl ?: "",
            redirectLink = this.redirectLink ?: "",
            chainId = this.chainId ?: ""
        )
    }
}

