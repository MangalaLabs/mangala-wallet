package com.mangala.wallet.features.addressbook.data.model.blockchain

import com.benasher44.uuid.uuid4
import com.mangala.wallet.utils.localDateTimeNow
import com.mangala.wallet.utils.localDateTimeToMillis
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * Entity cho loại blockchain
 * Tương ứng với bảng 'blockchain_types' trong database
 */
data class BlockchainTypeEntity(
val id: String = uuid4().toString(), // UUID
    val name: String = "Vault", // Ví dụ: "Ethereum", "Bitcoin", "Solana"
    val symbol: String = "EOS", // Ví dụ: "ETH", "BTC", "SOL"
    val addressFormat: String? = null, // Định dạng địa chỉ (regex)
    val validationRegex: String? = null, // Biểu thức chính quy để xác thực địa chỉ
    val icon: String? = null, // Icon của blockchain dưới dạng base64 hoặc đường dẫn
    val color: String? = null, // Mã màu của blockchain
    val networkType: String = NETWORK_MAINNET, // "mainnet" hoặc "testnet"
    val isActive: Boolean = true,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now()
) {
    /**
     * Kiểm tra địa chỉ có hợp lệ với blockchain này không
     * @param address Địa chỉ cần kiểm tra
     * @return true nếu địa chỉ hợp lệ
     */
    fun validateAddress(address: String): Boolean {
        if (validationRegex.isNullOrBlank()) return true

        return try {
            address.matches(validationRegex.toRegex())
        } catch (e: Exception) {
            // Nếu regex không hợp lệ, bỏ qua validation
            true
        }
    }

    /**
     * Trả về URL của blockchain explorer để xem địa chỉ
     * @param address Địa chỉ cần xem
     * @return URL của blockchain explorer
     */
    fun getExplorerUrl(address: String): String {
        return when (name.lowercase()) {
            "ethereum" -> {
                if (networkType == "mainnet") "https://etherscan.io/address/$address"
                else "https://goerli.etherscan.io/address/$address"
            }
            "bitcoin" -> {
                if (networkType == "mainnet") "https://www.blockchain.com/explorer/addresses/btc/$address"
                else "https://www.blockchain.com/explorer/addresses/btc-testnet/$address"
            }
            "solana" -> {
                if (networkType == "mainnet") "https://explorer.solana.com/address/$address"
                else "https://explorer.solana.com/address/$address?cluster=testnet"
            }
            // Thêm các blockchain khác khi cần
            else -> "#" // URL mặc định nếu không có explorer cụ thể
        }
    }

    companion object {
        // Network types
        const val NETWORK_MAINNET = "mainnet"
        const val NETWORK_TESTNET = "testnet"

        /**
         * Tạo một đối tượng BlockchainTypeEntity mới
         */
        fun create(
            id: String = uuid4().toString(), // UUID được tạo từ repository
            name: String,
            symbol: String,
            addressFormat: String? = null,
            validationRegex: String? = null,
            icon: String? = null,
            color: String? = null,
            networkType: String = NETWORK_MAINNET,
            isActive: Boolean = true
        ): BlockchainTypeEntity {
            val now = Instant.fromEpochMilliseconds(localDateTimeToMillis(localDateTimeNow()))
            return BlockchainTypeEntity(
                id = id,
                name = name,
                symbol = symbol,
                addressFormat = addressFormat,
                validationRegex = validationRegex,
                icon = icon,
                color = color,
                networkType = networkType,
                isActive = isActive,
                createdAt = now,
                updatedAt = now
            )
        }
    }
}