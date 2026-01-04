package com.wallet.iap.purchases.device

import com.wallet.iap.purchases.BillingResult
import com.wallet.iap.purchases.IapProduct
import com.wallet.iap.purchases.PaymentInfo

interface IAPManager {
    suspend fun finishTransaction(purchaseToken: String, purchaseId: String)
    suspend fun getPurchaseStatus(purchaseToken: String, purchaseId: String): Int
    fun initialize(onPurchaseFlowEmit: (PaymentInfo) -> Unit, onBillingResultEmit: (BillingResult) -> Unit)
    fun launchPurchaseFlow(productId: String, userUuid: String)
    suspend fun loadProduct(isPremiumAccount: Boolean): IapProduct?
    suspend fun getPurchases(isPremiumAccount: Boolean): List<PaymentInfo>
}