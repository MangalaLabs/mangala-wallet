package com.wallet.iap.purchases

enum class PaymentState(val value: Int) {
    PURCHASE_STARTED(0),
    PURCHASE_COMPLETED(1),
    PURCHASE_ERROR(2),
    PURCHASE_CANCELLED(3),
    RESTORE_STARTED(4),
    RESTORE_COMPLETED(5),
    RESTORE_ERROR(6);

    companion object {
        private val map = entries.associateBy(PaymentState::value)

        fun fromInt(v: Int) = map[v]
    }
}