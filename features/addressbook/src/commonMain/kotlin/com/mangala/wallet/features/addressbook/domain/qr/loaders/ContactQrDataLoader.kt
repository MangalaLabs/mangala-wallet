package com.mangala.wallet.features.addressbook.domain.qr.loaders

import com.mangala.wallet.features.addressbook.domain.qr.QrDataLoader
import com.mangala.wallet.features.addressbook.domain.qr.QrLoadResult
import com.mangala.wallet.features.addressbook.domain.qr.QrDisplayData
import com.mangala.wallet.features.addressbook.domain.qr.QrType
import com.mangala.wallet.features.addressbook.domain.usecase.contact.GetContactDetailByIdUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.FilterContactsUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.FilterCriteria

/**
 * Contact-specific QR data loader
 */
class ContactQrDataLoader(
    private val getContactDetailByIdUseCase: GetContactDetailByIdUseCase,
    private val filterContactsUseCase: FilterContactsUseCase
) : QrDataLoader<String> {
    
    override suspend fun loadData(id: String): QrLoadResult {
        return try {
            // First try filter search for complete ContactModel data
            val contact = findContactByFilter(id)
            
            if (contact != null) {
                val displayData = QrDisplayData(
                    type = QrType.CONTACT,
                    id = contact.contactId,
                    title = contact.contactName,
                    subtitle = contact.blockchainSymbol.ifEmpty { "Contact" },
                    primaryInfo = contact.walletAddress,
                    secondaryInfo = contact.blockchainSymbol,
                    icon = contact.blockchainIcon,
                    color = contact.blockChainColor,
                    metadata = mapOf<String, Any>(
                        "blockchainName" to contact.blockchainName,
                        "walletAlias" to contact.walletAlias,
                        "isFavorite" to contact.isFavorite,
                        "addedTime" to (contact.addedTime ?: 0L)
                    )
                )
                QrLoadResult.Success(displayData)
            } else {
                // Fallback to detailed contact lookup
                loadContactDetail(id)
            }
        } catch (e: Exception) {
            QrLoadResult.Error("Failed to load contact data", e)
        }
    }
    
    override fun getQrType(): QrType = QrType.CONTACT
    
    private suspend fun findContactByFilter(contactId: String): com.mangala.wallet.features.addressbook.data.model.ContactModel? {
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
        
        return result.getOrNull()?.firstOrNull { it.contactId == contactId }
    }
    
    private suspend fun loadContactDetail(contactId: String): QrLoadResult {
        return try {
            val contactDetail = getContactDetailByIdUseCase(contactId)
            
            if (contactDetail != null) {
                val primaryWallet = contactDetail.getPrimaryWalletAddress()
                
                val displayData = QrDisplayData(
                    type = QrType.CONTACT,
                    id = contactDetail.contact.id,
                    title = contactDetail.contact.name,
                    subtitle = primaryWallet?.blockchainType?.name ?: "Contact",
                    primaryInfo = primaryWallet?.walletAddress?.address,
                    secondaryInfo = primaryWallet?.blockchainType?.symbol,
                    icon = primaryWallet?.blockchainType?.icon,
                    color = primaryWallet?.blockchainType?.color,
                    metadata = mapOf<String, Any>(
                        "blockchainName" to (primaryWallet?.blockchainType?.name ?: ""),
                        "walletAlias" to (primaryWallet?.walletAddress?.alias ?: ""),
                        "isFavorite" to contactDetail.isFavorite,
                        "isSensitive" to (contactDetail.contact.isSensitive ?: false)
                    )
                )
                QrLoadResult.Success(displayData)
            } else {
                QrLoadResult.Error("Contact not found")
            }
        } catch (e: Exception) {
            QrLoadResult.Error("Failed to load contact detail", e)
        }
    }
}