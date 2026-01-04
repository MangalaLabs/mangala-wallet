package com.wallet.iap.purchases

actual fun getPurchaseStateFromInt(value: Int): PurchaseState {
    return when (value) {
        1 -> PurchaseState.PURCHASED
        2 -> PurchaseState.PENDING
        else -> PurchaseState.UNSPECIFIED_STATE
    }
}