package com.mangala.wallet.features.contacts.data.local

import com.mangala.wallet.model.contact.ContactEntity
import kotlinx.coroutines.flow.Flow

interface ContactLocalDataSource {

    suspend fun deleteContactById(id: Long)

    suspend fun getContactById(id: Long): ContactEntity?

    fun getContactByIdFlow(id: Long): Flow<ContactEntity?>

    suspend fun getAllContact(): List<ContactEntity>
    fun getAllContactFlow(): Flow<List<ContactEntity>>

    suspend fun getAllContactsByBlockchainUid(blockchainUid: String): List<ContactEntity>
    fun getAllContactsByBlockchainUidFlow(blockchainUid: String?): Flow<List<ContactEntity>>

    fun updateContact(contactEntity: ContactEntity)

    fun insertContact(contact: ContactEntity): Long
    suspend fun insertContacts(contacts: List<ContactEntity>)

    suspend fun countCoin(): Long
}