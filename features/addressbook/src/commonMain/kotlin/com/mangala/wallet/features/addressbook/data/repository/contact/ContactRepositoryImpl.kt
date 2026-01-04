package com.mangala.wallet.features.addressbook.data.repository.contact

import app.cash.paging.ExperimentalPagingApi
import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import app.cash.paging.map
import com.mangala.antelope.base.api.remote.EosRemoteDataSource
import com.mangala.wallet.domain.datastore.repository.DataStoreRepository
import com.mangala.wallet.features.addressbook.data.local.contact.ContactLocalDataSource
import com.mangala.wallet.features.addressbook.data.local.AddressBookDatabaseWrapper
import com.mangala.wallet.features.addressbook.data.local.transaction.AddressBookRecentTxRemoteKeyLocalDataSource
import com.mangala.wallet.features.addressbook.data.local.transaction.TransactionLocalDataSource
import com.mangala.wallet.features.addressbook.data.paging.ReactiveContactPagingSource
import com.mangala.wallet.features.addressbook.data.model.ContactDetailModel
import com.mangala.wallet.features.addressbook.data.model.ContactModel
import com.mangala.wallet.features.addressbook.data.model.ContactRecentTransactionModel
import com.mangala.wallet.features.addressbook.data.model.contact.ContactEntity
import com.mangala.wallet.features.addressbook.data.model.enum.DisplayMode
import com.mangala.wallet.features.addressbook.data.model.contact.EmailAddressEntity
import com.mangala.wallet.features.addressbook.data.model.contact.ImportantDateEntity
import com.mangala.wallet.features.addressbook.data.model.contact.PhoneNumberEntity
import com.mangala.wallet.features.addressbook.data.model.contact.PhysicalAddressEntity
import com.mangala.wallet.features.addressbook.data.model.contact.RelatedNameEntity
import com.mangala.wallet.features.addressbook.data.model.contact.SocialProfileEntity
import com.mangala.wallet.features.addressbook.data.paging.AddressBookRecentTransactionRemoteMediator
import com.mangala.wallet.features.addressbook.domain.repository.blockchain.BlockchainRepository
import com.mangala.wallet.features.addressbook.domain.repository.contact.ContactRepository
import com.mangala.wallet.features.addressbook.domain.model.ContactChangeEvent
import com.mangala.wallet.features.addressbook.domain.repository.contact.WalletAddressRepository
import com.mangala.wallet.features.addressbook.domain.repository.transaction.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.channels.BufferOverflow
import com.mangala.wallet.features.addressbook.util.ChangeEventEmitter
import com.mangala.wallet.features.chains.antelope_base.data.local.account.AccountLocalDataSource
import com.mangala.wallet.features.chains.antelope_base.domain.model.actions.ActionId
import com.mangala.wallet.utils.ext.toBoolean

class ContactRepositoryImpl(
    private val localDataSource: ContactLocalDataSource,
    private val addressBookTransactionLocalDataSource: TransactionLocalDataSource,
    private val antelopeAccountLocalDataSource: AccountLocalDataSource,
    private val addressBookRecentTxRemoteKeyLocalDataSource: AddressBookRecentTxRemoteKeyLocalDataSource,
    private val eosRemoteDataSource: EosRemoteDataSource,
    private val blockchainRepository: BlockchainRepository,
    private val walletAddressRepository: WalletAddressRepository,
    private val transactionRepository: TransactionRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val databaseWrapper: AddressBookDatabaseWrapper,
) : ContactRepository {
    
    // Tạo một MutableSharedFlow để phát các thông báo khi liên hệ thay đổi
    private val _contactChangesFlow = MutableSharedFlow<ContactChangeEvent>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    
    // Expose as public read-only flow
    override val contactChanges: Flow<ContactChangeEvent> = _contactChangesFlow.asSharedFlow()
    
    private val contactEventEmitter = ChangeEventEmitter(_contactChangesFlow, "ContactRepository")

    companion object {
        private const val RECENT_TRANSACTIONS_PAGE_SIZE = 20
        private const val CONTACTS_PAGE_SIZE = 20
    }

    override suspend fun getContactById(id: String): ContactEntity? {
        return localDataSource.getContactById(id)
    }

    override suspend fun getContactDetailById(id: String): ContactDetailModel? {
        val contact = localDataSource.getContactById(id) ?: return null

        return ContactDetailModel(
            contact = contact,
            phoneNumbers = localDataSource.getPhoneNumbersByContactId(id),
            emailAddresses = localDataSource.getEmailAddressesByContactId(id),
            physicalAddresses = localDataSource.getPhysicalAddressesByContactId(id),
            relatedNames = localDataSource.getRelatedNamesByContactId(id),
            importantDates = localDataSource.getImportantDatesByContactId(id),
            socialProfiles = localDataSource.getSocialProfilesByContactId(id),
            walletAddresses = blockchainRepository.getWalletAddressesWithBlockchainByContactId(id), // FIXED!
            isFavorite = localDataSource.isContactFavorite(id),
            tags = localDataSource.getTagsByContactId(id),
        )
    }

    override fun getAllContacts(
        limit: Int,
        offset: Int,
        sortOrder: String,
    ): Flow<List<ContactEntity>> {
        return localDataSource.getAllContacts(limit, offset, sortOrder)
    }

    override fun searchContacts(query: String, limit: Int, offset: Int): Flow<List<ContactEntity>> {
        return localDataSource.searchContacts(query, limit, offset)
    }

    override suspend fun searchContacts(query: String): List<ContactEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun insertContact(contact: ContactEntity): String {
        val id = localDataSource.insertContact(contact)
        
        // Thông báo sự thay đổi sau khi tạo contact thành công
        contactEventEmitter.emit(
            ContactChangeEvent.Created,
            "Notified contacts changed after inserting contact: $id"
        )
        
        return id
    }

    override suspend fun updateContact(contact: ContactEntity): Boolean {
        val result = localDataSource.updateContact(contact)

        // Thông báo sự thay đổi nếu cập nhật thành công
        if (result) {
            // Emit vào contactChangesFlow để thông báo dữ liệu đã thay đổi
            contactEventEmitter.emit(
                ContactChangeEvent.Updated,
                "Notified contacts changed after updating contact: ${contact.id}"
            )
        }

        return result
    }

    override suspend fun deleteContact(id: String): Boolean {
        val result = localDataSource.deleteContact(id)
        
        // Thông báo sự thay đổi nếu xóa thành công
        if (result) {
            contactEventEmitter.emit(
                ContactChangeEvent.Deleted,
                "Notified contacts changed after deleting contact: $id"
            )
        }
        
        return result
    }

    override suspend fun updateLastViewedAt(id: String): Boolean {
        return localDataSource.updateLastViewedAt(id)
    }

    override suspend fun getPhoneNumbersByContactId(contactId: String): List<PhoneNumberEntity> {
        return localDataSource.getPhoneNumbersByContactId(contactId)
    }

    override suspend fun insertPhoneNumber(phoneNumber: PhoneNumberEntity): String {
        return localDataSource.insertPhoneNumber(phoneNumber)
    }

    override suspend fun updatePhoneNumber(phoneNumber: PhoneNumberEntity): Boolean {
        return localDataSource.updatePhoneNumber(phoneNumber)
    }

    override suspend fun deletePhoneNumber(id: String): Boolean {
        return localDataSource.deletePhoneNumber(id)
    }

    override suspend fun getEmailAddressesByContactId(contactId: String): List<EmailAddressEntity> {
        return localDataSource.getEmailAddressesByContactId(contactId)
    }

    override suspend fun insertEmailAddress(emailAddress: EmailAddressEntity): String {
        return localDataSource.insertEmailAddress(emailAddress)
    }

    override suspend fun updateEmailAddress(emailAddress: EmailAddressEntity): Boolean {
        return localDataSource.updateEmailAddress(emailAddress)
    }

    override suspend fun deleteEmailAddress(id: String): Boolean {
        return localDataSource.deleteEmailAddress(id)
    }

    override suspend fun getPhysicalAddressesByContactId(contactId: String): List<PhysicalAddressEntity> {
        return localDataSource.getPhysicalAddressesByContactId(contactId)
    }

    override suspend fun insertPhysicalAddress(physicalAddress: PhysicalAddressEntity): String {
        return localDataSource.insertPhysicalAddress(physicalAddress)
    }

    override suspend fun updatePhysicalAddress(physicalAddress: PhysicalAddressEntity): Boolean {
        return localDataSource.updatePhysicalAddress(physicalAddress)
    }

    override suspend fun deletePhysicalAddress(id: String): Boolean {
        return localDataSource.deletePhysicalAddress(id)
    }

    override suspend fun getRelatedNamesByContactId(contactId: String): List<RelatedNameEntity> {
        return localDataSource.getRelatedNamesByContactId(contactId)
    }

    override suspend fun insertRelatedName(relatedName: RelatedNameEntity): String {
        return localDataSource.insertRelatedName(relatedName)
    }

    override suspend fun updateRelatedName(relatedName: RelatedNameEntity): Boolean {
        return localDataSource.updateRelatedName(relatedName)
    }

    override suspend fun deleteRelatedName(id: String): Boolean {
        return localDataSource.deleteRelatedName(id)
    }

    override suspend fun getImportantDatesByContactId(contactId: String): List<ImportantDateEntity> {
        return localDataSource.getImportantDatesByContactId(contactId)
    }

    override suspend fun insertImportantDate(importantDate: ImportantDateEntity): String {
        return localDataSource.insertImportantDate(importantDate)
    }

    override suspend fun updateImportantDate(importantDate: ImportantDateEntity): Boolean {
        return localDataSource.updateImportantDate(importantDate)
    }

    override suspend fun deleteImportantDate(id: String): Boolean {
        return localDataSource.deleteImportantDate(id)
    }

    override suspend fun isContactFavorite(contactId: String): Boolean {
        return localDataSource.isContactFavorite(contactId)
    }

    override suspend fun addFavorite(contactId: String): Boolean {
        val result = localDataSource.addFavorite(contactId)

        // Thông báo sự thay đổi nếu thêm yêu thích thành công
        if (result) {
            contactEventEmitter.emit(
                ContactChangeEvent.Updated,
                "Notified contacts changed after adding favorite: $contactId"
            )
        }

        return result
    }

    override suspend fun removeFavorite(contactId: String): Boolean {
        val result = localDataSource.removeFavorite(contactId)

        // Thông báo sự thay đổi nếu xóa yêu thích thành công
        if (result) {
            contactEventEmitter.emit(
                ContactChangeEvent.Updated,
                "Notified contacts changed after removing favorite: $contactId"
            )
        }

        return result
    }

//    override suspend fun getRecentContacts(limit: Int): List<ContactEntity> {
//        return localDataSource.getRecentContacts(limit)
//    }

    override suspend fun countAllContacts(): Int {
        return localDataSource.countAllContacts()
    }

    override suspend fun findContactByEmail(email: String): ContactEntity? {
        return localDataSource.findContactByEmail(email)
    }

    override suspend fun findContactByPhoneNumber(phoneNumber: String): ContactEntity? {
        return localDataSource.findContactByPhoneNumber(phoneNumber)
    }
    
    override suspend fun findContactByName(name: String): ContactEntity? {
        return localDataSource.findContactByName(name)
    }

    override suspend fun insertEmailAddressesBatch(emailAddresses: List<EmailAddressEntity>): Map<EmailAddressEntity, String> {
        return localDataSource.insertEmailAddressesBatch(emailAddresses)
    }

    override suspend fun insertPhoneNumbersBatch(phoneNumbers: List<PhoneNumberEntity>): Map<PhoneNumberEntity, String> {
        return localDataSource.insertPhoneNumbersBatch(phoneNumbers)
    }

    override suspend fun insertPhysicalAddressesBatch(physicalAddresses: List<PhysicalAddressEntity>): Map<PhysicalAddressEntity, String> {
        return localDataSource.insertPhysicalAddressesBatch(physicalAddresses)
    }

    override suspend fun insertRelatedNamesBatch(relatedNames: List<RelatedNameEntity>): Map<RelatedNameEntity, String> {
        return localDataSource.insertRelatedNamesBatch(relatedNames)
    }

    override suspend fun insertImportantDatesBatch(importantDates: List<ImportantDateEntity>): Map<ImportantDateEntity, String> {
        return localDataSource.insertImportantDatesBatch(importantDates)
    }

    override suspend fun insertSocialProfilesBatch(socialProfiles: List<SocialProfileEntity>): Map<SocialProfileEntity, String> {
        return localDataSource.insertSocialProfilesBatch(socialProfiles)
    }

    override suspend fun deleteSocialProfilesByContactId(contactId: String): Boolean {
        return localDataSource.deleteSocialProfilesByContactId(contactId)
    }

    override suspend fun filterContacts(
        query: String,
        tagIds: List<String>,
        groupIds: List<String>,
        blockchainIds: List<String>,
        onlyFavorites: Boolean,
        sortOrder: String,
        limit: Int,
        offset: Int,
    ): List<ContactModel> {
        return localDataSource.filterContacts(
            query = query,
            tagIds = tagIds,
            groupIds = groupIds,
            blockchainIds = blockchainIds,
            onlyFavorites = onlyFavorites,
            sortOrder = sortOrder,
            limit = limit,
            offset = offset,
        )
    }

    override suspend fun getContactByGroupIdIn(
        groupIds: List<String>,
        limit: Int,
        offset: Int,
    ): List<ContactEntity> {
        return localDataSource.getContactsByGroupIdIn(groupIds)
    }

    override suspend fun getFavoriteContactsFlow(
        limit: Int,
        offset: Int,
    ): Flow<List<ContactModel>> = localDataSource.getFavoriteContactsFlow(limit, offset)

    override fun observeContacts(): Flow<List<ContactEntity>> {
        return flow {
            // Kết hợp Flow từ database với flow thông báo thay đổi
            // Phát dữ liệu ban đầu
            emit(localDataSource.getAllContacts(limit = 1000).first())

            // Theo dõi cả thay đổi từ database và thông báo thay đổi thủ công
            kotlinx.coroutines.flow.merge(
                localDataSource.getAllContacts(limit = 1000),
                _contactChangesFlow.map { localDataSource.getAllContacts(limit = 1000).first() }
            ).collect {
                emit(it)
            }
        }
    }

    override fun observeContactById(id: String): Flow<ContactEntity?> {
        // Sử dụng Flow để quan sát sự thay đổi trên một liên hệ cụ thể
        return localDataSource.observeContactById(id)
    }

    @ExperimentalPagingApi
    override fun getPaginatedContactRecentTransactions(
        searchQuery: String?,
        statuses: List<String>,
    ): Flow<PagingData<ContactRecentTransactionModel>> {
            return Pager(
                config = PagingConfig(
                    pageSize = RECENT_TRANSACTIONS_PAGE_SIZE,
                    prefetchDistance = RECENT_TRANSACTIONS_PAGE_SIZE / 2,
                    initialLoadSize = RECENT_TRANSACTIONS_PAGE_SIZE,
                    enablePlaceholders = false,
                ),
                pagingSourceFactory = {
                    localDataSource.getContactRecentTransactionPagingSource(
                        searchQuery = searchQuery,
                        statuses = statuses
                    )
                },
                remoteMediator = AddressBookRecentTransactionRemoteMediator(
                    queryString = searchQuery,
                    remoteDataSource = eosRemoteDataSource,
                    localDataSource = addressBookTransactionLocalDataSource,
                    antelopeAccountLocalDataSource = antelopeAccountLocalDataSource,
                    remoteKeyDataSource = addressBookRecentTxRemoteKeyLocalDataSource,
                    blockchainRepository = blockchainRepository,
                    walletAddressRepository = walletAddressRepository,
                    transactionRepository = transactionRepository,
                    dataStoreRepository = dataStoreRepository,
                    filter = ActionId.ALL_TOKEN_TRANSFER,
                    sort = "desc",
                )
            ).flow.map { pagingData ->
                pagingData.map { recentContact ->
                    ContactRecentTransactionModel(
                        contactId = recentContact.contactId,
                        contactName = recentContact.contactName,
                        walletAddress = recentContact.walletAddress,
                        blockchainUid = recentContact.blockchain_type_id,
                        blockchainName = recentContact.blockchainName,
                        blockchainSymbol = recentContact.blockchainSymbol,
                        blockchainIcon = recentContact.blockchainIcon,
                        lastTransactionTime = recentContact.lastTransactionTime,
                        lastTransactionAmount = recentContact.lastTransactionAmount,
                        lastTokenSymbol = recentContact.lastTokenSymbol,
                        transactionStatus = recentContact.transactionStatus,
                        isSender = recentContact.isSender.toBoolean(),
                        isFavorite = recentContact.isFavorite.toBoolean(),
                        walletAddressId = recentContact.walletAddressId,
                        transactionId = recentContact.transactionId,
                        walletAlias = recentContact.walletAlias,
                        walletSensitive = recentContact.isSensitive.toBoolean(),
                        avatar = recentContact.avatar,
                        memo = recentContact.transactionNote ?: "",
                        privacyDisplayMode = DisplayMode.fromString(recentContact.privacyDisplayMode)
                    )
                }
            }
        }

    override fun getPaginatedContacts(
        searchQuery: String?,
        tagIds: List<String>?,
        checkTagId: String?,
        isFavoriteOnly: Boolean
    ): Flow<PagingData<ContactModel>> =
        Pager(
            config = PagingConfig(
                pageSize = CONTACTS_PAGE_SIZE,
                prefetchDistance = CONTACTS_PAGE_SIZE / 2,
                initialLoadSize = CONTACTS_PAGE_SIZE,
            ),
            pagingSourceFactory = {
                // Use ReactiveContactPagingSource that listens to changes
                ReactiveContactPagingSource(
                    dbQuery = databaseWrapper.database.addressBookDatabaseQueries,
                    searchQuery = searchQuery,
                    tagIds = tagIds,
                    isFavoriteOnly = isFavoriteOnly,
                    contactChanges = contactChanges,
                    checkTagId = checkTagId
                )
            }
        ).flow

    /**
     * Thông báo rằng dữ liệu liên hệ đã thay đổi
     * Hữu ích khi repository không tự động phát ra cập nhật
     */
    override suspend fun notifyContactsChanged(): Boolean {
        return try {
            _contactChangesFlow.emit(ContactChangeEvent.Updated)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Quan sát danh sách liên hệ đã lọc theo tiêu chí cụ thể
     * Khi có thay đổi, tự động phát ra kết quả mới
     */
    override fun observeFilteredContacts(
        query: String,
        tagIds: List<String>,
        groupIds: List<String>,
        blockchainIds: List<String>,
        onlyFavorites: Boolean,
        sortOrder: String,
        limit: Int,
        offset: Int,
    ): Flow<Result<List<ContactModel>>> = flow {
        // Phát ra kết quả đầu tiên
        try {
            val initialContacts = localDataSource.filterContacts(
                query = query,
                tagIds = tagIds,
                groupIds = groupIds,
                blockchainIds = blockchainIds,
                onlyFavorites = onlyFavorites,
                sortOrder = sortOrder,
                limit = limit,
                offset = offset
            )
            emit(Result.success(initialContacts))

            // Theo dõi sự thay đổi trong danh sách liên hệ
            localDataSource.getAllContacts(limit = 1000).collect {
                // Khi phát hiện thay đổi, cập nhật lại danh sách đã lọc
                val updatedContacts = localDataSource.filterContacts(
                    query = query,
                    tagIds = tagIds,
                    groupIds = groupIds,
                    blockchainIds = blockchainIds,
                    onlyFavorites = onlyFavorites,
                    sortOrder = sortOrder,
                    limit = limit,
                    offset = offset
                )
                emit(Result.success(updatedContacts))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun clearAllContacts(): Boolean {
        return localDataSource.clearAllContacts()
    }

}