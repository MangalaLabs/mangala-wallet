package com.mangala.wallet.features.addressbook.data.local.blockchain

import com.mangala.wallet.features.addressbook.data.model.blockchain.BlockchainTypeEntity
import com.mangala.wallet.features.addressbook.data.model.blockchain.TokenInformationEntity
import com.mangala.wallet.features.addressbook.data.model.blockchain.WalletAddressEntity
import com.mangala.wallet.features.addressbook.data.model.contact.ContactEntity

/**
 * Interface định nghĩa các phương thức truy cập dữ liệu liên quan đến blockchain từ local database
 */
interface BlockchainLocalDataSource {
    /**
     * Lấy một blockchain type theo ID
     * @param id ID của blockchain type cần lấy
     * @return BlockchainTypeEntity hoặc null nếu không tìm thấy
     */
    suspend fun getBlockchainTypeById(id: String): BlockchainTypeEntity?

    /**
     * Lấy danh sách tất cả blockchain types đang hoạt động
     * @return Danh sách các blockchain types
     */
    suspend fun getAllBlockchainTypes(): List<BlockchainTypeEntity>

    /**
     * Lưu một blockchain type mới
     * @param blockchainType BlockchainTypeEntity cần lưu
     * @return ID của blockchain type sau khi lưu
     */
    suspend fun insertBlockchainType(blockchainType: BlockchainTypeEntity): String

    /**
     * Cập nhật một blockchain type hiện có
     * @param blockchainType BlockchainTypeEntity cần cập nhật
     * @return true nếu cập nhật thành công
     */
    suspend fun updateBlockchainType(blockchainType: BlockchainTypeEntity): Boolean

    /**
     * Xóa một blockchain type
     * @param id ID của blockchain type cần xóa
     * @return true nếu xóa thành công
     */
    suspend fun deleteBlockchainType(id: String): Boolean

    /**
     * Lấy danh sách các wallet addresses của một contact
     * @param contactId ID của contact
     * @return Danh sách các wallet addresses
     */
    suspend fun getWalletAddressesByContactId(contactId: String): List<WalletAddressEntity>

    /**
     * Lấy danh sách các wallet addresses của một blockchain type
     * @param blockchainTypeId ID của blockchain type
     * @return Danh sách các wallet addresses
     */
    suspend fun getWalletAddressesByBlockchainTypeId(blockchainTypeId: String): List<WalletAddressEntity>

    /**
     * Lưu một wallet address mới
     * @param walletAddress WalletAddressEntity cần lưu
     * @return ID của wallet address sau khi lưu
     */
    suspend fun insertWalletAddress(walletAddress: WalletAddressEntity): String

    /**
     * Cập nhật một wallet address hiện có
     * @param walletAddress WalletAddressEntity cần cập nhật
     * @return true nếu cập nhật thành công
     */
    suspend fun updateWalletAddress(walletAddress: WalletAddressEntity): Boolean

    /**
     * Xóa một wallet address
     * @param id ID của wallet address cần xóa
     * @return true nếu xóa thành công
     */
    suspend fun deleteWalletAddress(id: String): Boolean

    /**
     * Đánh dấu một wallet address là primary cho một contact và blockchain type
     * @param walletAddressId ID của wallet address cần đánh dấu
     * @return true nếu đánh dấu thành công
     */
    suspend fun markWalletAddressAsPrimary(walletAddressId: String): Boolean

    /**
     * Xác thực một wallet address
     * @param walletAddressId ID của wallet address cần xác thực
     * @return true nếu xác thực thành công
     */
    suspend fun verifyWalletAddress(walletAddressId: String): Boolean

    /**
     * Tìm contact theo wallet address
     * @param address Địa chỉ wallet cần tìm
     * @return ContactEntity hoặc null nếu không tìm thấy
     */
    suspend fun findContactByWalletAddress(address: String): ContactEntity?

    /**
     * Lấy một token information theo ID
     * @param id ID của token information cần lấy
     * @return TokenInformationEntity hoặc null nếu không tìm thấy
     */
    suspend fun getTokenInformationById(id: String): TokenInformationEntity?

    /**
     * Lấy danh sách token information theo blockchain type
     * @param blockchainTypeId ID của blockchain type
     * @return Danh sách các TokenInformationEntity
     */
    suspend fun getTokenInformationByBlockchainType(blockchainTypeId: String): List<TokenInformationEntity>

    /**
     * Lấy token information của native token của một blockchain
     * @param blockchainTypeId ID của blockchain type
     * @return TokenInformationEntity hoặc null nếu không tìm thấy
     */
    suspend fun getNativeTokenForBlockchain(blockchainTypeId: String): TokenInformationEntity?

    /**
     * Lưu một token information mới
     * @param tokenInformation TokenInformationEntity cần lưu
     * @return ID của token information sau khi lưu
     */
    suspend fun insertTokenInformation(tokenInformation: TokenInformationEntity): String

    /**
     * Cập nhật một token information hiện có
     * @param tokenInformation TokenInformationEntity cần cập nhật
     * @return true nếu cập nhật thành công
     */
    suspend fun updateTokenInformation(tokenInformation: TokenInformationEntity): Boolean

    /**
     * Xóa một token information
     * @param id ID của token information cần xóa
     * @return true nếu xóa thành công
     */
    suspend fun deleteTokenInformation(id: String): Boolean


    suspend fun deleteTokenInformationByBlockchainTypeId(blockchainTypeId: String): Boolean

    /**
     * Kiểm tra tính hợp lệ của một địa chỉ wallet
     * @param address Địa chỉ cần kiểm tra
     * @param blockchainTypeId ID của blockchain type
     * @return true nếu địa chỉ hợp lệ
     */
    suspend fun validateAddress(address: String, blockchainTypeId: String): Boolean
}