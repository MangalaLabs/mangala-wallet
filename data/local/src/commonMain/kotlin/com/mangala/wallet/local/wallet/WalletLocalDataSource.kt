package com.mangala.wallet.local.wallet

import com.mangala.wallet.model.wallet.domain.WalletModel
import com.mangala.wallet.model.wallet.local.WalletEntity
import kotlinx.coroutines.flow.Flow

interface WalletLocalDataSource {

    suspend fun setSelectedWallet(walletId: String)

    fun getSelectedWallet(): WalletEntity?

    fun getSelectedWalletFlow(): Flow<WalletEntity?>

    suspend fun getAllWallets(): List<WalletEntity>

    suspend fun getWalletById(id: String): WalletModel?

    suspend fun insertWallet(walletEntity: WalletEntity)

    suspend fun updateWallet(walletEntity: WalletEntity)

    suspend fun deleteWallet(id: String)
    suspend fun deleteAllWallets()
    suspend fun saveWalletName(id: String, walletName: String)
    suspend fun updateWalletBackupStatus(id: String, isBackedUp: Boolean)
    fun getWalletByIdFlow(id: String): Flow<WalletModel?>

}