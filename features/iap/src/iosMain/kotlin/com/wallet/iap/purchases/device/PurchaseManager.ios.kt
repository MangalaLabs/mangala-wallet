package com.wallet.iap.purchases.device

import androidx.compose.runtime.Composable
import com.wallet.iap.purchases.BillingResult
import com.wallet.iap.purchases.IapProduct
import com.wallet.iap.purchases.PaymentInfo
import com.wallet.iap.purchases.domain.PurchaseStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

actual class PurchaseManager(private val iapManager: IAPManager) {

    private val scope = CoroutineScope(Dispatchers.IO)

    private val _billingResultFlow = MutableSharedFlow<BillingResult>()
    actual val billingResultFlow: Flow<BillingResult> = _billingResultFlow.asSharedFlow()

    private val _purchasesFlow = MutableSharedFlow<PaymentInfo>()
    actual val purchasesFlow: Flow<PaymentInfo> = _purchasesFlow.asSharedFlow()

    init {
        println("PurchaseManager init ${this.hashCode()}")
        iapManager.initialize(
            onPurchaseFlowEmit = {
                scope.launch {
                    println("IAPManager onPurchaseFlowEmit $it")
                    _purchasesFlow.emit(it)
                    println("IAPManager onPurchaseFlow after Emit $it")
                }
            },
            onBillingResultEmit = {
                scope.launch {
                    println("IAPManager onBillingResultEmit $it")
                    _billingResultFlow.emit(it)
                }
            }
        )
    }

    actual suspend fun getPurchaseStatus(purchaseToken: String?, purchaseId: String?): PurchaseStatus {
        val status = iapManager.getPurchaseStatus(purchaseToken.orEmpty(), purchaseId.orEmpty())

        println("PurchaseManager getPurchaseStatus $status")

        return when (status) {
            0 -> PurchaseStatus.SUCCESS
            1 -> PurchaseStatus.PENDING
            else -> PurchaseStatus.FAILURE
        }
    }

    actual suspend fun getPurchases(): Result<List<PaymentInfo>> {
        return Result.failure(Exception("Not implemented"))
    }

    actual suspend fun getPurchases(isPremiumAccount: Boolean): Result<List<PaymentInfo>> {
        return Result.success(iapManager.getPurchases(isPremiumAccount))
    }

    actual suspend fun loadProduct(isPremiumAccount: Boolean): IapProduct? {
        return iapManager.loadProduct(isPremiumAccount)
    }

    actual suspend fun consumePurchase(purchaseToken: String, purchaseId: String) {
        iapManager.finishTransaction(purchaseToken, purchaseId)
    }

    @Composable
    actual fun launchPurchaseFlow(
        productDetails: IapProduct,
        obfuscatedProfileId: String
    ) {
        iapManager.launchPurchaseFlow(productDetails.productId, obfuscatedProfileId)
    }
}