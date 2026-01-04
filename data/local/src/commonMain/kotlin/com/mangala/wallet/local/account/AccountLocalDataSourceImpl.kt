package com.mangala.wallet.local.account

import app.cash.sqldelight.coroutines.asFlow
import com.mangala.wallet.local.MangalaWalletDatabaseWrapper
import com.mangala.wallet.model.account.local.AccountEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class AccountLocalDataSourceImpl(
    databaseWrapper: MangalaWalletDatabaseWrapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): AccountLocalDataSource {

    private val database = databaseWrapper.instance
    private val dbQuery = database.mangalaWalletDatabaseQueries

    @Deprecated(
        "Use selectAccountByIdSuspend instead",
        replaceWith = ReplaceWith("selectAccountByIdSuspend(accountId)")
    )
    override fun selectAccountById(accountId: String): AccountEntity {
        dbQuery.selectAccountById(accountId, ::mapAccountsSelecting).executeAsOneOrNull()?.let {
            return it
        } ?: throw Exception("Account not found")
    }

    override suspend fun selectAccountByIdSuspend(accountId: String): AccountEntity =
        withContext(ioDispatcher) {
            dbQuery.selectAccountById(accountId, ::mapAccountsSelecting).executeAsOneOrNull()
                ?.let {
                    return@withContext it
                } ?: throw Exception("Account not found")
    }

    override fun selectAccountByIdFlow(accountId: String): Flow<AccountEntity> {
        return dbQuery.selectAccountById(accountId, ::mapAccountsSelecting).asFlow().map { it.executeAsOne() }
    }

    override fun selectAllAccountsByWalletIdFlow(walletId: String, filterHiddenAccounts: Boolean): Flow<List<AccountEntity>> {
        val query = if (filterHiddenAccounts) {
            dbQuery.selectAllActiveAccountsByWalletId(walletId, ::mapAccountsSelecting)
        } else {
            dbQuery.selectAllAccountsByWalletId(walletId, ::mapAccountsSelecting)
        }

        return query.asFlow().map { it.executeAsList() }.flowOn(ioDispatcher)
    }

    override suspend fun selectAllAccountsByWalletId(walletId: String, filterHiddenAccounts: Boolean): List<AccountEntity> = withContext(ioDispatcher) {
        val query = if (filterHiddenAccounts) {
            dbQuery.selectAllActiveAccountsByWalletId(walletId, ::mapAccountsSelecting)
        } else {
            dbQuery.selectAllAccountsByWalletId(walletId, ::mapAccountsSelecting)
        }

        return@withContext query.executeAsList()
    }

    override suspend fun getAccountById(id: String): AccountEntity = withContext(ioDispatcher) {
        TODO("Not yet implemented")
    }

    override suspend fun insertAccount(account: AccountEntity): Long = withContext(ioDispatcher) {
        with(account) {
            dbQuery.transactionWithResult {
                dbQuery.insertAccount(
                    id = id,
                    name = name,
                    type = type,
                    wallet_id = walletId,
                    derivation_path_index = derivationPathIndex.toLong(),
                    sorting_order = sortingOrder.toLong(),
                    bip_44_address = bip44Address,
                    bip_49_address = bip49Address,
                    bip_84_address = bip84Address
                )
                dbQuery.getLastInsertedTokenRowId().executeAsOne()
            }
        }
    }

    override fun deleteAccountById(id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAccount(id: String) = withContext(ioDispatcher) {
        dbQuery.transaction {
            dbQuery.deleteAccount(id)
        }
    }

    override suspend fun deleteAllAccounts() = withContext(ioDispatcher) {
        dbQuery.transaction {
            dbQuery.removeAllAccounts()
        }
    }

    override fun updateAccount(account: AccountEntity) {
        dbQuery.updateAccount(account.name, account.sortingOrder.toLong(), if (account.isHidden) 1L else 0L, account.id)
    }

    override fun updateAccounts(accounts: List<AccountEntity>) {
        dbQuery.transaction {
            accounts.forEach {
                updateAccount(it)
            }
        }
    }

    override fun setHiddenAccount(accountId: String) {
        dbQuery.updateHiddenAccount(accountId)
    }

    private fun mapAccountsSelecting(
        id: String,
        name: String?,
        type: String?,
        walletId: String?,
        derivationPathIndex: Long?,
        sortingOrder: Long?,
        isHidden: Long,
        bip44Address: String,
        bip49Address: String,
        bip84Address: String
    ): AccountEntity {

        return AccountEntity(
            id,
            name.orEmpty(),
            type.orEmpty(),
            walletId.orEmpty(),
            derivationPathIndex?.toInt() ?: 0,
            sortingOrder = sortingOrder?.toInt() ?: 0,
            isHidden = isHidden == 1L,
            bip44Address = bip44Address,
            bip49Address = bip49Address,
            bip84Address = bip84Address
        )
    }
}