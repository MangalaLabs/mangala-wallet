package com.wallet.iap.purchases

import com.android.billingclient.api.Purchase

actual fun getPurchaseStateFromInt(value: Int): PurchaseState {
    return when (value) {
        Purchase.PurchaseState.PURCHASED -> PurchaseState.PURCHASED
        Purchase.PurchaseState.PENDING -> PurchaseState.PENDING
        else -> PurchaseState.UNSPECIFIED_STATE
    }
}