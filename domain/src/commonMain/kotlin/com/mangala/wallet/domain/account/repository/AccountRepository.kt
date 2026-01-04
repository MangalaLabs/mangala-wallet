package com.mangala.wallet.domain.account.repository

import com.mangala.wallet.model.account.domain.AccountModel
import kotlinx.coroutines.flow.Flow

interface AccountRepository {

    @Deprecated("Use getAccountById instead", ReplaceWith("getAccountById(accountId)"))
    fun getAccountByIdUseCase(accountId: String): AccountModel
    suspend fun getAccountById(accountId: String): AccountModel
    fun getAccountByIdFlow(accountId: String): Flow<AccountModel>
    fun getAllAccountsByWalletIdFlow(walletId: String, filterHiddenAccounts: Boolean): Flow<List<AccountModel>>
    suspend fun getAllAccountsByWalletId(walletId: String, filterHiddenAccounts: Boolean): List<AccountModel>
    suspend fun saveAccount(account: AccountModel): Long
    fun updateAccount(account: AccountModel)
    fun updateAccounts(accounts: List<AccountModel>)
    suspend fun deleteAccount(accountId: String)
    suspend fun deleteAllAccounts()
    fun setHiddenAccount(accountId: String)
}