package com.wallet.iap.purchases.device

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingFlowParams.ProductDetailsParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesResult
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.SkuDetailsParams
import com.android.billingclient.api.consumePurchase
import com.android.billingclient.api.queryProductDetails
import com.android.billingclient.api.queryPurchasesAsync
import com.android.billingclient.api.querySkuDetails
import com.wallet.iap.purchases.IapProduct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.min

object IapManager {

    val scope = CoroutineScope(Dispatchers.IO)

    private val _billingResultFlow = MutableSharedFlow<BillingResult>()
    val billingResultFlow = _billingResultFlow.asSharedFlow()

    private val _purchasesFlow = MutableSharedFlow<List<Purchase>>()
    val purchasesFlow = _purchasesFlow.asSharedFlow()

    private lateinit var billingClient: BillingClient

    fun init(context: Context, onBillingSetupFinished: () -> Unit) {
        try {
            billingClient = BillingClient.newBuilder(context)
                .setListener { billingResult, purchases ->
                    scope.launch {
                        Log.d("TestIAP", "Purchases: $purchases billingResult $billingResult")

                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                            Log.d("TestIAP", "Purchases: $purchases")
                            _purchasesFlow.emit(purchases)
                        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                            // Handle an error caused by a user cancelling the purchase flow.
                            Log.d("TestIAP", "User cancelled")

                            _billingResultFlow.emit(billingResult)
                        } else {
                            _billingResultFlow.emit(billingResult)
                            // Handle any other error codes.
                            Log.d("TestIAP", "Error: ${billingResult.responseCode}")
                        }
                    }
                }
                .enablePendingPurchases()
                .build()

            billingClient.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    scope.launch {
                        Log.d("TestIAP", "onBillingSetupFinished $billingResult")
                        onBillingSetupFinished()
                    }
                }

                override fun onBillingServiceDisconnected() {
                    Log.d("TestIAP", "onBillingServiceDisconnected")
                }
            })
        } catch (e: Exception) {

        }
    }

    suspend fun queryPurchases(): PurchasesResult {
        return billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP).build()
        )
    }

    fun isPremiumProductId(productId: String): Boolean {
        return productId == PREMIUM_ACCOUNT_PRODUCT_ID
    }

    fun isStandardProductId(productId: String): Boolean {
        return productId == STANDARD_ACCOUNT_PRODUCT_ID
    }

    suspend fun loadProducts(isPremiumAccount: Boolean): List<IapProduct> {
        val productId =
            if (isPremiumAccount) PREMIUM_ACCOUNT_PRODUCT_ID else STANDARD_ACCOUNT_PRODUCT_ID
        val productIdList = listOf(productId)

        val productDetailsSupported =
            billingClient.isFeatureSupported(BillingClient.FeatureType.PRODUCT_DETAILS).responseCode == BillingClient.BillingResponseCode.OK

        return if (productDetailsSupported) {
            Log.d("TestIAP", "Billing setup finished")
            val productList = productIdList.map {
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(it)
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build()
            }

            val params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build()

            val productDetailsResult = billingClient.queryProductDetails(params)
            Log.d("TestIAP", "Product details: $productDetailsResult")

            productDetailsResult.productDetailsList?.map {
                IapProduct.Product(it)
            } ?: emptyList()
        } else {
            // Backwards compatibility for devices that doesn't support ProductDetails
            // https://developer.android.com/google/play/billing/integrate#querying_with_kotlin_extensions
            // https://developer.android.com/google/play/billing/migrate-gpblv6#showing-products

            val paramsBuilder = SkuDetailsParams.newBuilder()
            val params =
                paramsBuilder.setSkusList(productIdList).setType(BillingClient.SkuType.INAPP)
                    .build()

            val response = billingClient.querySkuDetails(params)

            response.skuDetailsList?.map {
                IapProduct.Sku(it)
            } ?: emptyList()
        }
    }

    fun launchPurchaseFlow(
        activity: Activity,
        iapProduct: IapProduct,
        obfuscatedAccountId: String? = null,
        obfuscatedProfileId: String? = null
    ) {
        val billingFlowParams = BillingFlowParams.newBuilder().apply {
            when (iapProduct) {
                is IapProduct.Product -> {
                    val productDetailsParamsList = listOf(
                        ProductDetailsParams.newBuilder()
                            .setProductDetails(iapProduct.productDetails)
                            .build()
                    )

                    setProductDetailsParamsList(productDetailsParamsList)
                }

                is IapProduct.Sku -> {
                    setSkuDetails(iapProduct.skuDetails)
                }
            }
            if (obfuscatedAccountId != null && obfuscatedProfileId != null) {
                setObfuscatedAccountId(obfuscatedAccountId)
                setObfuscatedProfileId(obfuscatedProfileId)
            }
        }.build()

        billingClient.launchBillingFlow(activity, billingFlowParams)
    }

    suspend fun verifyPurchase(purchase: Purchase): Result<Unit> {
        val consumeParams =
            ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
        val consumeResult = withContext(Dispatchers.IO) {
            billingClient.consumePurchase(consumeParams)
        }

        return if (consumeResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            Log.d("TestIAP", "Purchase consume succ: $purchase")
            return Result.success(Unit)
        } else {
            Log.d("TestIAP", "Purchase consume failed: $consumeResult")
            Result.failure(Exception("Purchase consume failed"))
        }
    }

    private const val PREMIUM_ACCOUNT_PRODUCT_ID = "mangala_premium_eos_account"
    private const val STANDARD_ACCOUNT_PRODUCT_ID = "mangala_regular_eos_native_acc"

}