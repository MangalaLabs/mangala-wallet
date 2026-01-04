package com.mangala.wallet.features.addressbook.data.model

/**
 * Model kết hợp thông tin wallet address với thông tin blockchain network
 * Được sử dụng khi cần hiển thị thông tin wallet kèm tên và symbol của blockchain
 */
data class WalletAddressWithNetworkModel(
    val id: String,
    val contactId: String,
    val blockchainNetworkId: String, // Tương đương với blockchainTypeId
    val address: String,
    val alias: String?,
    val walletType: String?,
    val isDefault: Boolean, // Tương đương với isPrimary
    val networkName: String?, // Tên của blockchain
    val networkSymbol: String?, // Symbol của blockchain
    val createdAt: Long,
    val updatedAt: Long?,
    val isSensitive: Boolean?, // Thay đổi từ Boolean sang Boolean?
    val isVerified: Boolean?, // Thay đổi từ Boolean sang Boolean?
)