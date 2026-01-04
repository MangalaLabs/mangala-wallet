package com.mangala.wallet.features.addressbook.domain.repository.transaction

import com.mangala.wallet.features.addressbook.data.model.TransactionDetailModel
import com.mangala.wallet.features.addressbook.data.model.transaction.TransactionHistoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface cho việc quản lý Transaction và Reminder
 */
interface TransactionRepository {
    /**
     * Lấy một transaction theo ID
     * @param id ID của transaction cần lấy
     * @return TransactionHistory hoặc null nếu không tìm thấy
     */
    suspend fun getTransactionById(id: String): TransactionHistoryEntity?

    /**
     * Lấy thông tin chi tiết của một transaction
     * @param id ID của transaction cần lấy
     * @return TransactionDetail hoặc null nếu không tìm thấy
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
     * @param transaction TransactionHistory cần lưu
     * @return ID của transaction sau khi lưu
     */
    suspend fun insertTransaction(transaction: TransactionHistoryEntity): String

    /**
     * Cập nhật một transaction hiện có
     * @param transaction TransactionHistory cần cập nhật
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
     * Xóa một reminder
     * @param id ID của reminder cần xóa
     * @return true nếu xóa thành công
     */
    suspend fun deleteReminder(id: String): Boolean

    /**
     * Hoàn thành một reminder
     * @param id ID của reminder cần hoàn thành
     * @return true nếu cập nhật thành công
     */
    suspend fun completeReminder(id: String): Boolean

    /**
     * Hủy một reminder
     * @param id ID của reminder cần hủy
     * @return true nếu cập nhật thành công
     */
    suspend fun cancelReminder(id: String): Boolean

    /**
     * Xử lý các reminder lặp lại đã đến hạn
     * @return Số lượng reminders mới được tạo
     */
    suspend fun processRecurringReminders(): Int

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

    /**
     * Clear remote keys for wallet reset
     * @return true if clearing was successful
     */
    suspend fun clearAllRecentTxRemoteKeys(): Boolean
}