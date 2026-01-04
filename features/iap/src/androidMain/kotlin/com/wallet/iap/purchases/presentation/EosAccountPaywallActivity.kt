//package com.wallet.iap.purchases.presentation
//
//import android.app.Activity
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.widget.Toast
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.viewModels
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.core.view.WindowCompat
//import org.koin.core.component.KoinComponent
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.CircularProgressIndicator
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.android.billingclient.api.Purchase
//import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
//import com.wallet.iap.purchases.PaymentConstant
//import com.wallet.iap.purchases.PaymentState
//import com.wallet.iap.purchases.device.IapManager
//
//class EosAccountPaywallActivity : ComponentActivity(), KoinComponent {
//
//    private val viewModel: EosAccountPaywallViewModel by viewModels()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        WindowCompat.setDecorFitsSystemWindows(window, true)
//        setContent {
//            AndroidIap()
//        }
//    }
//
//    override fun onStart() {
//        super.onStart()
//
//        val accountName = intent.getStringExtra("EXTRA_ACCOUNT_NAME").orEmpty()
//        val isPremiumAccount = AntelopeAccount.isPremiumAccountName(accountName)
//
//        viewModel.init(isPremiumAccount)
//    }
//
//    @Composable
//    fun AndroidIap() {
//        LaunchedEffect(Unit) {
//            IapManager.purchasesFlow.collect {
//                val purchase = it.first()
//
//                when (purchase.purchaseState) {
//                    Purchase.PurchaseState.PURCHASED -> {
//                        Log.d("TestIAP", "Purchase completed ${purchase.purchaseToken}")
//                        viewModel.isLoading.value = false
//                        val resultIntent = Intent().apply {
//                            putExtra(
//                                PaymentConstant.EXTRA_STATE,
//                                PaymentState.PURCHASE_COMPLETED.value
//                            )
//                            putExtra(PaymentConstant.EXTRA_INFO, purchase.originalJson)
//                        }
//                        setResult(Activity.RESULT_OK, resultIntent)
//                        finish()
//                    }
//
//                    Purchase.PurchaseState.PENDING -> {
//                        viewModel.onPurchasePending(
//                            intent.getStringExtra("EXTRA_ACCOUNT_NAME").orEmpty(),
//                            purchase.purchaseToken
//                        )
//                        viewModel.isLoading.value = true
//                        Toast.makeText(
//                            this@EosAccountPaywallActivity,
//                            "Purchase is pending",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//
//                    else -> {
//                        viewModel.isLoading.value = false
//                    }
//                }
//            }
//        }
//
//        Surface(
//            modifier = Modifier.fillMaxSize(),
//            color = MaterialTheme.colorScheme.background
//        ) {
//            Box(modifier = Modifier.fillMaxSize()) {
//                if (viewModel.isLoading.value) {
//                    CircularProgressIndicator(Modifier.align(Alignment.Center))
//                }
//
//                Column {
//                    Spacer(modifier = Modifier.height(16.dp))
//
//                    LazyColumn(modifier = Modifier
//                        .background(Color.LightGray)
//                        .fillMaxWidth()){
//                        items(viewModel.iapProductDetailsResult.value) { productDetails ->
//                            Button(
//                                onClick = {
//                                    viewModel.onPurchase(
//                                        this@EosAccountPaywallActivity,
//                                        productDetails
//                                    )
//                                },
//                                modifier = Modifier
//                                    .padding(10.dp)
//                                    .fillMaxWidth(),
//                                shape = RoundedCornerShape(12.dp),
//                                colors = ButtonDefaults.buttonColors(
//                                    containerColor = Color.Red
//                                )
//                            ) {
//                                Text(
//                                    text = productDetails.name + " - " + productDetails.formattedPrice,
//                                    fontSize = 16.sp
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
