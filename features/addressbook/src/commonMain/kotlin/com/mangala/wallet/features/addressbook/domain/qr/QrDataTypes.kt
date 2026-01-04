package com.mangala.wallet.features.addressbook.domain.qr

import com.mangala.wallet.features.addressbook.data.model.ContactModel
import com.mangala.wallet.features.addressbook.data.model.group.GroupModel
import com.mangala.wallet.features.addressbook.data.model.tag.TagEntity
import com.mangala.wallet.features.addressbook.data.model.blockchain.WalletAddressEntity

/**
 * Simplified QR data types with clear separation of concerns
 */
sealed class QrDataType {
    data class Contact(val contactId: String) : QrDataType()
    data class Group(val groupId: String) : QrDataType()
    data class Tag(val tagId: String) : QrDataType()
    data class Address(val addressId: String) : QrDataType()
}

/**
 * Unified QR display data structure
 */
data class QrDisplayData(
    val type: QrType,
    val id: String,
    val title: String,
    val subtitle: String,
    val primaryInfo: String?, // Address for contacts, member count for groups, etc.
    val secondaryInfo: String?, // Blockchain symbol, etc.
    val icon: String?,
    val color: String?,
    val metadata: Map<String, Any> = emptyMap()
)

enum class QrType {
    CONTACT, GROUP, TAG, ADDRESS
}

/**
 * QR load result with proper error handling
 */
sealed class QrLoadResult {
    data class Success(val data: QrDisplayData) : QrLoadResult()
    data class Error(val message: String, val cause: Throwable? = null) : QrLoadResult()
    object Loading : QrLoadResult()
}

/**
 * QR content generation result
 */
sealed class QrContentResult {
    data class Success(val content: String, val displayData: QrDisplayData) : QrContentResult()
    data class Error(val message: String) : QrContentResult()
}