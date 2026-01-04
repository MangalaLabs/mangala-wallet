package com.wallet.iap.purchases.device

import androidx.compose.runtime.Composable
import com.wallet.iap.purchases.BillingResult
import com.wallet.iap.purchases.IapProduct
import com.wallet.iap.purchases.PaymentInfo
import com.wallet.iap.purchases.domain.PurchaseStatus
import kotlinx.coroutines.flow.Flow

actual class PurchaseManager {
    actual suspend fun getPurchaseStatus(purchaseToken: String?, purchaseId: String?): PurchaseStatus {
        TODO("Not yet implemented")
    }

    actual val purchasesFlow: Flow<PaymentInfo>
        get() = TODO("Not yet implemented")
    actual val billingResultFlow: Flow<BillingResult>
        get() = TODO("Not yet implemented")

    actual suspend fun getPurchases(): Result<List<PaymentInfo>> {
        TODO("Not yet implemented")
    }

    actual suspend fun getPurchases(isPremiumAccount: Boolean): Result<List<PaymentInfo>> {
        TODO("Not yet implemented")
    }

    actual suspend fun loadProduct(isPremiumAccount: Boolean): IapProduct? {
        TODO("Not yet implemented")
    }

    actual suspend fun consumePurchase(purchaseToken: String, purchaseId: String) {
    }

    @Composable
    actual fun launchPurchaseFlow(
        productDetails: IapProduct,
        obfuscatedProfileId: String
    ) {
    }
}