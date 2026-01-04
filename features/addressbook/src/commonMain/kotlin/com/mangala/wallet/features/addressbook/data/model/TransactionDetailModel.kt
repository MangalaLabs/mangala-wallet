package com.mangala.wallet.features.addressbook.data.model

import com.mangala.wallet.features.addressbook.data.model.blockchain.BlockchainTypeEntity
import com.mangala.wallet.features.addressbook.data.model.contact.ContactEntity
import com.mangala.wallet.features.addressbook.data.model.transaction.TransactionHistoryEntity

/**
 * Model tổng hợp chi tiết về một giao dịch
 * Kết hợp TransactionHistory với thông tin liên quan như BlockchainType, Contact gửi/nhận
 */
data class TransactionDetailModel(
    val transaction: TransactionHistoryEntity = TransactionHistoryEntity(),
    val blockchainType: BlockchainTypeEntity = BlockchainTypeEntity(),
    val fromContact: ContactEntity? = null,
    val toContact: ContactEntity? = null
) {
    /**
     * Kiểm tra xem giao dịch có liên quan đến contact cụ thể không
     * @param contactId ID của contact cần kiểm tra
     * @return true nếu giao dịch có liên quan đến contact
     */
    fun isRelatedToContact(contactId: String): Boolean {
        return fromContact?.id == contactId || toContact?.id == contactId
    }

    /**
     * Xác định hướng giao dịch đối với một contact cụ thể
     * @param contactId ID của contact cần xác định
     * @return "in" nếu nhận, "out" nếu gửi, "internal" nếu cả gửi và nhận, null nếu không liên quan
     */
    fun getDirectionForContact(contactId: String): String? {
        val isSender = fromContact?.id == contactId
        val isReceiver = toContact?.id == contactId

        return when {
            isSender && isReceiver -> "internal"
            isSender -> "out"
            isReceiver -> "in"
            else -> null
        }
    }

    companion object {
        /**
         * Tạo một TransactionDetailModel với chỉ thông tin cơ bản
         * @param transaction Thông tin giao dịch
         * @param blockchainType Thông tin blockchain
         * @return TransactionDetailModel với các Contact là null
         */
        fun createBasic(
            transaction: TransactionHistoryEntity,
            blockchainType: BlockchainTypeEntity
        ): TransactionDetailModel {
            return TransactionDetailModel(
                transaction = transaction,
                blockchainType = blockchainType,
                fromContact = null,
                toContact = null
            )
        }
    }
}