package com.mangala.wallet.features.addressbook.domain.usecase.contact

import com.mangala.wallet.features.addressbook.data.model.blockchain.WalletAddressEntity
import com.mangala.wallet.features.addressbook.data.model.contact.ContactEntity
import com.mangala.wallet.features.addressbook.domain.repository.contact.ContactRepository
import com.mangala.wallet.features.addressbook.domain.repository.contact.WalletAddressRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class CreateContactWithAddressUseCase(
    private val contactRepository: ContactRepository,
    private val walletAddressRepository: WalletAddressRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(
        contactName: String,
        address: String,
        blockchainTypeId: String,
        isPrimary: Boolean = true
    ): Result<String> = withContext(dispatcher) {
        try {
            val contactId = Uuid.random().toString()
            val contactEntity = ContactEntity.create(
                id = contactId,
                name = contactName
            )
            
            val savedContactId = contactRepository.insertContact(contactEntity)
            
            val addressId = Uuid.random().toString()
            val walletAddressEntity = WalletAddressEntity.create(
                id = addressId,
                contactId = savedContactId,
                blockchainTypeId = blockchainTypeId,
                address = address,
                isPrimary = isPrimary
            )
            
            walletAddressRepository.insertWalletAddressesBatch(listOf(walletAddressEntity))
            
            Result.success(savedContactId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend operator fun invoke(
        contact: ContactEntity,
        walletAddresses: List<WalletAddressEntity> = emptyList()
    ): Result<String> = withContext(dispatcher) {
        try {
            val contactId = contactRepository.insertContact(contact)
            
            if (walletAddresses.isNotEmpty()) {
                val addressesWithContactId = walletAddresses.map { address ->
                    address.copy(contactId = contactId)
                }
                walletAddressRepository.insertWalletAddressesBatch(addressesWithContactId)
            }
            
            Result.success(contactId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend operator fun invoke(request: CreateContactRequest): Result<String> = 
        invoke(request.contact, request.walletAddresses)

    data class CreateContactRequest(
        val contact: ContactEntity,
        val walletAddresses: List<WalletAddressEntity> = emptyList()
    )
    
    data class SimpleCreateRequest(
        val contactName: String,
        val address: String,
        val blockchainTypeId: String,
        val isPrimary: Boolean = true
    )
    
    suspend operator fun invoke(request: SimpleCreateRequest): Result<String> = 
        invoke(
            contactName = request.contactName,
            address = request.address,
            blockchainTypeId = request.blockchainTypeId,
            isPrimary = request.isPrimary
        )
}