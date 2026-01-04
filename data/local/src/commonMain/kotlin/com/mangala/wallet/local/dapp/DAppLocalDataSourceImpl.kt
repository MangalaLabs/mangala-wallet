package com.mangala.wallet.local.dapp

import com.mangala.wallet.local.MangalaWalletDatabaseWrapper
import com.mangala.wallet.model.category_dapp.remote.DAppRemote
import com.mangala.wallet.model.category_dapp.local.DAppEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class DAppLocalDataSourceImpl(
    databaseWrapper: MangalaWalletDatabaseWrapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : DAppLocalDataSource {
    private val database = databaseWrapper.instance
    private val dbQuery = database.mangalaWalletDatabaseQueries

    override suspend fun getDApp(id: String): DAppEntity? = withContext(ioDispatcher) {
        val dAppEntity = dbQuery.selectDAppByUUID(id).executeAsOneOrNull()
        return@withContext dAppEntity?.let {
            mapToDAppEntity(
                it.uuid,
                it.title ?: "",
                it.description ?: "",
                it.iconUrl ?: "",
                it.bannerUrl ?: "",
                it.chainId ?: "",
                it.redirectLink ?: ""
            )
        }
    }

    override suspend fun saveDApp(dApp: DAppRemote) = withContext(ioDispatcher) {
        val dAppEntity = mapToDAppEntity(
            dApp.uuid, dApp.title ?: "", dApp.description ?: "", dApp.iconUrl ?: "", dApp.bannerUrl ?: "", dApp.chainId ?: "", dApp.redirectLink ?: ""
        )
        dbQuery.insertDApp(dAppEntity.uuid, dAppEntity.title, dAppEntity.description, dAppEntity.iconUrl, dAppEntity.bannerUrl, dAppEntity.chainId, dApp.redirectLink)
    }


    override suspend fun deleteDApp(id: String) = withContext(ioDispatcher) {
        dbQuery.deleteByUUID(id)
    }


    override suspend fun getListDApp(): List<DAppEntity> = withContext(ioDispatcher) {
        return@withContext dbQuery.selectAllDApp().executeAsList().map {
            mapToDAppEntity(
                it.uuid, it.title ?: "", it.description ?: "", it.iconUrl ?: "", it.bannerUrl ?: "", it.chainId ?: "", it.redirectLink ?: ""
            )
        }
    }

    override fun getDAppFlow(id: String): Flow<DAppEntity?> = flowOf(
        dbQuery.selectDAppByUUID(id).executeAsOneOrNull()?.let {
            mapToDAppEntity(it.uuid, it.title ?: "", it.description ?: "", it.iconUrl ?: "", it.bannerUrl ?: "", it.chainId ?: "", it.redirectLink ?: "")
        }
    ).flowOn(ioDispatcher)

    override fun getListDAppFlow(): Flow<List<DAppEntity>> = flowOf(
        dbQuery.selectAllDApp().executeAsList().map {
            mapToDAppEntity(it.uuid, it.title ?: "", it.description ?: "", it.iconUrl ?: "", it.bannerUrl ?: "", it.chainId ?: "", it.redirectLink ?: "")
        }
    ).flowOn(ioDispatcher)

    private fun mapToDAppEntity(
        uuid: String, title: String, description: String, iconUrl: String, bannerUrl: String, chainId: String, redirectLink: String
    ): DAppEntity {
        return DAppEntity(
            uuid, title, description, iconUrl, bannerUrl, chainId, redirectLink
        )
    }
}
