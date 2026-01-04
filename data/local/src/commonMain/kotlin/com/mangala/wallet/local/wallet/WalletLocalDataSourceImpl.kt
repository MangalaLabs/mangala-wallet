package com.mangala.wallet.local.wallet

import app.cash.sqldelight.coroutines.asFlow
import com.mangala.wallet.local.MangalaWalletDatabaseWrapper
import com.mangala.wallet.local.securestorage.SecureStorageWrapper
import com.mangala.wallet.model.wallet.domain.WalletModel
import com.mangala.wallet.model.wallet.local.WalletEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class WalletLocalDataSourceImpl(
    databaseWrapper: MangalaWalletDatabaseWrapper,
    private val secureStorageWrapper: SecureStorageWrapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : WalletLocalDataSource {

    private val database = databaseWrapper.instance
    private val dbQuery = database.mangalaWalletDatabaseQueries

    override suspend fun setSelectedWallet(walletId: String) = withContext(ioDispatcher){
        dbQuery.transaction {
            dbQuery.clearSelectedWallet()
            dbQuery.setSelectedWallet(walletId)
        }
    }

    override fun getSelectedWallet(): WalletEntity? {
        return dbQuery.getSelectedWallet(::mapWalletSelecting).executeAsOneOrNull()
    }

    override fun getSelectedWalletFlow(): Flow<WalletEntity?> {
        return dbQuery.getSelectedWallet(::mapWalletSelecting).asFlow()
            .map { it.executeAsOneOrNull() }.flowOn(ioDispatcher)
    }

    override suspend fun getAllWallets(): List<WalletEntity> = withContext(ioDispatcher) {
        return@withContext dbQuery.getAllWallets(::mapWalletSelecting).executeAsList()
    }

    override suspend fun getWalletById(id: String): WalletModel? = withContext(ioDispatcher) {
        val walletDbEntity = dbQuery.getWalletById(id).executeAsOneOrNull()
        return@withContext walletDbEntity?.let {
            mapWalletSelecting(
                it.id,
                it.name,
                it.words,
                it.passphrase,
                it.key,
                it.is_backed_up,
                it.is_selected
            )
        }?.mapToDomainModel()
    }

    override fun getWalletByIdFlow(id: String): Flow<WalletModel?> {
        return dbQuery.getWalletById(id).asFlow().map { it.executeAsOneOrNull() }
            .map { walletDbEntity ->
                walletDbEntity?.let {
                    mapWalletSelecting(
                        it.id,
                        it.name,
                        it.words,
                        it.passphrase,
                        it.key,
                        it.is_backed_up,
                        it.is_selected
                    )
                }?.mapToDomainModel()
            }.flowOn(ioDispatcher)
    }

    override suspend fun insertWallet(walletEntity: WalletEntity) = withContext(ioDispatcher) {
        with(walletEntity) {
            secureStorageWrapper.saveValue("wallet_${id}_words", words.orEmpty())

            dbQuery.transaction {
                if (isSelected == true) dbQuery.clearSelectedWallet()
                dbQuery.insertWallet(
                    id = id,
                    name = name,
                    words = "",
                    passphrase = "",
                    key = "",
                    is_backed_up = if (isBackedUp == true) 1 else 0,
                    is_selected = if (isSelected == true) 1 else 0
                )
            }
        }
    }

    override suspend fun updateWallet(walletEntity: WalletEntity) = withContext(ioDispatcher) {
        // TODO: Remember to clear the selected wallet if the updated wallet is selected
        TODO("Not yet implemented")
    }

    override suspend fun deleteWallet(id: String) = withContext(ioDispatcher) {
        dbQuery.transaction {
            dbQuery.deleteWallet(id)
        }
    }

    override suspend fun deleteAllWallets() = withContext(ioDispatcher) {
        dbQuery.transaction {
            dbQuery.deleteAllWallets()
        }
    }

    override suspend fun saveWalletName(id: String, walletName: String) = withContext(ioDispatcher) {
        dbQuery.updateWalletName(walletName, id)
    }

    private fun mapWalletSelecting(
        id: String,
        name: String?,
        words: String?,
        passphrase: String?,
        key: String?,
        isBackedUp: Long?,
        isSelected: Long?
    ): WalletEntity {
        val decryptedWords = secureStorageWrapper.getValue("wallet_${id}_words")

        return WalletEntity(
            id,
            name,
            decryptedWords,
            passphrase,
            key,
            isBackedUp == 1L,
            isSelected == 1L
        )
    }
}