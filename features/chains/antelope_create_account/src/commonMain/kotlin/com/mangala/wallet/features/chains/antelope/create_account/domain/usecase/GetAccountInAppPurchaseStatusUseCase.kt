package com.mangala.wallet.features.chains.antelope.create_account.domain.usecase

import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountByNameUseCase
import com.mangala.wallet.model.blockchain.BlockchainType
import com.wallet.iap.purchases.domain.PurchaseStatus
import com.wallet.iap.purchases.domain.usecases.GetPurchaseStatusUseCase

class GetAccountInAppPurchaseStatusUseCase(
    private val getAccountByNameUseCase: GetAccountByNameUseCase,
    private val getPurchaseStatusUseCase: GetPurchaseStatusUseCase
) {
    suspend operator fun invoke(
        accountName: String,
        blockchainType: BlockchainType
    ): Result<PurchaseStatus> {
        println("GetAccountInAppPurchaseStatusUseCase")

        val account = getAccountByNameUseCase(blockchainType, accountName)
            ?: return Result.failure(Exception("Account not found"))

        println("GetAccountInAppPurchaseStatusUseCase account: $account")

        val purchaseToken = account.purchaseToken
        val purchaseId = account.purchaseId

        if (purchaseToken == null && purchaseId == null) return Result.failure(NoPurchaseAssociatedWithAccountException())

        return Result.success(getPurchaseStatusUseCase(purchaseToken, purchaseId))
    }
}

class NoPurchaseAssociatedWithAccountException: Exception()