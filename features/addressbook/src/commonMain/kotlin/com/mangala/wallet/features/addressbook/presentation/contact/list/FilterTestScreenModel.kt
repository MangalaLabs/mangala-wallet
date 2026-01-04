package com.mangala.wallet.features.addressbook.presentation.contact.list

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.features.addressbook.data.model.contact.ContactEntity
import com.mangala.wallet.features.addressbook.data.model.enum.DisplayMode
import com.mangala.wallet.features.addressbook.data.model.setting.UserSettingEntity
import com.mangala.wallet.features.addressbook.domain.repository.contact.ContactRepository
import com.mangala.wallet.features.addressbook.domain.repository.tag.TagRepository
import com.mangala.wallet.features.addressbook.domain.repository.group.GroupRepository
import com.mangala.wallet.features.addressbook.domain.repository.blockchain.BlockchainRepository
import com.mangala.wallet.features.addressbook.domain.repository.contact.WalletAddressRepository
import com.mangala.wallet.features.addressbook.domain.repository.transaction.TransactionRepository
import com.mangala.wallet.features.addressbook.domain.usecase.setting.SaveUserSettingUseCase
import com.mangala.wallet.utils.localDateTimeNow
import com.mangala.wallet.utils.localDateTimeToMillis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant

//TODO: for test
class FilterTestScreenModel(
    private val contactRepository: ContactRepository,
    private val tagRepository: TagRepository,
    private val groupRepository: GroupRepository,
    private val blockchainRepository: BlockchainRepository,
    private val walletAddressRepository: WalletAddressRepository,
    private val saveUserSettingUseCase: SaveUserSettingUseCase,
    private val transactionHistoryRepository: TransactionRepository
) : ScreenModel {

    // Add these state flows to track filter state
    private val _filteredContacts = MutableStateFlow<List<ContactEntity>>(emptyList())
    val filteredContacts: StateFlow<List<ContactEntity>> = _filteredContacts.asStateFlow()


    init {
        saveUserSetting()
    }

    private val testDataGenerator = TestDataGenerator(
        contactRepository,
        tagRepository,
        groupRepository,
        walletAddressRepository,
        blockchainRepository,
        transactionHistoryRepository
    )

    fun generateTestData(count: Int = 1000) {
        screenModelScope.launch {
            testDataGenerator.generateTestData(count)
        }
    }

    fun generate200Groups() {
        screenModelScope.launch {
            testDataGenerator.generateLargeGroupDataset()
        }
    }

    fun generate50Transactions() {
        screenModelScope.launch {
            println("Generating 50 transactions...")
            // First ensure we have some contacts (generate minimal set if needed)
            val existingContacts = contactRepository.getAllContacts().first()
            if (existingContacts.isEmpty() || existingContacts.size < 10) {
                println("Not enough contacts, generating 10 contacts first...")
                testDataGenerator.generateTestData(10)
            }
            // Generate 50 transactions for existing contacts
            testDataGenerator.generateTransactionsForExistingContacts(50)
            println("Transaction generation completed!")
        }
    }

    suspend fun loadTags(): List<Pair<String, String>> = withContext(Dispatchers.IO) {
        tagRepository.getActiveTags().map { it.id to it.name }
    }

    suspend fun loadGroups(): List<Pair<String, String>> = withContext(Dispatchers.IO) {
        groupRepository.getAllGroups().map { it.id to it.name }
    }

    suspend fun loadBlockchains(): List<Pair<String, String>> = withContext(Dispatchers.IO) {
        blockchainRepository.getAllBlockchainTypes().map { it.id to it.name }
    }

    fun clearFilter() {
        _filteredContacts.value = emptyList()
    }

    fun testFilter(
        query: String = "",
        tagIds: List<String> = emptyList(),
        groupIds: List<String> = emptyList(),
        blockchainIds: List<String> = emptyList(),
        onlyFavorites: Boolean = false,
        sortOrder: String = "name_asc",
        limit: Int = 50,
        offset: Int = 0
    ) {

        // Use screenModelScope to manage coroutine
//        screenModelScope.launch(Dispatchers.IO) {
//            try {
//                println("FilterTestScreenModel: testFilter started with parameters:")
//                println("- Query: $query")
//                println("- TagIds: $tagIds")
//                println("- GroupIds: $groupIds")
//                println("- BlockchainIds: $blockchainIds")
//                println("- OnlyFavorites: $onlyFavorites")
//                println("- SortOrder: $sortOrder")
//        println("FilterTestScreenModel: testFilter() called with query: $query, tagIds: $tagIds, groupIds: $groupIds, blockchainIds: $blockchainIds, onlyFavorites: $onlyFavorites, sortOrder: $sortOrder, limit: $limit, offset: $offset")
//        val tag = tagRepository.getActiveTags().map { it.id to it.name }
//
//        val contact = contactRepository.getContactsByTags(
//            tagIds = tagIds,
//            0,
//            10
//        );
//
//        println("FilterTestScreenModel: testFilter() contact: $contact")
//
//        val groupContact = contactRepository.getContactByGroupIdIn(
//            groupIds = groupIds,
//            limit = 0,
//            offset = 0
//        )
//
//        println("FilterTestScreenModel: testFilter() groupContact: $groupContact")
//
//        contactRepository.filterContacts(
//            query = query,
//            tagIds = tagIds,
//            groupIds = groupIds,
//            blockchainIds = blockchainIds,
//            onlyFavorites = onlyFavorites,
//            sortOrder = sortOrder,
//            limit = limit,
//            offset = offset
//        )

                // Get filter results
//                val results = contactRepository.filterContacts(
//                    query = query,
//                    tagIds = tagIds,
//                    groupIds = groupIds,
//                    blockchainIds = blockchainIds,
//                    onlyFavorites = onlyFavorites,
//                    sortOrder = sortOrder,
//                    limit = limit,
//                    offset = offset
//                )
//
//                println("FilterTestScreenModel: Filter completed with ${results.size} contacts")
//
//                // Update state on the Main thread
//                _filteredContacts.value = results.toList()
//            } catch (e: Exception) {
//                println("FilterTestScreenModel: Error during filtering: ${e.message}")
//                e.printStackTrace()
//
//                // Update error state on the Main thread
//                _filteredContacts.value = emptyList()
//            }
//        }
    }

    private fun saveUserSetting() {
        screenModelScope.launch {
            try {
                val now = Instant.fromEpochMilliseconds(localDateTimeToMillis(localDateTimeNow()))
                // Tạo tham số mặc định cho testing
                val settingEntity = UserSettingEntity(
                    id = "current",
                    privacyModeEnabled = true,
                    defaultPrivacyDisplay = DisplayMode.FULL,
                    biometricAuthEnabled = true,
                    twoFactorAuthEnabled = true,
                    syncEnabled = true,
                    defaultSortOption = "NEWEST",
                    theme = "DARK",
                    reminderSettings = """{"daily":true,"weekly":false,"notifications":true}""",
                    safeZones = """[{"name":"Home","latitude":37.7749,"longitude":-122.4194,"radius":500}]""",
                    createdAt = now,
                    updatedAt = now
                )

                // Gọi use case để lưu setting
                val result = saveUserSettingUseCase(settingEntity)

                // Xử lý kết quả
                result.fold(
                    onSuccess = { success ->
                        if (success) {
                            println("User setting saved successfully with ID: current")
                        } else {
                            println("Failed to save user setting")
                        }
                    },
                    onFailure = { error ->
                        println("Error saving user setting: ${error.message}")
                    }
                )
            } catch (e: Exception) {
                println("Exception while saving user setting: ${e.message}")
            }
        }
    }
//    }
}