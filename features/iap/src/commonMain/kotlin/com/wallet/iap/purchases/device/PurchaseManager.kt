package com.wallet.iap.purchases.device

import androidx.compose.runtime.Composable
import com.wallet.iap.purchases.BillingResult
import com.wallet.iap.purchases.IapProduct
import com.wallet.iap.purchases.PaymentInfo
import com.wallet.iap.purchases.domain.PurchaseStatus
import kotlinx.coroutines.flow.Flow

expect class PurchaseManager {
    val purchasesFlow: Flow<PaymentInfo>
    val billingResultFlow: Flow<BillingResult>

    suspend fun getPurchaseStatus(purchaseToken: String?, purchaseId: String?): PurchaseStatus
    suspend fun getPurchases(): Result<List<PaymentInfo>>
    suspend fun getPurchases(isPremiumAccount: Boolean): Result<List<PaymentInfo>>
    suspend fun loadProduct(isPremiumAccount: Boolean): IapProduct?
    suspend fun consumePurchase(purchaseToken: String, purchaseId: String)

    @Composable
    fun launchPurchaseFlow(
        productDetails: IapProduct,
        obfuscatedProfileId: String
    )
}