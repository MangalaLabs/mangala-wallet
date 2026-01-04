package com.mangala.wallet.features.chains.antelope.create_account.domain.usecase

import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountsUseCase
import com.wallet.iap.purchases.PaymentInfo
import com.wallet.iap.purchases.device.PurchaseManager

class GetFirstUnassignedPurchaseUseCase(
    private val purchaseManager: PurchaseManager,
    private val getAccountsUseCase: GetAccountsUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
) {
    // This is particularly useful for iOS IAP, since pending purchases on iOS doesn't generate a purchase token nor a purchase id
    // returns the newly updated account if it was associated with a purchase, otherwise null
    suspend operator fun invoke(
        accountNameWithSuffix: String
    ): PaymentInfo? {
        val blockchainType = getSelectedNetworkUseCase().blockchainType
        val accounts = getAccountsUseCase(
            blockchainType,
            includeTempAccounts = true,
            includeIapInitializedAccounts = true
        )
        val purchases =
            purchaseManager.getPurchases(AntelopeAccount.isPremiumAccountName(accountNameWithSuffix))

        val unassignedPurchases = purchases.getOrNull()?.filter { purchase ->
            accounts.any { it.purchaseToken == purchase.purchaseToken || it.purchaseId == purchase.orderId }
        }

        if (unassignedPurchases.isNullOrEmpty()) return null
        return unassignedPurchases.firstOrNull()
    }
}