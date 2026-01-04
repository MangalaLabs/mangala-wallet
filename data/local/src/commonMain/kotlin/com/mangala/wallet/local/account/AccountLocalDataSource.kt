package com.mangala.wallet.local.account

import com.mangala.wallet.model.account.local.AccountEntity
import kotlinx.coroutines.flow.Flow

interface AccountLocalDataSource {

    @Deprecated("Use selectAccountByIdSuspend instead", ReplaceWith("selectAccountByIdSuspend(accountId)"))
    fun selectAccountById(accountId: String): AccountEntity
    suspend fun selectAccountByIdSuspend(accountId: String): AccountEntity
    fun selectAccountByIdFlow(accountId: String): Flow<AccountEntity>
    fun selectAllAccountsByWalletIdFlow(walletId: String, filterHiddenAccounts: Boolean): Flow<List<AccountEntity>>
    suspend fun selectAllAccountsByWalletId(walletId: String, filterHiddenAccounts: Boolean): List<AccountEntity>
    suspend fun getAccountById(id: String): AccountEntity
    suspend fun insertAccount(account: AccountEntity): Long
    fun deleteAccountById(id: String)
    suspend fun deleteAccount(id: String)
    suspend fun deleteAllAccounts()
    fun updateAccount(account: AccountEntity)
    fun updateAccounts(accounts: List<AccountEntity>)
    fun setHiddenAccount(accountId: String)
}