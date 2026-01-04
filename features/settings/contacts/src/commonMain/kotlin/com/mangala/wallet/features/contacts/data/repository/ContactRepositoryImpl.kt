package com.mangala.wallet.features.contacts.data.repository

import com.mangala.wallet.features.contacts.data.local.ContactLocalDataSource
import com.mangala.wallet.features.contacts.domain.repository.ContactRepository
import com.mangala.wallet.model.contact.ContactEntity
import kotlinx.coroutines.flow.Flow

class ContactRepositoryImpl(private val contactLocalDataSource: ContactLocalDataSource):
    ContactRepository {

    override suspend fun deleteContactById(id: Long) {
        contactLocalDataSource.deleteContactById(id)
    }

    override suspend fun getContactById(id: Long): ContactEntity? {
        return contactLocalDataSource.getContactById(id)
    }

    override fun getContactByIdFlow(id: Long): Flow<ContactEntity?> {
        return contactLocalDataSource.getContactByIdFlow(id)
    }

    override suspend fun getAllContact(): List<ContactEntity> {
        return contactLocalDataSource.getAllContact()
    }

    override fun getAllContactFlow(): Flow<List<ContactEntity>> {
        return contactLocalDataSource.getAllContactFlow()
    }

    override suspend fun getAllContactsByBlockchainUid(blockchainUid: String): List<ContactEntity> {
        return contactLocalDataSource.getAllContactsByBlockchainUid(blockchainUid)
    }

    override fun getAllContactsByBlockchainUidFlow(blockchainUid: String?): Flow<List<ContactEntity>> {
        return contactLocalDataSource.getAllContactsByBlockchainUidFlow(blockchainUid)
    }

    override fun updateContact(contact: ContactEntity) {
        return contactLocalDataSource.updateContact(contact)
    }

    override fun insertContact(contact: ContactEntity): Long  {
        return contactLocalDataSource.insertContact(contact)
    }

    override suspend fun insertContacts(contacts: List<ContactEntity>) {
        return contactLocalDataSource.insertContacts(contacts)
    }
}