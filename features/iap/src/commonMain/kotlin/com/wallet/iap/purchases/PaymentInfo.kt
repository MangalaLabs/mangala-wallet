package com.wallet.iap.purchases

import com.wallet.iap.purchases.domain.PurchaseStatus
import dev.icerock.moko.parcelize.Parcelable
import kotlinx.serialization.Serializable
import dev.icerock.moko.parcelize.Parcelize

@Serializable
@Parcelize
data class PaymentInfo(
    val orderId: String? = null,
    val packageName: String? = null,
    val productId: String? = null,
    val purchaseTime: Long? = null,
    val purchaseState: Int? = null,
    val purchaseToken: String? = null,
    val quantity: Int? = null,
    val acknowledged: Boolean? = null,
    val obfuscatedAccountId: String? = null,
    val obfuscatedProfileId: String? = null,
): Parcelable {
    val getPurchaseStateEnum: PurchaseState? = purchaseState?.let { getPurchaseStateFromInt(it) }
}

@Parcelize
enum class PurchaseState(val value: Int): Parcelable {
    PURCHASED(1),
    PENDING(2),
    UNSPECIFIED_STATE(0);

    fun toPurchaseStatus(): PurchaseStatus {
        return when (this) {
            PURCHASED -> PurchaseStatus.SUCCESS
            PENDING -> PurchaseStatus.PENDING
            UNSPECIFIED_STATE -> PurchaseStatus.FAILURE
        }
    }

    companion object {
        fun fromInt(value: Int): PurchaseState {
            return when (value) {
                1 -> PURCHASED
                2 -> PENDING
                else -> UNSPECIFIED_STATE
            }
        }
    }
}