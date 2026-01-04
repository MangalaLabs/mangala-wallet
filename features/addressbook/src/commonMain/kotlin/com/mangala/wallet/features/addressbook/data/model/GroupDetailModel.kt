package com.mangala.wallet.features.addressbook.data.model

import com.mangala.wallet.features.addressbook.data.model.group.GroupEntity
import com.mangala.wallet.features.addressbook.data.model.blockchain.BlockchainTypeEntity
import com.mangala.wallet.features.addressbook.data.model.blockchain.WalletAddressEntity

/**
 * Model tổng hợp chi tiết về một Group
 * Kết hợp Group với thông tin liên quan như danh sách contacts, blockchain chính
 */
data class GroupDetailModel(
    val group: GroupEntity,
    val contacts: List<ContactWithWalletAddressModel> = emptyList(),
    val mainBlockchainType: BlockchainTypeEntity? = null
) {
    /**
     * Kiểm tra xem một contact có thuộc group không
     * @param contactId ID của contact cần kiểm tra
     * @return true nếu contact thuộc group
     */
    fun containsContact(contactId: String): Boolean {
        return contacts.any { it.contact.id == contactId }
    }

    /**
     * Lấy số lượng thành viên trong group
     * @return Số lượng contacts trong group
     */
    fun getMemberCount(): Int {
        return contacts.size
    }

    /**
     * Lấy danh sách các wallet address được sử dụng trong group
     * @return Danh sách các wallet address
     */
    fun getWalletAddresses(): List<WalletAddressEntity> {
        return contacts.mapNotNull { it.walletAddress }
    }

    companion object {
        /**
         * Tạo một GroupDetailModel trống chỉ với thông tin cơ bản của group
         * @param group Thông tin cơ bản của group
         * @param mainBlockchainType Blockchain chính của group (có thể null)
         * @return GroupDetailModel với danh sách contacts trống
         */
        fun createEmpty(
            group: GroupEntity,
            mainBlockchainType: BlockchainTypeEntity? = null
        ): GroupDetailModel {
            return GroupDetailModel(
                group = group,
                contacts = emptyList(),
                mainBlockchainType = mainBlockchainType
            )
        }
    }
}