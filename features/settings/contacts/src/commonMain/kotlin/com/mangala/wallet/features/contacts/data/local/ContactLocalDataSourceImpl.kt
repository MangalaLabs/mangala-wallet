package com.mangala.wallet.features.contacts.data.local

import app.cash.sqldelight.coroutines.asFlow
import com.mangala.wallet.local.MangalaWalletDatabaseWrapper
import com.mangala.wallet.model.contact.ContactEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ContactLocalDataSourceImpl(
    databaseWrapper: MangalaWalletDatabaseWrapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) :
    ContactLocalDataSource {

    private val database = databaseWrapper.instance
    private val dbQuery = database.mangalaWalletDatabaseQueries


    override suspend fun deleteContactById(id: Long) = withContext(ioDispatcher) {
        dbQuery.transaction {
            dbQuery.deleteContact(id)
        }
    }

    override suspend fun getContactById(id: Long): ContactEntity? = withContext(ioDispatcher) {
        return@withContext dbQuery.selectContactById(id, ::mapContact).executeAsList().firstOrNull()
    }

    override fun getContactByIdFlow(id: Long): Flow<ContactEntity?> {
        return dbQuery.selectContactById(id, ::mapContact).asFlow().map { it.executeAsOneOrNull() }.flowOn(ioDispatcher)
    }

    override suspend fun getAllContact(): List<ContactEntity> = withContext(ioDispatcher) {
        return@withContext dbQuery.getAllContacts(::mapContact).executeAsList()
    }

    override fun getAllContactFlow(): Flow<List<ContactEntity>> {
        return dbQuery.getAllContacts(::mapContact).asFlow().map { it.executeAsList() }.flowOn(ioDispatcher)
    }

    override suspend fun getAllContactsByBlockchainUid(blockchainUid: String): List<ContactEntity> = withContext(ioDispatcher) {
        return@withContext dbQuery.getAllContactsByBlockchainUid(blockchainUid, ::mapContact).executeAsList()
    }

    override fun getAllContactsByBlockchainUidFlow(blockchainUid: String?): Flow<List<ContactEntity>> {
        return dbQuery.getAllContactsByBlockchainUid(blockchainUid, ::mapContact).asFlow().map { it.executeAsList() }.flowOn(ioDispatcher)
    }

    override fun updateContact(contactEntity: ContactEntity) {
        dbQuery.transaction {
            dbQuery.updateContact(
                contactEntity.name,
                contactEntity.blockchainUid,
                contactEntity.address,
                contactEntity.id
            )
        }
    }

    override fun insertContact(contact: ContactEntity): Long {
        return dbQuery.transactionWithResult {
            insert(contact)
            dbQuery.lastInsertedRowId().executeAsOne()
        }
    }

    override suspend fun countCoin(): Long = withContext(ioDispatcher) {
        dbQuery.countToken().executeAsOne().let { count ->
            return@let count
        }
    }

    override suspend fun insertContacts(contacts: List<ContactEntity>) = withContext(ioDispatcher) {
        dbQuery.transaction {
            contacts.forEach { contact ->
                insert(contact)
            }
        }
    }

    private fun mapContact(
        id: Long?,
        name: String?,
        blockchainUid: String?,
        address: String?
    ): ContactEntity {
        return ContactEntity(
            id ?: 0,
            name ?: "",
            blockchainUid ?: "",
            address ?: ""
        )
    }

    private fun insert(contactEntity: ContactEntity) {
        dbQuery.insertContact(
            contactEntity.name,
            contactEntity.blockchainUid,
            contactEntity.address
        )
    }
}