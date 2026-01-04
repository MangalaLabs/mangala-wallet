package com.mangala.wallet.domain.account.repository

import com.mangala.wallet.local.account.AccountLocalDataSource
import com.mangala.wallet.model.account.domain.AccountModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AccountRepositoryImpl(
    private val accountLocalDataSource: AccountLocalDataSource
): AccountRepository {

    override fun getAccountByIdUseCase(accountId: String): AccountModel {
        return accountLocalDataSource.selectAccountById(accountId).mapToDomainModel()
    }

    override suspend fun getAccountById(accountId: String): AccountModel {
        return accountLocalDataSource.selectAccountByIdSuspend(accountId).mapToDomainModel()
    }

    override fun getAccountByIdFlow(accountId: String): Flow<AccountModel> {
        return accountLocalDataSource.selectAccountByIdFlow(accountId).map { it.mapToDomainModel() }
    }

    override fun getAllAccountsByWalletIdFlow(walletId: String, filterHiddenAccounts: Boolean): Flow<List<AccountModel>> {
        return accountLocalDataSource.selectAllAccountsByWalletIdFlow(walletId, filterHiddenAccounts).map { it.map { it.mapToDomainModel() } }
    }

    override suspend fun getAllAccountsByWalletId(walletId: String, filterHiddenAccounts: Boolean): List<AccountModel> {
        return accountLocalDataSource.selectAllAccountsByWalletId(walletId, filterHiddenAccounts).map { it.mapToDomainModel() }
    }

    override suspend fun saveAccount(account: AccountModel): Long {
        return accountLocalDataSource.insertAccount(account.toLocalDto())
    }

    override fun updateAccount(account: AccountModel) {
        return accountLocalDataSource.updateAccount(account.toLocalDto())
    }

    override fun updateAccounts(accounts: List<AccountModel>) {
        return accountLocalDataSource.updateAccounts(accounts.map { it.toLocalDto() })
    }

    override suspend fun deleteAccount(accountId: String) {
        accountLocalDataSource.deleteAccount(accountId)
    }

    override suspend fun deleteAllAccounts() {
        accountLocalDataSource.deleteAllAccounts()
    }

    override fun setHiddenAccount(accountId: String) {
        accountLocalDataSource.setHiddenAccount(accountId)
    }
}