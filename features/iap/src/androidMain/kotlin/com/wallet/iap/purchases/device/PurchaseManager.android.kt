package com.wallet.iap.purchases.device

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.Purchase
import com.soywiz.krypto.sha256
import com.wallet.iap.purchases.BillingResult
import com.wallet.iap.purchases.IapProduct
import com.wallet.iap.purchases.PaymentInfo
import com.wallet.iap.purchases.domain.PurchaseStatus
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

actual class PurchaseManager(
    private val parsingJson: Json
) {

    actual val purchasesFlow: Flow<PaymentInfo> = IapManager.purchasesFlow.mapNotNull {
        Log.d("PurchaseManager", "Purchases $it")
        val firstPurchase = it.firstOrNull() ?: return@mapNotNull null

        val paymentInfo = firstPurchase.toPaymentInfo()
        paymentInfo.copy(purchaseState = firstPurchase.purchaseState)
    }

    actual val billingResultFlow: Flow<BillingResult> = IapManager.billingResultFlow.map {
        Log.d("PurchaseManager", "BillingResult $it")

        val responseCode = when (it.responseCode) {
            BillingClient.BillingResponseCode.OK -> BillingResult.ResponseCode.OK
            BillingClient.BillingResponseCode.USER_CANCELED -> BillingResult.ResponseCode.USER_CANCELED
            BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE -> BillingResult.ResponseCode.SERVICE_UNAVAILABLE
            BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> BillingResult.ResponseCode.BILLING_UNAVAILABLE
            BillingClient.BillingResponseCode.ITEM_UNAVAILABLE -> BillingResult.ResponseCode.ITEM_UNAVAILABLE
            BillingClient.BillingResponseCode.DEVELOPER_ERROR -> BillingResult.ResponseCode.DEVELOPER_ERROR
            BillingClient.BillingResponseCode.ERROR -> BillingResult.ResponseCode.ERROR
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> BillingResult.ResponseCode.ITEM_ALREADY_OWNED
            BillingClient.BillingResponseCode.ITEM_NOT_OWNED -> BillingResult.ResponseCode.ITEM_NOT_OWNED
            BillingClient.BillingResponseCode.NETWORK_ERROR -> BillingResult.ResponseCode.NETWORK_ERROR
            else -> BillingResult.ResponseCode.ERROR
        }

        BillingResult(responseCode)
    }

    private var firebaseInstallationId: String? = null

    init {
        GlobalScope.launch {
            firebaseInstallationId = getFirebaseInstallationId()
        }
    }

    actual suspend fun getPurchaseStatus(purchaseToken: String?, purchaseId: String?): PurchaseStatus {
        val purchases = IapManager.queryPurchases()

        purchases.purchasesList.find { it.purchaseToken == purchaseToken }?.let {
            return when (it.purchaseState) {
                Purchase.PurchaseState.PENDING -> PurchaseStatus.PENDING
                Purchase.PurchaseState.PURCHASED -> PurchaseStatus.SUCCESS
                else -> PurchaseStatus.FAILURE
            }
        }

        return PurchaseStatus.FAILURE
    }

    actual suspend fun getPurchases(): Result<List<PaymentInfo>> {
        // queryPurchases only return non consumed one-time purchases
        // https://developer.android.com/google/play/billing/integrate#fetch
        val result = IapManager.queryPurchases()

        if (result.billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
            return Result.failure(Exception("Failed to query purchases ${result.billingResult.debugMessage}"))
        }

        val paymentInfo = result.purchasesList.map {
            it.toPaymentInfo()
        }

        return Result.success(paymentInfo)
    }

    actual suspend fun getPurchases(isPremiumAccount: Boolean): Result<List<PaymentInfo>> {
        return getPurchases().map { purchases ->
            purchases.filter {
                if (isPremiumAccount) {
                    IapManager.isPremiumProductId(it.productId.orEmpty())
                } else {
                    IapManager.isStandardProductId(it.productId.orEmpty())
                }
            }
        }
    }

    actual suspend fun loadProduct(isPremiumAccount: Boolean): IapProduct? {
        val result = IapManager.loadProducts(isPremiumAccount)

        return result.firstOrNull()
    }

    @SuppressLint("ComposableNaming")
    @Composable
    actual fun launchPurchaseFlow(
        productDetails: IapProduct,
        obfuscatedProfileId: String
    ) {
        val activity = LocalContext.current.findActivity() ?: return

        val obfuscatedId = firebaseInstallationId?.toByteArray()?.sha256()?.hex

        IapManager.launchPurchaseFlow(
            activity,
            productDetails,
            obfuscatedAccountId = obfuscatedId,
            obfuscatedProfileId = obfuscatedProfileId
        )
    }

    actual suspend fun consumePurchase(purchaseToken: String, purchaseId: String) {
        // For Android, this is handled by the backend
    }

    private tailrec fun Context.findActivity(): Activity? = when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }

    private fun Purchase.toPaymentInfo(): PaymentInfo {
        return parsingJson.decodeFromString<PaymentInfo>(originalJson)
    }
}