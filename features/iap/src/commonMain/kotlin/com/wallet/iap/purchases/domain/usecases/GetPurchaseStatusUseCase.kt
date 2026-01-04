package com.wallet.iap.purchases.domain.usecases

import com.wallet.iap.purchases.device.PurchaseManager
import com.wallet.iap.purchases.domain.PurchaseStatus

class GetPurchaseStatusUseCase(private val purchaseManager: PurchaseManager) {

    suspend operator fun invoke(purchaseToken: String?, purchaseId: String?): PurchaseStatus {
        return purchaseManager.getPurchaseStatus(purchaseToken, purchaseId)
    }
}