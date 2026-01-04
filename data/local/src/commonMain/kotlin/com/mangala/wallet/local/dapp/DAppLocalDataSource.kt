package com.mangala.wallet.local.dapp

import com.mangala.wallet.model.category_dapp.remote.DAppRemote
import com.mangala.wallet.model.category_dapp.local.DAppEntity
import kotlinx.coroutines.flow.Flow

interface DAppLocalDataSource {

    suspend fun getDApp(id: String): DAppEntity?

    suspend fun saveDApp(dApp: DAppRemote)

    suspend fun deleteDApp(id: String)

    suspend fun getListDApp(): List<DAppEntity>

    fun getDAppFlow(id: String): Flow<DAppEntity?>

    fun getListDAppFlow(): Flow<List<DAppEntity>>
}