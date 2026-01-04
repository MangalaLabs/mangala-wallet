package com.mangala.wallet.features.chains.antelope.create_account.domain.usecase

import com.mangala.wallet.antelope_key_manager.domain.usecase.GenerateAccountKeyPairsUseCase
import com.mangala.wallet.domain.portfolio.usecases.EnsureAccountInPortfolioUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.DeleteAccountUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.SaveAccountUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.UpdateAccountStatusUseCase
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.utils.exception.MangalaRemoteException
import com.wallet.iap.purchases.PaymentInfo
import com.wallet.iap.purchases.device.PurchaseManager

class CreateAndSaveAccountWithInAppPurchaseUseCase(
    private val generateAccountKeyPairsUseCase: GenerateAccountKeyPairsUseCase,
    private val saveAccountUseCase: SaveAccountUseCase,
    private val updateAccountStatusUseCase: UpdateAccountStatusUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val createAccountWithInAppPurchaseUseCase: CreateAccountWithInAppPurchaseUseCase,
    private val purchaseManager: PurchaseManager,
    private val ensureAccountInPortfolioUseCase: EnsureAccountInPortfolioUseCase
) {
    suspend fun createWithExistingPurchase(
        accountName: String,
        blockchainType: BlockchainType,
        isPremiumAccount: Boolean
    ): Result<String> {
        val purchase = purchaseManager.getPurchases(isPremiumAccount).getOrNull()
            ?.find { it.acknowledged == false }
            ?: return Result.failure(Exception("No purchase found"))

        return invoke(
            accountName,
            blockchainType,
            purchase
        )
    }

    suspend operator fun invoke(
        accountName: String,
        blockchainType: BlockchainType,
        paymentInfo: PaymentInfo
    ): Result<String> {
        val purchaseToken =
            paymentInfo.purchaseToken ?: return Result.failure(Exception("Purchase token is null"))
        val purchaseId = paymentInfo.orderId.orEmpty()

        return invoke(accountName, blockchainType, purchaseToken, purchaseId)
    }

    suspend operator fun invoke(
        accountName: String,
        blockchainType: BlockchainType,
        purchaseToken: String,
        purchaseId: String
    ): Result<String> {
        val (activeKey, ownerKey) = generateAccountKeyPairsUseCase()

        saveAccountUseCase(
            accountName = accountName,
            activePrivateKey = activeKey,
            ownerPrivateKey = ownerKey,
            isTemp = true,
            createAccountState = AntelopeAccount.CreateAccountState.IAP_CREATE_ACCOUNT_PENDING,
            isReplace = true,
            purchaseToken = purchaseToken
        )

        val result = createAccountWithInAppPurchaseUseCase(
            accountName = accountName,
            blockchainType = blockchainType,
            purchaseToken = purchaseToken,
            purchaseId = purchaseId,
            activeKey = activeKey,
            ownerKey = ownerKey
        )

        if (result.isFailure) {
            val exception = result.exceptionOrNull()
            if (exception is MangalaRemoteException) {
                // If BE returns an error then can delete the account. Otherwise keep it around so that users can retry/ recover account
                when (exception) {
                    is MangalaRemoteException.NetworkException -> {
                        return Result.failure(exception)
                    }

                    else -> {
                        deleteAccountUseCase(
                            accountName = accountName,
                            blockchainType = blockchainType
                        )

                        return Result.failure(exception)
                    }
                }
            }
            return result
        }

        updateAccountStatusUseCase(
            accountName = accountName,
            isTemp = false,
            blockchainType = blockchainType,
            createAccountState = AntelopeAccount.CreateAccountState.DONE
        )

        // Add account to portfolio after successful creation
        ensureAccountInPortfolioUseCase(
            accountName = accountName,
            blockchainType = blockchainType
        )

        return result
    }
}