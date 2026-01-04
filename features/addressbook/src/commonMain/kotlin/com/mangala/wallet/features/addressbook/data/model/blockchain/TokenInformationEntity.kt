package com.mangala.wallet.features.addressbook.data.model.blockchain

import com.benasher44.uuid.uuid4
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.mangala.wallet.utils.localDateTimeNow
import com.mangala.wallet.utils.localDateTimeToMillis
import kotlinx.datetime.Instant
import kotlin.math.pow

/**
 * Entity cho thông tin token trên blockchain
 * Tương ứng với bảng 'token_information' trong database
 */
data class TokenInformationEntity(
    val id: String, // UUID
    val blockchainTypeId: String,
    val tokenName: String,
    val tokenSymbol: String,
    val contractAddress: String?, // Địa chỉ hợp đồng token (cho ERC20, SPL, etc.)
    val icon: String?, // Biểu tượng token dưới dạng base64 hoặc đường dẫn
    val decimals: Int, // Số vị trí thập phân của token
    val isNative: Boolean, // Là token gốc của blockchain (như ETH, BTC, SOL)
    val createdAt: Instant,
    val updatedAt: Instant
) {
    /**
     * Format số lượng token theo định dạng dễ đọc, có tính đến decimals
     * @param amount Số lượng token (chuỗi raw từ blockchain)
     * @return Chuỗi đã format với số thập phân phù hợp
     */
    fun formatAmount(amount: String): String {
        return try {
            val rawAmount = amount.toBigDecimal()
            val divisor = 10.0.pow(decimals).toBigDecimal()
            val formattedAmount = rawAmount.divide(divisor)

            // Format số với tối đa 8 chữ số thập phân
            val formatted = formattedAmount.toString()

            // Xử lý để loại bỏ số 0 thừa ở cuối và dấu thập phân nếu không cần thiết
            val trimmed = if (formatted.contains('.')) {
                formatted.trimEnd('0').trimEnd('.')
            } else {
                formatted
            }

            "$trimmed $tokenSymbol"
        } catch (e: Exception) {
            "$amount $tokenSymbol"
        }
    }

    /**
     * Trả về URL của blockchain explorer để xem token contract
     * @return URL của blockchain explorer
     */
    fun getContractExplorerUrl(): String? {
        if (contractAddress.isNullOrBlank() || isNative) return null

        // Lấy block explorer URL dựa trên blockchain type
        // Đây chỉ là ví dụ, cần phải triển khai đầy đủ hơn
        return when {
            tokenSymbol.contains("ETH", ignoreCase = true) ->
                "https://etherscan.io/token/$contractAddress"
            tokenSymbol.contains("SOL", ignoreCase = true) ->
                "https://explorer.solana.com/address/$contractAddress"
            // Thêm các blockchain khác khi cần
            else -> null
        }
    }

    companion object {
        /**
         * Tạo một đối tượng TokenInformationEntity mới
         */
        fun create(
            id: String = uuid4().toString(), // UUID được tạo từ repository
            blockchainTypeId: String,
            tokenName: String,
            tokenSymbol: String,
            contractAddress: String? = null,
            icon: String? = null,
            decimals: Int = 18,
            isNative: Boolean = false
        ): TokenInformationEntity {
            val now = Instant.fromEpochMilliseconds(localDateTimeToMillis(localDateTimeNow()))
            return TokenInformationEntity(
                id = id,
                blockchainTypeId = blockchainTypeId,
                tokenName = tokenName,
                tokenSymbol = tokenSymbol,
                contractAddress = contractAddress,
                icon = icon,
                decimals = decimals,
                isNative = isNative,
                createdAt = now,
                updatedAt = now
            )
        }
    }
}