package com.mangala.wallet.features.addressbook.domain.usecase.contact

import com.mangala.wallet.features.addressbook.data.local.contact.WalletAddressLocalDataSource
import com.mangala.wallet.features.addressbook.domain.model.group.GroupWallet
import com.mangala.wallet.features.addressbook.presentation.group.create.ContactWithAddress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Use case để lấy danh sách contacts và địa chỉ ví của họ theo blockchain type với phân trang
 */
class GetContactsWithWalletAddressPaginatedUseCase(
    private val walletAddressLocalDataSource: WalletAddressLocalDataSource
) {
    companion object {
        const val DEFAULT_PAGE_SIZE = 20
    }
    
    /**
     * Lấy danh sách GroupWallet theo blockchain type với phân trang thật sự
     * Sử dụng truy vấn tối ưu để lấy trực tiếp các GroupWallet từ database
     * @param blockchainId ID của blockchain type
     * @param page Số trang hiện tại (bắt đầu từ 0)
     * @param pageSize Số lượng items trên mỗi trang
     * @param searchQuery Từ khóa tìm kiếm (tùy chọn) - tìm theo wallet alias, wallet address, hoặc contact name
     * @return List của GroupWallet cho trang yêu cầu
     */
    suspend fun getPagedList(
        blockchainId: String,
        page: Int,
        pageSize: Int = DEFAULT_PAGE_SIZE,
        searchQuery: String = ""
    ): List<GroupWallet> {
        try {
            // Sử dụng phương thức mới để lấy các GroupWallet được lọc trực tiếp từ database
            val offset = page * pageSize
            return walletAddressLocalDataSource.getGroupWalletsByBlockchainAndAlias(
                blockchainId = blockchainId,
                limit = pageSize,
                offset = offset,
                searchQuery = searchQuery
            )
        } catch (e: Exception) {
            // Xử lý lỗi - trả về danh sách rỗng và log lỗi
            println("ERROR: Failed to load paged group wallets: ${e.message}")
            return emptyList()
        }
    }
    
    /**
     * Đếm tổng số wallet addresses phù hợp với blockchain type và searchQuery
     * Sử dụng truy vấn tối ưu để đếm trực tiếp từ database
     * @param blockchainId ID của blockchain type
     * @param searchQuery Từ khóa tìm kiếm (tùy chọn) - tìm theo wallet alias, wallet address, hoặc contact name
     * @return Tổng số wallet addresses thỏa mãn điều kiện
     */
    suspend fun count(blockchainId: String, searchQuery: String = ""): Int {
        try {
            // Sử dụng phương thức mới để đếm trực tiếp từ database
            return walletAddressLocalDataSource.countGroupWalletsByBlockchainAndAlias(
                blockchainId = blockchainId,
                searchQuery = searchQuery
            )
        } catch (e: Exception) {
            // Xử lý lỗi
            println("ERROR: Failed to count wallet addresses for blockchain: ${e.message}")
            return 0
        }
    }
}