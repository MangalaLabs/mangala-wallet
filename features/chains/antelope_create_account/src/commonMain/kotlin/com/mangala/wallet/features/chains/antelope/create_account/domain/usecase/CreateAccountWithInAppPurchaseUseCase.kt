package com.mangala.wallet.features.chains.antelope.create_account.domain.usecase

import com.mangala.wallet.features.chains.antelope_base.domain.repository.createaccount.CreateAccountRepository
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.utils.exception.MangalaRemoteException
import com.memtrip.eos.core.crypto.EosPrivateKey
import com.wallet.iap.purchases.device.PurchaseManager

class CreateAccountWithInAppPurchaseUseCase(
    private val createAccountRepository: CreateAccountRepository,
    private val purchaseManager: PurchaseManager
) {
    suspend operator fun invoke(
        accountName: String,
        blockchainType: BlockchainType,
        purchaseToken: String,
        purchaseId: String,
        activeKey: EosPrivateKey,
        ownerKey: EosPrivateKey
    ): Result<String> {
        createAccountRepository.createAccount(
            accountName = accountName,
            blockchainType = blockchainType,
            purchaseToken = purchaseToken,
            activePublicKey = activeKey.publicKey.toString(),
            ownerPublicKey = ownerKey.publicKey.toString(),
        ).fold(
            onSuccess = {
                purchaseManager.consumePurchase(purchaseToken, purchaseId)
                return Result.success(it)
            },
            onFailure = { exception ->
                when (exception) {
                    is MangalaRemoteException -> {
                        when (exception) {
                            is MangalaRemoteException.HttpException -> {
                                when (exception.message) {
                                    "Failed to verify purchase. State Cancled" -> {
                                        return Result.failure(CreateAccountError.PurchaseCancelled)
                                    }

                                    "Failed to verify purchase. State Pending" -> {
                                        return Result.failure(CreateAccountError.PurchasePending)
                                    }

                                    "IAP token had already exist" -> {
                                        return Result.failure(CreateAccountError.PurchaseAlreadyConsumed)
                                    }

                                    else -> {
                                        return Result.failure(CreateAccountError.AntelopeNodeError)
                                    }
                                }
                            }

                            is MangalaRemoteException.NetworkException -> return Result.failure(
                                CreateAccountError.NetworkError
                            )

                            is MangalaRemoteException.SerializationError, is MangalaRemoteException.UnknownError -> return Result.failure(
                                CreateAccountError.UnknownError
                            )
                        }
                    }

                    else -> {
                        return Result.failure(CreateAccountError.UnknownError)
                    }
                }
            }
        )
    }

    sealed class CreateAccountError : Throwable() {
        data object PurchaseCancelled : CreateAccountError()
        data object PurchasePending : CreateAccountError()
        data object PurchaseAlreadyConsumed : CreateAccountError()
        data object AntelopeNodeError : CreateAccountError()
        data object NetworkError : CreateAccountError()
        data object UnknownError : CreateAccountError()
    }
}