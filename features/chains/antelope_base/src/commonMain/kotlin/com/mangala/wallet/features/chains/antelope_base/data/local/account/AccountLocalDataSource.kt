package com.mangala.wallet.features.chains.antelope_base.data.local.account

import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccountBasicInfo
import com.mangala.wallet.features.chains.antelopebase.AntelopeAccountEntity
import kotlinx.coroutines.flow.Flow

interface AccountLocalDataSource {
    suspend fun countImportedAccounts(
        blockchainUid: String,
        includeTempAccounts: Boolean,
        includeIapInitializedAccounts: Boolean
    ): Int

    suspend fun updateAccountStatus(
        accountName: String,
        isTemp: Boolean,
        blockchainUid: String,
        createAccountState: AntelopeAccount.CreateAccountState,
    )

    suspend fun updateAccountStatus(
        accountName: String,
        isTemp: Boolean,
        blockchainUid: String,
        createAccountState: AntelopeAccount.CreateAccountState,
        purchaseToken: String,
        purchaseId: String
    )
    suspend fun updateNotificationRegistered(
        accountName: String,
        blockchainUid: String,
        isNotificationRegistered: Boolean
    )
    suspend fun getAccountsFailedToRegisterNotification(
        blockchainUid: String
    ): List<AntelopeAccountEntity>
    suspend fun listSoftDeletedAccounts(blockchainUid: String): List<AntelopeAccountEntity>
    suspend fun getAccounts(
        blockchainUid: String,
        includeTempAccounts: Boolean,
        includeIapInitializedAccounts: Boolean
    ): List<AntelopeAccountEntity>
    fun getAccountsFlow(
        blockchainUid: String,
        includeTempAccounts: Boolean,
        includeIapInitializedAccounts: Boolean
    ): Flow<List<AntelopeAccountEntity>>
    fun getAccountsFlowWithBasicInfo(
        blockchainUid: String,
        includeTempAccounts: Boolean,
        includeIapInitializedAccounts: Boolean
    ): Flow<List<AntelopeAccountBasicInfo>>
    suspend fun getAccountByName(accountName: String, blockchainUid: String): AntelopeAccountEntity?
    fun getAccountByNameFlow(
        accountName: String,
        blockchainUid: String
    ): Flow<AntelopeAccountEntity?>
    suspend fun insertAccount(accountEntity: AntelopeAccountEntity, isReplace: Boolean)
    suspend fun updateAccount(accountEntity: AntelopeAccountEntity)
    suspend fun updateAccounts(accountEntities: List<AntelopeAccountEntity>)
    suspend fun deleteAccount(accountName: String, blockchainUid: String)
    suspend fun deleteAccounts(
        blockchainUid: String,
        lastUpdatedTimeBefore: Long,
        createAccountState: AntelopeAccount.CreateAccountState
    )
    suspend fun softDeleteAccount(accountName: String, blockchainUid: String)
}