package com.mangala.wallet.features.addressbook.data.model

import com.mangala.wallet.features.addressbook.data.model.group.GroupEntity
import com.mangala.wallet.features.addressbook.data.model.tag.TagEntity
import com.mangala.wallet.features.addressbook.data.model.contact.ContactEntity
import com.mangala.wallet.features.addressbook.data.model.contact.EmailAddressEntity
import com.mangala.wallet.features.addressbook.data.model.contact.ImportantDateEntity
import com.mangala.wallet.features.addressbook.data.model.contact.PhoneNumberEntity
import com.mangala.wallet.features.addressbook.data.model.contact.PhysicalAddressEntity
import com.mangala.wallet.features.addressbook.data.model.contact.RelatedNameEntity
import com.mangala.wallet.features.addressbook.data.model.contact.SocialProfileEntity

/**
 * Model tổng hợp chi tiết đầy đủ về một Contact
 * Kết hợp Contact với các thông tin liên quan như phone numbers, emails, wallet addresses, v.v.
 */
data class ContactDetailModel(
    val contact: ContactEntity,
    val phoneNumbers: List<PhoneNumberEntity> = emptyList(),
    val emailAddresses: List<EmailAddressEntity> = emptyList(),
    val physicalAddresses: List<PhysicalAddressEntity> = emptyList(),
    val relatedNames: List<RelatedNameEntity> = emptyList(),
    val importantDates: List<ImportantDateEntity> = emptyList(),
    val walletAddresses: List<WalletAddressWithBlockchainModel> = emptyList(),
    val socialProfiles: List<SocialProfileEntity> = emptyList(),
    val tags: List<TagEntity> = emptyList(),
    val groups: List<GroupEntity> = emptyList(),
    val isFavorite: Boolean = false
) {
    /**
     * Lấy số điện thoại chính của contact
     * @return PhoneNumber chính hoặc null nếu không có
     */
    fun getPrimaryPhoneNumber(): PhoneNumberEntity? {
        return phoneNumbers.find { it.isPrimary } ?: phoneNumbers.firstOrNull()
    }

    /**
     * Lấy email chính của contact
     * @return EmailAddress chính hoặc null nếu không có
     */
    fun getPrimaryEmailAddress(): EmailAddressEntity? {
        return emailAddresses.find { it.isPrimary } ?: emailAddresses.firstOrNull()
    }

    /**
     * Lấy địa chỉ vật lý chính của contact
     * @return PhysicalAddress chính hoặc null nếu không có
     */
    fun getPrimaryPhysicalAddress(): PhysicalAddressEntity? {
        return physicalAddresses.find { it.isPrimary } ?: physicalAddresses.firstOrNull()
    }

    /**
     * Lấy wallet address chính của contact theo blockchain type
     * @param blockchainTypeId ID của blockchain type cần tìm
     * @return WalletAddressWithBlockchain chính hoặc null nếu không có
     */
    fun getPrimaryWalletAddress(blockchainTypeId: String? = null): WalletAddressWithBlockchainModel? {
        // Nếu có chỉ định blockchain type, tìm địa chỉ chính của blockchain đó
        if (blockchainTypeId != null) {
            val forType = walletAddresses.filter { it.walletAddress.blockchainTypeId == blockchainTypeId }
            return forType.find { it.walletAddress.isPrimary } ?: forType.firstOrNull()
        }

        // Nếu không chỉ định, tìm bất kỳ địa chỉ chính nào
        return walletAddresses.find { it.walletAddress.isPrimary } ?: walletAddresses.firstOrNull()
    }

    /**
     * Kiểm tra contact có nhạy cảm hay không
     * @return true nếu contact là nhạy cảm hoặc có bất kỳ wallet address nhạy cảm nào
     */
    fun isSensitive(): Boolean {
        return contact.isSensitive == true  || walletAddresses.any { it.walletAddress.isSensitive }
    }

    companion object {
        /**
         * Tạo một ContactDetailModel trống với chỉ thông tin cơ bản của contact
         * @param contact Thông tin cơ bản của contact
         * @return ContactDetailModel với các danh sách trống
         */
        fun createEmpty(contact: ContactEntity): ContactDetailModel {
            return ContactDetailModel(
                contact = contact,
                phoneNumbers = emptyList(),
                emailAddresses = emptyList(),
                physicalAddresses = emptyList(),
                relatedNames = emptyList(),
                importantDates = emptyList(),
                walletAddresses = emptyList(),
                tags = emptyList(),
                groups = emptyList(),
                isFavorite = false
            )
        }
    }
}