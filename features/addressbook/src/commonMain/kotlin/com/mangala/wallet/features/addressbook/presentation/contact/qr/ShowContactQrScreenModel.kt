package com.mangala.wallet.features.addressbook.presentation.contact.qr

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import com.mangala.wallet.features.addressbook.data.model.ContactModel
import com.mangala.wallet.features.addressbook.data.model.group.GroupModel
import com.mangala.wallet.features.addressbook.data.model.blockchain.WalletAddressEntity
import com.mangala.wallet.features.addressbook.domain.usecase.contact.FilterContactsUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.FilterCriteria
import com.mangala.wallet.features.addressbook.domain.usecase.contact.GetContactByIdUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.GetContactDetailByIdUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.group.GetGroupByIdUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.group.GetGroupDetailByIdUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.wallet_address.GetWalletAddressesForContactUseCase
import com.mangala.wallet.features.addressbook.data.model.ContactDetailModel
import com.mangala.wallet.features.addressbook.data.model.GroupDetailModel
import com.mangala.wallet.features.addressbook.data.model.contact.ContactEntity
import com.mangala.wallet.features.addressbook.data.model.group.GroupEntity
import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.features.addressbook.data.model.tag.TagEntity
import com.mangala.wallet.scanqr.QRCodeGenerator
import kotlinx.coroutines.delay
import org.koin.core.component.inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted

data class QrDisplayData(
    val title: String,
    val subtitle: String,
    val address: String?,
    val symbol: String?,
    val icon: String?,
    val data: Any
)

sealed class QrDataType {
    data class Contact(val contactId: String) : QrDataType()
    data class ContactWithData(val contactModel: ContactModel) : QrDataType()
    data class Address(val addressId: String) : QrDataType()
    data class AddressWithData(val addressEntity: WalletAddressEntity) : QrDataType()
    data class Group(val groupId: String) : QrDataType()
    data class GroupWithData(val groupModel: GroupModel) : QrDataType()
    data class Tag(val tagId: String) : QrDataType()
    data class TagWithData(val tagEntity: com.mangala.wallet.features.addressbook.data.model.tag.TagEntity) : QrDataType()
}

class ShowContactQrScreenModel(
    private val dataType: QrDataType,
    private val qrGenerator: QRCodeGenerator
) : ScreenModel, KoinComponent {
    
    // Inject use cases
    private val filterContactsUseCase: FilterContactsUseCase by inject()
    private val getContactDetailByIdUseCase: GetContactDetailByIdUseCase by inject()
    private val getGroupByIdUseCase: GetGroupByIdUseCase by inject()
    private val getGroupDetailByIdUseCase: GetGroupDetailByIdUseCase by inject()
    private val tagRepository: com.mangala.wallet.features.addressbook.domain.repository.tag.TagRepository by inject()
    private val walletAddressRepository: com.mangala.wallet.features.addressbook.domain.repository.contact.WalletAddressRepository by inject()

    private val _qrDisplayData = MutableStateFlow<QrDisplayData?>(null)
    val qrDisplayData: StateFlow<QrDisplayData?> = _qrDisplayData.asStateFlow()
    
    // Legacy support for contacts - map QrDisplayData to ContactModel
    val contactState: StateFlow<ContactModel?> = _qrDisplayData.map { displayData ->
        if (displayData?.data is ContactModel) displayData.data else null
    }.stateIn(
        scope = screenModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState.asStateFlow()
    
    private val _qrCodeImage = MutableStateFlow<Any?>(null)
    val qrCodeImage: StateFlow<Any?> = _qrCodeImage.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        screenModelScope.launch {
            _isLoading.value = true
            _errorState.value = null
            try {
                val displayData = when (dataType) {
                    is QrDataType.Contact -> loadContactData(dataType.contactId)
                    is QrDataType.ContactWithData -> createContactDisplayData(dataType.contactModel)
                    is QrDataType.Address -> loadAddressData(dataType.addressId)
                    is QrDataType.AddressWithData -> createAddressDisplayData(dataType.addressEntity)
                    is QrDataType.Group -> loadGroupData(dataType.groupId)
                    is QrDataType.GroupWithData -> createGroupDisplayData(dataType.groupModel)
                    is QrDataType.Tag -> loadTagData(dataType.tagId)
                    is QrDataType.TagWithData -> createTagDisplayData(dataType.tagEntity)
                }
                
                if (displayData != null) {
                    _qrDisplayData.value = displayData
                    generateQrCode(displayData)
                } else {
                    _errorState.value = "Data not found"
                }
            } catch (e: Exception) {
                _errorState.value = e.message ?: "Error loading data"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // Direct data creation methods - no usecase needed
    private fun createContactDisplayData(contact: ContactModel): QrDisplayData {
        return QrDisplayData(
            title = contact.contactName,
            subtitle = contact.blockchainSymbol.ifEmpty { "Contact" },
            address = contact.walletAddress,
            symbol = contact.blockchainSymbol,
            icon = contact.blockchainIcon,
            data = contact
        )
    }
    
    private fun createAddressDisplayData(address: WalletAddressEntity): QrDisplayData {
        return QrDisplayData(
            title = address.alias ?: "Wallet Address",
            subtitle = "${address.walletType?.uppercase() ?: "WALLET"} Address",
            address = address.address,
            symbol = null, // Address entity doesn't have blockchain info directly
            icon = null,
            data = address
        )
    }
    
    private fun createGroupDisplayData(group: GroupModel): QrDisplayData {
        return QrDisplayData(
            title = group.name,
            subtitle = "Group (${group.walletAddressCount} addresses)",
            address = null,
            symbol = group.mainBlockchainSymbol,
            icon = group.icon,
            data = group
        )
    }
    
    private fun createTagDisplayData(tag: com.mangala.wallet.features.addressbook.data.model.tag.TagEntity): QrDisplayData {
        return QrDisplayData(
            title = tag.name,
            subtitle = "Tag",
            address = null,
            symbol = null,
            icon = tag.icon,
            data = tag
        )
    }
    
    private suspend fun loadContactData(contactId: String): QrDisplayData? {
        // Input validation
        if (contactId.isBlank()) {
            return null
        }
        
        return try {
            
            // PHƯƠNG PHÁP CHÍNH: Sử dụng GetContactDetailByIdUseCase để lấy thông tin đầy đủ
            val contactDetail = getContactDetailByIdUseCase(contactId)
            
            when {
                contactDetail != null -> {
                    val contactModel = createContactModelFromDetail(contactDetail)
                    logContactInfo(contactDetail, contactModel)
                    createContactQrDisplayData(contactModel)
                }
                else -> {
                    loadContactViaFallback(contactId)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Fallback method to load contact via FilterContactsUseCase
     */
    private suspend fun loadContactViaFallback(contactId: String): QrDisplayData? {
        return try {
            val filterCriteria = FilterCriteria(
                searchQuery = contactId,
                tagIds = emptyList(),
                groupIds = emptyList(),
                blockchainIds = emptyList(),
                onlyFavorites = false,
                sortOrder = "name_asc"
            )
            
            val result = filterContactsUseCase(
                filterCriteria = filterCriteria,
                page = 0,
                pageSize = 100
            )
            
            val contact = result.getOrNull()?.firstOrNull { it.contactId == contactId }
            
            if (contact != null) {
                createContactDisplayData(contact)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Helper method to create ContactModel from ContactDetailModel
     */
    private fun createContactModelFromDetail(contactDetail: ContactDetailModel): ContactModel {
        val primaryWallet = contactDetail.getPrimaryWalletAddress()
        
        return ContactModel(
            contactId = contactDetail.contact.id,
            contactName = contactDetail.contact.name,
            walletAddress = primaryWallet?.walletAddress?.address ?: "",
            walletAddressId = primaryWallet?.walletAddress?.id ?: "",
            walletAlias = primaryWallet?.walletAddress?.alias ?: "",
            walletSensitive = primaryWallet?.walletAddress?.isSensitive == true,
            blockchainName = primaryWallet?.blockchainType?.name ?: "",
            blockchainSymbol = primaryWallet?.blockchainType?.symbol ?: "",
            blockchainIcon = primaryWallet?.blockchainType?.icon ?: "",
            blockChainColor = primaryWallet?.blockchainType?.color ?: "#627EEA",
            isFavorite = contactDetail.isFavorite,
            isSensitive = contactDetail.contact.isSensitive == true,
            addedTime = contactDetail.contact.createdAt?.toEpochMilliseconds() ?: Clock.System.now().toEpochMilliseconds(),
            avatar = contactDetail.contact.avatar
        )
    }
    
    /**
     * Helper method to create QrDisplayData from ContactModel
     */
    private fun createContactQrDisplayData(contactModel: ContactModel): QrDisplayData {
        return QrDisplayData(
            title = contactModel.contactName,
            subtitle = if (contactModel.blockchainSymbol.isNotEmpty()) 
                "${contactModel.blockchainSymbol} • ${contactModel.blockchainName}" 
            else "Contact",
            address = contactModel.walletAddress,
            symbol = contactModel.blockchainSymbol,
            icon = contactModel.blockchainIcon,
            data = contactModel
        )
    }
    
    /**
     * Helper method for logging contact information
     */
    private fun logContactInfo(contactDetail: ContactDetailModel, contactModel: ContactModel) {
    }
    
    private fun loadAddressData(addressId: String): QrDisplayData? {
        // TODO: Implement proper GetWalletAddressByIdUseCase
        return null
    }
    
    private suspend fun loadGroupData(groupId: String): QrDisplayData? {
        return try {
            // Try to get group entity first
            val groupEntity = getGroupByIdUseCase(groupId)
            
            if (groupEntity != null) {
                // Get detailed group information
                val groupDetail = getGroupDetailByIdUseCase(groupId)
                
                if (groupDetail != null) {
                    // Use detailed group information
                    QrDisplayData(
                        title = groupDetail.group.name,
                        subtitle = "Group (${groupDetail.getMemberCount()} members)",
                        address = null, // Groups don't have single addresses
                        symbol = groupDetail.mainBlockchainType?.symbol,
                        icon = groupDetail.group.icon,
                        data = GroupModel(
                            id = groupDetail.group.id,
                            name = groupDetail.group.name,
                            description = groupDetail.group.description,
                            icon = groupDetail.group.icon,
                            color = groupDetail.group.color,
                            privacyLevel = groupDetail.group.privacyLevel.name,
                            securityLevel = groupDetail.group.securityLevel.name,
                            createdAt = groupDetail.group.createdAt.toEpochMilliseconds(),
                            updatedAt = groupDetail.group.updatedAt.toEpochMilliseconds(),
                            mainBlockchainId = groupDetail.mainBlockchainType?.id,
                            mainBlockchainName = groupDetail.mainBlockchainType?.name,
                            mainBlockchainSymbol = groupDetail.mainBlockchainType?.symbol,
                            mainBlockchainIcon = groupDetail.mainBlockchainType?.icon,
                            walletAddressCount = groupDetail.getMemberCount()
                        )
                    )
                } else {
                    // Fallback to basic group entity info
                    QrDisplayData(
                        title = groupEntity.name,
                        subtitle = "Group",
                        address = null,
                        symbol = null,
                        icon = groupEntity.icon,
                        data = GroupModel(
                            id = groupEntity.id,
                            name = groupEntity.name,
                            description = groupEntity.description,
                            icon = groupEntity.icon,
                            color = groupEntity.color,
                            privacyLevel = groupEntity.privacyLevel.name,
                            securityLevel = groupEntity.securityLevel.name,
                            createdAt = groupEntity.createdAt.toEpochMilliseconds(),
                            updatedAt = groupEntity.updatedAt.toEpochMilliseconds(),
                            mainBlockchainId = null,
                            mainBlockchainName = null,
                            mainBlockchainSymbol = null,
                            mainBlockchainIcon = null,
                            walletAddressCount = 0 // Will be updated in detail view
                        )
                    )
                }
            } else {
                null // No group found
            }
        } catch (e: Exception) {
            null
        }
    }
    
    private suspend fun loadTagData(tagId: String): QrDisplayData? {
        return try {
            // Get tag entity
            val tagEntity = tagRepository.getTagById(tagId)
            
            if (tagEntity != null) {
                // Get contact count for this tag
                val contactIds = tagRepository.getContactIdsWithTag(tagId)
                val contactCount = contactIds.size
                
                QrDisplayData(
                    title = tagEntity.name,
                    subtitle = "Tag ($contactCount contacts)",
                    address = null, // Tags don't have addresses
                    symbol = null,
                    icon = tagEntity.icon,
                    data = tagEntity
                )
            } else {
                null // No tag found
            }
        } catch (e: Exception) {
            null
        }
    }
    
    private fun generateQrCode(displayData: QrDisplayData) {
        screenModelScope.launch {
            try {
                val qrData = generateQrContent(displayData)
                
                if (validateQrData(qrData)) {
                    val qrImage = qrGenerator.generateQRCode(qrData)
                    _qrCodeImage.value = qrImage
                } else {
                    _errorState.value = "Invalid QR data"
                }
            } catch (e: Exception) {
                _errorState.value = "Failed to generate QR code: ${e.message}"
            }
        }
    }
    
    /**
     * Validate QR data before generation
     */
    private fun validateQrData(qrData: String): Boolean {
        return qrData.isNotBlank() && qrData.length <= 4000 // QR code size limit
    }
    
    /**
     * Retry loading data if there was an error
     */
    fun retryLoadContact() {
        loadData()
    }
    
    /**
     * Generate enhanced Group QR with addresses for full group sharing
     * This creates a more comprehensive QR code that includes contact addresses
     */
    suspend fun generateEnhancedGroupQr(groupId: String): String? {
        return try {
            val groupDetail = getGroupDetailByIdUseCase(groupId)
            if (groupDetail != null) {
                buildJsonObject {
                    put("type", "addressbook_group_full")
                    put("version", "1.0")
                    put("group", buildJsonObject {
                        put("id", groupDetail.group.id)
                        put("name", groupDetail.group.name)
                        put("description", groupDetail.group.description ?: "")
                        put("color", groupDetail.group.color ?: "")
                        put("icon", groupDetail.group.icon ?: "")
                    })
                    
                    // Include addresses with minimal info for QR size constraints
                    put("addresses", kotlinx.serialization.json.buildJsonArray {
                        groupDetail.contacts.forEach { contact ->
                            contact.walletAddress?.let { address ->
                                add(buildJsonObject {
                                    put("name", contact.contact.name)
                                    put("address", address.address)
                                    put("blockchain", address.walletType ?: "")
                                    put("alias", address.alias ?: "")
                                })
                            }
                        }
                    })
                    
                    put("totalAddresses", groupDetail.contacts.size)
                    put("exportedAt", Clock.System.now().toEpochMilliseconds())
                }.toString()
            } else null
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Generate QR code content string for any data type
     */
    fun generateQrContent(displayData: QrDisplayData): String {
        return when (val data = displayData.data) {
            is ContactModel -> {
                // Simple, readable contact format
                data.walletAddress.ifEmpty { "No address" }
    //                buildJsonObject {
    //                    put("type", "contact")
    //                    put("name", data.contactName)
    //                    put("address", data.walletAddress.ifEmpty { "No address" })
    //                    put("blockchain", data.blockchainSymbol.ifEmpty { "Unknown" })
    //                    put("blockchainName", data.blockchainName.ifEmpty { "Unknown Blockchain" })
    //                    if (data.walletAlias.isNotEmpty()) {
    //                        put("alias", data.walletAlias)
    //                    }
    //                }.toString()
            }

            is WalletAddressEntity -> {
                // Direct address format
                data.address.ifEmpty { "No address" }
    //                buildJsonObject {
    //                    put("type", "address")
    //                    put("address", data.address)
    //                    put("alias", data.alias ?: "")
    //                    put("walletType", data.walletType ?: "")
    //                }.toString()
            }

            is GroupModel -> {
                // Group format with comprehensive information
                // Enable JSON format for proper group sharing
                buildJsonObject {
                    put("type", "addressbook_group")
                    put("version", "1.0")
                    put("id", data.id)
                    put("name", data.name)
                    put("description", data.description ?: "")
                    put("addressCount", data.walletAddressCount)
                    put("blockchain", data.mainBlockchainSymbol ?: "")
                    put("createdAt", data.createdAt)
                    put("color", data.color ?: "")
                    put("icon", data.icon ?: "")
                }.toString()
            }

            is TagEntity -> {
                // Tag format
                data.id.ifEmpty { "No address" }

    //                buildJsonObject {
    //                    put("type", "tag")
    //                    put("id", data.id)
    //                    put("name", data.name)
    //                    put("color", data.color)
    //                    put("icon", data.icon ?: "")
    //                    put("createdAt", data.createdAt.toEpochMilliseconds())
    //                }.toString()
            }

            else -> {
                // Generic fallback format with JSON
                displayData.address?.ifEmpty { "No address" }.toString()

                //                buildJsonObject {
    //                    put("type", "generic")
    //                    put("title", displayData.title)
    //                    put("address", displayData.address ?: "")
    //                    put("symbol", displayData.symbol ?: "")
    //                    put("subtitle", displayData.subtitle)
    //                }.toString()
            }
        }
    }
    

}