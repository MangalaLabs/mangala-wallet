package com.mangala.wallet.features.chains.antelope.create_account.domain.usecase

import com.mangala.wallet.antelope_key_manager.domain.usecase.GenerateAccountKeyPairsUseCase
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopePermissionType
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountByNameUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountPrivateKeyUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountWithBalanceInfoUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.SaveAccountPermissionUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.UpdateAccountStatusUseCase
import com.mangala.wallet.model.blockchain.BlockchainType
import com.wallet.iap.purchases.domain.PurchaseStatus
import com.wallet.iap.purchases.domain.usecases.GetPurchaseStatusUseCase

// For handling successful purchases but haven't sent request to our server to create account
class ContinueCreateInAppPurchaseAccountUseCase(
    private val getAccountByNameUseCase: GetAccountByNameUseCase,
    private val generateAccountKeyPairsUseCase: GenerateAccountKeyPairsUseCase,
    private val createAccountWithInAppPurchaseUseCase: CreateAccountWithInAppPurchaseUseCase,
    private val updateAccountStatusUseCase: UpdateAccountStatusUseCase,
    private val getAccountWithBalanceInfoUseCase: GetAccountWithBalanceInfoUseCase,
    private val getPurchaseStatusUseCase: GetPurchaseStatusUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val getAccountPrivateKeyUseCase: GetAccountPrivateKeyUseCase,
    private val saveAccountPermissionUseCase: SaveAccountPermissionUseCase,
) {

    suspend operator fun invoke(accountName: String): Result<String> {
        val blockchainType = getSelectedNetworkUseCase().blockchainType

        return invoke(accountName, blockchainType)
    }

    suspend operator fun invoke(
        accountName: String,
        blockchainType: BlockchainType,
        purchaseToken: String? = null, // pass to override purchase token used for associating purchase
        purchaseId: String? = null // pass to override purchase id used for associating purchase
    ): Result<String> {
        val account = getAccountByNameUseCase(accountName)
            ?: return Result.failure(Exception("Account not found"))

        val iapStatusCheck = checkIapStatus(account)
        val iapStatusCheckException = iapStatusCheck.exceptionOrNull()

        if (iapStatusCheckException != null) {
            return Result.failure(iapStatusCheckException)
        }

        return continueCreateAccount(accountName, blockchainType, account, purchaseToken, purchaseId)
    }

    suspend fun checkAccountCreated(
        accountName: String,
        blockchainType: BlockchainType
    ): Result<Unit> {
        return tryCheckAccountCreated(accountName, blockchainType)
    }

    suspend fun continueCreateAccount(
        accountName: String,
        blockchainType: BlockchainType,
        purchaseToken: String? = null, // pass to override purchase token used for associating purchase
        purchaseId: String? = null // pass to override purchase id used for associating purchase
    ): Result<String> {
        val account = getAccountByNameUseCase(accountName)
            ?: return Result.failure(Exception("Account not found"))

        return continueCreateAccount(
            accountName = accountName,
            blockchainType = blockchainType,
            account = account,
            purchaseToken = purchaseToken,
            purchaseId = purchaseId
        )
    }

    private suspend fun checkIapStatus(
        account: AntelopeAccount
    ): Result<Unit> {
        val purchaseToken =
            account.purchaseToken ?: return Result.failure(Exception("Purchase token is null"))
        val purchaseId = account.purchaseId
        val purchaseStatus = getPurchaseStatusUseCase(purchaseToken, purchaseId.orEmpty())

        // Early check & return to prevent BE spam requests
        if (purchaseStatus == PurchaseStatus.PENDING) {
            return Result.failure(CreateAccountWithInAppPurchaseUseCase.CreateAccountError.PurchasePending)
        }

        if (purchaseStatus == PurchaseStatus.FAILURE) {
            return Result.failure(CreateAccountWithInAppPurchaseUseCase.CreateAccountError.PurchaseCancelled)
        }

        return Result.success(Unit)
    }

    private suspend fun continueCreateAccount(
        accountName: String,
        blockchainType: BlockchainType,
        account: AntelopeAccount,
        purchaseToken: String? = null, // pass to override purchase token used for associating purchase
        purchaseId: String? = null // pass to override purchase id used for associating purchase
    ): Result<String> {
        val existingOwnerKey = getAccountPrivateKeyUseCase(
            accountName,
            AntelopePermissionType.Owner.permissionName,
            blockchainType
        )
        val existingActiveKey = getAccountPrivateKeyUseCase(
            accountName,
            AntelopePermissionType.Active.permissionName,
            blockchainType
        )

        val activeKey = existingActiveKey ?: run {
            val newKeyPair = generateAccountKeyPairsUseCase()
            val key = newKeyPair.activeKeyPair
            saveAccountPermissionUseCase.saveActivePermission(
                privateKey = key,
                accountName = accountName,
                blockchainUid = blockchainType.uid
            )
            key
        }

        val ownerKey = existingOwnerKey ?: run {
            val newKeyPair = generateAccountKeyPairsUseCase()
            val key = newKeyPair.ownerKeyPair
            saveAccountPermissionUseCase.saveOwnerPermission(
                privateKey = key,
                accountName = accountName,
                blockchainUid = blockchainType.uid
            )
            key
        }

        val response = createAccountWithInAppPurchaseUseCase(
            accountName = accountName,
            blockchainType = blockchainType,
            purchaseToken = purchaseToken ?: account.purchaseToken
                ?: return Result.failure(Exception("Purchase token is null")),
            purchaseId = purchaseId ?: account.purchaseId.orEmpty(),
            activeKey = activeKey,
            ownerKey = ownerKey
        )

        if (response.isFailure) {
            val exception = response.exceptionOrNull()
            if (exception is CreateAccountWithInAppPurchaseUseCase.CreateAccountError) {
                when (exception) {
                    CreateAccountWithInAppPurchaseUseCase.CreateAccountError.AntelopeNodeError,
                    CreateAccountWithInAppPurchaseUseCase.CreateAccountError.PurchaseCancelled -> return response

                    CreateAccountWithInAppPurchaseUseCase.CreateAccountError.PurchaseAlreadyConsumed -> {
                        if (tryCheckAccountCreated(accountName, blockchainType).isSuccess) return Result.success("Account recreated")
                    }

                    else -> {}
                }
            }

            return response
        }

        updateAccountStatusUseCase(
            accountName = accountName,
            isTemp = false,
            blockchainType = blockchainType,
            createAccountState = AntelopeAccount.CreateAccountState.DONE
        )

        return response
    }

    private suspend fun tryCheckAccountCreated(
        accountName: String,
        blockchainType: BlockchainType
    ): Result<Unit> {
        val getAccountResult = getAccountWithBalanceInfoUseCase(
            accountName,
            blockchainType,
            forceRefresh = true
        )

        val exception = getAccountResult.exceptionOrNull()
        if (exception == null) {
            updateAccountStatusUseCase(
                accountName = accountName,
                isTemp = false,
                blockchainType = blockchainType,
                createAccountState = AntelopeAccount.CreateAccountState.DONE
            )

            return Result.success(Unit)
        } else {
            return Result.failure(exception)
        }
    }
}