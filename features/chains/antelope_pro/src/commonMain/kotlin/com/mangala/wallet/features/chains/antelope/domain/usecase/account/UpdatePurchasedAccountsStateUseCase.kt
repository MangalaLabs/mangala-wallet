package com.mangala.wallet.features.chains.antelope.domain.usecase.account

import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.features.chains.antelope_base.domain.repository.AccountRepository
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountNameHashUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.UpdateAccountStatusUseCase
import com.mangala.wallet.model.blockchain.BlockchainType
import com.wallet.iap.purchases.PurchaseState
import com.wallet.iap.purchases.device.PurchaseManager
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.hours

class UpdatePurchasedAccountsStateUseCase(
    private val purchaseManager: PurchaseManager,
    private val getAccountsUseCase: GetAccountsUseCase,
    private val updateAccountStatusUseCase: UpdateAccountStatusUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val getAccountNameHashUseCase: GetAccountNameHashUseCase,
    private val accountRepository: AccountRepository
) {
    // To handle IAP purchases registered, but haven't been registered in DB to retry
    suspend operator fun invoke() {
        val blockchainType = getSelectedNetworkUseCase().blockchainType
        val purchases = purchaseManager.getPurchases()

        purchases.onSuccess { allPurchases ->
            val unacknowledgedPurchases = allPurchases.filter { it.acknowledged != true }

            val accounts =
                getAccountsUseCase(includeTempAccounts = true, includeIapInitializedAccounts = true)

            val accountNameHashesAndAccount =
                accounts.associateBy { getAccountNameHashUseCase(it.accountName) }

            // Since this will definitely be smaller than the list of accounts, we iterate over this
            unacknowledgedPurchases.forEach { purchase ->
                val accountNameHash = purchase.obfuscatedProfileId
                val account = accountNameHashesAndAccount[accountNameHash]

                account?.let {
                    val state =
                        if (purchase.getPurchaseStateEnum == PurchaseState.PURCHASED) {
                            AntelopeAccount.CreateAccountState.IAP_CREATE_ACCOUNT_PENDING
                        } else {
                            AntelopeAccount.CreateAccountState.IAP_PAYMENT_PENDING
                        }

                    updateAccountStatusUseCase(
                        account.accountName,
                        account.isTemp,
                        blockchainType,
                        state,
                        purchase.purchaseToken.orEmpty(),
                        purchase.orderId.orEmpty()
                    )
                }
            }
        }
    }
}