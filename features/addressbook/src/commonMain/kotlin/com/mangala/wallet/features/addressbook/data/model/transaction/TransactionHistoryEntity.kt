package com.mangala.wallet.features.addressbook.data.model.transaction

import com.benasher44.uuid.uuid4
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.mangala.wallet.features.addressbook.data.model.enum.TransactionStatus
import com.mangala.wallet.utils.ext.formatCompact
import com.mangala.wallet.utils.localDateTimeNow
import com.mangala.wallet.utils.localDateTimeToMillis
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * Entity cho lịch sử giao dịch
 * Tương ứng với bảng 'transaction_history' trong database
 */
data class TransactionHistoryEntity(
    val id: String = uuid4().toString(), // UUID
    val fromAddress: String = "",
    val toAddress: String = "",
    val blockchainTypeId: String = "",
    val amount: String = "0", // Dạng string để tránh lỗi số học với số lượng lớn
    val tokenSymbol: String = "",
    val transactionHash: String = "",
    val status: TransactionStatus = TransactionStatus.PENDING,
    val timestamp: Instant = Clock.System.now(),
    val fee: String? = null, // Phí giao dịch (dạng string)
    val note: String? = null,
    val isFromImportedWallet: Boolean = false, // Có phải giao dịch từ ví được import hay không
) {
    // TODO: The decimal scale should get from token decimal scale
    val formattedAmount: String by lazy { "${getAmountAsBigDecimal().formatCompact(decimalScale = 10, useScaleBasedCompactForSmallerThanOne = true)} $tokenSymbol" }
    val formattedFee: String? by lazy { fee?.let { "${getFeeAsBigDecimal().formatCompact(decimalScale = 10, useScaleBasedCompactForSmallerThanOne = true)} $tokenSymbol" } }
    val totalDebited: String by lazy {
        val total = getAmountAsBigDecimal() + getFeeAsBigDecimal()
        "${total.formatCompact(decimalScale = 10, useScaleBasedCompactForSmallerThanOne = true)} $tokenSymbol"
    }
    /**
     * Parse amount từ string về BigDecimal để thực hiện các phép tính
     * @return BigDecimal từ chuỗi amount hoặc 0 nếu không thể parse
     */
    private fun getAmountAsBigDecimal(): BigDecimal {
        return try {
            amount.toBigDecimal()
        } catch (e: Exception) {
            BigDecimal.ZERO
        }
    }

    /**
     * Parse fee từ string về BigDecimal để thực hiện các phép tính
     * @return BigDecimal từ chuỗi fee hoặc 0 nếu không thể parse
     */
    private fun getFeeAsBigDecimal(): BigDecimal {
        return try {
            fee.let { it?.toBigDecimal() ?: BigDecimal.ZERO }
        } catch (e: Exception) {
            BigDecimal.ZERO
        }
    }

    /**
     * Kiểm tra xem giao dịch có phải là giao dịch đến không
     * @param address Địa chỉ ví cần kiểm tra
     * @return true nếu giao dịch đến địa chỉ đó
     */
    fun isIncomingTransaction(address: String): Boolean {
        return toAddress.equals(address, ignoreCase = true)
    }

    /**
     * Kiểm tra xem giao dịch có phải là giao dịch đi không
     * @param address Địa chỉ ví cần kiểm tra
     * @return true nếu giao dịch đi từ địa chỉ đó
     */
    fun isOutgoingTransaction(address: String): Boolean {
        return fromAddress.equals(address, ignoreCase = true)
    }

    companion object {
        /**
         * Tạo một đối tượng TransactionHistoryEntity mới
         */
        fun create(
            id: String = uuid4().toString(), // UUID được tạo từ repository
            fromAddress: String,
            toAddress: String,
            blockchainTypeId: String,
            amount: String,
            tokenSymbol: String,
            transactionHash: String,
            status: TransactionStatus = TransactionStatus.PENDING,
            timestamp: Instant = Instant.fromEpochMilliseconds(localDateTimeToMillis(localDateTimeNow())),
            fee: String? = null,
            note: String? = null,
            isFromImportedWallet: Boolean = false,
        ): TransactionHistoryEntity {
            return TransactionHistoryEntity(
                id = id,
                fromAddress = fromAddress,
                toAddress = toAddress,
                blockchainTypeId = blockchainTypeId,
                amount = amount,
                tokenSymbol = tokenSymbol,
                transactionHash = transactionHash,
                status = status,
                timestamp = timestamp,
                fee = fee,
                note = note,
                isFromImportedWallet = isFromImportedWallet
            )
        }
    }
}