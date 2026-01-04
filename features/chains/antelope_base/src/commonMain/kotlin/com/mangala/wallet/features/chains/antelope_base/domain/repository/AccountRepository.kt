package com.mangala.wallet.features.chains.antelope_base.domain.repository

import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccountBasicInfo
import com.mangala.wallet.features.chains.antelope_base.domain.model.account.AntelopeAccountByAuthorizer
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.util.Resource
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    suspend fun countImportedAccounts(
        blockchainType: BlockchainType,
        includeTempAccounts: Boolean,
        includeIapInitializedAccounts: Boolean
    ): Int
    suspend fun getAccountsByAuthorizers(key: String, blockchainType: BlockchainType): Result<List<AntelopeAccountByAuthorizer>>
    suspend fun updateAccountStatus(
        accountName: String,
        isTemp: Boolean,
        blockchainType: BlockchainType,
        createAccountState: AntelopeAccount.CreateAccountState
    )
    suspend fun updateAccountStatus(
        accountName: String,
        isTemp: Boolean,
        blockchainType: BlockchainType,
        createAccountState: AntelopeAccount.CreateAccountState,
        purchaseToken: String,
        purchaseId: String
    )
    suspend fun updateNotificationRegistered(
        accountName: String,
        blockchainType: BlockchainType,
        isNotificationRegistered: Boolean
    )
    suspend fun getAccountsFailedToRegisterNotification(
        blockchainType: BlockchainType
    ): List<AntelopeAccount>
    suspend fun listSoftDeletedAccounts(blockchainType: BlockchainType): List<AntelopeAccount>
    suspend fun getAccountWithBalanceInfo(accountName: String, blockchainType: BlockchainType, forceRefresh: Boolean): Result<AntelopeAccount?>
    suspend fun getAccountWithBalanceInfoFlow(
        accountName: String,
        blockchainType: BlockchainType,
        includeIapInitializedAccounts: Boolean,
        forceRefresh: Boolean
    ): Flow<Resource<AntelopeAccount?>>
    suspend fun getAccountsWithBalanceInfoFlow(
        accountNames: List<String>,
        blockchainType: BlockchainType,
        includeTempAccounts: Boolean,
        includeIapInitializedAccounts: Boolean,
        forceRefresh: Boolean
    ): Flow<Resource<List<AntelopeAccount>?>>

    suspend fun getAccount(
        blockchainType: BlockchainType,
        accountName: String
    ): Result<AntelopeAccount>

    suspend fun getAccounts(
        blockchainType: BlockchainType,
        includeTempAccounts: Boolean,
        includeIapInitializedAccounts: Boolean
    ): List<AntelopeAccount>

    fun getAccountsFlow(
        blockchainType: BlockchainType,
        includeTempAccounts: Boolean,
        includeIapInitializedAccounts: Boolean
    ): Flow<List<AntelopeAccount>>
    fun invokeWithBasicInfo(
        includeTempAccounts: Boolean = false,
        includeIapInitializedAccounts: Boolean = false,
        blockchainType: BlockchainType
    ): Flow<List<AntelopeAccountBasicInfo>>

    suspend fun getAccountByName(accountName: String, blockchainType: BlockchainType): AntelopeAccount?
    suspend fun insertAccount(account: AntelopeAccount, blockchainType: BlockchainType, isReplace: Boolean)
    suspend fun deleteAccount(accountName: String, blockchainType: BlockchainType)
    suspend fun deleteAccounts(
        blockchainType: BlockchainType,
        lastUpdatedTimeBefore: Long,
        createAccountState: AntelopeAccount.CreateAccountState
    )
    suspend fun softDeleteAccount(accountName: String, blockchainType: BlockchainType)
}