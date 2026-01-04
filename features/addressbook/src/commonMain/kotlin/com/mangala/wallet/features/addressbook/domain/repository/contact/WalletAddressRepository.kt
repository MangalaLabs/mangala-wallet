package com.mangala.wallet.features.addressbook.domain.repository.contact

import com.mangala.wallet.features.addressbook.data.model.WalletAddressWithNetworkModel
import com.mangala.wallet.features.addressbook.data.model.blockchain.WalletAddressEntity
import com.mangala.wallet.features.addressbook.domain.model.group.GroupWallet
import com.mangala.wallet.features.addressbook.presentation.group.create.ContactWithAddress
import kotlinx.coroutines.flow.Flow

interface WalletAddressRepository {
    /**
     * Lấy danh sách wallet address cho một contact
     * @param contactId ID của contact
     * @param limit Số lượng tối đa kết quả trả về
     * @param offset Vị trí bắt đầu lấy kết quả
     * @return Danh sách wallet address cùng với thông tin network
     */
    suspend fun getWalletAddressesForContact(contactId: String, limit: Int = 5, offset: Int = 0): List<WalletAddressWithNetworkModel>

    /**
     * Đếm số lượng wallet address của một contact
     * @param contactId ID của contact
     * @return Số lượng wallet address
     */
    suspend fun countWalletAddressesForContact(contactId: String): Int

    /**
     * Lấy wallet address mặc định cho một contact và blockchain network
     * @param contactId ID của contact
     * @param blockchainNetworkId ID của blockchain network
     * @return Wallet address mặc định hoặc null nếu không có
     */
    suspend fun getDefaultWalletAddress(contactId: String, blockchainNetworkId: String): WalletAddressEntity?

    /**
     * Đặt một wallet address làm mặc định
     * @param id ID của wallet address
     * @param contactId ID của contact
     * @param blockchainNetworkId ID của blockchain network
     * @return true nếu thành công
     */
    suspend fun setWalletAddressAsDefault(id: String, contactId: String, blockchainNetworkId: String): Boolean

    /**
     * Lấy danh sách wallet address theo network
     * @param networkId ID của network
     * @param limit Số lượng tối đa kết quả trả về
     * @param offset Vị trí bắt đầu lấy kết quả
     * @return Danh sách wallet address cùng với thông tin network
     */
    suspend fun getWalletAddressesByNetwork(networkId: String, limit: Int = 5, offset: Int = 0): List<WalletAddressWithNetworkModel>

    /**
     * Lưu một wallet address mới
     * @param walletAddress Wallet address cần lưu
     * @return ID của wallet address sau khi lưu
     */
    suspend fun insertWalletAddress(walletAddress: WalletAddressEntity): String

    /**
     * Cập nhật một wallet address hiện có
     * @param walletAddress Wallet address cần cập nhật
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
     * Xóa tất cả wallet address của một contact
     * @param contactId ID của contact
     * @return true nếu xóa thành công
     */
    suspend fun deleteWalletAddressesByContactId(contactId: String): Boolean

    suspend fun insertWalletAddressesBatch(walletAddresses: List<WalletAddressEntity>): Map<WalletAddressEntity, String>

    suspend fun getWalletAddressById(walletId: String): WalletAddressWithNetworkModel
    
    suspend fun getWalletAddressByAddress(address: String): WalletAddressEntity?

    suspend fun getContactsWithAddressesByBlockchainType(blockchainId: String): List<ContactWithAddress>
    
    suspend fun getGroupWalletsByWalletIds(walletIds: List<String>): List<GroupWallet>
    
    /**
     * Gets wallet addresses filtered by blockchain and search query, with optional pagination
     * @param blockchainId ID of the blockchain to filter by
     * @param limit Maximum number of results to return
     * @param offset Starting position for pagination
     * @param searchQuery Optional search query to filter by wallet alias or address
     * @return List of GroupWallet objects matching the criteria
     */
    suspend fun getGroupWalletsByBlockchainAndAlias(
        blockchainId: String,
        limit: Int = 50,
        offset: Int = 0,
        searchQuery: String = ""
    ): List<GroupWallet>
}