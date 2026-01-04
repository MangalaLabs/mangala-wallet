package com.mangala.wallet.features.addressbook.data.local.transaction

import com.mangala.wallet.features.addressbook.data.model.TransactionDetailModel
import com.mangala.wallet.features.addressbook.data.model.transaction.TransactionHistoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Interface định nghĩa các phương thức truy cập dữ liệu liên quan đến Transaction từ local database
 */
interface TransactionLocalDataSource {
    /**
     * Lấy một transaction theo ID
     * @param id ID của transaction cần lấy
     * @return TransactionHistoryEntity hoặc null nếu không tìm thấy
     */
    suspend fun getTransactionById(id: String): TransactionHistoryEntity?

    /**
     * Lấy thông tin chi tiết của một transaction
     * @param id ID của transaction cần lấy
     * @return TransactionDetailModel hoặc null nếu không tìm thấy
     */
    suspend fun getTransactionDetailById(id: String): TransactionDetailModel?

    /**
     * Lấy lịch sử giao dịch của một contact
     * @param contactId ID của contact
     * @param limit Số lượng records tối đa
     * @param offset Vị trí bắt đầu
     * @return Flow danh sách transactions
     */
    fun getTransactionHistoryByContactId(contactId: String, limit: Int = 20, offset: Int = 0): Flow<List<TransactionHistoryEntity>>

    /**
     * Lấy lịch sử giao dịch của một wallet address
     * @param address Địa chỉ wallet
     * @param limit Số lượng records tối đa
     * @param offset Vị trí bắt đầu
     * @return Flow danh sách transactions
     */
    fun getTransactionHistoryByWalletAddress(address: String, limit: Int = 20, offset: Int = 0): Flow<List<TransactionHistoryEntity>>

    /**
     * Lấy thông tin chi tiết của lịch sử giao dịch của một contact
     * @param contactId ID của contact
     * @param limit Số lượng records tối đa
     * @param offset Vị trí bắt đầu
     * @return Flow danh sách transaction details
     */
    fun getTransactionDetailsByContactId(contactId: String, limit: Int = 20, offset: Int = 0): Flow<List<TransactionDetailModel>>

    /**
     * Lưu một transaction mới
     * @param transaction TransactionHistoryEntity cần lưu
     * @return ID của transaction sau khi lưu
     */
    suspend fun insertTransaction(transaction: TransactionHistoryEntity): String

    /**
     * Cập nhật một transaction hiện có
     * @param transaction TransactionHistoryEntity cần cập nhật
     * @return true nếu cập nhật thành công
     */
    suspend fun updateTransaction(transaction: TransactionHistoryEntity): Boolean

    /**
     * Xóa một transaction
     * @param id ID của transaction cần xóa
     * @return true nếu xóa thành công
     */
    suspend fun deleteTransaction(id: String): Boolean

    /**
     * Liên kết một transaction với contact
     * @param contactId ID của contact
     * @param transactionId ID của transaction
     * @param walletAddressId ID của wallet address liên quan
     * @param isSender true nếu contact là người gửi, false nếu là người nhận
     * @return true nếu liên kết thành công
     */
    suspend fun linkTransactionToContact(contactId: String, transactionId: String, walletAddressId: String, isSender: Boolean): Boolean

    /**
     * Lấy danh sách các giao dịch đang chờ xử lý
     * @return Danh sách các transactions
     */
    suspend fun getPendingTransactions(): List<TransactionHistoryEntity>

    /**
     * Clear all transaction history for wallet reset
     * @return true if clearing was successful
     */
    suspend fun clearAllTransactionHistory(): Boolean

    /**
     * Clear offline queue for wallet reset
     * @return true if clearing was successful
     */
    suspend fun clearAllOfflineQueue(): Boolean
}